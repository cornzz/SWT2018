package flowershop.order;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

import static flowershop.order.SubTransaction.SubTransactionType.REORDER;

/**
 * A Spring MVC controller to manage {@link SubTransaction}s with REORDER type.
 *
 * @author Friedrich Bethke
 */
@Controller
public class ReorderController {

	private final ReorderManager reorderManager;
	private final Inventory<InventoryItem> inventory;

	/**
	 * Creates a new {@link ReorderController} with the given {@link ReorderManager} and {@link Inventory}.
	 *
	 * @param reorderManager must not be {@literal null}.
	 * @param inventory must not be {@literal null}.
	 */
	ReorderController(ReorderManager reorderManager, Inventory<InventoryItem> inventory) {
		this.reorderManager = reorderManager;
		this.inventory = inventory;
	}

	/**
	 * Creates a new reorder.
	 *
	 * @param id will never be {@literal null}.
	 * @param reorderQuantity will never be {@literal null}.
	 * @param model will never be {@literal null}.
	 * @return the view name and, if creating the reorder was not successful, the inventory view.
	 */
	@PostMapping("/items/reorder/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String reorder(@PathVariable InventoryItemIdentifier id, String reorderQuantity, Model model) {
		Long quantity = reorderManager.validateQuantity(reorderQuantity, model);
		if (quantity == null) {
			return "forward:/items";
		}
		return inventory.findById(id).map(inventoryItem -> {
			reorderManager.createReorder(inventoryItem, Quantity.of(quantity), REORDER);
			return "redirect:/items?reorder";
		}).orElse("redirect:/items");
	}

	/**
	 * Displays all open reorders.
	 *
	 * @param model will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/reorders")
	@PreAuthorize("hasRole('ROLE_WHOLESALER') or hasRole('ROLE_BOSS')")
	ModelAndView reorders(Model model) {
		Streamable<SubTransaction> subTransactions = reorderManager.findAll().
				map(Transaction::getSubTransactions).flatMap(List::stream).
				filter(SubTransaction::getStatus).
				filter(subTransaction -> subTransaction.isType(REORDER));
		return new ModelAndView("reorders", "subTransactions", subTransactions);
	}

	/**
	 * Marks the reorder as done and sends the given amount to the inventory.
	 *
	 * @param reorderOptional will never be {@literal null}.
	 * @return the view name.
	 */
	@PostMapping("/reorders/send/{id}")
	@PreAuthorize("hasRole('ROLE_WHOLESALER')")
	String sendReorder(@PathVariable(name = "id") Optional<SubTransaction> reorderOptional) {
		reorderOptional.ifPresent(reorderManager::sendReorder);
		return "redirect:/reorders";
	}

}
