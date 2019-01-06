package flowershop.order;

import flowershop.products.CompoundFlowerShopProduct;
import flowershop.products.FlowerShopItem;
import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.StreamSupport;

import static flowershop.order.Transaction.TransactionType.ORDER;
import static org.salespointframework.order.OrderStatus.OPEN;
import static org.salespointframework.order.OrderStatus.PAID;
import static org.salespointframework.payment.Cash.CASH;


/**
 * A Spring MVC controller to manage the ordering process.
 *
 * @author Tomasz Ludyga
 * @author Cornelius Kummer
 */
@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class OrderController {

	private final OrderManager<Transaction> transactionManager;
	private final Inventory<InventoryItem> inventory;
	private final Catalog<Product> catalog;
	private final ReorderManager reorderManager;

	OrderController(OrderManager<Transaction> transactionManager, Inventory<InventoryItem> inventory, Catalog<Product> catalog, ReorderManager reorderManager) {
		this.transactionManager = transactionManager;
		this.inventory = inventory;
		this.catalog = catalog;
		this.reorderManager = reorderManager;
	}

	@PostMapping("/completeorder")
	String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount, Model model) {
		return userAccount.map(account -> {
			if (!sufficientStock(cart)) {
				model.addAttribute("message", "cart.add.notenough");
				return "/cart";
			}

			cart.forEach(cartItem -> {
				CompoundFlowerShopProduct product = (CompoundFlowerShopProduct) cartItem.getProduct();
				Quantity productQuantity = cartItem.getQuantity();
				product.getFlowerShopItemsWithQuantities().forEach(((flowerShopItem, itemQuantity) -> {
					Quantity productItemQuantity = multiplyQuantities(productQuantity, itemQuantity);
					inventory.findByProduct(flowerShopItem).ifPresent(inventoryItem -> inventoryItem.decreaseQuantity(productItemQuantity));
				}));
			});

			Transaction order = new Transaction(account, CASH, ORDER);
			cart.addItemsTo(order);
			cart.clear();
			transactionManager.save(order);
			reorderManager.refillInventory();
			return "redirect:/order/" + order.getId() + "?success";
		}).orElse("redirect:/");
	}


	@GetMapping("/orders")
	@PreAuthorize("isAuthenticated()")
	ModelAndView orders(Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		UserAccount user = loggedIn.get();
		Streamable<Transaction> orders;

		if (user.hasRole(Role.of("ROLE_BOSS"))) {
			model.addAttribute("statusOpen", OPEN);
			model.addAttribute("statusPaid", PAID);
			orders = findAllOrders();
		} else {
			orders = transactionManager.findBy(user);
		}

		return new ModelAndView("orders", "orders", orders);
	}

	@GetMapping("/order/update/{id}")
	String updateOrderStatus(@PathVariable(name = "id") Optional<Transaction> orderOptional) {
		return orderOptional.map(order -> {
			if (order.getOrderStatus().equals(OPEN)) {// open->paid
				transactionManager.payOrder(order);
			} else if (order.getOrderStatus().equals(PAID)) {// paid->complete

				// Add fake InventoryItems
				order.getOrderLines().forEach(orderLine -> {
					catalog.findById(orderLine.getProductIdentifier())
							.map(product -> inventory.save(new InventoryItem(product, orderLine.getQuantity())))
							.orElseThrow(NoSuchElementException::new);
				});

				transactionManager.completeOrder(order);

				// Remove fake InventoryItems
				Streamable.of(inventory.findAll()).
						filter(inventoryItem -> !(inventoryItem.getProduct() instanceof FlowerShopItem)).
						forEach(inventory::delete);
			}
			return "redirect:/orders?update";
		}).orElse("redirect:/orders");
	}

	@GetMapping("/order/{id}")
	@PreAuthorize("isAuthenticated()")
	String order(Model model, @PathVariable(name = "id") Optional<Transaction> orderOptional, @LoggedIn Optional<UserAccount> loggedIn) {
		orderOptional.ifPresent(order -> {
			loggedIn.ifPresent(user -> {
				if (user.hasRole(Role.of("ROLE_BOSS")) || order.getUserAccount().equals(user)) {
					model.addAttribute("order", order);
				}
			});
		});

		return "order";
	}

	boolean sufficientStock(Cart cart) {
		Map<FlowerShopItem, Quantity> requiredItems = new HashMap<>(); // Map with all FlowerShopItems required for given cart
		cart.forEach(cartItem -> {
			((CompoundFlowerShopProduct) cartItem.getProduct()).getFlowerShopItemsWithQuantities().forEach((item, quantity) -> {
				if (!requiredItems.containsKey(item)) {
					requiredItems.put(item, multiplyQuantities(cartItem.getQuantity(), quantity));
				} else {
					requiredItems.put(item, requiredItems.get(item).add(multiplyQuantities(cartItem.getQuantity(), quantity)));
				}
			});
		});

		// Check if there is sufficient stock for every required FlowerShopItem
		return requiredItems.keySet().stream()
				.allMatch(item -> inventory.findByProductIdentifier(item.getId()).get().hasSufficientQuantity(requiredItems.get(item)));
	}

	Streamable<Transaction> findAllOrders() {
		return Arrays.stream(OrderStatus.values())
				.map(transactionManager::findBy)
				.reduce(Streamable.empty(), Streamable::and)
				.filter(transaction -> transaction.getType().equals(ORDER));
	}

	Quantity multiplyQuantities(Quantity a, Quantity b) {
		return Quantity.of(a.getAmount().multiply(b.getAmount()).intValue());
	}

}