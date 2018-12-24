package flowershop.products;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class FlowerShopItem extends Product {
	private String description;

	@OneToMany(mappedBy = "flowerShopItem")
	private List<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProducts;

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