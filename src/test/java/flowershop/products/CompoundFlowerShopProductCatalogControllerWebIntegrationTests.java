package flowershop.products;

import flowershop.AbstractIntegrationTests;
import flowershop.products.controller.CompoundFlowerShopProductCatalogController;
import org.junit.jupiter.api.Test;
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
 * Integration test for the {@link CompoundFlowerShopProductCatalogController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Jonas Knobloch
 */

@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class CompoundFlowerShopProductCatalogControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;

	// TODO: Could not autowire. No beans of 'CompoundFlowerShopProductCatalog' type found.
	@Autowired
	CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog;

	@Test
	void preventsPublicAccessForAddProductView() throws Exception {
		mvc.perform(get("/products/add"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void addProductViewIsAccessibleForAdmin() throws Exception {
		mvc.perform(get("/products/add")
				.with(user("admin").roles("BOSS")))
				.andExpect(status().isOk());
	}

	@Test
	void compoundProductsAreAvailableToModel() throws Exception {
		mvc.perform(get("/products")).
				andExpect(status().isOk()).
				andExpect(model().attribute("products", is(compoundFlowerShopProductCatalog.findAll())));
	}
}