package springStripeEcommerceService.mainClasses.security;

import springStripeEcommerceService.mainClasses.account.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {

	@Autowired
	AccountRepo accountRepo;
	public static String getCurrentUserEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
