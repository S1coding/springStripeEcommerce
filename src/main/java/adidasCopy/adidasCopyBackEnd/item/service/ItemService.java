package adidasCopy.adidasCopyBackEnd.item.service;

import adidasCopy.adidasCopyBackEnd.item.ItemEntity;
import adidasCopy.adidasCopyBackEnd.item.ItemRepo;
import adidasCopy.adidasCopyBackEnd.item.service.helper.ItemMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ItemService {

	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	private ItemMgr itemMgr;

	private final static Logger logger = LoggerFactory.getLogger(ItemEntity.class);

	public void updateItems(List<ItemEntity> newItems){
		itemMgr.saveToItems(newItems);
		itemMgr.clearDeletedItems(newItems);
		logger.info("Updated database with items from newItems");
	}

	public List<ItemEntity> returnIfItemRepoHasItems(){  //checks if database has items
		return itemMgr.returnIfItemRepoHasItems();
	}
}
