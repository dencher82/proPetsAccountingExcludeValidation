package propets.accounting.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
@NoArgsConstructor
public class AccountExistsException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccountExistsException(String login) {
		super("Login " + login + " already exists");
	}
	
}
