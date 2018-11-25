package flowershop.products.controller;


import flowershop.products.FlowerShopItem;
import flowershop.products.FlowerShopItemCatalog;
import org.javamoney.moneta.Money;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class FlowerShopInventoryController {

	private final Inventory<InventoryItem> inventory;
	private final FlowerShopItemCatalog itemCatalog;

	FlowerShopInventoryController(Inventory<InventoryItem> inventory, FlowerShopItemCatalog itemCatalog) {
		this.itemCatalog = itemCatalog;
		this.inventory = inventory;
	}

	@GetMapping("/products/items/stock")
	public String inventory(Model model) {
		model.addAttribute("inventory", inventory.findAll());

		return "inventory";
	}

	// Do a Deficit
	@RequestMapping("/products/items/stock/deficit/{id}")
	public String deficit(@PathVariable InventoryItemIdentifier id, @RequestParam int deficit){
		inventory.findById(id).get().decreaseQuantity(Quantity.of(deficit));
		inventory.save(inventory.findById(id).get());

		return "redirect:/products/items/stock";
	}


	@GetMapping("/products/items/stock/add")
	public String add(Model model){
		return "inventory_add";
	}

	// Add a new InventoryItem
	@PostMapping("/products/items/stock/add")
	public String add(String name, int price, int amount, String description){
		FlowerShopItem item = new FlowerShopItem(name,Money.of(price,EURO), description);
		itemCatalog.save(item);
		inventory.save(new InventoryItem(item,Quantity.of(amount)));

		return "redirect:/products/items/stock";
	}
}