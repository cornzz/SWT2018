package flowershop.products;

import static org.assertj.core.api.Assertions.*;

import flowershop.AbstractIntegrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for {@link FlowerShopServiceCatalog}.
 *
 * @author Friedrich Bethke
 */
public class FlowerShopServiceIntegrationTests extends AbstractIntegrationTests {

	// TODO: Could not autowire. No beans of 'FlowerShopServiceCatalog' type found.
	@Autowired
	FlowerShopServiceCatalog serviceCatalog;

	@Test
	void findAll() {
		Iterable<FlowerShopService> flowerShopServiceCatalog = serviceCatalog.findAll();
		assertThat(flowerShopServiceCatalog).hasSize(1);
	}
}
