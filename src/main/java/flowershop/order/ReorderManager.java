package flowershop.order;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.MonetaryAmount;

import static flowershop.order.Transaction.TransactionType.REORDER;
import static org.salespointframework.payment.Cash.CASH;

@Service
@Transactional
public class ReorderManager {
	private final OrderManager<Transaction> reorderManager;
	private final Inventory<InventoryItem> inventory;
	private final UserAccountManager userAccountManager;

	public ReorderManager(OrderManager<Transaction> reorderManager, Inventory<InventoryItem> inventory, UserAccountManager userAccountManager) {
		this.reorderManager = reorderManager;
		this.inventory = inventory;
		this.userAccountManager = userAccountManager;
	}

	public void refillInventory() {
		inventory.findAll().forEach(inventoryItem -> {
			Quantity minimumQuantity = Quantity.of(5);
			if (inventoryItem.getQuantity().isLessThan(minimumQuantity)) {
				Transaction transaction = new Transaction(userAccountManager.findByUsername("admin").get(), CASH, Transaction.TransactionType.REORDER);
				Quantity amount = minimumQuantity.subtract(inventoryItem.getQuantity());
				transaction.setOptional(inventoryItem.getProduct().getPrice().multiply(amount.getAmount()).negate(), inventoryItem.getId(), inventoryItem.getProduct().getName(), amount);
				reorderManager.payOrder(transaction);
				reorderManager.save(transaction);
			}
		});
	}

	public void reorder(UserAccount account, InventoryItemIdentifier id, MonetaryAmount price, Quantity quantity) {

		Transaction transaction = new Transaction(account, CASH, REORDER);
		transaction.setOptional(price, id, inventory.findById(id).get().getProduct().getName(), quantity);
		reorderManager.payOrder(transaction);
		reorderManager.save(transaction);

	}

}
