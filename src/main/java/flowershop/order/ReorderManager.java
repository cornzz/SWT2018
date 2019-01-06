package flowershop.order;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.MonetaryAmount;
import java.util.Optional;

import static flowershop.order.SubTransaction.SubTransactionType.REORDER;
import static flowershop.order.Transaction.TransactionType.COLLECTION;
import static org.salespointframework.order.OrderStatus.PAID;
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
		inventory.findAll().forEach(item -> {
			Quantity threshold = Quantity.of(5);
			Quantity standardStock = Quantity.of(10);
			if (item.getQuantity().isLessThan(threshold)) {
				Quantity quantity = standardStock.subtract(item.getQuantity());
				Quantity reorderQuantity = findByInventoryId(item.getId()).map(t -> quantity.subtract(t.getQuantity())).orElse(quantity);
				createReorder(item, reorderQuantity, REORDER);
			}
		});
	}

	public void createReorder(InventoryItem inventoryItem, Quantity quantity, SubTransaction.SubTransactionType type) {
		userAccountManager.findByUsername("admin").ifPresent(userAccount -> {
			String name = inventoryItem.getProduct().getName();
			MonetaryAmount price = inventoryItem.getProduct().getPrice().multiply(quantity.getAmount());
			Transaction reorder = findByInventoryId(inventoryItem.getId()).orElse(new Transaction(userAccount, CASH, COLLECTION));
			if (!reorder.isPaid()) {
				reorderManager.payOrder(reorder);
			}
			reorder.setItemId(inventoryItem.getId());
			reorder.addSubTransaction(name, quantity, price, type);
			reorderManager.save(reorder);
		});
	}

	public void sendReorder(SubTransaction reorder) {
		Streamable.of(inventory.findAll()).stream().filter(item -> item.getProduct().getName().equals(reorder.getFlower())).findFirst().ifPresent(inventoryItem -> {
			inventoryItem.increaseQuantity(reorder.getQuantity());
			inventory.save(inventoryItem);
			reorder.setStatus(false);
		});
	}

	public Streamable<Transaction> findAll() {
		return reorderManager.findBy(PAID).filter(transaction -> transaction.getType() == COLLECTION);
	}

	public Optional<Transaction> findByInventoryId(InventoryItemIdentifier id) {
		return findAll().stream().filter(transaction -> transaction.getItemId().equals(id)).findFirst();
	}

}
