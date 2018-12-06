package flowershop.order;


import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class CartController {

	private final OrderController orderController;

	public CartController(OrderController orderController) {
		this.orderController = orderController;
	}

	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@RequestMapping("/cart")
	String basket() {
		return "cart";
	}

	@RequestMapping("/cart/add/{product}")
	String markProductToAdd(@PathVariable Product product) {
		return "add_to_cart";
	}

	@PostMapping("/cart/add")
	public String addToCart(@RequestParam("pid") Product product, @RequestParam("quantity") String qty, @ModelAttribute Cart cart, Model model) {
		Integer quantity = validateQuantity(qty, model);
		if (quantity == null)
			return "forward:/cart/add/" + product.getId();

		CartItem newCartItem = cart.addOrUpdateItem(product, quantity);
		if (!orderController.sufficientStock(cart)){
			model.addAttribute("message", "Es gibt nicht genug davon in Inventory.");
			cart.addOrUpdateItem(product, -quantity);
			if (newCartItem.getQuantity().subtract(Quantity.of(quantity)).isZeroOrNegative()) {
				cart.removeItem(newCartItem.getId());
			}
			return "forward:/cart/add/" + product.getId();
		}
		return "redirect:/cart";
	}

	@PostMapping("/cart/edit")
	public String editQuantity(@RequestParam("id") String itemId, @RequestParam("pid") Product product, @RequestParam("quantity") String qty, @ModelAttribute Cart cart, Model model) {
		Integer quantity = validateQuantity(qty, model);
		if (quantity == null) {
			return "forward:/cart";
		}

		Quantity currentItemQuantity = cart.getItem(itemId).get().getQuantity();
		cart.addOrUpdateItem(product, Quantity.of(quantity).subtract(currentItemQuantity));
		if (!orderController.sufficientStock(cart)) {
			model.addAttribute("message", "Es gibt nicht genug davon in Inventory.");
			cart.addOrUpdateItem(product, currentItemQuantity.subtract(Quantity.of(quantity)));
			return "forward:/cart";
		}

		return "cart";
	}

	@GetMapping("/cart/remove")
	String removeFromCart(@ModelAttribute Cart cart, @RequestParam(value = "id") String itemId){
		cart.removeItem(itemId);
		return "cart";
	}

	@PostMapping("/checkout")
	String buy() {
		return "order_confirm";
	}

	// TODO: move to external class (and make static)
	private Integer validateQuantity(String quantity, Model model) {
		int qty;

		try {
			qty = Integer.valueOf(quantity);
		} catch (NumberFormatException e) {
			model.addAttribute("message", "Invalide Anzahl");
			return null;
		}
		if (qty <= 0) {
			model.addAttribute("message", "Anzahl sollte positiv sein.");
			return null;
		}
		return qty;
	}
}

