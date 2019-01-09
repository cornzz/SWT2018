package flowershop.order;

import static org.assertj.core.api.Assertions.*;

import flowershop.AbstractIntegrationTests;
import flowershop.order.DeficitController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;


/**
 * Integration tests interacting with the {@link DeficitController} directly.
 *
 * @author Friedrich Bethke
 */
class DeficitControllerIntegrationTests extends AbstractIntegrationTests {

	// TODO: Could not autowire. No beans of 'FlowerShopDeficitController' type found.
	@Autowired
	DeficitController DeficitController;

	@Test
	@WithMockUser(roles = "BOSS")
	public void deficitMethodReturnsDeficitView() {
		Model model = new ExtendedModelMap();

		String returnedView = DeficitController.deficit(model);

		assertThat(returnedView).isEqualTo("deficits");
	}
}