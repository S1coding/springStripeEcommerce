package springStripeEcommerceService.mainClasses.payment.stripeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springStripeEcommerceService.mainClasses.basket.service.BasketService;
import springStripeEcommerceService.mainClasses.item.ItemEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StripeService {

	@Value("${stripe.api.secret.key.test}")
	private String stripeApiKey;

	@Value("${stripe.cli.webhook.secret}")
	private String webhookSecret;

	@Autowired
	BasketService basketService;

	@PostConstruct
	public void init() {
		Stripe.apiKey = stripeApiKey;
	}

	private final static Logger logger = LoggerFactory.getLogger(StripeService.class);

	public String getClientSecret() throws StripeException, JsonProcessingException {
		logger.info("Getting client secret");
		return createPaymentIntent();
	}

	public String createPaymentIntent() throws StripeException, JsonProcessingException {
		logger.info("Creating payment Intent");
		List<ItemEntity> basketItems = getItemsFromAccountActiveBasket();
		long cost = getCostInPenniesFromBasket();
		Map<String, String> metadata = setRequiredMetadataFromItems(basketItems);
		List<Object> paymentMethod = setPaymentMethod();
		Map<String, Object> params = setParams(metadata, cost, paymentMethod);
		PaymentIntent paymentIntent = makePaymentIntentFromParams(params);
		return paymentIntent.getClientSecret();
	}

	public void handleWebhook(String payload, String sigHeader) throws SignatureVerificationException, JsonProcessingException {
		logger.info("Handling webhook");
		Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
		String eventType = getEventTypeFromEvent(event);
		JsonNode metadata = getMetadataFromPayload(payload);
		String email = getEmailFromMetaData(metadata);
		handlePaymentStatus(email, event);
	}

	private String getEventTypeFromEvent(Event event){
		logger.info("Getting event type");
		return event.getType();
	}

	private JsonNode getMetadataFromPayload(String payload) throws JsonProcessingException {
		logger.info("Getting metadata from payload");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(payload);
		return root.path("data").path("object").path("metadata");
	}

	private String getEmailFromMetaData(JsonNode metadata){
		logger.info("Getting email from metadata");
		return Optional.ofNullable(metadata.path("userEmail").asText(null))
				.orElseThrow(() -> new NoSuchElementException("UserEmail not in metaData"));
	}


	private void handlePaymentStatus(String email, Event event){
		logger.info("Checking payment status");
		if(isPaymentIntentSuccessful(event)){
			handleSuccessfulPayment(event, email);
		} else{
			if(isPaymentIntentFailed(event)){
				handleFailedPayment(event);
			}
		}
	}

	private boolean isPaymentIntentSuccessful(Event event){ //this is causing an issue you
		logger.info("Payment status {}", event.getType());
		return event.getType().equals("payment_intent.succeeded");

	}
	private boolean isPaymentIntentFailed(Event event){
		logger.info("Payment status {}", event.getType());
		return event.getType().equals("payment_intent.payment_failed");
	}

	private ResponseEntity handleSuccessfulPayment(Event event, String email) { //THIS ISN'T RUNNING!!!
		//System.out.println("Payment succeeded for: " + event.getId() + " " + event.getType());
		logger.info("Handling successful payment for {}", email);
		basketService.checkOutActiveBasket(email); //this function is not deleting old basket
		return ResponseEntity.status(HttpStatus.OK).body("Payment succeeded for: " + event.getId());
	}

	private ResponseEntity handleFailedPayment(Event event) {
		logger.info("Handling failed payment");
		System.out.println("Payment failed for: " + event.getId());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Payment failed for: " + event.getId() + " " + event.getType());
	}


	private Map<String, Object> setParams(Map<String, String> metadata, long cost, List<Object> paymentMethodTypes){ //currency is gbp by default
		logger.info("Setting params");
		Map<String, Object> params = new HashMap<>();
		params.put("metadata", metadata);
		params.put("amount", cost);
		params.put("currency", "gbp");
		params.put("payment_method_types", paymentMethodTypes);
		return params;
	}

	private PaymentIntent makePaymentIntentFromParams(Map<String, Object> params) throws StripeException {
		logger.info("Making payment intent from params");
		PaymentIntent paymentIntent = PaymentIntent.create(params);
		return paymentIntent;
	}

	private List<Object> setPaymentMethod(){
		logger.info("Setting payment method");
		List<Object> paymentMethodTypes = new ArrayList<>();
		paymentMethodTypes.add("card");
		return paymentMethodTypes;
	}

	private List<ItemEntity> getItemsFromAccountActiveBasket(){
		logger.info("Getting items from account active basket");
		return basketService.getAllActiveBasketItems();
	}

	private String getAccountEmail(){
		logger.info("Getting account email from security context");
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private Map<String, String> setRequiredMetadataFromItems(List<ItemEntity> items){ //this method can only be used by user client in controller, as securityContext is required
		logger.info("Setting required meta data from items");
		Map<String, String> metadata = new HashMap<>();
		String email = getAccountEmail();
		metadata.put("userEmail", email);
		int i = 1;
		for (ItemEntity item : items) {
			metadata.put("item_" + i + "_id_", item.getUniqueId());
			metadata.put("item_" + i + "_text", item.getText());
			i++;
		}
		return metadata;
	}

	private long getCostInPenniesFromBasket(){
		logger.info("Getting cost in pennies: {}", basketService.calculateBasketCostInPennies());
		return basketService.calculateBasketCostInPennies();
	}

}
