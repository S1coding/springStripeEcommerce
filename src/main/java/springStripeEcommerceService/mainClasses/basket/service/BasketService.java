package springStripeEcommerceService.mainClasses.basket.service;

import springStripeEcommerceService.mainClasses.account.AccountRepo;
import springStripeEcommerceService.mainClasses.basket.BasketEntity;
import springStripeEcommerceService.mainClasses.basket.BasketRepo;
import springStripeEcommerceService.mainClasses.basket.OrderEntity;
import springStripeEcommerceService.mainClasses.basket.OrderRepo;
import springStripeEcommerceService.mainClasses.basket.service.helper.BasketMgr;
import springStripeEcommerceService.mainClasses.item.ItemEntity;
import springStripeEcommerceService.mainClasses.item.ItemRepo;
import springStripeEcommerceService.mainClasses.item.service.helper.ItemMgr;
import springStripeEcommerceService.mainClasses.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BasketService {

	@Autowired
	private BasketRepo basketRepo;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	private ItemMgr itemMgr;

	@Autowired
	private BasketMgr basketMgr;

	private static final Logger logger = LoggerFactory.getLogger(BasketService.class);

	public void deleteItemFromActiveBasketByItemId(String itemId){

		String email = getEmailFromSecurityContext();
		BasketEntity basket = basketMgr.getActiveBasketByEmail(email);
		ItemEntity item = getItemById(itemId);
		OrderEntity orderEntityToDelete = getOrderByBasketAndItem(basket, item).get(0);

		orderRepo.delete(orderEntityToDelete);
		logger.info("Deleting item {} from basket for user with email {}", itemId, email);
	}

	public OrderEntity saveNewOrderWithItemByText(String text){
		String email = getEmailFromSecurityContext();
		ItemEntity item = itemMgr.getItemByText(text);
		BasketEntity activeBasket = basketMgr.ensureOneActiveBasketByEmail(email);
		basketRepo.save(activeBasket);
		logger.info("basket {} saved for user {}", activeBasket, text);
		OrderEntity order = new OrderEntity(null, activeBasket.getUniqueId(), item.getUniqueId(), 1, LocalDateTime.now());
		order.setUniqueId(generateUniqueOrderForBasketEntityId());

		orderRepo.save(order);
		logger.info("Order {} added to basket {}", order.getUniqueId(), activeBasket.getUniqueId());
		return order;
	}


	public Long calculateBasketCostInPennies(){

		String email = getEmailFromSecurityContext();

		BasketEntity basket = basketMgr.getActiveBasketByEmail(email);
		List<OrderEntity> orderEntities = getOrdersForBasketByBasket(basket);

		long cost = 0L;
		for(OrderEntity orderEntity : orderEntities){
			ItemEntity item = getItemById(orderEntity.getItemId());
			cost += Long.valueOf(item.getCost());
		}

		cost *= 100L;
		logger.info("Email {}'s active basket {} has a cost of {} pennies", email, basket.getUniqueId(), cost);
		// assuming item cost is stored in a currency divisible by a hundred
		return cost;
	}

	public List<ItemEntity> getAllActiveBasketItems() {

		String email = getEmailFromSecurityContext();
		BasketEntity activeBasket = basketMgr.ensureOneActiveBasketByEmail(email);
		basketRepo.save(activeBasket);
		List<OrderEntity> orderEntities = getOrdersForBasketByBasket(activeBasket);

		logger.info("Orders from {}'s active basket retrieved most likely because auto redirected to basket", email);
		return orderEntities.stream()
				.map(this::getItemsByOrder)
				.toList();
	}

	public String getEmailFromSecurityContext(){
		return accountRepo.findByEmail(SecurityUtils.getCurrentUserEmail())
				.orElseThrow(() -> new EntityNotFoundException("No user exists in SecurityUtils.getCurrentUserEmail() or user does not exist"))
				.getEmail();
	}

	public BasketEntity checkOutActiveBasket(String email){
		logger.info("email {} bought their basket", email);
		return basketMgr.checkOutActiveBasket(email);
	}

	public void resetAllBasketsToFalseFromEmail(String email){ //it resets them to false then makes a new active one
		BasketEntity oldBasket = basketMgr.getActiveBasketByEmail(email);

		logger.info("active basket in resetAllBasketToFalseFromEmail", oldBasket);
		oldBasket.setActive(false);
		basketRepo.save(oldBasket); //not setting to false here for some reason
		List<BasketEntity> falseBaskets = basketMgr.setAllActiveBasketsByEmailToFalse(email);
		BasketEntity activeBasket = basketMgr.ensureOneActiveBasketByEmail(email);
		BasketEntity savedBasket = basketRepo.save(activeBasket);
		if(savedBasket.getUniqueId().equals(activeBasket.getUniqueId())){
			logger.info("Email {} already had only one active basket: {}", email, activeBasket.getUniqueId());
		}else {
			logger.info("Email {} now has a new active basket: {}", email, activeBasket.getUniqueId());
		}
	}

	//Move to ItemMgr then call from ItemMgr

	private String generateUniqueOrderForBasketEntityId(){
		String uuid = UUID.randomUUID().toString();
		while(orderRepo.findById(uuid).isPresent()){
			uuid = UUID.randomUUID().toString();
		}

		return uuid;
	}

	private ItemEntity getItemById(String id){
		return itemRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("No item with id "+id+" exists"));
	}

	private List<OrderEntity> getOrderByBasketAndItem(BasketEntity basket, ItemEntity item){
		return orderRepo.findByBasketIdAndItemId(basket.getUniqueId(), item.getUniqueId())
				.orElseThrow(() -> new EntityNotFoundException("No item " + item.getUniqueId() + " exists that is in an order, that is in basket"+ basket.getUniqueId()));
	}

	public List<OrderEntity> getOrdersForBasketByBasket(BasketEntity basket) {
		return orderRepo.findByBasketId(basket.getUniqueId())
				.orElseThrow(() -> new EntityNotFoundException("Basket " + basket.getUniqueId() + " has no orders"));
	}

	public ItemEntity getItemsByOrder(OrderEntity orderEntity) {
		return itemRepo.findById(orderEntity.getItemId())
				.orElseThrow(() -> new EntityNotFoundException("Order for basket " + orderEntity.getBasketId() + " has an item, with a null id"));
	}

	//Move to ItemMgr then call from ItemMgr


}
