package propets.accounting.service.security;

import propets.accounting.dto.AccountCreateDto;

public interface AccountingSecurity {

	String getLogin(String token);
	
	String createToken(AccountCreateDto accountCreateDto);
	
}
