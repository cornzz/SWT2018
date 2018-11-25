package flowershop.products;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class CompoundFlowerShopProduct extends Product {
	private String description;

	// TODO: change cascade
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<FlowerShopItem> flowerShopItems;

	// TODO: change cascade
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<FlowerShopService> flowerShopServices;

	@SuppressWarnings("unused")
	private CompoundFlowerShopProduct() {}

	public CompoundFlowerShopProduct(String name, String description, List<FlowerShopItem> flowerShopItems, List<FlowerShopService> flowerShopServices) {
		super(name, CompoundFlowerShopProduct.calcPrice(flowerShopItems, flowerShopServices));

		this.description = description;
		this.flowerShopItems = flowerShopItems;
		this.flowerShopServices = flowerShopServices;
	}

	private static Money calcPrice(Iterable<FlowerShopItem> flowerShopItems, Iterable<FlowerShopService> flowerShopServices) {
		// TODO: calc price
		return Money.of(300, "EUR");
	}

	public String getDescription() {
		return description;
	}

	public Iterable<FlowerShopItem> getFlowerShopItems() {
		return flowerShopItems;
	}

	public Iterable<FlowerShopService> getFlowerShopServices() {
		return flowerShopServices;
	}
}
