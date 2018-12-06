package flowershop.order;

import flowershop.products.CompoundFlowerShopProduct;
import flowershop.products.FlowerShopItem;
import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.StreamSupport;


@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class OrderController {

	private final OrderManager<Order> orderManager;
	private final Inventory<InventoryItem> inventory;
	private final Catalog<Product> catalog;

	OrderController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory, Catalog<Product> catalog) {
		this.orderManager = orderManager;
		this.inventory = inventory;
		this.catalog = catalog;
	}

	@PostMapping("/completeorder")
	String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount, Model model) {
		return userAccount.map(account -> {
			if (!sufficientStock(cart)) {
				model.addAttribute("message", "Es gibt nicht genug davon in Inventory.");
				return "/cart";
			}

			Iterator it = cart.iterator(); // Removes products from inventory
			while (it.hasNext()) {
				CartItem cartItem = (CartItem) it.next();
				Product product = cartItem.getProduct();
				Quantity quantity = cartItem.getQuantity();
				if (product instanceof FlowerShopItem) {
					inventory.findByProduct(product).get().decreaseQuantity(quantity);
				} else {
					Iterable<FlowerShopItem> products = ((CompoundFlowerShopProduct) product).getFlowerShopItems();
					Iterator<FlowerShopItem> iter = products.iterator();
					while (iter.hasNext()) {
						inventory.findByProduct(iter.next()).get().decreaseQuantity(quantity);
					}
				}
			}

			Order order = new Order(account, Cash.CASH);
			cart.addItemsTo(order);
			cart.clear();
			orderManager.save(order);

			return "redirect:/order/" + order.getId() + "?success";
		}).orElse("redirect:/");
	}

	@GetMapping("/orders")
	@PreAuthorize("isAuthenticated()")
	String orders(Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		UserAccount user = loggedIn.get();
		Streamable<Order> orders;

		if (user.hasRole(Role.of("ROLE_BOSS"))) {
			model.addAttribute("statusOpen", OrderStatus.OPEN);
			model.addAttribute("statusPaid", OrderStatus.PAID);
			orders = orderManager.findBy(OrderStatus.OPEN)
					.and(orderManager.findBy(OrderStatus.PAID))
					.and(orderManager.findBy(OrderStatus.COMPLETED))
					.and(orderManager.findBy(OrderStatus.CANCELLED));
		} else {
			orders = orderManager.findBy(user);
		}

		model.addAttribute("orders", orders);

		return "orders";
	}

	@GetMapping("/orders/update")
	String updateOrderStatus(@RequestParam(value = "id") Order order, Model model) {
		if (order.getOrderStatus().equals(OrderStatus.OPEN)) {// open->paid
			orderManager.payOrder(order);
		} else if (order.getOrderStatus().equals(OrderStatus.PAID)) {// paid->complete

			// Add fake InventoryItems
			order.getOrderLines().forEach(orderLine -> {
				catalog.findById(orderLine.getProductIdentifier())
						.map(product -> inventory.save(new InventoryItem(product, orderLine.getQuantity())))
						.orElseThrow(NoSuchElementException::new);
			});

			orderManager.completeOrder(order);

			// Remove fake InventoryItems
			StreamSupport.stream(inventory.findAll().spliterator(), false)
					.filter(inventoryItem -> !(inventoryItem.getProduct() instanceof FlowerShopItem))
					.forEach(inventory::delete);
		}

		return "redirect:/orders";
	}

	@GetMapping("/order/{id}")
	@PreAuthorize("isAuthenticated()")
	String order(Model model, @PathVariable(name = "id") Optional<Order> order, @LoggedIn Optional<UserAccount> loggedIn) {
		UserAccount user = loggedIn.get();
		if (order.isPresent() && (user.hasRole(Role.of("ROLE_BOSS")) || order.get().getUserAccount().equals(user))) {
			model.addAttribute("order", order.get());
		}

		return "order";
	}

	boolean sufficientStock(Cart cart) {
		Map<FlowerShopItem, Quantity> flowerShopItems = new HashMap<>(); // Map with all FlowerShopItems required for given cart
		for (CartItem cartItem : cart) {
			Product product = cartItem.getProduct();
			Quantity itemQuantity = cartItem.getQuantity();

			if (product instanceof CompoundFlowerShopProduct) {
				CompoundFlowerShopProduct compoundProduct = (CompoundFlowerShopProduct) product;
				for (FlowerShopItem productItem : compoundProduct.getFlowerShopItems()) {
					if (!flowerShopItems.containsKey(productItem)) {
						flowerShopItems.put(productItem, itemQuantity);
					} else {
						flowerShopItems.put(productItem, flowerShopItems.get(productItem).add(itemQuantity));
					}
				}
			} else if (product instanceof FlowerShopItem) {
				FlowerShopItem item = (FlowerShopItem) product;
				if (!flowerShopItems.containsKey(item)) {
					flowerShopItems.put(item, itemQuantity);
				} else {
					flowerShopItems.put(item, flowerShopItems.get(item).add(itemQuantity));
				}
			}
		}

		// Check if there is sufficient stock for every required FlowerShopItem
		return flowerShopItems.keySet().stream()
				.allMatch(item -> inventory.findByProductIdentifier(item.getId()).get().hasSufficientQuantity(flowerShopItems.get(item)));
	}

}
