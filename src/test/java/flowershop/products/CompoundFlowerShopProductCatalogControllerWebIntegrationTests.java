package flowershop.products;

import flowershop.AbstractIntegrationTests;
import flowershop.products.controller.CompoundFlowerShopProductCatalogController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the {@link CompoundFlowerShopProductCatalogController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Cornelius Kummer
 * @author Jonas Knobloch
 */

@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_OUT)
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

	@Test
	@WithMockUser(username = "test")
	void authenticatedIndexTest() throws Exception {
		mvc.perform(get("/")).
				andExpect(status().isOk()).
				andExpect(forwardedUrl("/products"));
	}

	@Test
	void unauthenticatedIndexTest() throws Exception {
		mvc.perform(get("/")).
				andExpect(status().isOk()).
				andExpect(view().name("home"));
	}

	@Test
	void productTest() throws Exception {
		mvc.perform(get("/products/test")).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/products"));
		CompoundFlowerShopProduct product = Streamable.of(compoundFlowerShopProductCatalog.findAll()).get().findFirst().get();
		mvc.perform(get("/products/" + product.getId())).
				andExpect(status().isOk()).
				andExpect(view().name("products_detail"));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void productEditTest() throws Exception {
		CompoundFlowerShopProduct product = Streamable.of(compoundFlowerShopProductCatalog.findAll()).get().findFirst().get();
		mvc.perform(get("/products/" + product.getId())).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/products/" + product.getId() + "/edit"));
		mvc.perform(get("/products/test/edit")).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/products"));
		mvc.perform(get("/products/" + product.getId() + "/edit")).
				andExpect(status().isOk()).
				andExpect(view().name("products_edit"));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void deleteProductTest() throws Exception {
		CompoundFlowerShopProduct prod = new CompoundFlowerShopProduct("t", "t", new HashMap<>(), new LinkedList<>(), "t");
		compoundFlowerShopProductCatalog.save(prod);

		mvc.perform(post("/products/" + prod.getId() + "/delete")).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/products"));

		assertFalse(compoundFlowerShopProductCatalog.findById(prod.getId()).isPresent());
	}

}