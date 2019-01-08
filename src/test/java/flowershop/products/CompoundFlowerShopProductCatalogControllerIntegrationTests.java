package flowershop.products;

import flowershop.AbstractIntegrationTests;
import flowershop.products.controller.CompoundFlowerShopProductCatalogController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Integration tests interacting with the {@link CompoundFlowerShopProductCatalogController} directly.
 *
 * @author Oliver Gierke
 */
class CompoundFlowerShopProductCatalogControllerIntegrationTests extends AbstractIntegrationTests {

	// TODO: Could not autowire. No beans of 'CompoundFlowerShopProductCatalogController' type found.
	@Autowired
	CompoundFlowerShopProductCatalogController compoundFlowerShopProductCatalogController;

	@Test
	public void productsMethodReturnsProductView() {
		Model model = new ExtendedModelMap();

		String returnedView = compoundFlowerShopProductCatalogController.products(model);

		assertThat(returnedView).isEqualTo("products");
	}
}