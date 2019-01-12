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

/**
 * A Spring MVC controller to manage {@link FlowerShopService}s.
 *
 * @author Friedrich Bethke
 */
@Controller
public class FlowerShopServiceController {

	private final FlowerShopServiceCatalog serviceCatalog;

	/**
	 * Creates a new {@link FlowerShopServiceController} with the given {@link FlowerShopServiceCatalog}.
	 *
	 * @param serviceCatalog must not be {@literal null}.
	 */
	FlowerShopServiceController(FlowerShopServiceCatalog serviceCatalog) {
		this.serviceCatalog = serviceCatalog;
	}

	/**
	 * Shows all available services.
	 *
	 * @param model will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/services")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String services(Model model) {
		model.addAttribute("services", serviceCatalog.findAll());

		return "services";
	}

	/**
	 * Shows the form for adding a new service.
	 *
	 * @param model will never be {@literal null}.
	 * @param form  will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/services/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public ModelAndView add(Model model, AddFlowerShopServiceForm form) {

		return new ModelAndView("service_add", "form", form);
	}

	/**
	 * Adds a new service.
	 *
	 * @param form   will never be {@literal null}.
	 * @param result will never be {@literal null}.
	 * @return the view name and, if adding was not successful, the adding form object.
	 */
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
