package flowershop.products.initializer;

import flowershop.products.FlowerShopItem;
import flowershop.products.FlowerShopItemCatalog;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static org.salespointframework.core.Currencies.EURO;

/**
 * Initializes {@link FlowerShopItem}s.
 *
 * @author Friedrich Bethke
 * @author  Jonas Knobloch
 */
@Component
@Order(20)
public class FlowerShopItemCatalogInitializer implements DataInitializer {

	private final FlowerShopItemCatalog flowerShopItemCatalog;

	FlowerShopItemCatalogInitializer(FlowerShopItemCatalog flowerShopItemCatalog) {
		Assert.notNull(flowerShopItemCatalog, "FlowerShopItemCatalog must not be null!");

		this.flowerShopItemCatalog = flowerShopItemCatalog;
	}

	@Override
	public void initialize() {
		if (flowerShopItemCatalog.findAll().iterator().hasNext()) {
			return;
		}

		flowerShopItemCatalog.save(new FlowerShopItem("Red FlowerShopItem", Money.of(100, EURO), Money.of(120, EURO), "Expensive red flower", 10));
		flowerShopItemCatalog.save(new FlowerShopItem("Green FlowerShopItem", Money.of(100, EURO), Money.of(120, EURO), "Expensive green flower", 10));
		flowerShopItemCatalog.save(new FlowerShopItem("Blue FlowerShopItem", Money.of(100, EURO), Money.of(120, EURO), "Expensive blue flower", 10));
	}
}
