package flowershop.products;

import flowershop.products.form.CompoundFlowerShopProductTransferObject;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;

import javax.persistence.*;
import java.util.*;

@Entity
public class CompoundFlowerShopProduct extends Product {
	private String description;

	@OneToMany(mappedBy = "compoundFlowerShopProduct", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
	private List<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProductFlowerShopItems;

	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<FlowerShopItem> flowerShopItems;

	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<FlowerShopService> flowerShopServices;

	@Type(type = "text")
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

		this.compoundFlowerShopProductFlowerShopItems = new ArrayList<>();

		// create relationships
		createCompoundFlowerShopProductFlowerShopItems(this.flowerShopItems);
	}

	public CompoundFlowerShopProduct(String name, String description, Map<FlowerShopItem, Quantity> flowerShopItemsWithQuantities, List<FlowerShopService> flowerShopServices, String image) {
		super(name, CompoundFlowerShopProduct.calcPrice(flowerShopItemsWithQuantities, flowerShopServices));

		this.description = description;
		this.flowerShopItems = new ArrayList<>(flowerShopItemsWithQuantities.keySet());
		this.flowerShopServices = flowerShopServices;
		this.image = image;

		this.compoundFlowerShopProductFlowerShopItems = new ArrayList<>();

		// create relationships
		flowerShopItemsWithQuantities.forEach((flowerShopItem, quantity) -> addFlowerShopItem(flowerShopItem, quantity));
	}

	private void createCompoundFlowerShopProductFlowerShopItems(List<FlowerShopItem> flowerShopItems) {
		for (FlowerShopItem flowerShopItem : flowerShopItems) {
			addFlowerShopItem(flowerShopItem, Quantity.of(1));
		}
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

	public Money getPrice() {
		return calcPrice(getFlowerShopItemsWithQuantities(), getFlowerShopServices());
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

	public List<CompoundFlowerShopProductFlowerShopItem> getCompoundFlowerShopProductFlowerShopItems() {
		return compoundFlowerShopProductFlowerShopItems;
	}

	// pure convenience, provides easier access to the product's ingredient quantities
	public Map<FlowerShopItem, Quantity> getFlowerShopItemsWithQuantities() {
		Map<FlowerShopItem, Quantity> flowerShopItemsWithQuantities = new HashMap<>();

		getCompoundFlowerShopProductFlowerShopItems()
				.forEach(compoundFlowerShopProductFlowerShopItem -> flowerShopItemsWithQuantities
						.put(compoundFlowerShopProductFlowerShopItem.getFlowerShopItem(), compoundFlowerShopProductFlowerShopItem.getQuantity()));

		return flowerShopItemsWithQuantities;
	}

	public void setCompoundFlowerShopProductFlowerShopItems(List<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProductFlowerShopItems) {
		this.compoundFlowerShopProductFlowerShopItems = compoundFlowerShopProductFlowerShopItems;
		refreshPrice();
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

	public List<FlowerShopService> getFlowerShopServices() {
		return flowerShopServices;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	// TODO: do we want this to be public?
	public void addCompoundFlowerShopProductFlowerShopItem(CompoundFlowerShopProductFlowerShopItem compoundFlowerShopProductFlowerShopItem) {
		compoundFlowerShopProductFlowerShopItems.add(compoundFlowerShopProductFlowerShopItem);
	}

	public CompoundFlowerShopProductFlowerShopItem addFlowerShopItem(FlowerShopItem flowerShopItem, Quantity quantity) {
		CompoundFlowerShopProductFlowerShopItem compoundFlowerShopProductFlowerShopItem = new CompoundFlowerShopProductFlowerShopItem();

		compoundFlowerShopProductFlowerShopItem.setCompoundFlowerShopProduct(this);
		compoundFlowerShopProductFlowerShopItem.setFlowerShopItem(flowerShopItem);
		compoundFlowerShopProductFlowerShopItem.setQuantity(quantity);

		addCompoundFlowerShopProductFlowerShopItem(compoundFlowerShopProductFlowerShopItem);

		return compoundFlowerShopProductFlowerShopItem;
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

	public void setFlowerShopServices(List<FlowerShopService> flowerShopServices) {
		this.flowerShopServices = flowerShopServices;
		refreshPrice();
	}

	// TODO: deprecated?
	public void refreshPrice() {
		super.setPrice(CompoundFlowerShopProduct.calcPrice(flowerShopItems, flowerShopServices));
	}

	public Optional<CompoundFlowerShopProductFlowerShopItem> getCompoundFlowerShopProductFlowerShopItemByFlowerShopItem(FlowerShopItem flowerShopItem) {
		return getCompoundFlowerShopProductFlowerShopItems().stream().filter(item -> item.getFlowerShopItem().equals(flowerShopItem)).findFirst();
	}

	public void removeCompoundFlowerShopProductFlowerShopItemByFlowerShopItem(FlowerShopItem flowerShopItem) {
		getCompoundFlowerShopProductFlowerShopItemByFlowerShopItem(flowerShopItem).ifPresent(compoundFlowerShopProductFlowerShopItem -> getCompoundFlowerShopProductFlowerShopItems().remove(compoundFlowerShopProductFlowerShopItem));
		flowerShopItems.remove(flowerShopItem);
		refreshPrice();
	}
}
