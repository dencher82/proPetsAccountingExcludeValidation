package propets.accounting.service.security;

import propets.accounting.dto.AccountCreateDto;

public interface TokenService {
	
	String createToken(AccountCreateDto accountCreateDto);
	
}
