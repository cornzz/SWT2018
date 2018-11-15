package kickstart.controller;

import kickstart.BouquetCatalog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BouquetCatalogController {

	private final BouquetCatalog bouquetCatalog;

	BouquetCatalogController(BouquetCatalog bouquetCatalog) {
		this.bouquetCatalog = bouquetCatalog;
	}

	@GetMapping("/")
	public String index() {
		return "redirect:/bouquets/";
	}

	@GetMapping("/bouquets")
	public String products(Model model) {

		model.addAttribute("bouquets", bouquetCatalog.findAll());

		return "bouquets";
	}
}
