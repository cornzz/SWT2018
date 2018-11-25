package flowershop.inventory;

import flowershop.catalog.Item;
import flowershop.catalog.ItemCatalog;
import org.javamoney.moneta.Money;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class InventoryController {

	private final Inventory<InventoryItem> inventory;
	private final ItemCatalog itemCatalog;

	InventoryController(Inventory<InventoryItem> inventory, ItemCatalog itemCatalog) {
		this.itemCatalog = itemCatalog;
		this.inventory = inventory;
	}

	@GetMapping("/")
	public String inventory(Model model) {
		model.addAttribute("inventory", inventory.findAll());

		return "inventory";
	}

	// Do a Deficit
	@RequestMapping("/deficit/{id}")
	public String deficit(@PathVariable InventoryItemIdentifier id, @RequestParam int deficit){
		inventory.findById(id).get().decreaseQuantity(Quantity.of(deficit));
		inventory.save(inventory.findById(id).get());

		return "redirect:/";
	}


	@GetMapping("/add")
	public String add(Model model){
		return "inventory_add";
	}

	// Add a new Inventory Item
	@PostMapping("/add")
	public String add(String name, int price, int amount){
		Item item = new Item(name,Money.of(price,EURO), Item.ItemType.BLUME);
		itemCatalog.save(item);
		inventory.save(new InventoryItem(item,Quantity.of(amount)));

		return "redirect:/";
	}
}
