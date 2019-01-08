package flowershop.products;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.salespointframework.core.Currencies.EURO;

public class FlowerShopItemUnitTest {
	@Test
	public void testGetters() {
		String name = "Foo";
		Money basePrice = Money.of(100, EURO);
		Money retailPrice = Money.of(120, EURO);
		String description = "Bar";
		int baseStock = 10;

		FlowerShopItem flowerShopItem = new FlowerShopItem(name, basePrice, retailPrice, description, baseStock);

		assertEquals(name, flowerShopItem.getName());
		assertEquals(basePrice, flowerShopItem.getBasePrice());
		assertEquals(retailPrice, flowerShopItem.getPrice());
		assertEquals(description, flowerShopItem.getDescription());
		assertEquals(baseStock, flowerShopItem.getBaseStock());
	}

	@Test
	public void testSetters() {
		FlowerShopItem flowerShopItem = new FlowerShopItem("Foo", Money.of(100, EURO), Money.of(120, EURO), "Bar", 10);

		String name = "Bar";
		Money price = Money.of(200, EURO);

		flowerShopItem.setName(name);
		flowerShopItem.setPrice(price);

		// TODO: test missing description setter

		assertEquals(name, flowerShopItem.getName());
		assertEquals(price, flowerShopItem.getBasePrice());
	}
}
