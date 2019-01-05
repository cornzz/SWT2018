package flowershop.error;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests interacting with the {@link FlowerShopErrorController} directly.
 *
 * @author Cornelius Kummer
 */
class FlowerShopErrorControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired FlowerShopErrorController controller;

	@Test
	void getErrorPathTest() {
		String result = controller.getErrorPath();
		assertEquals(result, "/error");
	}

}
