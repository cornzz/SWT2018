package flowershop.products;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CompoundFlowerShopProductFlowerShopItemIdUnitTest extends AbstractIntegrationTests {

	@Autowired CompoundFlowerShopProductCatalog catalog;
	@Autowired FlowerShopItemCatalog itemCatalog;

	CompoundFlowerShopProduct product;
	FlowerShopItem item;
	CompoundFlowerShopProductFlowerShopItemId id;

	@BeforeAll
	void setup2() {
		product = Streamable.of(catalog.findAll()).get().findFirst().get();
		item = Streamable.of(itemCatalog.findAll()).get().findFirst().get();
		id = new CompoundFlowerShopProductFlowerShopItemId();
		id.setCompoundFlowerShopProduct(product.getId());
		id.setFlowerShopItem(item.getId());
	}

	@Test
	void equalsTest() {
		CompoundFlowerShopProductFlowerShopItemId id2 = id;
		CompoundFlowerShopProductFlowerShopItemId id3 = new CompoundFlowerShopProductFlowerShopItemId();
		id3.setCompoundFlowerShopProduct(product.getId());
		id3.setFlowerShopItem(item.getId());
		assertNotEquals(id, null);
		assertEquals(id, id2);
		assertEquals(id, id3);
		assertEquals(id.hashCode(), id2.hashCode());
	}

	@Test
	void getSetTest() {
		assertEquals(product.getId(), id.getCompoundFlowerShopProduct());
		assertEquals(item.getId(), id.getFlowerShopItem());
	}

}
