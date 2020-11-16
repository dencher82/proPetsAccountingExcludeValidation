package propets.accounting.service.security;

import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import propets.accounting.dao.AccountingRepository;
import propets.accounting.dto.AccountLoginDto;
import propets.accounting.dto.exception.AccountNotFoundException;
import propets.accounting.dto.exception.TokenValidateException;
import propets.accounting.dto.exception.UnauthorizedException;
import propets.accounting.model.Account;

@Service
public class AccountingSecurityImpl implements AccountingSecurity {
	
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
