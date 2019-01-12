package flowershop.order;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.salespointframework.order.OrderStatus.PAID;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the {@link ReorderController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Cornelius Kummer
 * @author Friedrich Bethke
 */

@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class ReorderControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	Inventory<InventoryItem> inventory;
	@Autowired
	OrderManager<Transaction> transactionManager;

	@Test
	void preventsPublicAccessForDeficitView() throws Exception {
		mvc.perform(get("/reorders")).
				andExpect(status().isFound()).
				andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));//
	}

	@Test
	void deficitViewIsAccessibleForAdmin() throws Exception {
		mvc.perform(get("/reorders").
				with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(model().attributeExists("subTransactions"));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void reorderInvalidTest() throws Exception {
		InventoryItem item = Streamable.of(inventory.findAll()).get().findFirst().get();
		mvc.perform(post("/items/reorder/" + item.getId()).
				param("reorderQuantity", "test")
		).
				andExpect(status().isOk()).
				andExpect(forwardedUrl("/items"));
		mvc.perform(post("/items/reorder/test").
				param("reorderQuantity", "1")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/items"));
	}

	@Test
	@WithMockUser(username = "test", roles = {"BOSS", "WHOLESALER"})
	void reorderValidSendTest() throws Exception {
		InventoryItem item = Streamable.of(inventory.findAll()).get().findFirst().get();
		mvc.perform(post("/items/reorder/" + item.getId()).
				param("reorderQuantity", "1")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/items?reorder"));
		SubTransaction subTransaction = transactionManager.findBy(PAID).map(Transaction::getSubTransactions).
				flatMap(List::stream).get().findFirst().get();
		mvc.perform(post("/reorders/send/" + subTransaction.getId())).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/reorders"));
	}

}