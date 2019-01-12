package flowershop.order;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.OrderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Integration tests interacting with the {@link ReorderManager} directly.
 *
 * @author Cornelius Kummer
 */
class ReorderManagerIntegrationTests extends AbstractIntegrationTests {

	@Autowired ReorderManager reorderManager;
	@Autowired Inventory<InventoryItem> inventory;
	@Autowired OrderManager<Transaction> transactionManager;

	@Test
	void refillInventoryTest() {
		inventory.findAll().forEach(inventoryItem -> inventoryItem.decreaseQuantity(inventoryItem.getQuantity()));
		reorderManager.refillInventory();
		long result = reorderManager.findAll().stream().count();
		assertEquals(3, result);

		reorderManager.findAll().map(Transaction::getSubTransactions).flatMap(List::stream).forEach(reorder -> {
			reorderManager.sendReorder(reorder);
			InventoryItem i = Streamable.of(inventory.findAll()).stream().filter(item -> item.getProduct().getName().equals(reorder.getFlower())).findFirst().get();
			assertEquals(35, i.getQuantity().getAmount().intValue());
			assertFalse(reorder.getStatus());
		});
	}

}
