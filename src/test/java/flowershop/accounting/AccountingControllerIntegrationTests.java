package flowershop.accounting;

import flowershop.AbstractIntegrationTests;
import flowershop.order.Transaction;
import flowershop.user.UserManager;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.salespointframework.order.OrderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;

import static flowershop.order.Transaction.TransactionType.CUSTOM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.salespointframework.payment.Cash.CASH;

/**
 * Integration tests interacting with the {@link AccountingController} directly.
 *
 * @author Cornelius Kummer
 */
class AccountingControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired OrderManager<Transaction> transactionManager;
	@Autowired AccountingController accountingController;
	@Autowired UserManager userManager;

	@BeforeAll
	void setup2() {
		Transaction transaction = new Transaction(userManager.findByUsername("test").get().getUserAccount(), CASH, CUSTOM);
		transaction.setPrice(Money.of(100.00, "EUR"));
		transaction.setDescription("test");
		transactionManager.payOrder(transaction);
	}

	@Test
	@WithMockUser
	void findAllTransactionsTest() {
		Streamable<Transaction> transactions = accountingController.findAllTransactions();
		assertThat(transactions).isNotEmpty();
	}

}
