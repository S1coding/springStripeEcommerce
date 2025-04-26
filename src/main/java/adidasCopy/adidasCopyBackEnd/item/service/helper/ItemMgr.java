package adidasCopy.adidasCopyBackEnd.item.service.helper;

import adidasCopy.adidasCopyBackEnd.item.ItemEntity;
import adidasCopy.adidasCopyBackEnd.item.ItemRepo;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ItemMgr {

	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	ItemIdGenerator itemIdGenerator;

	private final static Logger logger = LoggerFactory.getLogger(ItemMgr.class);

	public void saveToItems(List<ItemEntity> newItems){
		throwIfItemListEmpty(newItems); //throws exception if newItems is empty
		for (ItemEntity node : newItems) {
			updateOldItemOrAssignUniqueId(node);
			itemRepo.save(node);
		}
		logger.info("Added or updated nodes from database with nodes from newItems");
	}

	public void clearDeletedItems(List<ItemEntity> newItems){
		List<ItemEntity> databaseItems = returnIfItemRepoHasItems(); //gets all items from itemRepo or throws exception if empty
		syncTables(databaseItems, newItems); //deletes item from database if it isn't in new items
		logger.info("Deleted items from database which are not in new items");
	}

	public List<ItemEntity> returnIfItemRepoHasItems(){  //checks if database has items
		logger.info("Checking if table has items");
		List<ItemEntity> items = itemRepo.findAll();
		if(items.isEmpty()){
			throw new IllegalStateException("ItemRepo has no items");
		}
		return items;
	}

	public void updateOldItemOrAssignUniqueId(ItemEntity newItem){
		ItemEntity existingItem = getExistingItemOrNullFromText(newItem.getText()); //only returns if item with newItem text already exists in database
		if(existingItem!=null){
			copyItemId(existingItem, newItem);
		} else{
			assignNewId(newItem);
		}
		logger.info("Assigned item {} with new id or updated old item already in database with new item", newItem.getText());
	}

	public void copyItemId(ItemEntity oldItem, ItemEntity newItem){
		newItem.setUniqueId(oldItem.getUniqueId());
		logger.info("Updated new item uniqueId with old item uniqueId {}", newItem.getUniqueId());
	}
	public void assignNewId(ItemEntity item){
		item.setUniqueId(itemIdGenerator.generateUniqueIdForItemId());
		logger.info("Assigned new id for item text {}", item.getText());
	}

	//ensures table has no deleted items
	public void syncTables(List<ItemEntity> databaseItems, List<ItemEntity> newItems){ //deletes databaseItem if databaseItem is not in newItems
		for(ItemEntity databaseItem: databaseItems){
			if(!isItemInList(databaseItem, newItems)){
				itemRepo.delete(databaseItem);
			}
		}
		logger.info("Deleted redundant items from database");
	}

	public boolean isItemInList(ItemEntity itemToCheck, List<ItemEntity> items){
		boolean exists = items.stream().anyMatch(item -> itemsEqual(itemToCheck, item));

		if (exists) {
			logger.info("Item {} in list", itemToCheck);
		} else {
			logger.info("Item {} not in list", itemToCheck);
		}

		return exists;
	}

	public boolean itemsEqual(ItemEntity itemOne, ItemEntity itemTwo){
		if(itemOne.getUniqueId().equals(itemTwo.getUniqueId())){ //checks if two items have the same uniqueId
			logger.info("Item of text {} and item of text {} have the same id", itemTwo.getText(), itemTwo.getText());
			return true;
		}else{
			return false;
		}
	}

	public ItemEntity getExistingItemOrNullFromText(String text){
		logger.info("Checking if item of text {} exists", text);
		return itemRepo.findByText(text).orElse(null);
	}


	public List<ItemEntity> throwIfItemListEmpty(List<ItemEntity> items){
		logger.info("Checking if a list has items");
		if(items.isEmpty()){
			throw new IllegalStateException("New items has no items");
		}
		return items;
	}

	public ItemEntity getItemByText(String text){
		return itemRepo.findByText(text)
				.orElseThrow(() -> new EntityNotFoundException("No (or more than one) item with text "+text));
	}
}
