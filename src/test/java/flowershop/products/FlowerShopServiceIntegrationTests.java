package flowershop.products;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link FlowerShopServiceCatalog}.
 *
 * @author Friedrich Bethke
 */
public class FlowerShopServiceIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	FlowerShopServiceCatalog serviceCatalog;

	@Test
	void findAll() {
		Iterable<FlowerShopService> flowerShopServiceCatalog = serviceCatalog.findAll();
		assertThat(flowerShopServiceCatalog).hasSize(1);
	}
}
