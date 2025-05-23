package springStripeEcommerceService.mainClasses.basket;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderEntity {

	@Id
	String uniqueId;
	String basketId;
	String itemId;
	int quantity;
	LocalDateTime dateMade;
}
