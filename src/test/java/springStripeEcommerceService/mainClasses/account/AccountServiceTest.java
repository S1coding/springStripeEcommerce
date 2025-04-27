package springStripeEcommerceService.mainClasses.account;


import springStripeEcommerceService.mainClasses.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

	@Mock
	private AccountRepo accountRepo;

	@InjectMocks
	private AccountService accountService;

	@Test
	public void AccountService_CreateAccount_ReturnsAccount(){

	}

	//public JwtResponse jwtResponseFromLogin(Login login){

	//public void registerCustomer(Register register){
}
