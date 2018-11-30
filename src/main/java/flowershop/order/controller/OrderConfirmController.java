package flowershop.order.controller;

import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Optional;


@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class OrderConfirmController {

	private final OrderManager<Order> orderManager;

	OrderConfirmController(OrderManager<Order> orderManager) {

		Assert.notNull(orderManager, "OrderManager must not be null!");
		this.orderManager = orderManager;
	}

	@GetMapping("/order")
	String basket() {
		return "order_confirm";
	}

	@PostMapping("/orderdone")
	String bought(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount){
		return userAccount.map(account -> {

			// (｡◕‿◕｡)
			// Mit completeOrder(…) wird der Warenkorb in die Order überführt, diese wird dann bezahlt und abgeschlossen.
			// Orders können nur abgeschlossen werden, wenn diese vorher bezahlt wurden.
			Order order = new Order(account, Cash.CASH);

			cart.addItemsTo(order);

			orderManager.payOrder(order);
			//orderManager.completeOrder(order); - wirft ein Error, TODO: fixen

			cart.clear();

			return "redirect:/";
		}).orElse("redirect:/");
	}

//	@GetMapping("/orders")
//	@PreAuthorize("hasRole('ROLE_BOSS')")
//	String orders(Model model) {
//
//		model.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));
//
//		return "orders";
//	}

}
