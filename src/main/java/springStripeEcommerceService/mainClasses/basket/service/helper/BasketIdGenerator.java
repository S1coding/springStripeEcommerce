package springStripeEcommerceService.mainClasses.basket.service.helper;

import springStripeEcommerceService.mainClasses.basket.BasketRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BasketIdGenerator {

	@Autowired
	BasketRepo basketRepo;

	private final static Logger logger = LoggerFactory.getLogger(BasketIdGenerator.class);
	public String generateUniqueBasketId(){
		String uuid = UUID.randomUUID().toString();
		while(basketRepo.findByUniqueId(uuid).isPresent()){
			uuid = UUID.randomUUID().toString();
		}

		logger.info("generated unique basketId {}", uuid);
		return uuid;
	}
}
