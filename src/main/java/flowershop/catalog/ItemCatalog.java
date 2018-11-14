package flowershop.catalog;
import flowershop.catalog.Item.*;
import org.springframework.data.domain.Sort.Direction;
import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;


public interface ItemCatalog extends  Catalog<Item>{

	static final Sort DEFAULT_SORT = new Sort(Sort.Direction.DESC, "productIdentifier");

	Iterable<Item> findByType(ItemType type, Sort sort);

	default Iterable<Item> findByType(Item.ItemType type) { return findByType(type, DEFAULT_SORT); }

}
