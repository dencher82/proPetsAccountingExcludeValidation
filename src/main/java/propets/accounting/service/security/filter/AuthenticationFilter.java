package propets.accounting.service.security.filter;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import static propets.accounting.configuration.Constants.*;

import propets.accounting.dao.AccountingRepository;
import propets.accounting.dto.exception.AccountNotFoundException;
import propets.accounting.dto.exception.TokenExpiredException;
import propets.accounting.model.Account;
import propets.accounting.service.security.AccountingSecurity;
import propets.accounting.service.security.TokenService;

@Service
@Order(10)
public class AuthenticationFilter implements Filter {

	@Autowired
	AccountingSecurity securityService;

	@Autowired
	TokenService tokenService;

	@Autowired
	AccountingRepository repository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		if (!"/account/en/v1/registration".equals(path)) {
			try {
				if ("/account/en/v1/login".equals(path)) {
					String token = request.getHeader("Authorization");
					String login = securityService.getLogin(token);
					Account account = repository.findById(login).orElse(null);
					request = new WrapperRequest(request, login);
					response.setHeader(TOKEN_HEADER, tokenService.createToken(account));
				} else {
					String xToken = request.getHeader(TOKEN_HEADER);
					String login = tokenService.getLogin(xToken);
					request = new WrapperRequest(request, login);
					response.setHeader(TOKEN_HEADER, tokenService.tokenValidation(xToken));
				}
			} catch (AccountNotFoundException e) {
				e.printStackTrace();
				response.sendError(404, "Account not found");
				return;
			} catch (TokenExpiredException e) {
				e.printStackTrace();
				response.sendError(400, "Token expired");
				return;
			} catch (Exception e) {
				e.printStackTrace();
				response.sendError(401);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private class WrapperRequest extends HttpServletRequestWrapper {
		String user;

		public WrapperRequest(HttpServletRequest request, String user) {
			super(request);
			this.user = user;
		}

		@Override
		public Principal getUserPrincipal() {
			return new Principal() {

				@Override
				public String getName() {
					return user;
				}
			};
		}
	}

}
