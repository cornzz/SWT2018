package flowershop.order;

import flowershop.AbstractIntegrationTests;
import flowershop.products.CompoundFlowerShopProduct;
import flowershop.products.CompoundFlowerShopProductCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests interacting with the {@link OrderController} directly.
 *
 * @author Cornelius Kummer
 */
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class CartControllerOrderControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired MockMvc mvc;
	@Autowired CompoundFlowerShopProductCatalog catalog;

	@Test
	@WithMockUser(username = "test", password = "test")
	void addToCartGetTest() throws Exception {
		CompoundFlowerShopProduct product = Streamable.of(catalog.findAll()).stream().findFirst().get();
		mvc.perform(post("/cart/add/" + product.getId().getIdentifier())).
				andExpect(status().isOk()).
				andExpect(view().name("cart_add"));
	}

	@Test
	@WithMockUser(username = "test", password = "test")
	void addToCartPostInvalidTest() throws Exception {
		CompoundFlowerShopProduct product = Streamable.of(catalog.findAll()).stream().findFirst().get();
		mvc.perform(post("/cart/add").
				param("pid", "pid").
				param("quantity", "1")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/products"));
		mvc.perform(post("/cart/add").
				param("pid", product.getId().getIdentifier()).
				param("quantity", "test")
		).
				andExpect(status().isOk()).
				andExpect(forwardedUrl("/cart/add/" + product.getId()));
		mvc.perform(post("/cart/add").
				param("pid", product.getId().getIdentifier()).
				param("quantity", "1000")
		).
				andExpect(status().isOk()).
				andExpect(forwardedUrl("/cart/add/" + product.getId()));
	}

	@Test
	@WithMockUser(username = "test", password = "test")
	void addToCartPostValidTest() throws Exception {
		CompoundFlowerShopProduct product = Streamable.of(catalog.findAll()).stream().findFirst().get();
		mvc.perform(post("/cart/add").
				param("pid", product.getId().getIdentifier()).
				param("quantity", "1")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/cart"));
	}

	@Test
	@WithMockUser(username = "test", password = "test")
	void checkoutTest() throws Exception {
		mvc.perform(get("/checkout")).
				andExpect(status().isOk()).
				andExpect(view().name("order_confirm"));
	}

}