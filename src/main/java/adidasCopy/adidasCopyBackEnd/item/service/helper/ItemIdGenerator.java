package adidasCopy.adidasCopyBackEnd.item.service.helper;

import adidasCopy.adidasCopyBackEnd.item.ItemRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ItemIdGenerator {

	@Autowired
	private ItemRepo itemRepo;

	private final static Logger logger = LoggerFactory.getLogger(ItemIdGenerator.class);

	public String generateUniqueIdForItemId(){
		String uuid = UUID.randomUUID().toString();
		while(itemRepo.findById(uuid).isPresent()){
			uuid = UUID.randomUUID().toString();
		}

		logger.info("generated uniqueId for an item");
		return uuid;
	}
}
