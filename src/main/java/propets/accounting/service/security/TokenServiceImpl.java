package propets.accounting.service.security;

import static propets.accounting.configuration.Constants.*;

import java.net.URI;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import propets.accounting.dto.AccountCreateDto;
import propets.accounting.dto.exception.TokenValidateException;

@Service
public class TokenServiceImpl implements TokenService {

	@Value("${validation.url}")
	String validationServiceUrl;
	
	@Autowired
	RestTemplate restTemplate;
	
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
