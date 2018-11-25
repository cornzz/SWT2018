package flowershop.catalog;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;


@Entity
public class FlowerShopItem extends Product{
	/*public static enum ItemType {
		BLUME;
	}
	private ItemType type; */
	private String description;
	private FlowerShopItem() {}

	public FlowerShopItem(String name, Money price, String description) {

		super(name, price);
		this.description = description;
//		this.type=type;

	}
//	public ItemType getType(){return type;}
}