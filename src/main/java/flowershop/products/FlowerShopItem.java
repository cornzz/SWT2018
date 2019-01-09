package flowershop.products;

import org.salespointframework.catalog.Product;

import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Optional;

/**
 * An extension of {@link Product}.
 *
 * @author Jonas Knobloch
 */
@Entity
public class FlowerShopItem extends Product {
	private MonetaryAmount basePrice;
	private MonetaryAmount retailPrice;
	private String description;
	private int baseStock;

	@OneToMany(mappedBy = "flowerShopItem", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
	private List<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProductFlowerShopItems;

	@SuppressWarnings("unused")
	private FlowerShopItem() {
	}

	public FlowerShopItem(String name, MonetaryAmount basePrice, MonetaryAmount retailPrice, String description, int baseStock) {
		super(name, basePrice);

		this.basePrice = basePrice;
		this.retailPrice = retailPrice;
		this.description = description;
		this.baseStock = baseStock;
	}

	@Override
	public MonetaryAmount getPrice() {
		return retailPrice;
	}

	public MonetaryAmount getBasePrice() {
		return basePrice;
	}

	public String getDescription() {
		return description;
	}

	public int getBaseStock() {
		return baseStock;
	}

	@Override
	public void setPrice(MonetaryAmount price) {
		basePrice = price;
		super.setPrice(price);
	}

	public List<CompoundFlowerShopProductFlowerShopItem> getCompoundFlowerShopProductFlowerShopItems() {
		return compoundFlowerShopProductFlowerShopItems;
	}

	public Optional<CompoundFlowerShopProductFlowerShopItem> getCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(CompoundFlowerShopProduct compoundFlowerShopProduct) {
		return getCompoundFlowerShopProductFlowerShopItems().stream().filter(compoundFlowerShopProductFlowerShopItem -> compoundFlowerShopProductFlowerShopItem.getCompoundFlowerShopProduct().equals(compoundFlowerShopProduct)).findFirst();
	}

	/**
	 * Removes {@link CompoundFlowerShopProduct} from compoundFlowerShopProductFlowerShopItems list.
	 *
	 * @param compoundFlowerShopProduct must not be {@literal null}.
	 */
	public void removeCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(CompoundFlowerShopProduct compoundFlowerShopProduct) {
		getCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(compoundFlowerShopProduct).ifPresent(compoundFlowerShopProductFlowerShopItem -> getCompoundFlowerShopProductFlowerShopItems().remove(compoundFlowerShopProductFlowerShopItem));
	}
}