package flowershop.products;

import flowershop.AbstractIntegrationTests;
import flowershop.products.controller.FlowerShopInventoryController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Integration tests interacting with the {@link FlowerShopInventoryController} directly.
 *
 * @author Friedrich Bethke
 */
class FlowerShopInventoryControllerIntegrationTests extends AbstractIntegrationTests {

	// TODO: Could not autowire. No beans of 'FlowerShopInventoryController' type found.
	@Autowired
	FlowerShopInventoryController flowerShopInventoryController;

	@Test
	@WithMockUser(roles = "BOSS")
	public void inventoryMethodReturnsInventoryView() {
		Model model = new ExtendedModelMap();

		String returnedView = flowerShopInventoryController.inventory(model);

		assertThat(returnedView).isEqualTo("inventory");
	}
}