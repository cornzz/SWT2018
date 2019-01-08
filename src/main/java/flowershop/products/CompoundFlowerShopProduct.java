package flowershop.products;

import flowershop.products.form.CompoundFlowerShopProductTransferObject;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class CompoundFlowerShopProduct extends Product {
	private String description;

	@OneToMany(mappedBy = "compoundFlowerShopProduct", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
	private List<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProductFlowerShopItems = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<FlowerShopService> flowerShopServices;

	@Type(type = "text")
	private String image;

	private boolean inStock;

	@SuppressWarnings("unused")
	private CompoundFlowerShopProduct() {
	}

	public CompoundFlowerShopProduct(String name, String description, Map<FlowerShopItem, Quantity> flowerShopItemsWithQuantities, List<FlowerShopService> flowerShopServices, String image) {
		super(name, CompoundFlowerShopProduct.calcPrice(flowerShopItemsWithQuantities, flowerShopServices));

		this.description = description;
		this.flowerShopServices = flowerShopServices;
		this.image = image;

		// create relationships
		flowerShopItemsWithQuantities.forEach(this::addFlowerShopItem);
	}

	@Override
	public Money getPrice() {
		return calcPrice(getFlowerShopItemsWithQuantities(), getFlowerShopServices());
	}

	public String getDescription() {
		return description;
	}

	public List<CompoundFlowerShopProductFlowerShopItem> getCompoundFlowerShopProductFlowerShopItems() {
		return compoundFlowerShopProductFlowerShopItems;
	}

	public List<FlowerShopService> getFlowerShopServices() {
		return flowerShopServices;
	}

	public String getImage() {
		return image;
	}

	public boolean getInStock() {
		return this.inStock;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCompoundFlowerShopProductFlowerShopItems(List<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProductFlowerShopItems) {
		this.compoundFlowerShopProductFlowerShopItems = compoundFlowerShopProductFlowerShopItems;
	}

	public void setFlowerShopServices(List<FlowerShopService> flowerShopServices) {
		this.flowerShopServices = flowerShopServices;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	// pure convenience, provides easier access to the product's ingredient quantities
	public Map<FlowerShopItem, Quantity> getFlowerShopItemsWithQuantities() {
		return getCompoundFlowerShopProductFlowerShopItems().stream().collect(
				Collectors.toMap(CompoundFlowerShopProductFlowerShopItem::getFlowerShopItem, CompoundFlowerShopProductFlowerShopItem::getQuantity));
	}

	public CompoundFlowerShopProductFlowerShopItem addFlowerShopItem(FlowerShopItem flowerShopItem, Quantity quantity) {
		CompoundFlowerShopProductFlowerShopItem compoundFlowerShopProductFlowerShopItem = new CompoundFlowerShopProductFlowerShopItem();

		compoundFlowerShopProductFlowerShopItem.setCompoundFlowerShopProduct(this);
		compoundFlowerShopProductFlowerShopItem.setFlowerShopItem(flowerShopItem);
		compoundFlowerShopProductFlowerShopItem.setQuantity(quantity);

		addCompoundFlowerShopProductFlowerShopItem(compoundFlowerShopProductFlowerShopItem);

		return compoundFlowerShopProductFlowerShopItem;
	}

	// TODO: do we want this to be public?
	public void addCompoundFlowerShopProductFlowerShopItem(CompoundFlowerShopProductFlowerShopItem compoundFlowerShopProductFlowerShopItem) {
		compoundFlowerShopProductFlowerShopItems.add(compoundFlowerShopProductFlowerShopItem);
	}

	public Optional<CompoundFlowerShopProductFlowerShopItem> getCompoundFlowerShopProductFlowerShopItemByFlowerShopItem(FlowerShopItem flowerShopItem) {
		return getCompoundFlowerShopProductFlowerShopItems().stream().filter(item -> item.getFlowerShopItem().equals(flowerShopItem)).findFirst();
	}

	public void removeCompoundFlowerShopProductFlowerShopItemByFlowerShopItem(FlowerShopItem flowerShopItem) {
		getCompoundFlowerShopProductFlowerShopItemByFlowerShopItem(flowerShopItem).ifPresent(compoundFlowerShopProductFlowerShopItem -> getCompoundFlowerShopProductFlowerShopItems().remove(compoundFlowerShopProductFlowerShopItem));
	}

	public static Money calcPrice(Map<FlowerShopItem, Quantity> flowerItemsWithQuantities, Iterable<FlowerShopService> flowerShopServices) {
		Money price = Money.of(0, "EUR");

		for (Map.Entry<FlowerShopItem, Quantity> entry : flowerItemsWithQuantities.entrySet()) {
			price = price.add(entry.getKey().getPrice().multiply(entry.getValue().getAmount()));
		}

		for (FlowerShopService flowerShopService : flowerShopServices) {
			price = price.add(flowerShopService.getPrice());
		}

		return price;
	}

	public CompoundFlowerShopProductTransferObject createTransferObject() {
		CompoundFlowerShopProductTransferObject form = new CompoundFlowerShopProductTransferObject();

		form.setId(getId());
		form.setName(getName());
		form.setDescription(getDescription());
		form.setImageBase64(getImage());

		// TODO: keySet from quantities?
		List<FlowerShopItem> flowerShopItems = new ArrayList<>();
		getCompoundFlowerShopProductFlowerShopItems()
				.forEach(compoundFlowerShopProductFlowerShopItem -> flowerShopItems
						.add(compoundFlowerShopProductFlowerShopItem.getFlowerShopItem()));

		form.setSelectedFlowerShopItems(flowerShopItems);

		Map<FlowerShopItem, Quantity> quantities = new HashMap<>();
		getCompoundFlowerShopProductFlowerShopItems()
				.forEach(compoundFlowerShopProductFlowerShopItem -> quantities
						.put(compoundFlowerShopProductFlowerShopItem.getFlowerShopItem(),
								compoundFlowerShopProductFlowerShopItem.getQuantity()));

		form.setQuantities(quantities);

		Iterable<FlowerShopService> serviceIterable = getFlowerShopServices();
		List<FlowerShopService> flowerShopServices = new ArrayList<>();
		serviceIterable.forEach(flowerShopServices::add);

		form.setSelectedFlowerShopServices(flowerShopServices);

		return form;
	}

}
