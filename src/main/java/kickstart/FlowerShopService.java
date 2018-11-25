package kickstart;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.Entity;

@Entity
public class FlowerShopService extends Product {
	private String desrciption;

	@SuppressWarnings("unused")
	private FlowerShopService() {}

	public FlowerShopService(String name, Money price, String description) {
		super(name, price);

		this.desrciption = description;
	}

	public String getDescription() {
		return desrciption;
	}
}

