package flowershop.user;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

/**
 * {@link ControllerAdvice} for all controllers in this project.
 *
 * @author Cornelius Kummer
 */
@ControllerAdvice
public class UserControllerAdvice {

	private final UserManager userManager;

	public UserControllerAdvice(UserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * Adds an {@link Optional}<{@link UserAccount}> to the {@link Model} during all controller calls in this project.
	 *
	 * @param model    will never be {@literal null}.
	 * @param loggedIn will never be {@literal null}.
	 */
	@ModelAttribute
	public void addUserToModel(Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		loggedIn.ifPresent(u -> model.addAttribute("loggedIn", userManager.findByAccount(u).get()));
	}

}
