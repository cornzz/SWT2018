package flowershop.products.controller;

import flowershop.products.CompoundFlowerShopProduct;
import flowershop.products.CompoundFlowerShopProductCatalog;
import flowershop.products.FlowerShopItemCatalog;
import flowershop.products.FlowerShopServiceCatalog;
import flowershop.products.form.AddCompoundFlowerShopProductForm;
import flowershop.products.form.EditCompoundFlowerShopProductForm;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class CompoundFlowerShopProductCatalogController {

	private final CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog;
	private final FlowerShopItemCatalog flowerShopItemCatalog;
	private final FlowerShopServiceCatalog flowerShopServiceCatalog;

	CompoundFlowerShopProductCatalogController(CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog, FlowerShopItemCatalog flowerShopItemCatalog, FlowerShopServiceCatalog flowerShopServiceCatalog) {
		this.compoundFlowerShopProductCatalog = compoundFlowerShopProductCatalog;
		this.flowerShopItemCatalog = flowerShopItemCatalog;
		this.flowerShopServiceCatalog = flowerShopServiceCatalog;
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

		model.addAttribute("products", compoundFlowerShopProductCatalog.findAll());

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

		compoundFlowerShopProductCatalog.save(new CompoundFlowerShopProduct(form.getName(), form.getDescription(), form.getSelectedFlowerShopItems(), form.getSelectedFlowerShopServices()));

		return "redirect:/products";
	}


	@GetMapping("/products/{id}")
	public String product(@PathVariable ProductIdentifier id, Model model, @LoggedIn Optional<UserAccount> userAccountOptional) {

		if (compoundFlowerShopProductCatalog.findById(id).isPresent()) {

			model.addAttribute("product", compoundFlowerShopProductCatalog.findById(id).get());

			if (!userAccountOptional.isPresent()) {
				return "products_detail";
			}

			return userAccountOptional.get().hasRole(Role.of("ROLE_BOSS")) ? "products_edit" : "products_detail";
		}

		return "redirect:/products";
	}

	@PostMapping("/products/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String editProduct(@PathVariable ProductIdentifier id, @Valid EditCompoundFlowerShopProductForm form, Errors result) {
		if (result.hasErrors()) {
			return "redirect:/products/" + id.getIdentifier();
		}

		if (compoundFlowerShopProductCatalog.findById(id).isPresent()) {
			CompoundFlowerShopProduct compoundFlowerShopProduct = compoundFlowerShopProductCatalog.findById(id).get();

			compoundFlowerShopProduct.setName(form.getName());
			compoundFlowerShopProduct.setDescription(form.getDescription());

			compoundFlowerShopProductCatalog.save(compoundFlowerShopProduct);

			return "redirect:/products/";
		}

		return "redirect:/products/" + id.getIdentifier();
	}
}
