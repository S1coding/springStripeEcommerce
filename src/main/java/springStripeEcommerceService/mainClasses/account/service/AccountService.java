package adidasCopy.adidasCopyBackEnd.account.service;

import adidasCopy.adidasCopyBackEnd.account.AccountEntity;
import adidasCopy.adidasCopyBackEnd.account.AccountRepo;
import adidasCopy.adidasCopyBackEnd.account.Login;
import adidasCopy.adidasCopyBackEnd.account.Register;
import adidasCopy.adidasCopyBackEnd.account.service.helpers.AccountMgr;
import adidasCopy.adidasCopyBackEnd.account.service.helpers.AccountIdGenerator;
import adidasCopy.adidasCopyBackEnd.account.service.helpers.AccountJwtMgr;
import adidasCopy.adidasCopyBackEnd.basket.BasketEntity;
import adidasCopy.adidasCopyBackEnd.basket.BasketRepo;
import adidasCopy.adidasCopyBackEnd.security.jwtfilter.JwtResponse;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private BasketRepo basketRepo;
	
	@Autowired
	private AccountIdGenerator accGen;
	
	@Autowired
	private AccountMgr accMgr;
	
	@Autowired
	private AccountJwtMgr jwtMgr;

	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

	public JwtResponse jwtResponseFromLogin(Login login){
		JwtResponse token = jwtMgr.generateJwtTokenForAccount(login);
		logger.info("generated jwt token {}", token);
		return token;
	}

	public void registerCustomer(Register register){
		throwExceptionIfAccountExistsByEmail(register.getEmail());
		Register encodedRegister = encodeRegisterPassword(register);
		AccountEntity account = accMgr.createAccountFromRegister(encodedRegister);
		BasketEntity basket = accMgr.createEmptyActiveBasketForAccount(account);
		accountRepo.save(account);
		basketRepo.save(basket);
		logger.info("Account with email {} registered with new active basket {}", account.getEmail(), basket.getUniqueId());
	}

	private Register encodeRegisterPassword(Register register){
		register.setPassword(passwordEncoder.encode(register.getPassword()));
		return  register;
	}

	private void throwExceptionIfAccountExistsByEmail(String email){
		accountRepo.findByEmail(email)
				.ifPresent(e -> {throw new EntityExistsException("Account of email "+email+" already exists");
				});
	}


}
