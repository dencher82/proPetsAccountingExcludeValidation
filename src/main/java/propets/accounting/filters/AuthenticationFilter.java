package propets.accounting.filters;

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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static propets.accounting.configuration.Constants.*;

import propets.accounting.configuration.ValidationFeignProxy;
import propets.accounting.dao.AccountingRepository;
import propets.accounting.dto.exception.AccountNotFoundException;
import propets.accounting.dto.exception.TokenExpiredException;
import propets.accounting.security.AccountingSecurity;

@RefreshScope
@Service
@Order(10)
public class AuthenticationFilter implements Filter {
	
	@Autowired
	ValidationFeignProxy feignProxy;

	@Autowired
	AccountingSecurity securityService;

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
					String base64token = request.getHeader("Authorization");
					try {
						ResponseEntity<String> responseEntity = feignProxy.createToken(base64token);
						request = new WrapperRequest(request, securityService.getLogin(base64token));
						response.setHeader(TOKEN_HEADER, responseEntity.getHeaders().getFirst(TOKEN_HEADER));
					} catch (Exception e) {
						e.printStackTrace();
						response.sendError(401);
						return;
					}					
				} else {
					String xToken = request.getHeader(TOKEN_HEADER);
					try {
						ResponseEntity<String> responseEntity = feignProxy.validateToken(xToken);
						request = new WrapperRequest(request, responseEntity.getHeaders().getFirst(LOGIN_HEADER));
						response.setHeader(TOKEN_HEADER, responseEntity.getHeaders().getFirst(TOKEN_HEADER));
					} catch (Exception e) {
						e.printStackTrace();
						response.sendError(401);
						return;
					}
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
