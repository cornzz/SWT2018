package flowershop.products.initializer;

import flowershop.products.FlowerShopService;
import flowershop.products.FlowerShopServiceCatalog;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Order(20)
public class ServiceCatalogInitializer implements DataInitializer {

	private final FlowerShopServiceCatalog flowerShopServiceCatalog;

	ServiceCatalogInitializer(FlowerShopServiceCatalog flowerShopServiceCatalog) {
		Assert.notNull(flowerShopServiceCatalog, "FlowerShopServiceCatalog must not be null!");

		this.flowerShopServiceCatalog = flowerShopServiceCatalog;
	}

	@Override
	public void initialize() {
		if(flowerShopServiceCatalog.findAll().iterator().hasNext()) {
			return;
		}

		flowerShopServiceCatalog.save(new FlowerShopService("Make CompoundFlowerShopProduct", Money.of(100, "EUR"), "Basic service"));
	}
}