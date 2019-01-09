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
	@WithMockUser
	void addToCartGetTest() throws Exception {
		mvc.perform(login("test", "test"));
		CompoundFlowerShopProduct product = Streamable.of(catalog.findAll()).stream().findFirst().get();
		mvc.perform(post("/cart/add/" + product.getId().getIdentifier())).
				andExpect(status().isOk()).
				andExpect(view().name("cart_add"));
	}

	@Test
	@WithMockUser
	void addToCartPostInvalidTest() throws Exception {
		mvc.perform(login("test", "test"));
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
	@WithMockUser
	void addToCartPostValidTest() throws Exception {
		mvc.perform(login("test", "test"));
		CompoundFlowerShopProduct product = Streamable.of(catalog.findAll()).stream().findFirst().get();
		mvc.perform(post("/cart/add").
				param("pid", product.getId().getIdentifier()).
				param("quantity", "1")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/cart"));
	}

	@Test
	@WithMockUser
	void checkoutTest() throws Exception {
		mvc.perform(login("test", "test"));
		mvc.perform(get("/checkout")).
				andExpect(status().isOk()).
				andExpect(view().name("order_confirm"));
	}

}