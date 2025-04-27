package springStripeEcommerceService.mainClasses.basket;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BasketEntity {
	@Id
	String uniqueId;
	String email;
	LocalDateTime dateMade;
	LocalDateTime dateBought;
	boolean active;
}
