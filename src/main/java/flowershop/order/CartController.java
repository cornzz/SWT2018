package flowershop.order;


import flowershop.products.CompoundFlowerShopProduct;
import flowershop.products.FlowerShopItem;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.order.Cart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class CartController {

	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@GetMapping("/cart")
	String basket() {
		return "cart";
	}

	@PostMapping("/checkout")
	String buy() {
		return "redirect:/order";
	}

	@RequestMapping("/cart/add/{product}")
	String markProductToAdd(@PathVariable Product product) {
		return "add_to_cart";
	}

	@PostMapping("/cart/add")
	public String addToCart(@RequestParam("pid") Product product, @RequestParam("quantity") int quantity, @ModelAttribute Cart cart, Model model) {
		if (quantity <= 0) {
			model.addAttribute("message", "Quantity has to be positive");
			return "forward:/cart/add/" + product.getId();
		}
		//if (product instanceof CompoundFlowerShopProduct){
		//	ArrayList<FlowerShopItem> items = (ArrayList<FlowerShopItem>) ((CompoundFlowerShopProduct) product).getFlowerShopItems();
		//}
		cart.addOrUpdateItem(product, quantity);
		return "redirect:/cart";
	}
}
