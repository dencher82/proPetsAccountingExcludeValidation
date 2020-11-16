package propets.accounting.service;

import java.util.Set;

import org.springframework.http.ResponseEntity;

import propets.accounting.dto.AccountCreateDto;
import propets.accounting.dto.AccountDto;
import propets.accounting.dto.AccountUpdateDto;

public interface AccountingService {

	ResponseEntity<AccountDto> registerUser(AccountCreateDto accountCreateDto);
	
	AccountDto loginUser(String login);
	
	AccountDto getUser(String login);
	
	AccountDto updateUser(String login, AccountUpdateDto accountUpdateDto);
	
	AccountDto removeUser(String login);
	
	Set<String> addRole(String login, String role);
	
	Set<String> deleteRole(String login, String role);
	
	boolean blockUser(String login, String blockStatus);
	
	void addFavorite(String login, String postId);
	
	void addActivity(String login, String postId);
	
	void removeFavorite(String login, String postId);
	
	void removeActivity(String login, String postId);
	
	Set<String> getUserDate(String login, boolean dataType);
	
	void tokenValidation(String token);
	
}
