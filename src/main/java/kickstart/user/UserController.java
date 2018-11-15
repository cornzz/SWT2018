package kickstart.user;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class UserController {

	private final UserManagement userManagement;

	public UserController(UserManagement userManagement) {
		this.userManagement = userManagement;
	}

	@GetMapping("/register")
	String register(Model model, RegistrationForm form) {
		model.addAttribute("form", form);
		return "register";
	}

	@PostMapping("/register")
	String registerNew(Model model, @Valid RegistrationForm form, Errors result, HttpServletRequest request) {
		if (result.hasErrors()) {
			System.out.println("Reg error");
			return "register";
		}
		String username = (form.getFirstName() + form.getLastName()).toLowerCase();
		if (userManagement.nameExists(username)) {
			model.addAttribute("userError", String.format("An account with the username '%s' already exists!", username));
			return "register";
		}
		if (userManagement.mailExists(form.getEmail())) {
			model.addAttribute("emailError", String.format("An account with the email address '%s' already exists!", form.getEmail()));
			return "register";
		}

		userManagement.createUser(form);
		// Authenticate before redirecting
		try {
			request.login(username, form.getPassword());
		} catch (ServletException e) {};
		model.addAttribute("welcomeMsg", String.format("Thank you for signing up, %s!", form.getFirstName()));

		return "forward:/";
	}

	@GetMapping("/account")
	@PreAuthorize("isAuthenticated()")
	String account(Model model, RegistrationForm form, @LoggedIn Optional<UserAccount> loggedIn) {
		model.addAttribute("form", form);

		return "account";
	}

	@PostMapping("/account")
	@PreAuthorize("isAuthenticated()")
	String updateAccount(Model model, RegistrationForm form, @LoggedIn Optional<UserAccount> loggedIn) {
		model.addAttribute("form", form);

		return "account";
	}

}
