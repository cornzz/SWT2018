package flowershop.products;

import org.salespointframework.catalog.ProductIdentifier;

import java.io.Serializable;
import java.util.Objects;

public class CompoundFlowerShopProductFlowerShopItemId implements Serializable {

	private ProductIdentifier compoundFlowerShopProduct;
	private ProductIdentifier flowerShopItem;

	public ProductIdentifier getCompoundFlowerShopProduct() {
		return compoundFlowerShopProduct;
	}

	public void setCompoundFlowerShopProduct(ProductIdentifier compoundFlowerShopProduct) {
		this.compoundFlowerShopProduct = compoundFlowerShopProduct;
	}

	public ProductIdentifier getFlowerShopItem() {
		return flowerShopItem;
	}

	public void setFlowerShopItem(ProductIdentifier flowerShopItem) {
		this.flowerShopItem = flowerShopItem;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CompoundFlowerShopProductFlowerShopItemId compoundFlowerShopProductFlowerShopItemId = (CompoundFlowerShopProductFlowerShopItemId) o;
		return Objects.equals(compoundFlowerShopProduct, compoundFlowerShopProductFlowerShopItemId.getCompoundFlowerShopProduct()) &&
				Objects.equals(flowerShopItem, compoundFlowerShopProductFlowerShopItemId.getFlowerShopItem());
	}

	@Override
	public int hashCode() {
		return Objects.hash(compoundFlowerShopProduct, flowerShopItem);
	}
}
