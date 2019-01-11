package flowershop.order;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReorderManagerUnitTests extends AbstractIntegrationTests {

	@Autowired ReorderManager reorderManager;

	@Test
	void validateQuantityTest() {
		Model model = new ExtendedModelMap();
		Long result = reorderManager.validateQuantity("1", model);
		assertEquals((long) result, (long) 1);
		result = reorderManager.validateQuantity("test", model);
		assertNull(result);
		result = reorderManager.validateQuantity("-1", model);
		assertNull(result);
	}

}
