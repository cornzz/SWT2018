package flowershop.products.initializer;

import flowershop.products.FlowerShopItemCatalog;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Order(20)
public class FlowerShopItemInventoryInitializer implements DataInitializer {
	private final Inventory<InventoryItem> inventory;
	private final FlowerShopItemCatalog flowerShopItemCatalog;

	FlowerShopItemInventoryInitializer(Inventory<InventoryItem> inventory, FlowerShopItemCatalog flowerShopItemCatalog) {

		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(flowerShopItemCatalog, "Inventory must not be null!");

		this.inventory = inventory;
		this.flowerShopItemCatalog = flowerShopItemCatalog;
	}

	@Override
	public void initialize() {
		flowerShopItemCatalog.findAll().forEach(flower -> {
			inventory.findByProduct(flower).orElseGet(() -> inventory.save(new InventoryItem(flower, Quantity.of(10))));
		});
	}
}
