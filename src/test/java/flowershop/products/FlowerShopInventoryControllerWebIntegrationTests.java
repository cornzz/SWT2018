package flowershop.products;

import flowershop.AbstractIntegrationTests;
import flowershop.products.controller.CompoundFlowerShopProductCatalogController;
import org.junit.jupiter.api.Test;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the {@link FlowerShopInventoryControllerWebIntegrationTests} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Friedrich Bethke
 */

@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class FlowerShopInventoryControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	Inventory<InventoryItem> inventory;

	@Test
	void preventsPublicAccessForInventoryView() throws Exception {

		mvc.perform(get("/items"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void inventoryViewIsAccessibleForAdmin() throws Exception {
		mvc.perform(get("/items")
				.with(user("admin").roles("BOSS")))
				.andExpect(status().isOk());
	}

	@Test
	void preventsPublicAccessForAddInventoryView() throws Exception {
		mvc.perform(get("/items/add"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void addInventoryViewIsAccessibleForAdmin() throws Exception {
		mvc.perform(get("/items/add")
				.with(user("admin").roles("BOSS")))
				.andExpect(status().isOk());
	}

	@Test
	void inventoryItemsAreAvailableToModel() throws Exception {
		mvc.perform(get("/items")
				.with(user("admin").roles("BOSS")))
				.andExpect(status().isOk())
				.andExpect(model().attribute("inventory", is(inventory.findAll())));
	}
}