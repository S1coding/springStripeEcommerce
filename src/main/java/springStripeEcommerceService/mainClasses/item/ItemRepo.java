package adidasCopy.adidasCopyBackEnd.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepo extends JpaRepository<ItemEntity, String> {

	public Optional<ItemEntity> findByText(String text);
}
