package springStripeEcommerceService.mainClasses.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springStripeEcommerceService.mainClasses.payment.stripeService.ClientSecretResponse;
import springStripeEcommerceService.mainClasses.payment.stripeService.StripeService;
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

@RestController
public class PaymentController {

	@Autowired
	StripeService stripeService;

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@PostMapping("/getClientSecret")
	public ResponseEntity getClientSecret() throws StripeException, JsonProcessingException {
		logger.info("Getting client secret");
		System.out.println("entered getClientSecret");
		String clientSecret = stripeService.getClientSecret();
		return ResponseEntity.status(HttpStatus.OK).body(new ClientSecretResponse(clientSecret));
	}

	@PostMapping("/stripeWebHook")
	public ResponseEntity stripeWebHook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws SignatureVerificationException, JsonProcessingException {
		logger.info("Stripe webhook being called");
		stripeService.handleWebhook(payload, sigHeader);
		return ResponseEntity.ok("webhook handled successfully");
	}
}
