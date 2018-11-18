package flowershop.catalog;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;


@Entity
public class Item extends Product{
	public static enum ItemType {
		BLUME,
		STRAUSS;
	}
	private ItemType type;
	private Item() {}

	public Item(String name, Money price, ItemType type) {

		super(name, price);
		this.type=type;

	}
	public ItemType getType(){return type;}
}
