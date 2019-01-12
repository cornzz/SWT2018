package flowershop.products;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.Entity;

/**
 * An extension of {@link Product} to add flower shop specific methods and values.
 *
 * @author Friedrich Bethke
 */
@Entity
public class FlowerShopService extends Product {
	private String description;

	@SuppressWarnings("unused")
	private FlowerShopService() {
	}

	public FlowerShopService(String name, Money price, String description) {
		super(name, price);

		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}

