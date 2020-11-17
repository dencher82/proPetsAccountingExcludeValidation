package propets.accounting.service;

import java.util.Set;

import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static propets.accounting.configuration.Constants.*;
import propets.accounting.dao.AccountingRepository;
import propets.accounting.dto.AccountCreateDto;
import propets.accounting.dto.AccountDto;
import propets.accounting.dto.AccountUpdateDto;
import propets.accounting.dto.exception.AccountExistsException;
import propets.accounting.dto.exception.AccountNotFoundException;
import propets.accounting.dto.exception.BadRequestException;
import propets.accounting.dto.exception.TokenValidateException;
import propets.accounting.model.Account;
import propets.accounting.service.security.AccountingSecurity;
import propets.accounting.service.security.TokenService;

@Service
public class AccountingServiceImpl implements AccountingService {

	@Autowired
	ModelMapper mapper;

	@Autowired
	AccountingRepository repository;

	@Autowired
	AccountingSecurity securityService;
	
	@Autowired
	TokenService tokenService;

	@Value("${default.avatar}")
	private String defaultAvatar;

	@Value("${default.role}")
	private String defaultRole;

	@Override
	public ResponseEntity<AccountDto> registerUser(AccountCreateDto accountCreateDto) {
		if (repository.existsById(accountCreateDto.getEmail())) {
			System.out.println(repository.findById(accountCreateDto.getEmail()).orElse(null).getEmail());
			throw new AccountExistsException(accountCreateDto.getEmail());
		}
		passwordCheck(accountCreateDto.getPassword());
		loginCheck(accountCreateDto.getEmail());
		String hashPassword = BCrypt.hashpw(accountCreateDto.getPassword(), BCrypt.gensalt());
		Account account = new Account(accountCreateDto.getEmail(), accountCreateDto.getName());
		account.setPassword(hashPassword);
		account.setAvatar(defaultAvatar);
		account.addRole(defaultRole);
		repository.save(account);
		AccountDto accountDto = mapper.map(account, AccountDto.class);	
		String token;
		try {
			token = tokenService.createToken(accountCreateDto);
		} catch (Exception e) {
			repository.delete(account);
			e.printStackTrace();
			throw new TokenValidateException();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(TOKEN_HEADER, token);
		return new ResponseEntity<AccountDto>(accountDto, headers, HttpStatus.OK);
	}

	private void loginCheck(String email) {
		EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(email)) {
			throw new BadRequestException();
		}
	}

	private void passwordCheck(String password) {
		if (password.length() < 7) {
			throw new BadRequestException();
		} else {
			boolean upper = false;
			boolean lower = false;
			boolean number = false;
			for (char c : password.toCharArray()) {
				if (Character.isUpperCase(c)) {
					upper = true;
				} else if (Character.isLowerCase(c)) {
					lower = true;
				} else if (Character.isDigit(c)) {
					number = true;
				}
			}
			if (!upper || !lower || !number) {
				throw new BadRequestException();
			}
		}
	}

	@Override
	public AccountDto loginUser(String login) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		return mapper.map(account, AccountDto.class);
	}

	@Override
	public AccountDto getUser(String login) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		return mapper.map(account, AccountDto.class);
	}

	@Override
	public AccountDto updateUser(String login, AccountUpdateDto accountUpdateDto) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		if (accountUpdateDto.getAvatar() != null && !accountUpdateDto.getAvatar().isEmpty()) {
			account.setAvatar(accountUpdateDto.getAvatar());
		}
		if (accountUpdateDto.getName() != null && !accountUpdateDto.getName().isEmpty()) {
			account.setName(accountUpdateDto.getName());
		}
		if (accountUpdateDto.getPhone() != null && !accountUpdateDto.getPhone().isEmpty()) {
			account.setPhone(accountUpdateDto.getPhone());
		}
		repository.save(account);
		return mapper.map(account, AccountDto.class);
	}

	@Override
	public AccountDto removeUser(String login) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		repository.deleteById(login);
		return mapper.map(account, AccountDto.class);
	}

	@Override
	public Set<String> addRole(String login, String role) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		account.addRole(role);
		repository.save(account);
		return account.getRoles();
	}

	@Override
	public Set<String> deleteRole(String login, String role) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		account.removeRole(role);
		repository.save(account);
		return account.getRoles();
	}

	@Override
	public boolean blockUser(String login, String blockStatus) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		boolean res = account.blockAccount(blockStatus);
		repository.save(account);
		return res;
	}

	@Override
	public void addFavorite(String login, String postId) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		account.addFavorite(postId);
		repository.save(account);
	}

	@Override
	public void addActivity(String login, String postId) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		account.addActivity(postId);
		repository.save(account);
	}

	@Override
	public void removeFavorite(String login, String postId) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		account.removeFavorite(postId);
		repository.save(account);
	}

	@Override
	public void removeActivity(String login, String postId) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		account.removeActivity(postId);
		repository.save(account);
	}

	@Override
	public Set<String> getUserDate(String login, boolean dataType) {
		Account account = repository.findById(login).orElseThrow(() -> new AccountNotFoundException(login));
		if (dataType) {
			return account.getActivities();
		} else {
			return account.getFavorites();
		}
	}

}
