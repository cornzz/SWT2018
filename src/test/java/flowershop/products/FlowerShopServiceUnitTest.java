package flowershop.products;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.salespointframework.core.Currencies.EURO;

public class FlowerShopServiceUnitTest {
	@Test
	public void testGetters() {

		String name = "Foo";
		Money price = Money.of(100, EURO);
		String description = "Bar";

		FlowerShopService flowerShopService= new FlowerShopService(name, price, description);

		assertEquals(name, flowerShopService.getName());
		assertEquals(price, flowerShopService.getPrice());
		assertEquals(description, flowerShopService.getDescription());
	}

	@Test
	public void testSetters() {
		FlowerShopService flowerShopService = new FlowerShopService("Foo", Money.of(100, EURO), "Bar");

		String name = "Bar";
		Money price = Money.of(200, EURO);

		flowerShopService.setName(name);
		flowerShopService.setPrice(price);

		assertEquals(name, flowerShopService.getName());
		assertEquals(price, flowerShopService.getPrice());
	}
}
