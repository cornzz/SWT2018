package flowershop.products.controller;


import flowershop.products.FlowerShopService;
import flowershop.products.FlowerShopServiceCatalog;
import flowershop.products.form.AddFlowerShopServiceForm;
import org.javamoney.moneta.Money;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class FlowerShopServiceController {

	private final FlowerShopServiceCatalog serviceCatalog;

	FlowerShopServiceController(FlowerShopServiceCatalog serviceCatalog) {
		this.serviceCatalog = serviceCatalog;
	}

	@GetMapping("/services")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String services(Model model) {
		model.addAttribute("services", serviceCatalog.findAll());

		return "services";
	}

	@GetMapping("/services/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public ModelAndView add(Model model, AddFlowerShopServiceForm form) {

		return new ModelAndView("service_add", "form", form);
	}

	@PostMapping("/services/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public ModelAndView add(@ModelAttribute("form") @Validated AddFlowerShopServiceForm form, BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("service_add", "form", form);
		}
		FlowerShopService service = new FlowerShopService(form.getName(), Money.of(Double.valueOf(form.getPrice()), EURO), form.getDescription());
		serviceCatalog.save(service);
		return new ModelAndView("redirect:/services");
	}
}
