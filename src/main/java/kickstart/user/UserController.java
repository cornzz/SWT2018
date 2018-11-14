package kickstart.user;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.swing.text.html.Option;
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
	String registerNew(@Valid RegistrationForm form, Errors result) {
		if (result.hasErrors()) {
			System.out.println("reg error");
			return "register";
		}

		userManagement.createUser(form);

		return "redirect:/";
	}

	@GetMapping("/account")
	@PreAuthorize("isAuthenticated()")
	String account(Model model, RegistrationForm form, @LoggedIn Optional<UserAccount> loggedIn) {
		model.addAttribute("form", form);

		return "account";
	}

}
