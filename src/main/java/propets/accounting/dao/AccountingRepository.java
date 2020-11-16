package propets.accounting.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import propets.accounting.model.Account;

@Component
public interface AccountingRepository extends MongoRepository<Account, String>{

}
