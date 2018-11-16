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

		if (result.hasErrors()) {
			return "redirect:/bouquets/add";
		}

		System.out.println(form.getName());
		System.out.println(form.getDescription());

		// TODO: get flowers and services from form
		// System.out.println(form.getFlowerIDs().size());
		// System.out.println(form.getServiceIDs().size());

		// convert list to iterable
		Iterable<Flower> flowersIterable = flowerCatalog.findAll();
		List<Flower> flowers = new ArrayList<>();
		flowersIterable.forEach(flowers::add);

		// convert list to iterable
		Iterable<Service> serviceIterable = serviceCatalog.findAll();
		List<Service> services = new ArrayList<>();
		serviceIterable.forEach(services::add);

		bouquetCatalog.save(new Bouquet(form.getName(), form.getDescription(), flowers, services));

		return "redirect:/bouquets";
	}
}
