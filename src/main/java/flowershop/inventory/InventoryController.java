package flowershop.inventory;

import flowershop.catalog.Item;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

	@PostMapping("/deficit/{id}")
	public String deficit(@PathVariable InventoryItemIdentifier id){
		inventory.findById(id).get().decreaseQuantity(Quantity.of(1));
		inventory.save(inventory.findById(id).get());


		return "redirect:/";
	}

}
