package adidasCopy.adidasCopyBackEnd.account;


import com.stripe.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AccountRepoTest {

	@Autowired
	private AccountRepo embeddedAccountRepo;

	private AccountEntity basicAccount = AccountEntity.builder()
			.email("email")
			.firstName("firstName")
			.lastName("lastname")
			.username("username")
			.authority("CUSTOMER")
			.authenticated(false)
			.uniqueId("someUniqueId")
			.password("password")
			.build();

	private AccountEntity basicAccount2 = AccountEntity.builder()
			.email("email2")
			.firstName("firstName2")
			.lastName("lastname2")
			.username("username2")
			.authority("CUSTOMER")
			.authenticated(false)
			.uniqueId("someUniqueId2")
			.password("password2")
			.build();

	@Test
	public void EmbeddedAccountRepo_Save_ReturnsSavedAccount(){
		//added here to use primitives directly instead of calling them
		AccountEntity basicAccount = AccountEntity.builder()
				.email("email")
				.firstName("firstName")
				.lastName("lastname")
				.username("username")
				.authority("CUSTOMER")
				.authenticated(false)
				.uniqueId("someUniqueId")
				.password("password")
				.build();
		//Act
		AccountEntity savedAccount = embeddedAccountRepo.save(basicAccount);

		//Assert
		Assertions.assertNotEquals(null, savedAccount);
		Assertions.assertEquals("email", savedAccount.getEmail());
	}

	@Test
	public void EmbeddedAccountRepo_GetAll_ReturnsSavedAccounts() {
		//adding 2 account entities here directly would make this test function too annoying to read,
		//but might be good practice to do so anyway to ensure test is isolated,
		//but in this case the object type matters, not whether its modified

		AccountEntity accountToSave1 = basicAccount;
		AccountEntity accountToSave2 = basicAccount2;
		embeddedAccountRepo.save(accountToSave1);
		embeddedAccountRepo.save(accountToSave2);
		List<AccountEntity> accounts = embeddedAccountRepo.findAll();
		Assertions.assertNotEquals(null, accounts);
		Assertions.assertEquals(2, accounts.size());
	}

	@Test
	public void EmbeddedAccountRepo_FindByUniqueId_ReturnsAccountOfId(){
		//added here to use primitives directly instead of calling them
		AccountEntity basicAccount = AccountEntity.builder()
				.email("email")
				.firstName("firstName")
				.lastName("lastname")
				.username("username")
				.authority("CUSTOMER")
				.authenticated(false)
				.uniqueId("someUniqueId")
				.password("password")
				.build();

		embeddedAccountRepo.save(basicAccount);
		AccountEntity savedAccount = embeddedAccountRepo.findByUniqueId("someUniqueId").get();
		Assertions.assertNotEquals(null, savedAccount);
		Assertions.assertEquals("someUniqueId", savedAccount.getUniqueId());
	}

	@Test
	public void EmbeddedAccountRepo_FindByEmail_ReturnsAccountOfEmail(){
		AccountEntity basicAccount = AccountEntity.builder()
				.email("email")
				.firstName("firstName")
				.lastName("lastname")
				.username("username")
				.authority("CUSTOMER")
				.authenticated(false)
				.uniqueId("someUniqueId")
				.password("password")
				.build();
		embeddedAccountRepo.save(basicAccount);
		AccountEntity savedAccount = embeddedAccountRepo.findByEmail("email").get();

		Assertions.assertNotEquals(null, savedAccount);
		Assertions.assertEquals("email", savedAccount.getEmail());
	}

	@Test
	public void EmbeddedAccountRepo_SaveAccountWithId_UpdatesAccountWithId(){
		//saves basic account
		AccountEntity basicAccount = AccountEntity.builder()
				.email("email")
				.firstName("firstName")
				.lastName("lastname")
				.username("username")
				.authority("CUSTOMER")
				.authenticated(false)
				.uniqueId("someUniqueId")
				.password("password")
				.build();
		AccountEntity savedAccount = embeddedAccountRepo.save(basicAccount);

		Assertions.assertNotEquals(null, savedAccount);
		Assertions.assertEquals("email", savedAccount.getEmail());


		//sets basic account email to a new one
		basicAccount.setEmail("newEmail");
		AccountEntity updatedAccount = embeddedAccountRepo.save(basicAccount);

		Assertions.assertNotEquals(null, updatedAccount);
		Assertions.assertEquals("newEmail", updatedAccount.getEmail());
	}

	@Test
	public void EmbeddedAccountRepo_DeleteAccountWithUniqueId_DeletesAccountWithUniqueId(){

		//save account first
		AccountEntity basicAccount = AccountEntity.builder()
				.email("email")
				.firstName("firstName")
				.lastName("lastname")
				.username("username")
				.authority("CUSTOMER")
				.authenticated(false)
				.uniqueId("someUniqueId")
				.password("password")
				.build();
		AccountEntity savedAccount = embeddedAccountRepo.save(basicAccount);

		Assertions.assertNotEquals(null, basicAccount);


		//delete account
		embeddedAccountRepo.deleteById("someUniqueId");
		Optional<AccountEntity> account = embeddedAccountRepo.findByUniqueId("someUniqueId");

		Assertions.assertEquals(true, account.isEmpty());
	}
}
