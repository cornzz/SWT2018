package flowershop.catalog;
import org.salespointframework.catalog.Catalog;


public interface ItemCatalog extends  Catalog<FlowerShopItem>{
	/*
	static final Sort DEFAULT_SORT = new Sort(Sort.Direction.DESC, "productIdentifier");

	Iterable<FlowerShopItem> findByType(ItemType type, Sort sort);

	default Iterable<FlowerShopItem> findByType(FlowerShopItem.ItemType type) { return findByType(type, DEFAULT_SORT); }
	*/
}
