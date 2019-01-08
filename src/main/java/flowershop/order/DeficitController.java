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


@Controller
public class DeficitController {

	private final ReorderManager reorderManager;
	private final Inventory<InventoryItem> inventory;
	private final OrderManager<Transaction> transactionManager;

	DeficitController(ReorderManager reorderManager, Inventory<InventoryItem> inventory, OrderManager<Transaction> transactionManager) {
		this.reorderManager = reorderManager;
		this.inventory = inventory;
		this.transactionManager = transactionManager;
	}

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

	@GetMapping("/deficits")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String deficit(Model model) {
		Streamable<SubTransaction> subTransactions = transactionManager.findBy(PAID).
				filter(transaction -> transaction.getType() == COLLECTION).
				map(Transaction::getSubTransactions).get().flatMap(List::stream).
				filter(subTransaction -> subTransaction.isType(DEFICIT)).
				filter(SubTransaction::getStatus).
				map(Streamable::of).reduce(Streamable.empty(), Streamable::and);
		MonetaryAmount total = subTransactions.get().map(SubTransaction::getPrice).reduce(ZERO_EURO, MonetaryAmount::add);

		model.addAttribute("deficits", subTransactions);
		model.addAttribute("total", total);

		return "deficits";
	}

}
