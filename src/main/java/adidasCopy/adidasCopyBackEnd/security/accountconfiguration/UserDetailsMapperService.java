package adidasCopy.adidasCopyBackEnd.security.accountconfiguration;

import adidasCopy.adidasCopyBackEnd.account.AccountEntity;
import adidasCopy.adidasCopyBackEnd.account.AccountRepo;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsMapperService implements UserDetailsService {
	@Autowired
	AccountRepo accountRepo;

	private Logger logger = LoggerFactory.getLogger(UserDetailsMapper.class);

	@Override
	public UserDetailsMapper loadUserByUsername(String email){
		AccountEntity accountEntity = accountRepo.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(email));
		logger.info("account retrieved from database for {}",email);
		return new UserDetailsMapper(accountEntity);
	}
}
