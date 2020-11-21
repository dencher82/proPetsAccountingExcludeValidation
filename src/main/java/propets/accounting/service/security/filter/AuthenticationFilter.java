package propets.accounting.service.security.filter;

import java.io.IOException;
import java.net.URI;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static propets.accounting.configuration.Constants.*;

import propets.accounting.dao.AccountingRepository;
import propets.accounting.dto.exception.AccountNotFoundException;
import propets.accounting.dto.exception.TokenExpiredException;
import propets.accounting.service.security.AccountingSecurity;

@Service
@Order(10)
public class AuthenticationFilter implements Filter {
	
	@Value("${validation.url}")
	String validationServiceUrl;

	@Autowired
	AccountingSecurity securityService;

	@Autowired
	AccountingRepository repository;
	
	@Autowired
	RestTemplate restTemplate;

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
					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", base64token);
					try {
						RequestEntity<String> requestEntity = new RequestEntity<String>(headers, HttpMethod.GET, new URI(validationServiceUrl + "/token/create"));
						ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
						request = new WrapperRequest(request, securityService.getLogin(base64token));
						response.setHeader(TOKEN_HEADER, responseEntity.getHeaders().getFirst(TOKEN_HEADER));
					} catch (Exception e) {
						e.printStackTrace();
						response.sendError(401);
						return;
					}					
				} else {
					String xToken = request.getHeader(TOKEN_HEADER);
					HttpHeaders headers = new HttpHeaders();
					headers.add(TOKEN_HEADER, xToken);
					try {
						RequestEntity<String> requestEntity = new RequestEntity<String>(headers, HttpMethod.GET, new URI(validationServiceUrl + "/token/validation"));
						ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
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
