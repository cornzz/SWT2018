package flowershop.products;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Optional;

@Entity
public class FlowerShopItem extends Product {
	private MonetaryAmount basePrice;
	private double profit;
	private String description;
	private int minStock;

	@OneToMany(mappedBy = "flowerShopItem", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
	private List<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProductFlowerShopItems;

	@SuppressWarnings("unused")
	private FlowerShopItem() {
	}

	public FlowerShopItem(String name, Money price, double profit, String description, int minStock) {
		super(name, price);

		this.description = description;
		this.profit = profit;
		this.basePrice = price;
		this.minStock = minStock;
	}

	@Override
	public MonetaryAmount getPrice() {
		return super.getPrice().multiply(profit).add(super.getPrice());
	}

	public MonetaryAmount getBasePrice() {
		return basePrice;
	}

	public double getProfit() {
		return profit;
	}

	public String getDescription() {
		return description;
	}

	public int getMinStock() {
		return minStock;
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

	public void removeCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(CompoundFlowerShopProduct compoundFlowerShopProduct) {
		getCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(compoundFlowerShopProduct).ifPresent(compoundFlowerShopProductFlowerShopItem -> getCompoundFlowerShopProductFlowerShopItems().remove(compoundFlowerShopProductFlowerShopItem));
	}
}