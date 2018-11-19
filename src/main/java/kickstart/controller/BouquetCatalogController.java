package kickstart.controller;

import kickstart.*;
import kickstart.form.BouquetForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BouquetCatalogController {

	private final BouquetCatalog bouquetCatalog;
	private final FlowerCatalog flowerCatalog;
	private final ServiceCatalog serviceCatalog;

	BouquetCatalogController(BouquetCatalog bouquetCatalog, FlowerCatalog flowerCatalog, ServiceCatalog serviceCatalog) {
		this.bouquetCatalog = bouquetCatalog;
		this.flowerCatalog = flowerCatalog;
		this.serviceCatalog = serviceCatalog;
	}

	@GetMapping("/")
	public String index() {
		return "redirect:/bouquets/";
	}

	@GetMapping("/bouquets")
	public String bouquets(Model model) {

		model.addAttribute("bouquets", bouquetCatalog.findAll());

		return "bouquets";
	}

	@GetMapping("/bouquets/add")
	public String addBouquets(Model model, BouquetForm form) {

		model.addAttribute("form", form);

		model.addAttribute("flowers", flowerCatalog.findAll());
		model.addAttribute("services", serviceCatalog.findAll());

		return "bouquets_add";
	}

	@PostMapping("/bouquets/add")
	public String addBouquets(@Valid BouquetForm form, Errors result) {

		// TODO: use form validator to check if there is at least one product selected
		if (result.hasErrors() || (form.getSelectedFlowers().isEmpty() && form.getSelectedServices().isEmpty())) {
			return "redirect:/bouquets/add";
		}

		bouquetCatalog.save(new Bouquet(form.getName(), form.getDescription(), form.getSelectedFlowers(), form.getSelectedServices()));

		return "redirect:/bouquets";
	}
}
