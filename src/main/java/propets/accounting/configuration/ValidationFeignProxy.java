package propets.accounting.configuration;

import static propets.accounting.configuration.Constants.*;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "zuul-api-gateway-server")
@RibbonClient(name = "validation-service")
public interface ValidationFeignProxy {
	
	@GetMapping("validation-service/account/en/v1/token/create")
	ResponseEntity<String> createToken(@RequestHeader("Authorization") String base64token);
	
	@GetMapping("validation-service/account/en/v1/token/validation")
	public ResponseEntity<String> validateToken(@RequestHeader(TOKEN_HEADER) String token);
	
}
