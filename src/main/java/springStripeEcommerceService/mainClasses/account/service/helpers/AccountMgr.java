package adidasCopy.adidasCopyBackEnd.account.service.helpers;

import adidasCopy.adidasCopyBackEnd.account.AccountEntity;
import adidasCopy.adidasCopyBackEnd.account.AccountRepo;
import adidasCopy.adidasCopyBackEnd.account.Register;
import adidasCopy.adidasCopyBackEnd.basket.BasketEntity;
import adidasCopy.adidasCopyBackEnd.basket.service.helper.BasketIdGenerator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Component
public class AccountMgr {
	@Autowired
	private AccountRepo accountRepo;

	AccountIdGenerator accGenerator = new AccountIdGenerator(accountRepo);
	BasketIdGenerator basketGenerator = new BasketIdGenerator();

	public AccountEntity createAccountFromRegister(Register register) {
		String uuid = accGenerator.generateAccountUniqueId();
		return AccountEntity.builder()
				.uniqueId(uuid)
				.username(register.getUsername())
				.email(register.getEmail())
				.firstName(register.getFirstName())
				.lastName(register.getLastName())
				.password(register.getPassword())
				.authority("CUSTOMER")
				.authenticated(false)
				.build();
	}

	public BasketEntity createEmptyActiveBasketForAccount(AccountEntity account){
		String uuid = basketGenerator.generateUniqueBasketId();
		return BasketEntity.builder()
				.email(account.getEmail())
				.uniqueId(uuid)
				.dateBought(null)
				.dateMade(LocalDateTime.now())
				.active(true)
				.build();
	}

}
