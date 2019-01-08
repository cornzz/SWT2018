package flowershop.products;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompoundFlowerShopProductFlowerShopItemRepository extends CrudRepository<CompoundFlowerShopProductFlowerShopItem, CompoundFlowerShopProductFlowerShopItemId> {

}
