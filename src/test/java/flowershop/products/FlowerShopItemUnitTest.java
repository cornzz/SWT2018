package flowershop.products;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.quantity.Quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.salespointframework.core.Currencies.EURO;

public class FlowerShopItemUnitTest {
	@Test
	public void testGetters() {

		String name = "Foo";
		Money price = Money.of(100, EURO);
		String description = "Bar";
		Double profit = 0.2;

		FlowerShopItem flowerShopItem = new FlowerShopItem(name, price, description, profit);

		assertEquals(name, flowerShopItem.getName());
		assertEquals(price, flowerShopItem.getBasePrice());
		assertEquals(description, flowerShopItem.getDescription());
	}

	@Test
	public void testSetters() {
		FlowerShopItem flowerShopItem = new FlowerShopItem("Foo", Money.of(100, EURO), "Bar", 0.2);

		String name = "Bar";
		Money price = Money.of(200, EURO);

		flowerShopItem.setName(name);
		flowerShopItem.setPrice(price);

		// TODO: test missing description setter

		assertEquals(name, flowerShopItem.getName());
		assertEquals(price, flowerShopItem.getBasePrice());
	}
}
