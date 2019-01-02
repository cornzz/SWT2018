package flowershop.order;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.MonetaryAmount;

import java.util.Date;

import static flowershop.order.Transaction.TransactionType.COLLECTION;
import static org.salespointframework.order.OrderStatus.PAID;
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
				createReorder(inventoryItem, quantity, SubTransaction.SubTransactionType.REORDER);
			}
		});
	}

	public void createReorder(InventoryItem inventoryItem, Quantity quantity, SubTransaction.SubTransactionType type) {
		userAccountManager.findByUsername("admin").ifPresent(userAccount -> {
			Streamable<Transaction> reorders = reorderManager.findBy(PAID).filter(transaction -> transaction.getType() == COLLECTION);
			for (Transaction reorder : reorders) {
				if (reorder.getItemId().equals(inventoryItem.getId())) {
					reorder.addSubTransaction(new SubTransaction(quantity, new Date(), inventoryItem.getProduct().getPrice().multiply(quantity.getAmount()), inventoryItem.getProduct().getName(), type));
					transactionManager.save(reorder);
					return;
				}
			}
			Transaction transaction = new Transaction(userAccount, CASH, COLLECTION);
			MonetaryAmount price = inventoryItem.getProduct().getPrice().multiply(quantity.getAmount()).negate();
			transaction.setOptional(inventoryItem.getId(), price, inventoryItem.getProduct().getName());
			transaction.addSubTransaction(new SubTransaction(quantity, new Date(), inventoryItem.getProduct().getPrice().multiply(quantity.getAmount()), inventoryItem.getProduct().getName(), type));
			reorderManager.payOrder(transaction);
			reorderManager.save(transaction);
		});
	}

	public void sendReorder(SubTransaction reorder, InventoryItem inventoryItem) {
		inventoryItem.increaseQuantity(reorder.getQuantity());
		inventory.save(inventoryItem);
		Transaction reorderTransaction = transactionManager.findBy(PAID)
				.filter(transaction -> transaction.getType() == COLLECTION)
				.filter(transaction -> transaction.getItemId().equals(inventoryItem.getId())).get()
				.findFirst().get();
		reorderTransaction.getSubTransactions().stream()
				.filter(subTransaction -> subTransaction.equals(reorder))
				.findFirst().get().setStatus(false);
		transactionManager.save(reorderTransaction);
	}
}
