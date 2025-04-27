package springStripeEcommerceService.mainClasses.basket.service.helper;

import springStripeEcommerceService.mainClasses.basket.BasketEntity;
import springStripeEcommerceService.mainClasses.basket.BasketRepo;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class BasketMgr {

	@Autowired
	private BasketRepo basketRepo;

	@Autowired
	BasketIdGenerator basketIdGenerator;

	private final static Logger logger = LoggerFactory.getLogger(BasketMgr.class);

	//testing, all children are testing, just have to setup test for overall function, will be long
	public BasketEntity ensureOneActiveBasketByEmail(String email) {
		logger.info("Ensuring there is one active basket for {}", email);
		if (noActiveBaskets(email)) {
			logger.warn("There are no active baskets, making sure there is one active basket");
			return makeActiveBasketForEmail(email);
		}

		if (manyActiveBaskets(email)) { //
			logger.warn("There are many active baskets, making sure there is only one active basket");
			setAllActiveBasketsByEmailToFalse(email);
			return makeActiveBasketForEmail(email);
		}
		logger.info("there was only one active basket {} with active status: {} for {}", basketRepo.findFirstByEmailAndActiveTrue(email).get().getUniqueId(), basketRepo.findFirstByEmailAndActiveTrue(email).get().isActive(), email);
		return basketRepo.findFirstByEmailAndActiveTrue(email)
				.orElseThrow(() -> new NoSuchElementException("Logical error: Expected exactly one active basket, but none or multiple found."));
	}

	public BasketEntity checkOutActiveBasket(String email){
		logger.info("checking out for email {}", email);
		BasketEntity basket = basketRepo.findFirstByEmailAndActiveTrue(email)
				.orElseThrow(()-> new NoSuchElementException("User has no active baskets"));
		logger.info("received basket with id {}", basket.getUniqueId());
		basket.setActive(false);
		basket.setDateBought(LocalDateTime.now());
		basketRepo.save(basket);
		ensureOneActiveBasketByEmail(email); //added later to make sure checkout gives new basket after user checks out basket
		return basket;

	}

	//tested
	public BasketEntity makeActiveBasketForEmail(String email){
		BasketEntity basket = new BasketEntity("id", email, LocalDateTime.now(), null, true);
		basket.setUniqueId(basketIdGenerator.generateUniqueBasketId());
		return basket;
	}

	//tested
	public boolean manyActiveBaskets(String email){ //assumes there will be more than one
		boolean isEmpty = noActiveBaskets(email);
		if(isEmpty){
			logger.warn("No active baskets for email found, when checking if there are many active baskets for email");
			throw new NoSuchElementException("No active baskets for email found when checking if there are many active baskets for email");
		}
		return basketRepo.findAllByEmailAndActiveTrue(email).size()>1;
	}

	//tested
	public List<BasketEntity> setAllActiveBasketsByEmailToFalse(String email){
		List<BasketEntity> baskets = basketRepo.findAllByEmailAndActiveTrue(email);
		for(BasketEntity basket: baskets){
			basket.setActive(false);
		}
		saveManyBaskets(baskets);
		return baskets;
	}

	//tested
	public List<BasketEntity> saveManyBaskets(List<BasketEntity> baskets){
		for(BasketEntity basket: baskets){
			basketRepo.save(basket);
		}
		return basketRepo.findAll();
	}

	//tested <--- error?
	public boolean noActiveBaskets(String email){
		return basketRepo.findFirstByEmailAndActiveTrue(email).isEmpty();
	}


	public BasketEntity getActiveBasketByEmail(String email) {
		return basketRepo.findFirstByEmailAndActiveTrue(email)
				.orElseThrow(() -> new EntityNotFoundException("More than one active basket, or no active basket, found for account with email " + email));
	}

}
