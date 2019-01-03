package flowershop.products;

import org.salespointframework.quantity.Quantity;

import javax.persistence.*;

@Entity
@IdClass(CompoundFlowerShopProductFlowerShopItemId.class)
public class CompoundFlowerShopProductFlowerShopItem {

	@Id
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn
	private CompoundFlowerShopProduct compoundFlowerShopProduct;

	@Id
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn
	private FlowerShopItem flowerShopItem;

	@Column
	private Quantity quantity;

	public CompoundFlowerShopProduct getCompoundFlowerShopProduct() {
		return compoundFlowerShopProduct;
	}

	public void setCompoundFlowerShopProduct(CompoundFlowerShopProduct compoundFlowerShopProduct) {
		this.compoundFlowerShopProduct = compoundFlowerShopProduct;
	}

	public FlowerShopItem getFlowerShopItem() {
		return flowerShopItem;
	}

	public void setFlowerShopItem(FlowerShopItem flowerShopItem) {
		this.flowerShopItem = flowerShopItem;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}
}
