package flowershop.order;

import flowershop.products.FlowerShopItem;
import flowershop.user.UserManager;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.money.MonetaryAmount;
import java.util.Optional;

import static flowershop.order.SubTransaction.SubTransactionType.REORDER;
import static flowershop.order.Transaction.TransactionType.COLLECTION;
import static org.salespointframework.order.OrderStatus.PAID;
import static org.salespointframework.payment.Cash.CASH;

/**
 * A manager for {@link Transaction}s.
 *
 * @author Friedrich Bethke
 */
@Service
@Transactional
public class ReorderManager {
	private final OrderManager<Transaction> orderManager;
	private final Inventory<InventoryItem> inventory;
	private final UserManager userManager;

	/**
	 * Creates a new {@link ReorderManager} with the given {@link OrderManager}, {@link Inventory} and {@link UserAccountManager}.
	 *  @param orderManager       must not be {@literal null}.
	 * @param inventory          must not be {@literal null}.
	 * @param userManager must not be {@literal null}.
	 * @param userManager
	 */
	public ReorderManager(OrderManager<Transaction> orderManager, Inventory<InventoryItem> inventory,
						  UserManager userManager) {
		this.orderManager = orderManager;
		this.inventory = inventory;
		this.userManager = userManager;
	}

	/**
	 * Checks if the quantity of every {@link FlowerShopItem} in the {@link Inventory} is high enough,
	 * if that is not the case a new {@link SubTransaction} from type REORDER, for that {@link FlowerShopItem}, will be created.
	 */
	public void refillInventory() {
		inventory.findAll().forEach(item -> {
			Quantity standardStock = Quantity.of(((FlowerShopItem) item.getProduct()).getBaseStock());
			Quantity threshold = standardStock.subtract(Quantity.of(standardStock.getAmount().intValue() / 2));
			if (item.getQuantity().isLessThan(threshold)) {
				Quantity quantity = standardStock.subtract(item.getQuantity());
				Quantity reorderQuantity = findByInventoryId(item.getId()).
						map(t -> quantity.subtract(t.getQuantity())).orElse(quantity);
				createReorder(item, reorderQuantity, REORDER);
			}
		});
	}

	/**
	 * Creates a new {@link SubTransaction} for the given {@link FlowerShopItem}.
	 *
	 * @param inventoryItem will never be {@literal null}.
	 * @param quantity      will never be {@literal null}.
	 * @param type          will never be {@literal null}.
	 */
	public void createReorder(InventoryItem inventoryItem, Quantity quantity, SubTransaction.SubTransactionType type) {
		if (quantity.isLessThan(Quantity.of(1))) {
			return;
		}
		userManager.findByRole(Role.of("ROLE_BOSS")).ifPresent(userAccount -> {
			String name = inventoryItem.getProduct().getName();
			FlowerShopItem item = (FlowerShopItem) inventoryItem.getProduct();
			MonetaryAmount price = item.getBasePrice().multiply(quantity.getAmount());
			Transaction reorder = findByInventoryId(inventoryItem.getId()).
					orElse(new Transaction(userAccount.getUserAccount(), CASH, COLLECTION));
			if (!reorder.isPaid()) {
				orderManager.payOrder(reorder);
			}
			reorder.setItemId(inventoryItem.getId());
			reorder.addSubTransaction(name, quantity, price, type);
			orderManager.save(reorder);
		});
	}

	/**
	 * Increases the quantity for the given item in the inventory and marks the reorder as done.
	 *
	 * @param reorder will never be {@literal null}.
	 */
	public void sendReorder(SubTransaction reorder) {
		Streamable.of(inventory.findAll()).stream().filter(item -> item.getProduct().getName().equals(reorder.getFlower())).
				findFirst().ifPresent(inventoryItem -> {
			inventoryItem.increaseQuantity(reorder.getQuantity());
			inventory.save(inventoryItem);
			reorder.setStatus(false);
		});
	}

	public Streamable<Transaction> findAll() {
		return orderManager.findBy(PAID).filter(transaction -> transaction.getType() == COLLECTION);
	}

	public Optional<Transaction> findByInventoryId(InventoryItemIdentifier id) {
		return findAll().stream().filter(transaction -> transaction.getItemId().equals(id)).findFirst();
	}

	Long validateQuantity(String quantity, Model model) {
		long qty;

		try {
			qty = Long.valueOf(quantity);
		} catch (NumberFormatException e) {
			model.addAttribute("message", "inventory.quantity.invalid");
			return null;
		}
		if (qty <= 0) {
			model.addAttribute("message", "inventory.quantity.positive");
			return null;
		}
		return qty;
	}

}
