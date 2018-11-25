package flowershop.inventory;

import flowershop.catalog.FlowerShopItem;
import flowershop.catalog.ItemCatalog;
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
public class InventoryController {

	private final Inventory<InventoryItem> inventory;
	private final ItemCatalog itemCatalog;

	InventoryController(Inventory<InventoryItem> inventory, ItemCatalog itemCatalog) {
		this.itemCatalog = itemCatalog;
		this.inventory = inventory;
	}

	// Just for testing
	@GetMapping("/")
	public String index() {
		return "redirect:/products/services";
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

		return "redirect:/";
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

		return "redirect:/";
	}
}
