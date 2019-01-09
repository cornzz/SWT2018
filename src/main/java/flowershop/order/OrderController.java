package flowershop.order;

import flowershop.products.CompoundFlowerShopProduct;
import flowershop.products.FlowerShopItem;
import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

	/**
	 * Tries to check out a users {@link Cart}. If successful, a new Transaction is created and the inventory is refilled if necessary.
	 *
	 * @param cart        will never be {@literal null}.
	 * @param userAccount will never be {@literal null}.
	 * @param model       will never be {@literal null}.
	 * @return the view name.
	 */
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


	/**
	 * Displays all orders the current user has made or, if the user is admin, displays all {@link Transaction}s of
	 * {@link flowershop.order.Transaction.TransactionType} <code>ORDER</code> in the system.
	 *
	 * @param model    will never be {@literal null}.
	 * @param loggedIn will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/orders")
	@PreAuthorize("isAuthenticated()")
	ModelAndView orders(Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		return loggedIn.map(user -> {
			Streamable<Transaction> orders;

			if (user.hasRole(Role.of("ROLE_BOSS"))) {
				model.addAttribute("statusOpen", OPEN);
				model.addAttribute("statusPaid", PAID);
				orders = findAllOrders();
			} else {
				orders = transactionManager.findBy(user);
			}
			return new ModelAndView("orders", "orders", orders);
		}).orElse(new ModelAndView("orders"));

	}

	/**
	 * Lets the admin update the status of a users order.
	 *
	 * @param orderOptional will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/order/update/{id}")
	String updateOrderStatus(@PathVariable(name = "id") Optional<Transaction> orderOptional) {
		return orderOptional.map(order -> {
			if (order.getOrderStatus().equals(OPEN)) {// open->paid
				transactionManager.payOrder(order);
			} else if (order.getOrderStatus().equals(PAID)) {// paid->complete

				// Add fake InventoryItems
				order.getOrderLines().forEach(orderLine -> {
					Product product = catalog.findById(orderLine.getProductIdentifier()).get();
					inventory.save(new InventoryItem(product, orderLine.getQuantity()));
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

	/**
	 * Displays information to a specific order.
	 *
	 * @param model         will never be {@literal null}.
	 * @param orderOptional will never be {@literal null}.
	 * @param loggedIn      will never be {@literal null}.
	 * @return the view name and order information, if the user is authorized to see those.
	 */
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

	/**
	 * Checks whether there is enough stock in the {@link Inventory} for all {@link org.salespointframework.order.CartItem}s
	 * in the given {@link Cart}.
	 *
	 * @param cart must not be {@literal null}.
	 * @return <code>true</code> if there is enough stock, <code>false</code> otherwise.
	 */
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

	/**
	 * @return {@link Streamable} containing all {@link Transaction}s of {@link flowershop.order.Transaction.TransactionType}
	 * <code>ORDER</code>.
	 */
	Streamable<Transaction> findAllOrders() {
		return Arrays.stream(OrderStatus.values())
				.map(transactionManager::findBy)
				.reduce(Streamable.empty(), Streamable::and)
				.filter(transaction -> transaction.isType(ORDER));
	}

	/**
	 * Helper method to multiply two {@link Quantity} Objects, since there is no native method for that.
	 *
	 * @param a first {@link Quantity} to be multiplied.
	 * @param b second {@link Quantity} to be multiplied.
	 * @return product of the operation.
	 */
	Quantity multiplyQuantities(Quantity a, Quantity b) {
		return Quantity.of(a.getAmount().multiply(b.getAmount()).intValue());
	}

}