package kickstart.initializer;

import kickstart.Flower;
import kickstart.FlowerCatalog;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static org.salespointframework.core.Currencies.EURO;

@Component
@Order(20)
public class FlowerCatalogInitializer implements DataInitializer {

	private final FlowerCatalog flowerCatalog;

	FlowerCatalogInitializer(FlowerCatalog flowerCatalog) {
		Assert.notNull(flowerCatalog, "FlowerCatalog must not be null!");

		this.flowerCatalog = flowerCatalog;
	}

	@Override
	public void initialize() {
		if (flowerCatalog.findAll().iterator().hasNext()) {
			return;
		}

		flowerCatalog.save(new Flower("Red Flower", Money.of(100, EURO), "Expensive red flower"));
		flowerCatalog.save(new Flower("Green Flower", Money.of(100, EURO), "Expensive green flower"));
		flowerCatalog.save(new Flower("Blue Flower", Money.of(100, EURO), "Expensive blue flower"));
	}
}
