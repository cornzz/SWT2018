package flowershop.catalog;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import static org.salespointframework.core.Currencies.EURO;

@Component
public class CatalogInitializer implements DataInitializer{

	private final ItemCatalog itemCatalog;


	CatalogInitializer(ItemCatalog itemCatalog) {

		this.itemCatalog = itemCatalog;
	}

	@Override
	public void initialize() {

		if (itemCatalog.findAll().iterator().hasNext()) {
			return;
		}
		itemCatalog.save(new Item("Tulpe", Money.of(1.0,EURO), Item.ItemType.BLUME));
		itemCatalog.save(new Item("Rose", Money.of(2.0,EURO), Item.ItemType.BLUME));
		itemCatalog.save(new Item("Nelke", Money.of(3.0,EURO), Item.ItemType.BLUME));



	}

}
