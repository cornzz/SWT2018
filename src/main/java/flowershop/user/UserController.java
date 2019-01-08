package flowershop.user;

import flowershop.user.form.UserDataTransferObject;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * A Spring MVC controller to manage {@link User}s.
 *
 * @author Cornelius Kummer
 */
@Controller
public class UserController {

	private final UserManager userManager;

	/**
	 * Creates a new {@link UserController} with the given {@link UserManager}.
	 *
	 * @param userManager must not be {@literal null}.
	 */
	public UserController(UserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * Lets the user log in.
	 *
	 * @param loggedIn will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping("/login")
	String login(@LoggedIn Optional<UserAccount> loggedIn) {
		return loggedIn.isPresent() ? "redirect:/products" : "login";
	}


	/**
	 * Shows the registration form.
	 *
	 * @param form     will never be {@literal null}.
	 * @param loggedIn will never be {@literal null}.
	 * @return the view name and, if the user is not logged in, the registration form object.
	 */
	@GetMapping("/register")
	ModelAndView register(UserDataTransferObject form, @LoggedIn Optional<UserAccount> loggedIn) {
		if (loggedIn.isPresent()) {
			return new ModelAndView("redirect:/account");
		}

		return new ModelAndView("register", "form", form);
	}

	/**
	 * Registers a new {@link User}.
	 *
	 * @param form     must not be {@literal null}.
	 * @param result   will never be {@literal null}.
	 * @param request  will never be {@literal null}.
	 * @param loggedIn will never be {@literal null}.
	 * @return the view name and, if registration is not successful, the registration form object.
	 */
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
		} catch (ServletException ignored) {}

		return new ModelAndView("forward:/", "newUser", form.getFirstName());
	}

	/**
	 * Lets the user view his account data.
	 *
	 * @param form     will never be {@literal null}.
	 * @param loggedIn will never be {@literal null}.
	 * @return the view name and the account data form object.
	 */
	@GetMapping("/account")
	@PreAuthorize("isAuthenticated()")
	ModelAndView myAccount(UserDataTransferObject form, @LoggedIn Optional<UserAccount> loggedIn) {
		loggedIn.ifPresent(userAccount -> populateForm(form, userAccount));

		return new ModelAndView("account", "form", form);
	}

	/**
	 * Lets the user update his account data.
	 *
	 * @param form     will never be {@literal null}.
	 * @param result   will never be {@literal null}.
	 * @param loggedIn will never be {@literal null}.
	 * @return the view name and, if update process unsuccessful, the account data form object.
	 */
	@PostMapping("/account")
	@PreAuthorize("isAuthenticated()")
	ModelAndView updateMyAccount(@ModelAttribute("form") @Validated(UserDataTransferObject.UpdateAccProcess.class) UserDataTransferObject form,
								 BindingResult result, @LoggedIn Optional<UserAccount> loggedIn) {
		if (result.hasErrors()) {
			return new ModelAndView("account", "form", form);
		}
		loggedIn.ifPresent(userAccount -> userManager.modifyUser(form, userAccount));

		return new ModelAndView("redirect:/account?success");
	}

	/**
	 * Lets the admin view the account data of any user.
	 *
	 * @param userAccount must not be {@literal null}.
	 * @param form        will never be {@literal null}.
	 * @return the view name and the account data form object.
	 */
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

	/**
	 * Lets the admin update the account data of any user.
	 *
	 * @param userAccount must not be {@literal null}.
	 * @param form        must not be {@literal null}.
	 * @return the view name.
	 */
	@PostMapping("/account/{id}")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView updateAccount(@PathVariable(name = "id") Optional<UserAccount> userAccount, @ModelAttribute("form") UserDataTransferObject form) {
		return userAccount.map(account -> {
			userManager.modifyUser(form, account);
			return new ModelAndView("redirect:/account/{id}?success");
		}).orElse(new ModelAndView("redirect:/account/{id}"));
	}

	/**
	 * Displays the password change form.
	 *
	 * @param form will never be {@literal null}.
	 * @return the view name and account data form object.
	 */
	@GetMapping("/account/changepass")
	@PreAuthorize("isAuthenticated()")
	ModelAndView changeMyPasswordForm(UserDataTransferObject form) {
		return new ModelAndView("account_changepass", "form", form);
	}

	/**
	 * Lets the user update his password
	 *
	 * @param form     must not be {@literal null}.
	 * @param result   will never be {@literal null}.
	 * @param loggedIn will never be {@literal null}.
	 * @return the view name and, if update process unsuccessful, the account data form object.
	 */
	@PostMapping("/account/changepass")
	@PreAuthorize("isAuthenticated()")
	ModelAndView changeMyPassword(@ModelAttribute("form") @Validated(UserDataTransferObject.ChangePassProcess.class) UserDataTransferObject form,
								  BindingResult result, @LoggedIn Optional<UserAccount> loggedIn) {
		if (result.hasErrors()) {
			populateForm(form, loggedIn.get());
			return new ModelAndView("account_changepass", "form", form);
		}

		loggedIn.ifPresent(userAccount -> userManager.changePass(form, userAccount));

		return new ModelAndView("redirect:/account?success");
	}

	/**
	 * Displays the password change form for the admin.
	 *
	 * @param userAccount must not be {@literal null}.
	 * @param form        will never be {@literal null}.
	 * @return the view name and account data form object.
	 */
	@GetMapping("/account/{id}/changepass")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView changePasswordForm(@PathVariable(name = "id") Optional<UserAccount> userAccount, UserDataTransferObject form) {
		form.setIsEmpty(true);
		userAccount.ifPresent(account -> {
			form.setIsEmpty(false);
			form.setAction("/account/" + account.getId());
		});

		return new ModelAndView("account_changepass_admin", "form", form);
	}

	/**
	 * Lets the admin update the password of any user.
	 *
	 * @param userAccount must not be {@literal null}.
	 * @param form        must not be {@literal null}.
	 * @return the view name.
	 */
	@PostMapping("/account/{id}/changepass")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView changePassword(@PathVariable(name = "id") Optional<UserAccount> userAccount, @ModelAttribute("form") UserDataTransferObject form) {
		return userAccount.map(account -> {
			userManager.changePass(form, account);
			return new ModelAndView("redirect:/account/{id}?success");
		}).orElse(new ModelAndView("account_changepass_admin"));
	}

	/**
	 * Displays all {@link User}s in the system to the admin.
	 *
	 * @param model will never be {@literal null}.
	 * @return the view name and all existing {@link User} instances
	 */
	@RequestMapping("/users")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	ModelAndView users(ModelAndView model) {
		model.addObject("userList", userManager.findAll()).setViewName("users");
		return model;
	}


	/**
	 * Populates the given {@link UserDataTransferObject} with the account data of the given {@link UserAccount}.
	 *
	 * @param form        must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 */
	void populateForm(UserDataTransferObject form, UserAccount userAccount) {
		userManager.findByAccount(userAccount).ifPresent(user -> {
			form.setFirstName(userAccount.getFirstname());
			form.setLastName(userAccount.getLastname());
			form.setEmail(userAccount.getEmail());
			form.setPhone(user.getPhone());
			form.setIsEmpty(false);
		});
	}

}
