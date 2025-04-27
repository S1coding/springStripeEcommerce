package springStripeEcommerceService.mainClasses.account;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class Register {
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
}
