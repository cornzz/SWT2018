package flowershop.products.controller;

import flowershop.products.*;
import flowershop.products.form.CompoundFlowerShopProductTransferObject;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.*;

/**
 * A Spring MVC controller to manage {@link CompoundFlowerShopProduct}s.
 *
 * @author Jonas Knobloch
 */
@Controller
public class CompoundFlowerShopProductCatalogController {

	private final CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog;
	private final FlowerShopItemCatalog flowerShopItemCatalog;
	private final FlowerShopServiceCatalog flowerShopServiceCatalog;
	private final Inventory<InventoryItem> inventory;

	private final CompoundFlowerShopProductFlowerShopItemRepository compoundFlowerShopProductFlowerShopItemRepository;

	/**
	 * Creates new {@link CompoundFlowerShopProductCatalogController}.
	 *
	 * @param compoundFlowerShopProductCatalog                  must not be {@literal null}.
	 * @param flowerShopItemCatalog                             must not be {@literal null}.
	 * @param flowerShopServiceCatalog                          must not be {@literal null}.
	 * @param compoundFlowerShopProductFlowerShopItemRepository must not be {@literal null}.
	 */
	CompoundFlowerShopProductCatalogController(CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog, FlowerShopItemCatalog flowerShopItemCatalog, FlowerShopServiceCatalog flowerShopServiceCatalog, Inventory<InventoryItem> inventory, CompoundFlowerShopProductFlowerShopItemRepository compoundFlowerShopProductFlowerShopItemRepository) {
		this.compoundFlowerShopProductCatalog = compoundFlowerShopProductCatalog;
		this.flowerShopItemCatalog = flowerShopItemCatalog;
		this.flowerShopServiceCatalog = flowerShopServiceCatalog;
		this.inventory = inventory;

		this.compoundFlowerShopProductFlowerShopItemRepository = compoundFlowerShopProductFlowerShopItemRepository;
	}

	// TODO: move to new controller
	@RequestMapping("/")
	public String index(@LoggedIn Optional<UserAccount> userAccountOptional) {
		if (!userAccountOptional.isPresent()) {
			return "home";
		}
		return "forward:/products";
	}

	/**
	 * Shows product catalog.
	 *
	 * @param model will never be {@literal null}.
	 * @return the view name.
	 */
	@RequestMapping("/products")
	public String products(Model model) {

		Iterable<CompoundFlowerShopProduct> compoundFlowerShopProducts = compoundFlowerShopProductCatalog.findAll();
		model.addAttribute("products", compoundFlowerShopProducts);
		compoundFlowerShopProducts.forEach(product -> product.setInStock(inStock(product)));

		return "products";
	}

	/**
	 * Shows add product dialog.
	 *
	 * @param model will never be {@literal null}.
	 * @param form  will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/products/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addProduct(Model model, CompoundFlowerShopProductTransferObject form) {

		model.addAttribute("form", form);

		model.addAttribute("flowerShopItems", flowerShopItemCatalog.findAll());
		model.addAttribute("flowerShopServices", flowerShopServiceCatalog.findAll());

		return "products_add";
	}

	/**
	 * Adds new {@link CompoundFlowerShopProduct} to the catalog.
	 *
	 * @param form   will never be {@literal null}.
	 * @param result will never be {@literal null}.
	 * @return redirect to products view.
	 */
	@PostMapping("/products/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addProduct(@Valid CompoundFlowerShopProductTransferObject form, Errors result) {
		if (result.hasErrors()) {
			return "redirect:/products/add";
		}

		compoundFlowerShopProductCatalog.save(form.convertToObject());

		return "redirect:/products";
	}

	/**
	 * Returns edit/detail view depending on the users role.
	 *
	 * @param id                  will never be {@literal null}.
	 * @param model               will never be {@literal null}.
	 * @param userAccountOptional must not be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/products/{id}")
	public String product(@PathVariable ProductIdentifier id, Model model, @LoggedIn Optional<UserAccount> userAccountOptional) {

		if (compoundFlowerShopProductCatalog.findById(id).isPresent()) {

			model.addAttribute("product", compoundFlowerShopProductCatalog.findById(id).get());

			return userAccountOptional.filter(userAccount -> userAccount.hasRole(Role.of("ROLE_BOSS"))).
					map(userAccount -> "redirect:/products/" + id + "/edit").orElse("products_detail");

		}

		return "redirect:/products";
	}

	/**
	 * Compares the given form values with the existing {@link CompoundFlowerShopProduct} and updates changed values.
	 *
	 * @param id     will never be {@literal null}.
	 * @param form   will never be {@literal null}.
	 * @param result will never be {@literal null}.
	 * @return redirect to products view.
	 */
	@PostMapping("/products/{id}/edit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String updateProduct(@PathVariable ProductIdentifier id, @Valid CompoundFlowerShopProductTransferObject form, Errors result) {

		if (result.hasErrors()) {
			// TODO: display error message
			return "redirect:/products/" + id + "/edit";
		}

		if (!compoundFlowerShopProductCatalog.findById(id).isPresent()) {
			return "redirect:/products";
		}

		CompoundFlowerShopProduct compoundFlowerShopProduct = compoundFlowerShopProductCatalog.findById(id).get();
		CompoundFlowerShopProduct newCompoundFlowerShopProduct = form.convertToObject();

		Set<CompoundFlowerShopProductFlowerShopItem> newRelations = new HashSet<>();
		Set<CompoundFlowerShopProductFlowerShopItem> doomedRelations = new HashSet<>();

		if (!newCompoundFlowerShopProduct.getName().equals(compoundFlowerShopProduct.getName())) {
			compoundFlowerShopProduct.setName(newCompoundFlowerShopProduct.getName());
		}

		if (!newCompoundFlowerShopProduct.getDescription().equals(compoundFlowerShopProduct.getDescription())) {
			compoundFlowerShopProduct.setDescription(newCompoundFlowerShopProduct.getDescription());
		}

		if (!newCompoundFlowerShopProduct.getImage().equals(compoundFlowerShopProduct.getImage())) {
			compoundFlowerShopProduct.setImage(newCompoundFlowerShopProduct.getImage());
		}

		if (!newCompoundFlowerShopProduct.getFlowerShopItemsWithQuantities().equals(compoundFlowerShopProduct.getFlowerShopItemsWithQuantities())) {

			Map<FlowerShopItem, Quantity> newQuantities = newCompoundFlowerShopProduct.getFlowerShopItemsWithQuantities();
			Map<FlowerShopItem, Quantity> currentQuantities = compoundFlowerShopProduct.getFlowerShopItemsWithQuantities();

			// different quantity objects since the generated relationship object do not persist!

			Set<FlowerShopItem> newItems = newQuantities.keySet();
			Set<FlowerShopItem> currentItems = currentQuantities.keySet();

			// new items
			for (FlowerShopItem newFlowerShopItem : newItems) {
				if (!currentItems.contains(newFlowerShopItem)) {

					Optional<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProductFlowerShopItemOptional = newCompoundFlowerShopProduct.getCompoundFlowerShopProductFlowerShopItemByFlowerShopItem(newFlowerShopItem);

					if (compoundFlowerShopProductFlowerShopItemOptional.isPresent()) {
						CompoundFlowerShopProductFlowerShopItem compoundFlowerShopProductFlowerShopItem = compoundFlowerShopProductFlowerShopItemOptional.get();

						compoundFlowerShopProductFlowerShopItem.setCompoundFlowerShopProduct(compoundFlowerShopProduct);
						compoundFlowerShopProduct.addCompoundFlowerShopProductFlowerShopItem(compoundFlowerShopProductFlowerShopItem);

						newRelations.add(compoundFlowerShopProductFlowerShopItem);
					}
				}
			}

			// remove removed
			for (FlowerShopItem currentFlowerShopItem : currentItems) {
				if (!newItems.contains(currentFlowerShopItem)) {

					// remove relation object from compound product
					compoundFlowerShopProduct.removeCompoundFlowerShopProductFlowerShopItemByFlowerShopItem(currentFlowerShopItem);

					// remove relation object from flower shop item
					currentFlowerShopItem.removeCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(compoundFlowerShopProduct);

					Iterable<CompoundFlowerShopProductFlowerShopItem> relations = compoundFlowerShopProductFlowerShopItemRepository.findAll();

					// doom relating compoundFlowerShopProductFlowerShopItem
					for (CompoundFlowerShopProductFlowerShopItem relation : relations) {
						if (relation.getCompoundFlowerShopProduct().equals(compoundFlowerShopProduct) && relation.getFlowerShopItem().equals(currentFlowerShopItem)) {
							doomedRelations.add(relation);
						}
					}
				}
			}

			// changed quantities
			for (Map.Entry<FlowerShopItem, Quantity> entry : newQuantities.entrySet()) {

				if (currentQuantities.containsKey(entry.getKey())) {

					// Red FlowerShopItem 1.00 -> 1 ("fresh" quantity vs persisted quantity) -> convert to BigInteger -> I don't give a shit anymore

					BigInteger currentQuantity = currentQuantities.get(entry.getKey()).getAmount().toBigInteger();
					BigInteger newQuantity = entry.getValue().getAmount().toBigInteger();

					if (!newQuantity.equals(currentQuantity)) {

						// update quantity in compoundFlowerShopProductFlowerShopItem
						Optional<CompoundFlowerShopProductFlowerShopItem> compoundFlowerShopProductFlowerShopItemOptional = compoundFlowerShopProduct.getCompoundFlowerShopProductFlowerShopItemByFlowerShopItem(entry.getKey());
						compoundFlowerShopProductFlowerShopItemOptional.ifPresent(compoundFlowerShopProductFlowerShopItem -> compoundFlowerShopProductFlowerShopItem.setQuantity(entry.getValue()));
					}
				}
			}
		}

		if (!newCompoundFlowerShopProduct.getFlowerShopServices().equals(compoundFlowerShopProduct.getFlowerShopServices())) {
			compoundFlowerShopProduct.setFlowerShopServices(newCompoundFlowerShopProduct.getFlowerShopServices());
		}

		// no need to save -> cascade types for the win
		// compoundFlowerShopProductFlowerShopItemRepository.saveAll(newRelations);
		compoundFlowerShopProductFlowerShopItemRepository.deleteAll(doomedRelations);

		compoundFlowerShopProductCatalog.save(compoundFlowerShopProduct);

		return "redirect:/products";
	}

	/**
	 * Returns the edit product view if a {@link CompoundFlowerShopProduct} with the given {@link ProductIdentifier} if found.
	 *
	 * @param id    will never be {@literal null}.
	 * @param model will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/products/{id}/edit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String editProduct(@PathVariable ProductIdentifier id, Model model) {

		if (!compoundFlowerShopProductCatalog.findById(id).isPresent()) {
			return "redirect:/products";
		}

		CompoundFlowerShopProduct compoundFlowerShopProduct = compoundFlowerShopProductCatalog.findById(id).get();

		CompoundFlowerShopProductTransferObject form = compoundFlowerShopProduct.createTransferObject();

		model.addAttribute("flowerShopItems", flowerShopItemCatalog.findAll());
		model.addAttribute("flowerShopServices", flowerShopServiceCatalog.findAll());
		model.addAttribute("form", form);

		return "products_edit";
	}

	/**
	 * Deletes the compound product with the given {@link ProductIdentifier} and returns the product view.
	 *
	 * @param id    will never be {@literal null}.
	 * @return the view name.
	 */
	@PostMapping("/products/{id}/delete")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String deleteProduct(@PathVariable ProductIdentifier id) {

		Optional<CompoundFlowerShopProduct> compoundFlowerShopProductOptional = compoundFlowerShopProductCatalog.findById(id);

		if (compoundFlowerShopProductOptional.isPresent()) {

			CompoundFlowerShopProduct compoundFlowerShopProduct = compoundFlowerShopProductOptional.get();

			// there is no need to remove the relation from the compound product since it gets deleted anyway

			// remove relation object from flower shop items
			compoundFlowerShopProduct
					.getFlowerShopItemsWithQuantities()
					.keySet()
					.forEach(flowerShopItem -> flowerShopItem.removeCompoundFlowerShopProductFlowerShopItemByCompoundFlowerShopProduct(compoundFlowerShopProduct));

			compoundFlowerShopProductCatalog.delete(compoundFlowerShopProduct);
		}

		return "redirect:/products";
	}

	public boolean inStock(CompoundFlowerShopProduct product) {
		Map<FlowerShopItem, Quantity> itemQuantities = product.getFlowerShopItemsWithQuantities();
		return itemQuantities.keySet().stream().allMatch(flowerShopItem ->
				inventory.findByProductIdentifier(flowerShopItem.getId()).
						map(item -> item.hasSufficientQuantity(itemQuantities.get(flowerShopItem))).
						orElse(false)
		);
	}
}
