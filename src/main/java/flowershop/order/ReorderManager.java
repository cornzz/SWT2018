package flowershop.order;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.MonetaryAmount;

import static flowershop.order.Transaction.TransactionType.DONE;
import static flowershop.order.Transaction.TransactionType.REORDER;
import static org.salespointframework.payment.Cash.CASH;

@Service
@Transactional
public class ReorderManager {
	private final OrderManager<Transaction> reorderManager;
	private final Inventory<InventoryItem> inventory;
	private final UserAccountManager userAccountManager;
	private final OrderManager<Transaction> transactionManager;

	public ReorderManager(OrderManager<Transaction> reorderManager, Inventory<InventoryItem> inventory, UserAccountManager userAccountManager, OrderManager<Transaction> transactionManager) {
		this.reorderManager = reorderManager;
		this.inventory = inventory;
		this.userAccountManager = userAccountManager;
		this.transactionManager = transactionManager;
	}

	public void refillInventory() {
		Quantity threshold = Quantity.of(5);
		Quantity standardStock = Quantity.of(10);
		inventory.findAll().forEach(inventoryItem -> {
			if (inventoryItem.getQuantity().isLessThan(threshold)) {
				Quantity quantity = standardStock.subtract(inventoryItem.getQuantity());
				createReorder(inventoryItem, quantity);
			}
		});
	}

	public void createReorder(InventoryItem inventoryItem, Quantity quantity) {
		userAccountManager.findByUsername("admin").ifPresent(userAccount -> {
			Transaction transaction = new Transaction(userAccount, CASH, REORDER);
			MonetaryAmount price = inventoryItem.getProduct().getPrice().multiply(quantity.getAmount()).negate();
			transaction.setOptional(inventoryItem.getId(), quantity, price, inventoryItem.getProduct().getName());
			reorderManager.payOrder(transaction);
			reorderManager.save(transaction);
		});
	}

	public void sendReorder(Transaction reorder, InventoryItem inventoryItem) {
			inventoryItem.increaseQuantity(reorder.getQuantity());
			inventory.save(inventoryItem);
			reorder.setType(DONE);
			transactionManager.save(reorder);
	}

}
