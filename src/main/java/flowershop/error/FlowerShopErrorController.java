package flowershop.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FlowerShopErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handleError(HttpServletRequest request, Model model) {

		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");

		model.addAttribute("statusCode", statusCode);
		model.addAttribute("exception", exception);

		return "error";
	}

	@RequestMapping("/test/error")
	public void testError() {
		throw new RuntimeException("I'm a teapot");
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}
