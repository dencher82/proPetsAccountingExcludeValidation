package propets.accounting.service.security;

import static propets.accounting.configuration.Constants.*;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import propets.accounting.dto.exception.TokenExpiredException;
import propets.accounting.dto.exception.TokenValidateException;
import propets.accounting.model.Account;

@Service
public class TokenServiceImpl implements TokenService {
	
	@Value("${secret.value}")
	private String secret;
	
	@Value("${validation.url}")
	String validationServiceUrl;
	
	@Autowired
	private SecretKey secretKey;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Override
	public String createToken(Account account) {
		String base64token = createBase64token(account.getEmail(), account.getPassword());
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

	@Override
	public String tokenValidation(String token) {
		Jws<Claims> jws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
		Claims claims = jws.getBody();
		Instant time = Instant.ofEpochMilli(Long.parseLong(claims.get("timestamp").toString()));
		if (time.isBefore(Instant.now())) {
			throw new TokenExpiredException();
		}
		claims.put("timestamp", Instant.now().plus(TOKEN_PERIOD_DAYS, ChronoUnit.DAYS).toEpochMilli());
		token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, secretKey).compact();
		return token;
	}
	
	@Override
	public String getLogin(String token) {
		Jws<Claims> jws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
		Claims claims = jws.getBody();
		return claims.get("login", String.class);
	}
	
	@Bean
	public SecretKey secretKey() {
		return new SecretKeySpec(Base64.getUrlEncoder().encode(secret.getBytes()), "AES");
	}

}
