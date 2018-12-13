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
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static org.salespointframework.core.Currencies.EURO;

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

	@RequestMapping("/products/items/stock")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String inventory(Model model) {
		model.addAttribute("inventory", inventory.findAll());

		return "inventory";
	}

	@PostMapping("/products/items/stock/deficit/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String deficit(@PathVariable InventoryItemIdentifier id, String deficitQuantity, Model model, @LoggedIn Optional<UserAccount> userAccount) {
		Long quantity = validateQuantity(deficitQuantity, model);
		if (quantity == null) {
			return "forward:/products/items/stock";
		}
		return inventory.findById(id).map(inventoryItem -> {
			inventoryItem.decreaseQuantity(Quantity.of(quantity));
			inventory.save(inventoryItem);
			reorderManager.refillInventory();
			return "redirect:/products/items/stock?deficit";
		}).orElse("redirect:/products/items/stock");
	}

	@PostMapping("/products/items/stock/reorder/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String reorder(@PathVariable InventoryItemIdentifier id, String reorderQuantity, Model model, @LoggedIn Optional<UserAccount> userAccount) {
		Long quantity = validateQuantity(reorderQuantity, model);
		if (quantity == null) {
			return "forward:/products/items/stock";
		}
		return inventory.findById(id).map(inventoryItem -> {
			reorderManager.createReorder(inventoryItem, Quantity.of(quantity));
			return "redirect:/products/items/stock?reorder";
		}).orElse("redirect:/products/items/stock");
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

	// TODO: Find better solution, move to external class (and make static)
	private Long validateQuantity(String quantity, Model model) {
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
