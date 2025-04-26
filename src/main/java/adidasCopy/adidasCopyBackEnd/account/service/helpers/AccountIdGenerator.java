package adidasCopy.adidasCopyBackEnd.account.service.helpers;

import adidasCopy.adidasCopyBackEnd.account.AccountRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Component
public class AccountIdGenerator {
	@Autowired
	private AccountRepo accountRepo;

	public String generateAccountUniqueId(){
		String uuid = UUID.randomUUID().toString();
		while(accountRepo.findById(uuid).isPresent()){
			uuid = UUID.randomUUID().toString();
		}

		return uuid;
	}
}
