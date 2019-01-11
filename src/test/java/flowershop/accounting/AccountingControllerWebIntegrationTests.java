package flowershop.accounting;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static flowershop.order.Transaction.TransactionType.CUSTOM;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the {@link AccountingController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Cornelius Kummer
 */
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class AccountingControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired MockMvc mvc;
	@Autowired AccountingController accountingController;

	@Test
	void accountingTest() throws Exception {
		mvc.perform(get("/accounting").with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(view().name("accounting")).
				andExpect(model().attributeExists("transactions")).
				andExpect(model().attributeExists("reorders")).
				andExpect(model().attributeExists("total"));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void addTest() throws Exception {
		mvc.perform(get("/accounting/transaction/add")).
				andExpect(status().isOk()).
				andExpect(view().name("accounting_add")).
				andExpect(model().attributeExists("form"));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void addProcessEmptyTest() throws Exception {
		mvc.perform(post("/accounting/transaction/add")).
				andExpect(status().isOk()).
				andExpect(view().name("accounting_add"));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void addProcessInvalidTest() throws Exception {
		mvc.perform(post("/accounting/transaction/add").
				param("amount", "test").
				param("description", "")
		).
				andExpect(status().isOk()).
				andExpect(view().name("accounting_add"));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void addProcessPopulatedTest() throws Exception {
		mvc.perform(post("/accounting/transaction/add").
				param("amount", "123").
				param("description", "test")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/accounting"));

		assertTrue(accountingController.findAllTransactions().filter(transaction -> transaction.isType(CUSTOM)).
				get().anyMatch(transaction -> transaction.getDescription().equals("test")));
	}

}