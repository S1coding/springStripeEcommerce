package adidasCopy.adidasCopyBackEnd.basket;

import adidasCopy.adidasCopyBackEnd.account.AccountRepo;
import adidasCopy.adidasCopyBackEnd.basket.service.BasketService;
import adidasCopy.adidasCopyBackEnd.basket.service.ItemToBuy;
import adidasCopy.adidasCopyBackEnd.basket.service.ItemToDelete;
import adidasCopy.adidasCopyBackEnd.item.ItemEntity;
import adidasCopy.adidasCopyBackEnd.item.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Controller
public class BasketController {

	@Autowired
	private BasketRepo basketRepo;

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	BasketService basketService;

	@PostMapping("/deleteFromBasket")
	public ResponseEntity deleteFromBasket(@RequestBody ItemToDelete itemToDelete){

		//add @RestControllerAdvice exception handling here
		basketService.deleteItemFromActiveBasketByItemId(itemToDelete.getItemId());
		return ResponseEntity.status(HttpStatus.OK).body(itemToDelete+" deleted from basket");
	}

	@GetMapping("/getBasketOrders")
	public ResponseEntity getBasketOrders(){

		//add @RestControllerAdvice exception handling here
		List<ItemEntity> items = basketService.getAllActiveBasketItems();
		return ResponseEntity.status(HttpStatus.OK).body(items);
	}

	@PostMapping("/addOrderToBasket")
	public ResponseEntity addOrderToBasket(@RequestBody ItemToBuy itemToBuy){

		String text = itemToBuy.getName(); //change itemToBuy field from name to text, so its getText here
		//add @RestControllerAdvice exception handling here
		OrderEntity order = basketService.saveNewOrderWithItemByText(text);
		return ResponseEntity.status(HttpStatus.OK).body("Oder "+ order.getUniqueId()+" added to basket "+order.getBasketId()+" with item "+order.getItemId());
	}

}
