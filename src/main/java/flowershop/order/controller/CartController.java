package flowershop.order.controller;


import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class CartController {

	private Cart cart;
	private Product productToAdd;

	@ModelAttribute("cart")
	Cart initializeCart() {
		Cart cart=new Cart();
		this.cart=cart;
		return cart;
	}

	@GetMapping("/cart")
	String basket() {
		return "cart";
	}

	@PostMapping("/checkout")
	String buy() {
		return "redirect:/order";
	}

	@GetMapping("/cart/add/{product}")
	String markProductToAdd(@PathVariable Product product) {
		this.productToAdd=product;
		return "cart_add";
	}

	@PostMapping("/cart/add")
	public String addToCart(String quantity){//quantity enthalt Ziffern
		try{
			if (quantity.equals("0")) {
				throw new Exception();
			}
			int intQuantity = Integer.valueOf(quantity);
			cart.addOrUpdateItem(productToAdd,intQuantity);
			return "redirect:/cart";}
		catch (Exception e){
			return "redirect:/cart/add/"+productToAdd.getId();
		}
	}
}
