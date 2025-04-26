package adidasCopy.adidasCopyBackEnd.account;

import adidasCopy.adidasCopyBackEnd.account.service.AccountService;
import adidasCopy.adidasCopyBackEnd.basket.service.BasketService;
import adidasCopy.adidasCopyBackEnd.security.jwtfilter.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AccountController {

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private AccountService accountService;

	@Autowired
	private BasketService basketService;


	@PostMapping("/login")
	public ResponseEntity login(@RequestBody Login login){
		//add @RestControllerAdvice exception handling here
		JwtResponse jwtResponse = accountService.jwtResponseFromLogin(login);
		return ResponseEntity.status(HttpStatus.OK).body(jwtResponse);
	}

	@PostMapping("/register")
	public ResponseEntity register(@RequestBody Register register){
		//add @RestControllerAdvice exception handling here
		accountService.registerCustomer(register);
		return ResponseEntity.status(HttpStatus.OK).body(register.getEmail() + " registered");
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/testAdminEndpoint")
	public ResponseEntity testAdminEndpoint(){
		return ResponseEntity.status(HttpStatus.OK).body("has authority ADMIN");
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/testOwnerEndpoint")
	public ResponseEntity testOwnerEndpoint(){
		return ResponseEntity.status(HttpStatus.OK).body("has authority OWNER");
	}
}
