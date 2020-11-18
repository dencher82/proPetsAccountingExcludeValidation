package propets.accounting.service.security;

import static propets.accounting.configuration.Constants.TOKEN_HEADER;

import java.net.URI;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import propets.accounting.dao.AccountingRepository;
import propets.accounting.dto.AccountCreateDto;
import propets.accounting.dto.AccountLoginDto;
import propets.accounting.dto.exception.AccountNotFoundException;
import propets.accounting.dto.exception.TokenValidateException;
import propets.accounting.dto.exception.UnauthorizedException;
import propets.accounting.model.Account;

@Service
public class AccountingSecurityImpl implements AccountingSecurity {
	
	@Value("${validation.url}")
	String validationServiceUrl;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	AccountingRepository repository;

	@Override
	public String getLogin(String token) {
		AccountLoginDto accountLoginDto = tokenDecode(token);
		Account account = repository.findById(accountLoginDto.getLogin())
				.orElseThrow(() -> new AccountNotFoundException(accountLoginDto.getLogin()));
		if (!BCrypt.checkpw(accountLoginDto.getPassword(), account.getPassword())) {
			throw new UnauthorizedException();
		}
		return account.getEmail();
	}
	
	@Override
	public String createToken(AccountCreateDto accountCreateDto) {
		String base64token = createBase64token(accountCreateDto.getEmail(), accountCreateDto.getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", base64token);
		try {
			RequestEntity<String> requestEntity = new RequestEntity<String>(headers, HttpMethod.GET, new URI(validationServiceUrl + "/token/create"));
			ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
			return responseEntity.getHeaders().getFirst(TOKEN_HEADER);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TokenValidateException();
		}		
	}

	private String createBase64token(String login, String password) {
		String credentials = login + ":" + password;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
		return "Basic " + encodedCredentials;
	}

	private AccountLoginDto tokenDecode(String token) {
		try {
			String[] credentials = token.split(" ");
			String credential = new String(Base64.getDecoder().decode(credentials[1]));
			credentials = credential.split(":");
			return new AccountLoginDto(credentials[0], credentials[1]);
		} catch (Exception e) {
			throw new TokenValidateException();
		}
	}
	
}
