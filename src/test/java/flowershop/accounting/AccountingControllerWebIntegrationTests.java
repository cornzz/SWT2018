package flowershop.accounting;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
	void addTest() throws Exception {
		mvc.perform(get("/accounting/transaction/add").with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(view().name("accounting_add")).
				andExpect(model().attributeExists("form"));
	}

	@Test
	@WithMockUser
	void addProcessEmptyTest() throws Exception {
		mvc.perform(login("test", "test"));
		mvc.perform(post("/accounting/transaction/add")).
				andExpect(status().isOk()).
				andExpect(view().name("accounting_add"));
	}

	@Test
	@WithMockUser
	void addProcessPopulatedTest() throws Exception {
		mvc.perform(login("test", "test"));
		mvc.perform(post("/accounting/transaction/add").
				param("amount", "123").
				param("description", "test")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/accounting"));
	}

}