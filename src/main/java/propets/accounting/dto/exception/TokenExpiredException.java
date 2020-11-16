package propets.accounting.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class TokenExpiredException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5954226015742319128L;

}
