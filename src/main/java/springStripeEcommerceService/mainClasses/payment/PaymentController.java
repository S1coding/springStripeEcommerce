package adidasCopy.adidasCopyBackEnd.payment;

import adidasCopy.adidasCopyBackEnd.payment.stripeService.ClientSecretResponse;
import adidasCopy.adidasCopyBackEnd.payment.stripeService.StripeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentController {

	@Autowired
	StripeService stripeService;

	@PostMapping("/getClientSecret")
	public ResponseEntity getClientSecret() throws StripeException, JsonProcessingException {
		System.out.println("entered getClientSecret");
		String clientSecret = stripeService.getClientSecret();
		return ResponseEntity.status(HttpStatus.OK).body(new ClientSecretResponse(clientSecret));
	}

	@PostMapping("/stripeWebHook")
	public ResponseEntity stripeWebHook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws SignatureVerificationException, JsonProcessingException {
		stripeService.handleWebhook(payload, sigHeader);
		return ResponseEntity.ok("webhook handled successfully");
	}
}
