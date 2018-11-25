package flowershop.products.controller;

import flowershop.products.CompoundFlowerShopProduct;
import flowershop.products.CompoundFlowerShopProductCatalog;
import flowershop.products.FlowerShopItemCatalog;
import flowershop.products.FlowerShopServiceCatalog;
import flowershop.products.form.AddCompoundFlowerShopProductForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

	@GetMapping("/")
	public String index() {
		return "redirect:/products/";
	}

	@GetMapping("/products")
	public String products(Model model) {

		model.addAttribute("products", compoundFlowerShopProductCatalog.findAll());

		return "products";
	}

	@GetMapping("/products/add")
	public String addProduct(Model model, AddCompoundFlowerShopProductForm form) {

		model.addAttribute("form", form);

		model.addAttribute("flowerShopItems", flowerShopItemCatalog.findAll());
		model.addAttribute("flowerShopServices", flowerShopServiceCatalog.findAll());

		return "products_add";
	}

	@PostMapping("/products/add")
	public String addProduct(@Valid AddCompoundFlowerShopProductForm form, Errors result) {

		// TODO: use form validator to check if there is at least one product selected
		if (result.hasErrors() || (form.getSelectedFlowerShopItems().isEmpty() && form.getSelectedFlowerShopServices().isEmpty())) {
			return "redirect:/products/add";
		}

		compoundFlowerShopProductCatalog.save(new CompoundFlowerShopProduct(form.getName(), form.getDescription(), form.getSelectedFlowerShopItems(), form.getSelectedFlowerShopServices()));

		return "redirect:/products";
	}
}
