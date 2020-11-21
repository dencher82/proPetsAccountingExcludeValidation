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
		String[] credentials = tokenDecode(token);
		Account account = repository.findById(credentials[0])
				.orElseThrow(() -> new AccountNotFoundException(credentials[0]));
		if (!BCrypt.checkpw(credentials[1], account.getPassword())) {
			throw new UnauthorizedException();
		}
		return account.getEmail();
	}
	
	private String[] tokenDecode(String token) {
		try {
			String[] credentials = token.split(" ");
			String credential = new String(Base64.getDecoder().decode(credentials[1]));
			return credential.split(":");
		} catch (Exception e) {
			throw new TokenValidateException();
		}
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

	
}
