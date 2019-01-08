package flowershop.products;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CompoundFlowerShopProductCatalog}.
 *
 * @author Jonas Knobloch
 */
public class CompoundFlowerShopProductCatalogIntegrationTests extends AbstractIntegrationTests {

	// TODO: Could not autowire. No beans of 'CompoundFlowerShopProductCatalog' type found.
	@Autowired
	CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog;

	@Test
	void findAll() {
		Iterable<CompoundFlowerShopProduct> compoundFlowerShopProducts = compoundFlowerShopProductCatalog.findAll();
		assertThat(compoundFlowerShopProducts).hasSize(3);
	}

	@Test
	void compoundProductsContainFlowerShopItemsWithQuantities() {
		Iterable<CompoundFlowerShopProduct> compoundFlowerShopProducts = compoundFlowerShopProductCatalog.findAll();
		compoundFlowerShopProducts.forEach(product -> assertThat(product.getFlowerShopItemsWithQuantities()).hasSize(3));
		compoundFlowerShopProducts
				.forEach(product -> product.getFlowerShopItemsWithQuantities()
				.forEach((item, quantity) -> assertThat(quantity.isGreaterThanOrEqualTo(Quantity.of(1)))));
	}

	@Test
	void compoundProductsContainFlowerShopServices() {
		Iterable<CompoundFlowerShopProduct> compoundFlowerShopProducts = compoundFlowerShopProductCatalog.findAll();
		compoundFlowerShopProducts.forEach(product -> assertThat(product.getFlowerShopServices()).hasSize(1));
	}
}
