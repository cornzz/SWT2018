package flowershop.products.controller;


import flowershop.products.FlowerShopItem;
import flowershop.products.FlowerShopItemCatalog;
import flowershop.products.form.AddFlowerShopItemForm;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FlowerShopInventoryController {

	private final Inventory<InventoryItem> inventory;
	private final FlowerShopItemCatalog itemCatalog;

	FlowerShopInventoryController(Inventory<InventoryItem> inventory, FlowerShopItemCatalog itemCatalog) {
		this.itemCatalog = itemCatalog;
		this.inventory = inventory;
	}

	@RequestMapping("/items")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String inventory(Model model) {
		model.addAttribute("inventory", inventory.findAll());

		return "inventory";
	}

	@GetMapping("/items/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public ModelAndView add(Model model, AddFlowerShopItemForm form) {

		return new ModelAndView("inventory_add", "form", form);
	}

	@PostMapping("/items/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public ModelAndView add(@ModelAttribute("form") @Validated AddFlowerShopItemForm form, BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("inventory_add", "form", form);
		}
		FlowerShopItem item = form.convertToObject();
		itemCatalog.save(item);
		inventory.save(new InventoryItem(item, Quantity.of(Integer.valueOf(form.getAmount()))));

		return new ModelAndView("redirect:/items");
	}

}
