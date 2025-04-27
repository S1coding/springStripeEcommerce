package springStripeEcommerceService.mainClasses.basket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, String> {

	public Optional<List<OrderEntity>> findByBasketId(String id);
	public Optional<List<OrderEntity>> findByBasketIdAndItemId(String basketId, String itemId);
}
