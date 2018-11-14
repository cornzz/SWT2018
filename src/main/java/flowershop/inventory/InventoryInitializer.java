package flowershop.inventory;

import flowershop.catalog.ItemCatalog;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
class InventoryInitializer implements DataInitializer {

	private final Inventory<InventoryItem> inventory;
	private final ItemCatalog itemCatalog;

	InventoryInitializer(Inventory<InventoryItem> inventory, ItemCatalog itemCatalog) {

		this.inventory = inventory;
		this.itemCatalog = itemCatalog;
	}
	@Override
	public void initialize() {


		itemCatalog.findAll().forEach(item -> {

			// Try to find an InventoryItem for the project and create a default one with 10 items if none available
			inventory.findByProduct(item) //
					.orElseGet(() -> inventory.save(new InventoryItem(item, Quantity.of(5))));
		});
	}
}
