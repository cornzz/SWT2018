package flowershop.products;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.quantity.Quantity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.salespointframework.core.Currencies.EURO;

public class CompoundFlowerShopProductUnitTest {

	private static final String NAME = "Foo";
	private static final String DESCRIPTION = "Bar";

	private static final Money FLOWER_PRICE = Money.of(100, EURO);
	private static final Money SERVICE_PRICE = Money.of(100, EURO);
	private static final Money COMPOUND_PRICE = Money.of(340, EURO);

	private static final String IMAGE = "Base64EncodedDummyImage";

	private static final Quantity FLOWER_QUANTITY = Quantity.of(2);

	private static final FlowerShopItem FLOWER = new FlowerShopItem("Foo", FLOWER_PRICE, Money.of(120, EURO), "Bar", 10);
	private static final FlowerShopService SERVICE = new FlowerShopService("Foo", SERVICE_PRICE, "Bar");

	private static CompoundFlowerShopProduct createDummyProduct() {

		Map<FlowerShopItem, Quantity> flowerShopItemWithQuantities = new HashMap<>();
		flowerShopItemWithQuantities.put(FLOWER, FLOWER_QUANTITY);

		List<FlowerShopService> flowerShopServices = new ArrayList<>();
		flowerShopServices.add(SERVICE);

		return new CompoundFlowerShopProduct(NAME, DESCRIPTION, flowerShopItemWithQuantities, flowerShopServices, IMAGE);
	}

	@Test
	public void testGetters() {
		CompoundFlowerShopProduct compoundFlowerShopProduct = createDummyProduct();

		assertEquals(NAME, compoundFlowerShopProduct.getName());
		assertEquals(COMPOUND_PRICE, compoundFlowerShopProduct.getPrice());
		assertEquals(DESCRIPTION, compoundFlowerShopProduct.getDescription());
		assertEquals(IMAGE, compoundFlowerShopProduct.getImage());

	}

	@Test
	public void testSetters() {
		CompoundFlowerShopProduct compoundFlowerShopProduct = createDummyProduct();

		String name = "Bar";
		String description = "Foo";

		compoundFlowerShopProduct.setName(name);
		compoundFlowerShopProduct.setDescription(description);

		assertEquals(name, compoundFlowerShopProduct.getName());
		assertEquals(description, compoundFlowerShopProduct.getDescription());

	}
}
