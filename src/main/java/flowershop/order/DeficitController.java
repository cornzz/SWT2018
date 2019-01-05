package flowershop.order;


import org.salespointframework.order.OrderManager;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.money.MonetaryAmount;
import java.util.List;

import static flowershop.order.SubTransaction.SubTransactionType.DEFICIT;
import static flowershop.order.Transaction.TransactionType.COLLECTION;
import static org.salespointframework.core.Currencies.ZERO_EURO;
import static org.salespointframework.order.OrderStatus.PAID;


@Controller
public class DeficitController {

	private final OrderManager<Transaction> transactionManager;

	DeficitController(OrderManager<Transaction> transactionManager) {
		this.transactionManager = transactionManager;
	}

	@GetMapping("/products/deficits")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String deficit(Model model) {
		Streamable<SubTransaction> subTransactions = transactionManager.findBy(PAID).
				filter(transaction -> transaction.getType() == COLLECTION).
				map(Transaction::getSubTransactions).get().flatMap(List::stream).
				filter(subTransaction -> subTransaction.getType().equals(DEFICIT)).
				filter(subTransaction -> subTransaction.getStatus().equals(true)).
				map(Streamable::of).reduce(Streamable.empty(), Streamable::and);
		MonetaryAmount total = subTransactions.get().map(SubTransaction::getPrice).reduce(ZERO_EURO, MonetaryAmount::add);

		model.addAttribute("deficits", subTransactions);
		model.addAttribute("total", total);

		return "deficits";
	}
}
