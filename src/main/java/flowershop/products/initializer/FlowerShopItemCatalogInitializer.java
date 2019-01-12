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
 * @author Jonas Knobloch
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

		flowerShopItemCatalog.save(new FlowerShopItem("Rose", Money.of(2, EURO), Money.of(3, EURO), "Teure rote Blume.", 35));
		flowerShopItemCatalog.save(new FlowerShopItem("Margerite", Money.of(1.5, EURO), Money.of(2.5, EURO), "Teure weiße Blume.", 35));
		flowerShopItemCatalog.save(new FlowerShopItem("Gerbera", Money.of(1.5, EURO), Money.of(2.5, EURO), "Teure bunte Blume.", 35));
	}
}
