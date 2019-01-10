package flowershop.accounting;

import flowershop.accounting.form.TransactionDataTransferObject;
import flowershop.order.SubTransaction;
import flowershop.order.Transaction;
import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.money.MonetaryAmount;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static flowershop.order.SubTransaction.SubTransactionType.REORDER;
import static flowershop.order.Transaction.TransactionType.COLLECTION;
import static flowershop.order.Transaction.TransactionType.CUSTOM;
import static org.salespointframework.core.Currencies.ZERO_EURO;
import static org.salespointframework.order.OrderStatus.OPEN;
import static org.salespointframework.payment.Cash.CASH;


/**
 * Displays {@link Transaction}s relevant for accounting.
 *
 * @author Cornelius Kummer
 */
@Controller
public class AccountingController {

	private final OrderManager<Transaction> transactionManager;

	public AccountingController(OrderManager<Transaction> transactionManager) {
		this.transactionManager = transactionManager;
	}

	@GetMapping("/accounting")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	String accounting(Model model) {
		Streamable<Transaction> transactions = findAllTransactions().filter(transaction -> transaction.getType() != COLLECTION);
		Streamable<SubTransaction> subTransactions = findAllTransactions().filter(transaction -> transaction.getType() == COLLECTION).
				map(Transaction::getSubTransactions).get().flatMap(List::stream).
				filter(subTransaction -> subTransaction.isType(REORDER)).
				map(Streamable::of).reduce(Streamable.empty(), Streamable::and);
		MonetaryAmount total = transactions.stream().map(Order::getTotalPrice).reduce(ZERO_EURO, MonetaryAmount::add).
				add(subTransactions.get().map(SubTransaction::getPrice).reduce(ZERO_EURO, MonetaryAmount::add).negate());

		model.addAttribute("transactions", transactions);
		model.addAttribute("reorders", subTransactions);
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

		loggedIn.ifPresent(userAccount -> {
			Transaction transaction = new Transaction(userAccount, CASH, CUSTOM);
			transaction.setPrice(Money.of(Double.valueOf(form.getAmount()), "EUR"));
			transaction.setDescription(form.getDescription());
			transactionManager.payOrder(transaction);
		});

		return new ModelAndView("redirect:/accounting");
	}

	Streamable<Transaction> findAllTransactions() {
		return Arrays.stream(OrderStatus.values())
				.filter(status -> status != OPEN)
				.map(transactionManager::findBy)
				.reduce(Streamable.empty(), Streamable::and);
	}
}
