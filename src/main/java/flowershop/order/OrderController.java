package flowershop.order;

import flowershop.products.CompoundFlowerShopProduct;
import flowershop.products.FlowerShopItem;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;


@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class OrderController {

	private final OrderManager<Order> orderManager;
	private final Inventory<InventoryItem> inventory;

	OrderController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory) {

		Assert.notNull(orderManager, "OrderManager must not be null!");
		this.orderManager = orderManager;
		this.inventory = inventory;
	}

	@GetMapping("/order")
	String basket() {
		return "order_confirm";
	}

	@PostMapping("/completeorder")
	String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount, Model model){
		return userAccount.map(account -> {

			if (!sufficientStock(cart)) {
				model.addAttribute("message", "Es gibt nicht genug davon in Inventory.");
				return "/cart";
			}

			Order order = new Order(account, Cash.CASH);
			cart.addItemsTo(order);
			orderManager.payOrder(order);

			// Temporarily add the CompoundFlowerShopProducts to inventory, in order to execute completeOrder()
			cart.iterator().forEachRemaining(cartItem -> {
				CompoundFlowerShopProduct product = (CompoundFlowerShopProduct) cartItem.getProduct();
				inventory.save(new InventoryItem(product, cartItem.getQuantity()));
				product.getFlowerShopItems().forEach(flowerShopItem -> inventory.findByProductIdentifier(flowerShopItem.getId())
						.ifPresent(inventoryItem -> inventoryItem.decreaseQuantity(cartItem.getQuantity())));
			});

			try {
				orderManager.completeOrder(order);
			} catch (OrderCompletionFailure e) {
				e.printStackTrace();
				for (OrderLine l : order.getOrderLines()) {
					System.out.println("Orderline: " + l.toString());
				}
				System.out.println("OrderStatus: " + order.getOrderStatus());
			}

			// Remove CompoundFlowerShopProducts from inventory
			cart.iterator().forEachRemaining(cartItem -> {
				CompoundFlowerShopProduct product = (CompoundFlowerShopProduct) cartItem.getProduct();
				inventory.deleteById(inventory.findByProduct(product).get().getId());
			});

			cart.clear();

			return "redirect:/";
		}).orElse("redirect:/");
	}

	public boolean sufficientStock(Cart cart) {
		Map<FlowerShopItem, Quantity> flowerShopItems = new HashMap<>();
		for (Iterator<CartItem> cartItems = cart.iterator(); cartItems.hasNext();) {
			CartItem cartItem = cartItems.next();
			CompoundFlowerShopProduct product = (CompoundFlowerShopProduct) cartItem.getProduct();

			for (Iterator<FlowerShopItem> productItems = product.getFlowerShopItems().iterator(); productItems.hasNext();) {
				FlowerShopItem productItem = productItems.next();
				Quantity itemQuantity = cartItem.getQuantity();
				if (!flowerShopItems.containsKey(productItem)) {
					flowerShopItems.put(productItem, itemQuantity);
				} else {
					flowerShopItems.put(productItem, flowerShopItems.get(productItem).add(itemQuantity));
				}
			}
		}

		for (FlowerShopItem item : flowerShopItems.keySet()) {
			if (!inventory.findByProductIdentifier(item.getId()).get().hasSufficientQuantity(flowerShopItems.get(item))) {
				return false;
			}
		}

		return true;
	}

	@GetMapping("/orders")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	String orders(Model model) {

		model.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));

		return "orders";
	}

}
