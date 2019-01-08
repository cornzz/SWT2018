package flowershop.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class FlowerShopErrorController implements ErrorController {

	@ExceptionHandler(Exception.class)
	public void errorHandler(HttpServletResponse response, Exception e) throws IOException {
		response.sendError(500, e.toString());
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

	@RequestMapping("/error")
	public String handleError(HttpServletRequest request, Model model) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
		String message = (String) request.getAttribute("javax.servlet.error.message");
		model.addAttribute("statusCode", statusCode);
		model.addAttribute("exception", exception);
		model.addAttribute("message", message);

		return "error";
	}

	@RequestMapping("/test/error")
	public void testError() {
		throw new RuntimeException("I'm a teapot");
	}

	@RequestMapping("/accessDenied")
	public void handleAccessDenied(HttpServletResponse response) throws IOException {
		response.sendError(403, "Access Denied.");
	}

}
