package springStripeEcommerceService.mainClasses.account.service.helpers;

import springStripeEcommerceService.mainClasses.account.Login;
import springStripeEcommerceService.mainClasses.security.jwtfilter.JwtResponse;
import springStripeEcommerceService.mainClasses.security.jwtfilter.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Component
public class AccountJwtMgr {

	@Autowired
	private AuthenticationManager authenticationManager;

	private Logger logger = LoggerFactory.getLogger(AccountJwtMgr.class);

	public JwtResponse generateJwtTokenForAccount(Login login){
		try {
			UsernamePasswordAuthenticationToken authToken = loginToAuthToken(login);
			authenticationManager.authenticate(authToken);
			String jwtToken = JwtUtil.generateToken(login.getEmail());
			logger.info("JwtResponse generated for account with email {}", login.getEmail());
			return new JwtResponse(jwtToken);
		} catch (AuthenticationException e) {
			throw new BadCredentialsException("Invalid login credentials");
		}
	}

	private UsernamePasswordAuthenticationToken loginToAuthToken(Login login){
		return new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword());
	}
}
