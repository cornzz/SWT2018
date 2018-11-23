package flowershop.order;

import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;

import org.salespointframework.payment.Cash;
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

import java.util.Optional;

@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
class OrderController {

	@PostMapping("/checkout")
	String buy() {


			// (｡◕‿◕｡)
			// Mit completeOrder(…) wird der Warenkorb in die Order überführt, diese wird dann bezahlt und abgeschlossen.
			// Orders können nur abgeschlossen werden, wenn diese vorher bezahlt wurden.
			//Order order = new Order(account, Cash.CASH);

			//cart.addItemsTo(order);

			//orderManager.payOrder(order);
			//orderManager.completeOrder(order);

			//cart.clear();

			return "redirect:/order";
	}

}
