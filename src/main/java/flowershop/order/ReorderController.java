package flowershop.order;


import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.OrderManager;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static flowershop.order.Transaction.TransactionType.*;
import static org.salespointframework.order.OrderStatus.PAID;


@Controller
public class ReorderController {

	private final OrderManager<Transaction> transactionManager;
	private final ReorderManager reorderManager;
	private final Inventory<InventoryItem> inventory;

	ReorderController(OrderManager<Transaction> transactionManager, ReorderManager reorderManager, Inventory<InventoryItem> inventory) {
		this.transactionManager = transactionManager;
		this.reorderManager = reorderManager;
		this.inventory = inventory;
	}

	@GetMapping("/products/reorders")
	@PreAuthorize("hasRole('ROLE_WHOLESALER')")
	ModelAndView reorders(Model model) {
		Streamable<Transaction> transactions = transactionManager.findBy(PAID).filter(transaction -> transaction.getType() == REORDER);

		return new ModelAndView("reorders", "transactions", transactions);
	}

	@PostMapping("/products/reorders/send/{id}")
	@PreAuthorize("hasRole('ROLE_WHOLESALER')")
	String sendReorder(@PathVariable(name = "id") Optional<Transaction> reorderOptional, @LoggedIn Optional<UserAccount> userAccount) {
		return reorderOptional.map(reorder -> inventory.findById(reorder.getItemId()).map(inventoryItem -> {
			reorderManager.sendReorder(reorder, inventoryItem);
			return "redirect:/products/reorders?success";
		}).orElse("redirect:/products/reorders")).orElse("redirect:/products/reorders");
	}

}
