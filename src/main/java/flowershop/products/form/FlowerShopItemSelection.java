package flowershop.products.form;

import flowershop.products.FlowerShopItem;
import org.salespointframework.quantity.Quantity;

public class FlowerShopItemSelection {
	private FlowerShopItem flowerShopItem;
	private Quantity quantity;

	public FlowerShopItem getFlowerShopItem() {
		return flowerShopItem;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setFlowerShopItem(FlowerShopItem flowerShopItem) {
		this.flowerShopItem = flowerShopItem;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}
}
