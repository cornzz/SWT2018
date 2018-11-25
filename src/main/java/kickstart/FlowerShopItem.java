package kickstart;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.Entity;

@Entity
public class FlowerShopItem extends Product {
	private String description;

	@SuppressWarnings("unused")
	private FlowerShopItem() {}

	public FlowerShopItem(String name, Money price, String description) {
		super(name, price);

		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}