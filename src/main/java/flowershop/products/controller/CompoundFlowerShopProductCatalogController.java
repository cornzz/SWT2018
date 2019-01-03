package flowershop.products.controller;

import flowershop.products.*;
import flowershop.products.form.AddCompoundFlowerShopProductForm;
import flowershop.products.form.CompoundFlowerShopProductTransferObject;
import org.salespointframework.catalog.ProductIdentifier;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
public class CompoundFlowerShopProductCatalogController {

	private final CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog;
	private final FlowerShopItemCatalog flowerShopItemCatalog;
	private final FlowerShopServiceCatalog flowerShopServiceCatalog;

	private final CompoundFlowerShopProductFlowerShopItemRepository compoundFlowerShopProductFlowerShopItemRepository;

	CompoundFlowerShopProductCatalogController(CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog, FlowerShopItemCatalog flowerShopItemCatalog, FlowerShopServiceCatalog flowerShopServiceCatalog, CompoundFlowerShopProductFlowerShopItemRepository compoundFlowerShopProductFlowerShopItemRepository) {
		this.compoundFlowerShopProductCatalog = compoundFlowerShopProductCatalog;
		this.flowerShopItemCatalog = flowerShopItemCatalog;
		this.flowerShopServiceCatalog = flowerShopServiceCatalog;

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

	@RequestMapping("/products")
	public String products(Model model) {

		Iterable<CompoundFlowerShopProduct> compoundFlowerShopProducts = compoundFlowerShopProductCatalog.findAll();

		model.addAttribute("products", compoundFlowerShopProducts);

		return "products";
	}

	@GetMapping("/products/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addProduct(Model model, AddCompoundFlowerShopProductForm form) {

		model.addAttribute("form", form);

		model.addAttribute("flowerShopItems", flowerShopItemCatalog.findAll());
		model.addAttribute("flowerShopServices", flowerShopServiceCatalog.findAll());

		return "products_add";
	}

	@PostMapping("/products/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addProduct(@Valid AddCompoundFlowerShopProductForm form, Errors result) {
		if (result.hasErrors()) {
			return "redirect:/products/add";
		}

		compoundFlowerShopProductCatalog.save(new CompoundFlowerShopProduct(form.getName(), form.getDescription(), form.getSelectedFlowerShopItemsWithQuantities(), form.getSelectedFlowerShopServices(), form.getImageBase64()));

		return "redirect:/products";
	}


	@GetMapping("/products/{id}")
	public String product(@PathVariable ProductIdentifier id, Model model, @LoggedIn Optional<UserAccount> userAccountOptional) {

		if (compoundFlowerShopProductCatalog.findById(id).isPresent()) {

			model.addAttribute("product", compoundFlowerShopProductCatalog.findById(id).get());

			if (!userAccountOptional.isPresent()) {
				return "products_detail";
			}

			return userAccountOptional.get().hasRole(Role.of("ROLE_BOSS")) ? "redirect:/products/" + id + "/edit" : "products_detail";
		}

		return "redirect:/products";
	}

	@PostMapping("/products/{id}/edit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String updateProductBar(@PathVariable ProductIdentifier id, CompoundFlowerShopProductTransferObject form) {

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
						compoundFlowerShopProduct.refreshPrice(); // refresh price manually
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

	@GetMapping("/products/{id}/edit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String editProductFoo(@PathVariable ProductIdentifier id, Model model) {

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
}
