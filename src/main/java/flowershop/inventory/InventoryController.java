package flowershop.inventory;

import flowershop.catalog.Item;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class InventoryController {

	private final Inventory<InventoryItem> inventory;


	InventoryController(Inventory<InventoryItem> inventory) {
		this.inventory = inventory;
	}

	@GetMapping("/")

	String inventory(Model model) {

		model.addAttribute("inventory", inventory.findAll());

		return "inventory";
	}

	@RequestMapping("/deficit/{id}")
	public String deficit(@PathVariable InventoryItemIdentifier id, @RequestParam int deficit){
		inventory.findById(id).get().decreaseQuantity(Quantity.of(deficit));
		inventory.save(inventory.findById(id).get());


		return "redirect:/";
	}

}
