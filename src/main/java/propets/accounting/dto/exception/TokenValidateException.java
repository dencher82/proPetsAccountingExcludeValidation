package propets.accounting.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class TokenValidateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
