package propets.accounting.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import propets.accounting.dto.AccountCreateDto;
import propets.accounting.dto.AccountDto;
import propets.accounting.dto.AccountUpdateDto;
import propets.accounting.service.AccountingService;

import static propets.accounting.configuration.Constants.*;

@RestController
@RequestMapping("/account/en/v1")
public class AccountingController {
	
	@Autowired
	AccountingService accountingService;
	
	@PostMapping("/registration")
	public ResponseEntity<AccountDto> registerUser(@RequestBody AccountCreateDto accountCreateDto) {
		return accountingService.registerUser(accountCreateDto);		
	}
	
	@PostMapping("/login")
	public AccountDto loginUser(Principal principal) {
		return accountingService.loginUser(principal.getName());
	}
	
	@GetMapping("/{login}/info")
	public AccountDto getUser(@PathVariable String login) {
		return accountingService.getUser(login);
	}
	
	@PutMapping("/{login}")
	public AccountDto updateUser(@PathVariable String login, @RequestBody AccountUpdateDto accountUpdateDto) {
		return accountingService.updateUser(login, accountUpdateDto);
	}
	
	@DeleteMapping("/{login}")
	public AccountDto removeUser(@PathVariable String login) {
		return accountingService.removeUser(login);
	}
	
	@PutMapping("/{login}/role/{role}")
	public Iterable<String> addRole(@PathVariable String login, @PathVariable String role) {
		return accountingService.addRole(login, role);
	}
	
	@DeleteMapping("/{login}/role/{role}")
	public Iterable<String> deleteRole(@PathVariable String login, @PathVariable String role) {
		return accountingService.deleteRole(login, role);
	}
	
	@PutMapping("/{login}/block/{status}")
	public boolean blockUser(@PathVariable String login, @PathVariable String status) {
		return accountingService.blockUser(login, status);
	}
	
	@PutMapping("/{login}/favorite/{postId}")
	public void addFavorite(@PathVariable String login, @PathVariable String postId, @RequestHeader(value = SERVICE_HEADER) String serviceName) {
		accountingService.addFavorite(login, postId, serviceName);
	}
	
	@PutMapping("/{login}/activity/{postId}")
	public void addActivity(@PathVariable String login, @PathVariable String postId, @RequestHeader(value = SERVICE_HEADER) String serviceName) {
		accountingService.addActivity(login, postId, serviceName);
	}
	
	@DeleteMapping("/{login}/favorite/{postId}")
	public void removeFavorite(@PathVariable String login, @PathVariable String postId, @RequestHeader(value = SERVICE_HEADER) String serviceName) {
		accountingService.removeFavorite(login, postId, serviceName);
	}
	
	@DeleteMapping("/{login}/activity/{postId}")
	public void removeActivity(@PathVariable String login, @PathVariable String postId, @RequestHeader(value = SERVICE_HEADER) String serviceName) {
		accountingService.removeActivity(login, postId, serviceName);
	}
	
	@GetMapping("/{login}")
	public Map<String, Set<String>> getUserDate(@PathVariable String login, @RequestParam(value = "dataType") boolean dataType) {
		return accountingService.getUserDate(login, dataType);
	}
	
}
