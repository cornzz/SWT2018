package kickstart.initializer;

import kickstart.FlowerCatalog;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Order(20)
public class FlowerInventoryInitializer implements DataInitializer {
	private final Inventory<InventoryItem> inventory;
	private final FlowerCatalog flowerCatalog;

	FlowerInventoryInitializer(Inventory<InventoryItem> inventory, FlowerCatalog flowerCatalog) {

		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(flowerCatalog, "Inventory must not be null!");

		this.inventory = inventory;
		this.flowerCatalog = flowerCatalog;
	}

	@Override
	public void initialize() {
		flowerCatalog.findAll().forEach(flower -> {
			inventory.findByProduct(flower).orElseGet(() -> inventory.save(new InventoryItem(flower, Quantity.of(10))));
		});
	}
}
