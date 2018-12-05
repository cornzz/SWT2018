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
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

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
	private final Catalog<Product> catalog;

	OrderController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory, Catalog<Product> catalog) {

		Assert.notNull(orderManager, "OrderManager must not be null!");
		this.orderManager = orderManager;
		this.inventory = inventory;
		this.catalog = catalog;
	}

	@GetMapping("/order")
	String basket() {
		return "order_confirm";
	}


	@PostMapping("/completeorder")
	String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount, Model model) {
		return userAccount.map(account -> {
			if (!sufficientStock(cart)) {
				model.addAttribute("message", "Es gibt nicht genug davon in Inventory.");
				return "/cart";
			}
			Iterator it = cart.iterator();//removes products from inventory
			while(it.hasNext()) {
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
			return "redirect:/";
		}).orElse("redirect:/");
	}

	public boolean sufficientStock(Cart cart) {
		Map<FlowerShopItem, Quantity> flowerShopItems = new HashMap<>();
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

		for (FlowerShopItem item : flowerShopItems.keySet()) {
			if (!inventory.findByProductIdentifier(item.getId()).get().hasSufficientQuantity(flowerShopItems.get(item))) {
				return false;
			}
		}

		return true;
	}


	@GetMapping("/orders")
	@PreAuthorize("isAuthenticated()")
	String orders(Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		UserAccount user = loggedIn.get();
		Streamable<Order> orders;
		if (user.hasRole(Role.of("ROLE_BOSS"))){
			model.addAttribute("statusOpen", OrderStatus.OPEN);
			model.addAttribute("statusPaid", OrderStatus.PAID);
			model.addAttribute("statusCompleted", OrderStatus.COMPLETED);
			model.addAttribute("statusCancelled", OrderStatus.CANCELLED);
			Streamable<Order> completeOrders = orderManager.findBy(OrderStatus.COMPLETED);
			Streamable<Order> paidOrders = orderManager.findBy(OrderStatus.PAID);
			Streamable<Order> openOrders = orderManager.findBy(OrderStatus.OPEN);
			Streamable<Order> cancelledOrders = orderManager.findBy(OrderStatus.CANCELLED);
			orders = completeOrders.and(paidOrders).and(openOrders).and(cancelledOrders);
		} else {
			orders = orderManager.findBy(user);
		}

		model.addAttribute("orders", orders);

		return "orders";
	}

	@GetMapping("/orders/update")
	String updateOrderStatus(@RequestParam(value = "id") Order order, Model model){
		if (order.getOrderStatus().equals(OrderStatus.OPEN)) {//open->paid
			orderManager.payOrder(order);
			return "redirect:/orders";
		}
		if (order.getOrderStatus().equals(OrderStatus.PAID)) {//paid->complete

			//adding fake InventoryItems
			Streamable<OrderLine> orderLines = order.getOrderLines();
			Iterator<OrderLine> iter = orderLines.iterator();
			while(iter.hasNext()){
				OrderLine orderLine = iter.next();
				Quantity quantity = orderLine.getQuantity();
				Product product = catalog.findById(orderLine.getProductIdentifier()).get();
				inventory.save(new InventoryItem(product,quantity));
			}

			orderManager.completeOrder(order);//removing fake InventoryItems

			//clearing
			Iterator<InventoryItem> it = inventory.findAll().iterator();
			while(it.hasNext()){
				InventoryItem inventoryItem = it.next();
				if (!(inventoryItem.getProduct() instanceof FlowerShopItem))
					inventory.delete(inventoryItem);
			}

			return "redirect:/orders";
		}
		return "redirect:/orders";
	}


}
