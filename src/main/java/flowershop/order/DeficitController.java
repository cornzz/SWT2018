package flowershop.order;


import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.money.MonetaryAmount;
import java.util.List;

import static flowershop.order.SubTransaction.SubTransactionType.DEFICIT;
import static flowershop.order.Transaction.TransactionType.COLLECTION;
import static org.salespointframework.core.Currencies.ZERO_EURO;
import static org.salespointframework.order.OrderStatus.PAID;

/**
 * A Spring MVC controller to view {@link SubTransaction}s with DEFICIT type.
 *
 * @author Friedrich Bethke
 */
@Controller
public class DeficitController {

	private final ReorderManager reorderManager;
	private final Inventory<InventoryItem> inventory;
	private final OrderManager<Transaction> transactionManager;

	/**
	 * Creates a new {@link DeficitController} with the given {@link ReorderManager}, {@link Inventory} and {@link OrderManager}.
	 *
	 * @param reorderManager     must not be {@literal null}.
	 * @param inventory          must not be {@literal null}.
	 * @param transactionManager must not be {@literal null}.
	 */
	DeficitController(ReorderManager reorderManager, Inventory<InventoryItem> inventory, OrderManager<Transaction> transactionManager) {
		this.reorderManager = reorderManager;
		this.inventory = inventory;
		this.transactionManager = transactionManager;
	}

	/**
	 * Creates a new deficit.
	 *
	 * @param id              will never be {@literal null}.
	 * @param deficitQuantity will never be {@literal null}.
	 * @param model           will never be {@literal null}.
	 * @return the view name and, if creating the deficit was not successful, the inventory view.
	 */
	@PostMapping("/items/deficit/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String deficit(@PathVariable InventoryItemIdentifier id, String deficitQuantity, Model model) {
		Long quantity = reorderManager.validateQuantity(deficitQuantity, model);
		if (quantity == null) {
			return "forward:/items";
		}
		return inventory.findById(id).map(inventoryItem -> {
			if (!Quantity.of(quantity).isGreaterThan(inventoryItem.getQuantity())) {
				inventoryItem.decreaseQuantity(Quantity.of(quantity));
				inventory.save(inventoryItem);
				reorderManager.refillInventory();
				reorderManager.createReorder(inventoryItem, Quantity.of(quantity), SubTransaction.SubTransactionType.DEFICIT);
				return "redirect:/items?deficit";
			} else {
				model.addAttribute("message", "inventory.notenough");
				return "forward:/items";
			}
		}).orElse("redirect:/items");
	}

	/**
	 * Displays all deficits.
	 *
	 * @param model will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/deficits")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String deficit(Model model) {
		Streamable<SubTransaction> subTransactions = reorderManager.findAll().
				map(Transaction::getSubTransactions).flatMap(List::stream).
				filter(SubTransaction::getStatus).
				filter(subTransaction -> subTransaction.isType(DEFICIT));
		MonetaryAmount total = subTransactions.get().map(SubTransaction::getPrice).reduce(ZERO_EURO, MonetaryAmount::add);

		model.addAttribute("deficits", subTransactions);
		model.addAttribute("total", total);

		return "deficits";
	}

}
