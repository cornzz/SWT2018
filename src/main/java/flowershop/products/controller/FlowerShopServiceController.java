package flowershop.products.controller;


import flowershop.products.FlowerShopService;
import flowershop.products.FlowerShopServiceCatalog;
import org.javamoney.moneta.Money;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class FlowerShopServiceController {

	private final FlowerShopServiceCatalog serviceCatalog;

	FlowerShopServiceController(FlowerShopServiceCatalog serviceCatalog) {
		this.serviceCatalog = serviceCatalog;
	}

	@GetMapping("/products/services")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String services(Model model) {
		model.addAttribute("services", serviceCatalog.findAll());

		return "services";
	}

	@GetMapping("/products/services/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String add(Model model) {
		return "service_add";
	}

	@PostMapping("/products/services/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String add(String name, int price, String description) {
		FlowerShopService service = new FlowerShopService(name, Money.of(price, EURO), description);
		serviceCatalog.save(service);
		return "redirect:/products/services";
	}
}
