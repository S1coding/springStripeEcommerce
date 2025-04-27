package springStripeEcommerceService.mainClasses.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import springStripeEcommerceService.mainClasses.account.service.AccountService;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false) //doesnt run security filters
@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AccountService accountService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test //https://junit.org/junit5/docs/current/user-guide/ for more annotations
	public void test(){

	}
}
