package kickstart.user;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class UserControllerAdvice {

	private final UserManagement userManagement;

	public UserControllerAdvice(UserManagement userManagement) {
		this.userManagement = userManagement;
	}

	@ModelAttribute
	public void addUserToModel(Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		loggedIn.ifPresent(u -> model.addAttribute("loggedIn", userManagement.findByAccount(u).get()));
	}

}
