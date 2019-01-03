package flowershop.products;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Optional;

@Entity
public class FlowerShopItem extends Product {
	private String description;

	@OneToMany(mappedBy = "flowerShopItem", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
	private List<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProductFlowerShopItems;

	@SuppressWarnings("unused")
	private FlowerShopItem() {}

	public FlowerShopItem(String name, Money price, String description) {
		super(name, price);

		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public List<CompoundFlowerShopProductFlowerShopItem> getCompoundFlowerShopProductFlowerShopItems() {
		return compoundFlowerShopProductFlowerShopItems;
	}

	public Optional<CompoundFlowerShopProductFlowerShopItem> getCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(CompoundFlowerShopProduct compoundFlowerShopProduct) {
		return getCompoundFlowerShopProductFlowerShopItems().stream().filter(compoundFlowerShopProductFlowerShopItem -> compoundFlowerShopProductFlowerShopItem.getCompoundFlowerShopProduct().equals(compoundFlowerShopProduct)).findFirst();
	}

	public void removeCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(CompoundFlowerShopProduct compoundFlowerShopProduct) {
		getCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(compoundFlowerShopProduct).ifPresent(compoundFlowerShopProductFlowerShopItem -> getCompoundFlowerShopProductFlowerShopItems().remove(compoundFlowerShopProductFlowerShopItem));
	}
}