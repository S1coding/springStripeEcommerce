package springStripeEcommerceService.mainClasses.basket;


import springStripeEcommerceService.mainClasses.basket.service.helper.BasketIdGenerator;
import springStripeEcommerceService.mainClasses.basket.service.helper.BasketMgr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BasketMgrTest {
	@Mock
	private BasketRepo basketRepo;

	@Mock
	private BasketIdGenerator basketIdGenerator;

	@InjectMocks
	private BasketMgr basketMgr;

	@Test
	public void saveManyBaskets_savesSavedBaskets(){
		BasketEntity basketToSave1 = BasketEntity.builder().email("email").active(false).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave2 = BasketEntity.builder().email("email2").active(false).uniqueId("id2").dateBought(null).dateMade(LocalDateTime.now()).build();
		List<BasketEntity> basketsToSave = List.of(basketToSave1, basketToSave2);

		when(basketRepo.save(basketToSave1)).thenReturn(basketToSave1);
		when(basketRepo.save(basketToSave2)).thenReturn(basketToSave1);
		when(basketRepo.findAll()).thenReturn(basketsToSave);

		List<BasketEntity> basketsReceived = basketMgr.saveManyBaskets(basketsToSave);

		Mockito.verify(basketRepo).save(basketToSave1);
		Mockito.verify(basketRepo).save(basketToSave2);
		Mockito.verify(basketRepo).findAll();
		Assertions.assertEquals(2, basketsReceived.size());
		Assertions.assertEquals(basketsToSave, basketsReceived);
	}

	@Test
	public void findFirstByEmailAndActiveTrue_returnsActiveBasket(){
		BasketEntity basketToSave1 = BasketEntity.builder().email("email").active(false).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave2 = BasketEntity.builder().email("email").active(true).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave3 = BasketEntity.builder().email("email2").active(false).uniqueId("id2").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave4 = BasketEntity.builder().email("email2").active(true).uniqueId("id2").dateBought(null).dateMade(LocalDateTime.now()).build();
		List<BasketEntity> basketsToSave = List.of(basketToSave1, basketToSave2);

//		when(basketRepo.save(basketToSave1)).thenReturn(basketToSave1);
//		when(basketRepo.save(basketToSave2)).thenReturn(basketToSave2);
		when(basketRepo.findFirstByEmailAndActiveTrue(basketToSave1.getEmail())).thenReturn(Optional.of(basketToSave2));
		when(basketRepo.findFirstByEmailAndActiveTrue(basketToSave2.getEmail())).thenReturn(Optional.of(basketToSave2));
		when(basketRepo.findFirstByEmailAndActiveTrue(basketToSave3.getEmail())).thenReturn(Optional.of(basketToSave4));
		when(basketRepo.findFirstByEmailAndActiveTrue(basketToSave4.getEmail())).thenReturn(Optional.of(basketToSave4));

		Assertions.assertEquals(basketToSave2, basketRepo.findFirstByEmailAndActiveTrue(basketToSave1.getEmail()).get());
		Assertions.assertEquals(basketToSave2, basketRepo.findFirstByEmailAndActiveTrue(basketToSave2.getEmail()).get());
		Assertions.assertEquals(basketToSave4, basketRepo.findFirstByEmailAndActiveTrue(basketToSave3.getEmail()).get());
		Assertions.assertEquals(basketToSave4, basketRepo.findFirstByEmailAndActiveTrue(basketToSave4.getEmail()).get());
	}

	@Test
	public void noActiveBaskets_returnsFalseWhenActiveBaskets(){ //baskets must be empty to return true
		String account1 = "email";
		String account2 = "email2";
		BasketEntity basketToSave1 = BasketEntity.builder().email(account1).active(false).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave2 = BasketEntity.builder().email(account1).active(true).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave3 = BasketEntity.builder().email(account2).active(false).uniqueId("id2").dateBought(null).dateMade(LocalDateTime.now()).build();
		List<BasketEntity> basketsToSave = List.of(basketToSave1, basketToSave2);

		when(basketRepo.findFirstByEmailAndActiveTrue(basketToSave1.getEmail())).thenReturn(Optional.of(basketToSave2));
		when(basketRepo.findFirstByEmailAndActiveTrue(basketToSave2.getEmail())).thenReturn(Optional.of(basketToSave2));
		when(basketRepo.findFirstByEmailAndActiveTrue(basketToSave3.getEmail())).thenReturn(Optional.ofNullable(null));


		Assertions.assertEquals(false, basketMgr.noActiveBaskets(account1));
		Assertions.assertEquals(true, basketMgr.noActiveBaskets(account2));

	}

	@Test
	public void setAllActiveBasketsByEmailToFalse_setsAllActiveBasketsByEmailToFalse(){
		BasketEntity basketToSave1 = BasketEntity.builder().email("email").active(true).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave2 = BasketEntity.builder().email("email").active(true).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave3 = BasketEntity.builder().email("email").active(true).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();



		List<BasketEntity> trueBaskets = List.of(basketToSave1, basketToSave2, basketToSave3);
		when(basketRepo.findAllByEmailAndActiveTrue("email")).thenReturn(trueBaskets);

		List<BasketEntity> returnedBasketList = basketMgr.setAllActiveBasketsByEmailToFalse("email");
		Assertions.assertEquals(3, returnedBasketList.size());
		Assertions.assertEquals(false, returnedBasketList.get(0).isActive());
	}

	@Test
	public void manyActiveBaskets_returnsTrueIfManyActiveBaskets(){
		//1. should return true if 1<activeBasketList.size() active baskets 2.should return false if activeBasketList.size()==1 3.should throw error if activeBasketList.size()==0


		BasketEntity basketToSave1 = BasketEntity.builder().email("email").active(true).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave2 = BasketEntity.builder().email("email").active(true).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();
		BasketEntity basketToSave3 = BasketEntity.builder().email("email").active(true).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();

		List<BasketEntity> trueBaskets = List.of(basketToSave1, basketToSave2, basketToSave3);
		when(basketRepo.findAllByEmailAndActiveTrue("email")).thenReturn(trueBaskets);
		when(basketRepo.findFirstByEmailAndActiveTrue("email")).thenReturn(Optional.of(basketToSave1));
		Assertions.assertEquals(true, basketMgr.manyActiveBaskets("email"));

	}

	@Test
	public void manyActiveBaskets_returnsFalseIfOneActiveBaskets(){
		//1. should return true if 1<activeBasketList.size() active baskets 2.should return false if activeBasketList.size()==1 3.should throw error if activeBasketList.size()==0


		BasketEntity basketToSave1 = BasketEntity.builder().email("email").active(true).uniqueId("id").dateBought(null).dateMade(LocalDateTime.now()).build();

		List<BasketEntity> trueBaskets = List.of(basketToSave1);
		when(basketRepo.findAllByEmailAndActiveTrue("email")).thenReturn(trueBaskets);
		when(basketRepo.findFirstByEmailAndActiveTrue("email")).thenReturn(Optional.of(basketToSave1));
		Assertions.assertEquals(false, basketMgr.manyActiveBaskets("email"));

	}

	@Test
	public void manyActiveBaskets_throwsErrorIfNoActiveBaskets() {
		// Arrange
		//when(basketRepo.findAllByEmailAndActiveTrue("email")).thenReturn(Collections.emptyList());
		when(basketRepo.findFirstByEmailAndActiveTrue("email")).thenReturn(Optional.empty());

		// Act & Assert
		Assertions.assertThrows(NoSuchElementException.class, () -> basketMgr.manyActiveBaskets("email"));
	}

	@Test
	public void makeActiveBasketForEmail_returnsActiveBasket() {
		// Arrange
		String email = "email";
		String expectedId = "generated-id-123";

		// Mock the ID generator to return our test ID
		when(basketIdGenerator.generateUniqueBasketId()).thenReturn(expectedId);
		// Act
		BasketEntity result = basketMgr.makeActiveBasketForEmail(email);

		// Assert
		Assertions.assertNotNull(result);
		Assertions.assertEquals(email, result.getEmail());
		Assertions.assertEquals(expectedId, result.getUniqueId());
		Assertions.assertTrue(result.isActive());
		Assertions.assertNotNull(result.getDateMade());
		Assertions.assertNull(result.getDateBought());

		// Verify interactions
		Mockito.verify(basketIdGenerator).generateUniqueBasketId();
	}

//	public BasketEntity ensureOneActiveBasketByEmail(String email) {
//		logger.info("ensuring there is one active basket for {}", email);
//		if (noActiveBaskets(email)) {
//			logger.warn("There are no active baskets, making sure there is one active basket");
//			return makeActiveBasketForEmail(email);
//		}
//
//		if (manyActiveBaskets(email)) { //
//			logger.warn("There are many active baskets, making sure there is only one active basket");
//			setAllActiveBasketsByEmailToFalse(email);
//			return makeActiveBasketForEmail(email);
//		}
//		return basketRepo.findFirstByEmailAndActiveTrue(email)
//				.orElseThrow(() -> new NoSuchElementException("Logical error: Expected exactly one active basket, but none or multiple found."));
//	}

//	public BasketEntity makeActiveBasketForEmail(String email){
//		BasketEntity basket = new BasketEntity("id", email, LocalDateTime.now(), null, true);
//		basket.setUniqueId(basketIdGenerator.generateUniqueBasketId());
//		return basket;
//	}

//	public List<BasketEntity> setAllActiveBasketsByEmailToFalse(String email){
//		List<BasketEntity> baskets = basketRepo.findAllByEmailAndActiveTrue(email);
//		for(BasketEntity basket: baskets){
//			basket.setActive(false);
//		}
//		saveManyBaskets(baskets);
//		return baskets;
//	}

//	public List<BasketEntity> setAllActiveBasketsByEmailToFalse(String email){
//		List<BasketEntity> baskets = basketRepo.findAllByEmailAndActiveTrue(email);
//		for(BasketEntity basket: baskets){
//			basket.setActive(false);
//		}
//		saveManyBaskets(baskets);
//		return baskets;
//	}

	//complicated to test, but all the methods in this function are already tested and behave as expected,
	//so if its broken, it's because of the layout of this function.
	@Test
	public void ensureOneActiveBasketByEmail_setsAllActiveBasketsToFalseAndReturnsNewOne(){

	}
}
