package kickstart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CatalogController {

	@GetMapping("/")
	public String index() {
		return "redirect:/products/";
	}

	@GetMapping("/products")
	public String products() {
		return "products";
	}
}
