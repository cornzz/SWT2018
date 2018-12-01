package flowershop.accounting;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.money.MonetaryAmount;


@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class AccountingController {

	private final OrderManager<Order> orderManager;

	public AccountingController(OrderManager<Order> orderManager) {
		this.orderManager = orderManager;
	}


	@GetMapping("/accounting")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	String orders(Model model) {
		Streamable<Order> transactions = orderManager.findBy(OrderStatus.COMPLETED);
		MonetaryAmount total = transactions.stream().map(Order::getTotalPrice).reduce(Money.parse("EUR 0"), MonetaryAmount::add);

		model.addAttribute("transactions", transactions);
		model.addAttribute("total", total);

		return "accounting";
	}
}
