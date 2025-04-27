package springStripeEcommerceService.mainClasses.item;


import springStripeEcommerceService.mainClasses.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;


@Controller
public class ItemController {

	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	private ItemService itemService;


	@PostMapping("/saveTree")
	public ResponseEntity saveData(@RequestBody List<ItemEntity> nodes) throws IOException {
		itemService.updateItems(nodes);
		return ResponseEntity.status(HttpStatus.OK).body("Data saved successfully");
	}

	@GetMapping("/getTree")
	public ResponseEntity getTree(){

		List<ItemEntity> items = itemService.returnIfItemRepoHasItems();
		return ResponseEntity.status(HttpStatus.OK).body(items);
	}
}
