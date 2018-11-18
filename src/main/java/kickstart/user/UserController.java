package kickstart.user;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

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
	String register(Model model, UserDto form, @LoggedIn Optional<UserAccount> loggedIn) {
		// Redirect to "My Account" if user is already logged in
		if (loggedIn.isPresent()) {
			return "redirect:/account";
		}

		model.addAttribute("form", form);
		return "register";
	}

	@PostMapping("/register")
	ModelAndView registerNew(@ModelAttribute("form") @Valid UserDto form, BindingResult result, HttpServletRequest request, Errors errors) {
		// Check form for errors
		try {
			checkForm(form, result, errors, true);
		} catch (FormErrorException e) {
			return new ModelAndView("register", "form", form);
		}

		userManagement.createUser(form);
		// Authenticate before redirecting
		try {
			String username = (form.getFirstName() + form.getLastName()).toLowerCase();
			request.login(username, form.getPassword());
		} catch (ServletException e) {}

		return new ModelAndView("forward:/", "message", String.format("Thank you for signing up, %s!", form.getFirstName()));
	}

	@GetMapping("/account")
	@PreAuthorize("isAuthenticated()")
	String account(Model model, UserDto form, @LoggedIn Optional<UserAccount> loggedIn) {
		populateForm(form, loggedIn.get());
		model.addAttribute("form", form);

		return "account";
	}

	@PostMapping("/account")
	@PreAuthorize("isAuthenticated()")
	ModelAndView updateAccount(Model model, @ModelAttribute("form") @Valid UserDto form, BindingResult result, @LoggedIn Optional<UserAccount> loggedIn, Errors errors) {
		try {
			checkForm(form, result, errors, false);
		} catch (FormErrorException e) {
			System.out.println(e);
			return new ModelAndView("account", "form", form);
		}

		userManagement.modifyUser(form, loggedIn.get());

		return new ModelAndView("account", "form", form);
	}

	@PostMapping("/account/changepass")
	@PreAuthorize("isAuthenticated()")
	ModelAndView changePassword(Model model, @ModelAttribute("form") @Valid UserDto form, BindingResult result, @LoggedIn Optional<UserAccount> loggedIn, Errors errors) {

		return new ModelAndView("account", "form", form);
	}

	@GetMapping("/users")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	String users(Model model) {
		model.addAttribute("userList", userManagement.findAll());

		return "users";
	}

	private void checkForm(@ModelAttribute("form") @Valid UserDto form, BindingResult result, Errors errors, boolean checkDuplicate) throws FormErrorException {
		// Check for validation errors
		if (result.hasErrors()) {
			// System.out.println("Reg error:" + result);
			throw new FormErrorException("Regular error exception!");
		}
		if (!checkDuplicate) {
			return;
		}
		// Check for already existing username / email
		String username = (form.getFirstName() + form.getLastName()).toLowerCase();
		if (userManagement.nameExists(username)) {
			errors.rejectValue("username", null, String.format("An account with the username '%s' already exists!", username));
			throw new FormErrorException("Username taken exception!");
		}
		if (userManagement.mailExists(form.getEmail())) {
			errors.rejectValue("email", null, String.format("An account with the email address '%s' already exists!", form.getEmail()));
			throw new FormErrorException("Email taken exception!");
		}
	}

	private void populateForm(UserDto form, @LoggedIn UserAccount loggedIn) {
		User user = userManagement.findByAccount(loggedIn).get();
		form.setFirstName(loggedIn.getFirstname());
		form.setLastName(loggedIn.getLastname());
		form.setEmail(loggedIn.getEmail());
		form.setPhone(user.getPhone());
	}

}
