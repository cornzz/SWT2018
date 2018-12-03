package flowershop.order;


import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.quantity.Quantity;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;


@Controller
public class ReorderController {

	private final OrderManager<Transaction> transactionManager;
	private final Inventory<InventoryItem> inventory;


	ReorderController(OrderManager<Transaction> transactionManager, Inventory<InventoryItem> inventory){
		Assert.notNull(transactionManager, "OrderManager must not be null!");
		this.transactionManager = transactionManager;
		this.inventory = inventory;
	}

	// A Reorder view for the FlowerTrader

	@GetMapping("/products/reorder")
		public String reorder(Model model) {

		//This is not perfect but it works

		transactionManager.findBy(OrderStatus.PAID).forEach(transaction -> {
			if (transaction.getType() == Transaction.TransactionType.REORDER) {
				model.addAttribute("transactions", transaction);
			}
		});
		return "reorder";
	}

	//Todo: Remove Reorder after it was send

	@PostMapping("/products/reorder/send/{id}")
	public String send(@PathVariable InventoryItemIdentifier id, Quantity quantity, OrderIdentifier oid,  @LoggedIn Optional<UserAccount> userAccount){
		inventory.findById(id).get().increaseQuantity(quantity);
		inventory.save(inventory.findById(id).get());


		/*
		transactionManager.findBy(OrderStatus.PAID).forEach(transaction -> {
			if (transaction.getId()== oid) {
				transaction.setType(Transaction.TransactionType.DONE);
			}
		}); */

		return "redirect:/products/reorder";
	}



}
