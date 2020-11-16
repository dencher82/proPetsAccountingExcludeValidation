package propets.accounting.service.security;

import propets.accounting.model.Account;

public interface TokenService {
	
	String createToken(Account account);
	
	String tokenValidation(String token);

	String getLogin(String token);
	
}
