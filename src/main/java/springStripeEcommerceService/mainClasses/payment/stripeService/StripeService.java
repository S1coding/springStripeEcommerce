package adidasCopy.adidasCopyBackEnd.payment.stripeService;

import adidasCopy.adidasCopyBackEnd.basket.service.BasketService;
import adidasCopy.adidasCopyBackEnd.item.ItemEntity;
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

	public String getClientSecret() throws StripeException, JsonProcessingException {
		return createPaymentIntent();
	}

	public String createPaymentIntent() throws StripeException, JsonProcessingException {
		List<ItemEntity> basketItems = getItemsFromAccountActiveBasket();
		long cost = getCostInPenniesFromBasket();
		Map<String, String> metadata = setRequiredMetadataFromItems(basketItems);
		List<Object> paymentMethod = setPaymentMethod();
		Map<String, Object> params = setParams(metadata, cost, paymentMethod);
		PaymentIntent paymentIntent = makePaymentIntentFromParams(params);
		return paymentIntent.getClientSecret();
	}

	public void handleWebhook(String payload, String sigHeader) throws SignatureVerificationException, JsonProcessingException {
		Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
		String eventType = getEventTypeFromEvent(event);
		JsonNode metadata = getMetadataFromPayload(payload);
		String email = getEmailFromMetaData(metadata);
		handlePaymentStatus(email, event);
	}

	private String getEventTypeFromEvent(Event event){
		return event.getType();
	}

	private JsonNode getMetadataFromPayload(String payload) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(payload);
		return root.path("data").path("object").path("metadata");
	}

	private String getEmailFromMetaData(JsonNode metadata){
		return Optional.ofNullable(metadata.path("userEmail").asText(null))
				.orElseThrow(() -> new NoSuchElementException("UserEmail not in metaData"));
	}


	private void handlePaymentStatus(String email, Event event){
		if(isPaymentSuccessful(event)){
			handleSuccessfulPayment(event, email);
		} else{
			if(isPaymentFailed(event)){
				handleFailedPayment(event);
			}
		}
	}

	private boolean isPaymentSuccessful(Event event){
		return event.getType().equals("payment_intent.succeeded") || event.getType().equals("charge.succeeded");

	}
	private boolean isPaymentFailed(Event event){
		return event.getType().equals("charge.failed") || event.getType().equals("payment_intent.payment_failed");
	}


	private Map<String, Object> setParams(Map<String, String> metadata, long cost, List<Object> paymentMethodTypes){ //currency is gbp by default
		Map<String, Object> params = new HashMap<>();
		params.put("metadata", metadata);
		params.put("amount", cost);
		params.put("currency", "gbp");
		params.put("payment_method_types", paymentMethodTypes);
		return params;
	}

	private PaymentIntent makePaymentIntentFromParams(Map<String, Object> params) throws StripeException {
		PaymentIntent paymentIntent = PaymentIntent.create(params);
		return paymentIntent;
	}

	private List<Object> setPaymentMethod(){
		List<Object> paymentMethodTypes = new ArrayList<>();
		paymentMethodTypes.add("card");
		return paymentMethodTypes;
	}

	private List<ItemEntity> getItemsFromAccountActiveBasket(){
		return basketService.getAllActiveBasketItems();
	}

	private String getAccountEmail(){
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private Map<String, String> setRequiredMetadataFromItems(List<ItemEntity> items){ //this method can only be used by user client in controller, as securityContext is required
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
		return basketService.calculateBasketCostInPennies();
	}


	private ResponseEntity handleSuccessfulPayment(Event event, String email) { //THIS ISN'T RUNNING!!!
		System.out.println("Payment succeeded for: " + event.getId() + " " + event.getType());
		basketService.checkOutActiveBasket(email); //this function is not deleting old basket
		return ResponseEntity.status(HttpStatus.OK).body("Payment succeeded for: " + event.getId());
	}

	private ResponseEntity handleFailedPayment(Event event) {
		System.out.println("Payment failed for: " + event.getId());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Payment failed for: " + event.getId() + " " + event.getType());
	}
}
