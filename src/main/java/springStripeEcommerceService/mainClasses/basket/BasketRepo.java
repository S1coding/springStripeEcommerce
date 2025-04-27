package springStripeEcommerceService.mainClasses.basket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasketRepo extends JpaRepository<BasketEntity, String> {

	//don't use optionals for list in spring
	List<BasketEntity> findAllByEmailAndActiveTrue(String email); //exclusively for checking if there are more than one active baskets

	Optional<BasketEntity> findFirstByEmailAndActiveTrue(String email);

	Optional<BasketEntity> findByUniqueId(String uniqueId);
}
