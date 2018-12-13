package flowershop.user;

import flowershop.user.form.UserDataTransferObject;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class UserController {

	private final UserManager userManager;
	private final AuthenticationManager authenticationManager;

	public UserController(UserManager userManager, AuthenticationManager authenticationManager) {
		this.userManager = userManager;
		this.authenticationManager = authenticationManager;
	}


	@GetMapping("/login")
	String login(@LoggedIn Optional<UserAccount> loggedIn) {
		return loggedIn.isPresent() ? "redirect:/products" : "login";
	}

	@GetMapping("/register")
	ModelAndView register(UserDataTransferObject form, @LoggedIn Optional<UserAccount> loggedIn) {
		if (loggedIn.isPresent()) {
			return new ModelAndView("redirect:/account");
		}

		return new ModelAndView("register", "form", form);
	}

	@PostMapping("/register")
	ModelAndView registerNew(@ModelAttribute("form") @Validated(UserDataTransferObject.RegistrationProcess.class) UserDataTransferObject form,
							 BindingResult result, HttpServletRequest request, @LoggedIn Optional<UserAccount> loggedIn) {
		if (loggedIn.isPresent()) {
			return new ModelAndView("redirect:/account");
		}
		if (result.hasErrors()) {
			return new ModelAndView("register", "form", form);
		}

		userManager.createUser(form);
		// Authenticate before redirecting
		try {
			String username = (form.getFirstName() + form.getLastName()).toLowerCase();
			request.login(username, form.getPassword());
		} catch (ServletException e) {
		}

		return new ModelAndView("forward:/", "newUser", form.getFirstName());
	}

	@GetMapping("/account")
	@PreAuthorize("isAuthenticated()")
	ModelAndView myAccount(UserDataTransferObject form, @LoggedIn Optional<UserAccount> loggedIn) {

		populateForm(form, loggedIn.get());

		return new ModelAndView("account", "form", form);
	}

	@PostMapping("/account")
	@PreAuthorize("isAuthenticated()")
	ModelAndView updateMyAccount(@ModelAttribute("form") @Validated(UserDataTransferObject.UpdateAccProcess.class) UserDataTransferObject form,
							   BindingResult result, @LoggedIn Optional<UserAccount> loggedIn) {
		if (result.hasErrors()) {
			return new ModelAndView("account", "form", form);
		}

		userManager.modifyUser(form, loggedIn.get());

		return new ModelAndView("redirect:/account?success");
	}

	@GetMapping("/account/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView account(@PathVariable(name = "id") Optional<UserAccount> userAccount, UserDataTransferObject form) {
		form.setIsEmpty(true);
		userAccount.ifPresent(account -> {
			populateForm(form, account);
			form.setAction("/account/" + account.getId());
		});

		return new ModelAndView("account", "form", form);
	}

	@PostMapping("/account/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView updateAccount(@PathVariable(name = "id") Optional<UserAccount> userAccount, @ModelAttribute("form") UserDataTransferObject form) {
		return userAccount.map(account -> {
			userManager.modifyUser(form, account);
			return new ModelAndView("redirect:/account/{id}?success");
		}).orElse(new ModelAndView("redirect:/account/{id}"));
	}

	@GetMapping("/account/changepass")
	@PreAuthorize("isAuthenticated()")
	ModelAndView changeMyPassword(UserDataTransferObject form) {
		return new ModelAndView("account_changepass", "form", form);
	}

	@PostMapping("/account/changepass")
	@PreAuthorize("isAuthenticated()")
	ModelAndView changeMyPassword(@ModelAttribute("form") @Validated(UserDataTransferObject.ChangePassProcess.class) UserDataTransferObject form,
								BindingResult result, @LoggedIn Optional<UserAccount> loggedIn) {
		if (result.hasErrors()) {
			populateForm(form, loggedIn.get());
			return new ModelAndView("account_changepass", "form", form);
		}

		userManager.changePass(form, loggedIn.get());

		return new ModelAndView("redirect:/account?success");
	}

	@GetMapping("/account/{id}/changepass")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView changePassword(@PathVariable(name = "id") Optional<UserAccount> userAccount, UserDataTransferObject form) {
		form.setIsEmpty(true);
		userAccount.ifPresent(account -> {
			form.setIsEmpty(false);
			form.setAction("/account/" + account.getId());
		});

		return new ModelAndView("account_changepass_admin", "form", form);
	}

	@PostMapping("/account/{id}/changepass")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView changePassword(@PathVariable(name = "id") Optional<UserAccount> userAccount, @ModelAttribute("form") UserDataTransferObject form, @LoggedIn Optional<UserAccount> loggedIn) {
		return userAccount.map(account -> {
			userManager.changePass(form, account);
			return new ModelAndView("redirect:/account/{id}?success");
		}).orElse(new ModelAndView("account_changepass_admin"));
	}

	@GetMapping("/users")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView users(Model model) {
		return new ModelAndView("users", "userList", userManager.findAll());
	}

	private void populateForm(UserDataTransferObject form, @LoggedIn UserAccount loggedIn) {
		User user = userManager.findByAccount(loggedIn).get();
		form.setFirstName(loggedIn.getFirstname());
		form.setLastName(loggedIn.getLastname());
		form.setEmail(loggedIn.getEmail());
		form.setPhone(user.getPhone());
		form.setIsEmpty(false);
	}

}
