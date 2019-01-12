package flowershop.products;

import flowershop.AbstractIntegrationTests;
import flowershop.products.controller.FlowerShopServiceController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Integration tests interacting with the {@link FlowerShopServiceController} directly.
 *
 * @author Friedrich Bethke
 */
class FlowerShopServiceControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	FlowerShopServiceController flowerShopServiceController;

	@Test
	@WithMockUser(roles = "BOSS")
	public void serviceMethodReturnsServiceView() {
		Model model = new ExtendedModelMap();

		String returnedView = flowerShopServiceController.services(model);

		assertThat(returnedView).isEqualTo("services");
	}
}