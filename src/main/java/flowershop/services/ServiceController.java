package flowershop.services;

import org.javamoney.moneta.Money;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import static org.salespointframework.core.Currencies.EURO;

@Controller
public class ServiceController {

	private final ServiceCatalog serviceCatalog;

	ServiceController(ServiceCatalog serviceCatalog){
		this.serviceCatalog = serviceCatalog;
	}

	@GetMapping("/products/services")
	public String services(Model model) {
		model.addAttribute("services", serviceCatalog.findAll());

		return "services";
	}

	@GetMapping("/products/services/add")
	public String add(Model model){
		return "service_add";
	}

	// Add a new Service
	@PostMapping("/products/services/add")
	public String add(String name, int price, String description){
		Service service = new Service(name, Money.of(price,EURO), description);
		serviceCatalog.save(service);
		return "redirect:/products/services";
	}
}
