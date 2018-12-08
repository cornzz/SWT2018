package flowershop.products.controller;


import flowershop.order.ReorderManager;
import flowershop.order.Transaction;
import flowershop.products.FlowerShopItem;
import flowershop.products.FlowerShopItemCatalog;
import org.javamoney.moneta.Money;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.money.MonetaryAmount;

import java.util.Optional;

import static flowershop.order.Transaction.TransactionType.DEFICIT;
import static flowershop.order.Transaction.TransactionType.REORDER;
import static org.salespointframework.core.Currencies.EURO;
import static org.salespointframework.payment.Cash.CASH;

@Controller
public class FlowerShopInventoryController {

	private final Inventory<InventoryItem> inventory;
	private final FlowerShopItemCatalog itemCatalog;
	private final OrderManager<Transaction> transactionManager;
	private final ReorderManager reorderManager;

	FlowerShopInventoryController(Inventory<InventoryItem> inventory, FlowerShopItemCatalog itemCatalog, OrderManager<Transaction> transactionManager, ReorderManager reorderManager) {
		this.itemCatalog = itemCatalog;
		this.inventory = inventory;
		this.transactionManager = transactionManager;
		this.reorderManager = reorderManager;
	}

	@GetMapping("/products/items/stock")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String inventory(Model model) {
		model.addAttribute("inventory", inventory.findAll());

		return "inventory";
	}

	@PostMapping("/products/items/stock/deficit/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String deficit(Model model, @PathVariable InventoryItemIdentifier id, int deficit, @LoggedIn Optional<UserAccount> userAccount) {

		InventoryItem item = inventory.findById(id).get();
		item.decreaseQuantity(Quantity.of(deficit));
		inventory.save(inventory.findById(id).get());
		reorderManager.refillInventory();

		return "redirect:/products/items/stock";


	}

	@PostMapping("/products/items/stock/reorder/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String reorder(Model model, @PathVariable InventoryItemIdentifier id, int reorder, @LoggedIn Optional<UserAccount> userAccount) {

		MonetaryAmount price = inventory.findById(id).get().getProduct().getPrice().multiply(reorder).negate();
		reorderManager.reorder(userAccount.get(), id, price, Quantity.of(reorder));
		return "redirect:/products/items/stock";


	}

	@GetMapping("/products/items/stock/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String add(Model model) {
		return "inventory_add";
	}

	@PostMapping("/products/items/stock/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String add(String name, int price, int amount, String description) {
		FlowerShopItem item = new FlowerShopItem(name, Money.of(price, EURO), description);
		itemCatalog.save(item);
		inventory.save(new InventoryItem(item, Quantity.of(amount)));

		return "redirect:/products/items/stock";
	}


}
