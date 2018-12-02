package flowershop.accounting;

import flowershop.accounting.validation.TransactionDataTransferObject;
import org.javamoney.moneta.Money;
import org.salespointframework.order.ChargeLine;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.money.MonetaryAmount;
import java.util.Optional;


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
		Streamable<Order> transactions = orderManager.findBy(OrderStatus.PAID).and(orderManager.findBy(OrderStatus.COMPLETED));
		MonetaryAmount total = transactions.stream().map(Order::getTotalPrice).reduce(Money.parse("EUR 0"), MonetaryAmount::add);

		model.addAttribute("transactions", transactions);
		model.addAttribute("total", total);

		return "accounting";
	}

	@GetMapping("/accounting/transaction/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView add(Model model, TransactionDataTransferObject form) {

		return new ModelAndView("accounting_add", "form", form);
	}

	@PostMapping("/accounting/transaction/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView addProcess(@ModelAttribute("form") @Validated TransactionDataTransferObject form, BindingResult result, @LoggedIn Optional<UserAccount> loggedIn) {
		if (result.hasErrors()) {
			return new ModelAndView("accounting_add", "form", form);
		}

		Order order = new Order(loggedIn.get(), Cash.CASH);
		ChargeLine chargeLine = new ChargeLine(Money.of(Double.valueOf(form.getAmount()), "EUR"), form.getDescription());
		order.add(chargeLine);
		orderManager.payOrder(order);

		return new ModelAndView("redirect:/accounting");
	}
}
