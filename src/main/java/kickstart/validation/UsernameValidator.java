package kickstart.validation;

import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import kickstart.user.UserDataTransferObject;
import kickstart.user.UserManagement;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;

public class UsernameValidator implements ConstraintValidator<ValidUsername, Object> {

		private AuthenticationManager authenticationManager;
		private UserManagement userManagement;

		public UsernameValidator(AuthenticationManager authenticationManager, UserManagement userManagement) {
				this.authenticationManager = authenticationManager;
				this.userManagement = userManagement;
		}

		@Override
		public void initialize(ValidUsername constraintAnnotation) {
		}

		@Override
		public boolean isValid(Object form, ConstraintValidatorContext context) {
				return validateUsername((UserDataTransferObject) form);
		}

		private boolean validateUsername(UserDataTransferObject form) {
				String username = (form.getFirstName() + form.getLastName()).toLowerCase();
				Optional<UserAccount> loggedIn = authenticationManager.getCurrentUser();
				System.out.println(username + " " + (userManagement.nameExists(username) && (!loggedIn.isPresent() || loggedIn.get().getUsername().equals(username))));
				return !userManagement.nameExists(username) || (loggedIn.isPresent() && loggedIn.get().getUsername().equals(username));
		}
}