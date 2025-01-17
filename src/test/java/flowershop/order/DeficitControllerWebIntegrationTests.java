package flowershop.order;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the {@link DeficitController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Cornelius Kummer
 * @author Friedrich Bethke
 */

@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class DeficitControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	Inventory<InventoryItem> inventory;

	@Test
	void preventsPublicAccessForDeficitView() throws Exception {
		mvc.perform(get("/deficits")).
				andExpect(status().isFound()).
				andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));//
	}

	@Test
	void deficitViewIsAccessibleForAdmin() throws Exception {
		mvc.perform(get("/deficits").
				with(user("admin").roles("BOSS"))).
				andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void deficitInvalidTest() throws Exception {
		InventoryItem item = Streamable.of(inventory.findAll()).get().findFirst().get();
		mvc.perform(post("/items/deficit/" + item.getId()).
				param("deficitQuantity", "9999")
		).
				andExpect(status().isOk()).
				andExpect(forwardedUrl("/items"));
		mvc.perform(post("/items/deficit/test").
				param("deficitQuantity", "1")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/items"));
		mvc.perform(post("/items/deficit/" + item.getId()).
				param("deficitQuantity", "test")
		).
				andExpect(status().isOk()).
				andExpect(forwardedUrl("/items"));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void deficitValidTest() throws Exception {
		InventoryItem item = Streamable.of(inventory.findAll()).get().findFirst().get();
		mvc.perform(post("/items/deficit/" + item.getId()).
				param("deficitQuantity", "1")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/items?deficit"));
	}

}