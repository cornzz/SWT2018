package flowershop.products;

import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class CompoundFlowerShopProduct extends Product {
	private String description;

	// TODO: change cascade
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private List<FlowerShopItem> flowerShopItems;

	// TODO: change cascade
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<FlowerShopService> flowerShopServices;

	@Type(type="text")
	private String image;

	@SuppressWarnings("unused")
	private CompoundFlowerShopProduct() {
	}

	public CompoundFlowerShopProduct(String name, String description, List<FlowerShopItem> flowerShopItems, List<FlowerShopService> flowerShopServices, String image) {
		super(name, CompoundFlowerShopProduct.calcPrice(flowerShopItems, flowerShopServices));

		this.description = description;
		this.flowerShopItems = flowerShopItems;
		this.flowerShopServices = flowerShopServices;
		this.image = image;
	}

	private static Money calcPrice(Iterable<FlowerShopItem> flowerShopItems, Iterable<FlowerShopService> flowerShopServices) {

		Money price = Money.of(0, "EUR");

		for (FlowerShopItem flowerShopItem : flowerShopItems) {
			price = price.add(flowerShopItem.getPrice());
		}

		for (FlowerShopService flowerShopService : flowerShopServices) {
			price = price.add(flowerShopService.getPrice());
		}

		return price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Iterable<FlowerShopItem> getFlowerShopItems() {
		return flowerShopItems;
	}

	public Iterable<FlowerShopService> getFlowerShopServices() {
		return flowerShopServices;
	}

	public String getImage() {
		return image;
	}
}
