package flowershop.order;

import flowershop.AbstractIntegrationTests;
import flowershop.products.CompoundFlowerShopProductCatalog;
import flowershop.products.FlowerShopServiceCatalog;
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
 * Integration test for the {@link DeficitControllerWebIntegrationTests} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Friedrich Bethke
 */

@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class DeficitControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;

	@Test
	void preventsPublicAccessForDeficitView() throws Exception {

		mvc.perform(get("/deficits"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));//
	}

	@Test
	void deficitViewIsAccessibleForAdmin() throws Exception {
		mvc.perform(get("/deficits")
				.with(user("admin").roles("BOSS")))
				.andExpect(status().isOk());
	}
}