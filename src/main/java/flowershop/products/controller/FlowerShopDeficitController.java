package flowershop.products.controller;


import flowershop.order.SubTransaction;
import flowershop.order.Transaction;
import org.salespointframework.order.OrderManager;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

import static flowershop.order.Transaction.TransactionType.COLLECTION;
import static org.salespointframework.order.OrderStatus.PAID;


@Controller
public class FlowerShopDeficitController {

	private final OrderManager<Transaction> transactionManager;

	FlowerShopDeficitController(OrderManager<Transaction> transactionManager) {
		this.transactionManager = transactionManager;
	}

	@GetMapping("/products/deficit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String deficit(Model model) {
		Streamable<Transaction> transactions = transactionManager.findBy(PAID).filter(transaction -> transaction.getType() == COLLECTION);
		List<SubTransaction> subTransactionList = new ArrayList<>();
		transactions.forEach(transaction -> transaction.getSubTransactions().forEach(subTransaction -> {
			if (subTransaction.getType().equals(SubTransaction.SubTransactionType.DEFICIT) && subTransaction.getStatus().equals(true)) {
				subTransactionList.add(subTransaction);
			}
		}));
		Streamable<SubTransaction> subTransactions = Streamable.of(subTransactionList);
		model.addAttribute("deficits", subTransactions);

		return "deficit";
	}
}
