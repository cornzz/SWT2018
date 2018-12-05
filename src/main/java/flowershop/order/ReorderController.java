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
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Iterator;
import java.util.Optional;


@Controller
public class ReorderController {

	private final OrderManager<Transaction> transactionManager;
	private final Inventory<InventoryItem> inventory;


	ReorderController(OrderManager<Transaction> transactionManager, Inventory<InventoryItem> inventory){
		this.transactionManager = transactionManager;
		this.inventory = inventory;
	}

	// A Reorder view for the FlowerTrader
	@GetMapping("/products/reorder")
		public String reorder(Model model) {

		Streamable<Transaction> transactions = transactionManager.findBy(OrderStatus.PAID)
				.filter(transaction -> transaction.getType() == Transaction.TransactionType.REORDER);
		model.addAttribute("transactions", transactions);

		return "reorder";
	}

	@PostMapping("/products/reorder/send/{id}")
	public String send(@PathVariable OrderIdentifier id, Quantity quantity, @LoggedIn Optional<UserAccount> userAccount){

		Streamable<Transaction> transactions = transactionManager.findBy(OrderStatus.PAID)
				.filter(transaction -> transaction.getType() == Transaction.TransactionType.REORDER);
		for(Iterator<Transaction> iterator = transactions.iterator();iterator.hasNext();){
			Transaction transaction = iterator.next();
			if(transaction.getId().equals(id)){
				inventory.findById(transaction.getFlower()).get().increaseQuantity(quantity);
				inventory.save(inventory.findById(transaction.getFlower()).get());
				transaction.setType(Transaction.TransactionType.DONE);
				transactionManager.save(transaction);
			}
		}

		return "redirect:/products/reorder";
	}



}
