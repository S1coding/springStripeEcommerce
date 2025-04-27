package springStripeEcommerceService.mainClasses.security.accountconfiguration;

import springStripeEcommerceService.mainClasses.account.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserDetailsMapper implements UserDetails {

	private Logger logger = LoggerFactory.getLogger(UserDetailsMapper.class);
	private AccountEntity accountEntity;

	public UserDetailsMapper(AccountEntity accountEntity){
		this.accountEntity = accountEntity;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//System.out.println("user details entered");
		Set<SimpleGrantedAuthority> roles = new HashSet<>();
		roles.add(new SimpleGrantedAuthority(accountEntity.getAuthority()));
		logger.info("roles returned for {}",accountEntity.getEmail());
		return roles;
	}

	@Override
	public String getPassword() {
		return accountEntity.getPassword();
	}

	@Override
	public String getUsername() {
		return accountEntity.getEmail();
	}

}
