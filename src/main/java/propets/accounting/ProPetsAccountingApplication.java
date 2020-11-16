package propets.accounting;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import propets.accounting.dao.AccountingRepository;
import propets.accounting.model.Account;

@SpringBootApplication
public class ProPetsAccountingApplication implements CommandLineRunner {
	
	@Value("${default.avatar}")
	private String defaultAvatar;
	
	@Autowired
	AccountingRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ProPetsAccountingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (!repository.existsById("admin")) {
			String hashPassword = BCrypt.hashpw("admin", BCrypt.gensalt());
			Account admin = new Account("admin", "admin");
			admin.setAvatar(defaultAvatar);
			admin.setPassword(hashPassword);
			admin.addRole("ADMIN");
			admin.addRole("MODERATOR");
			admin.addRole("USER");
			repository.save(admin);
		}
		
	}

}
