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
		double profit = 0.2;
		String description = "Bar";
		int minStock = 10;

		FlowerShopItem flowerShopItem = new FlowerShopItem(name, price, profit, description, minStock);

		assertEquals(name, flowerShopItem.getName());
		assertEquals(price, flowerShopItem.getBasePrice());
		assertEquals(description, flowerShopItem.getDescription());
		assertEquals(profit, flowerShopItem.getProfit());
		assertEquals(minStock, flowerShopItem.getMinStock());
	}

	@Test
	public void testSetters() {
		FlowerShopItem flowerShopItem = new FlowerShopItem("Foo", Money.of(100, EURO), 0.2, "Bar", 10);

		String name = "Bar";
		Money price = Money.of(200, EURO);

		flowerShopItem.setName(name);
		flowerShopItem.setPrice(price);

		// TODO: test missing description setter

		assertEquals(name, flowerShopItem.getName());
		assertEquals(price, flowerShopItem.getBasePrice());
	}
}
