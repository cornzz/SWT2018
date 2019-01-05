package flowershop.products;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import flowershop.products.controller.CompoundFlowerShopProductCatalogController;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration test for the {@link CompoundFlowerShopProductCatalogController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Jonas Knobloch
 */

@SpringBootTest
@AutoConfigureMockMvc
class CompoundFlowerShopProductCatalogControllerWebIntegrationTests {

	@Autowired MockMvc mvc;

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