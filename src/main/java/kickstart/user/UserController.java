package kickstart.user;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class UserController {

		private final UserManagement userManagement;
		private final AuthenticationManager authenticationManager;

		public UserController(UserManagement userManagement, AuthenticationManager authenticationManager) {
				this.userManagement = userManagement;
				this.authenticationManager = authenticationManager;
		}

		@GetMapping("/register")
		String register(Model model, UserDataTransferObject form, @LoggedIn Optional<UserAccount> loggedIn) {
				if (loggedIn.isPresent()) {
						return "redirect:/account";
				}

				model.addAttribute("form", form);
				return "register";
		}

		@PostMapping("/register")
		ModelAndView registerNew(@ModelAttribute("form") @Validated(UserDataTransferObject.RegistrationProcess.class) UserDataTransferObject form, BindingResult result,
				HttpServletRequest request, @LoggedIn Optional<UserAccount> loggedIn) {
				if (loggedIn.isPresent()) {
						return new ModelAndView("redirect:/account", "form", null);
				}
				if (result.hasErrors()) {
						return new ModelAndView("register", "form", form);
				}

				userManagement.createUser(form);
				// Authenticate before redirecting
				try {
						String username = (form.getFirstName() + form.getLastName()).toLowerCase();
						request.login(username, form.getPassword());
				} catch (ServletException e) {
				}

				return new ModelAndView("forward:/", "message", String.format("Thank you for signing up, %s!", form.getFirstName()));
		}

		@GetMapping("/account")
		@PreAuthorize("isAuthenticated()")
		String account(Model model, UserDataTransferObject form, @LoggedIn Optional<UserAccount> loggedIn) {
				populateForm(form, loggedIn.get());
				model.addAttribute("form", form);

				return "account";
		}

		@PostMapping("/account")
		@PreAuthorize("isAuthenticated()")
		ModelAndView updateAccount(Model model, @ModelAttribute("form") @Validated(UserDataTransferObject.UpdateAccProcess.class) UserDataTransferObject form, BindingResult result,
				@LoggedIn Optional<UserAccount> loggedIn) {
				if (result.hasErrors()) {
						return new ModelAndView("account", "form", form);
				}

				userManagement.modifyUser(form, loggedIn.get());

				return new ModelAndView("redirect:/account?success", "form", form);
		}

		@GetMapping("/account/changepass")
		@PreAuthorize("isAuthenticated()")
		String redirectAccount() {
				return "redirect:/account";
		}

		@PostMapping("/account/changepass")
		@PreAuthorize("isAuthenticated()")
		ModelAndView changePassword(Model model, @ModelAttribute("form") @Validated(UserDataTransferObject.ChangePassProcess.class) UserDataTransferObject form, BindingResult result,
				@LoggedIn Optional<UserAccount> loggedIn) {
				if (result.hasErrors()) {
						populateForm(form, loggedIn.get());
						return new ModelAndView("account", "form", form);
				}

				userManagement.changePass(form, loggedIn.get());

				return new ModelAndView("redirect:/account?success", "form", form);
		}

		@GetMapping("/users")
		@PreAuthorize("hasRole('ROLE_BOSS')")
		String users(Model model) {
				model.addAttribute("userList", userManagement.findAll());

				return "users";
		}

		private void populateForm(UserDataTransferObject form, @LoggedIn UserAccount loggedIn) {
				User user = userManagement.findByAccount(loggedIn).get();
				form.setFirstName(loggedIn.getFirstname());
				form.setLastName(loggedIn.getLastname());
				form.setEmail(loggedIn.getEmail());
				form.setPhone(user.getPhone());
		}

}
