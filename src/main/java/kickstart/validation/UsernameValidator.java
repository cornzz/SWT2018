package kickstart.validation;

import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import kickstart.user.UserDataTransferObject;
import kickstart.user.UserManagement;
import org.apache.tomcat.util.bcel.Const;
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
				return validateUsername((UserDataTransferObject) form, context);
		}

		private boolean validateUsername(UserDataTransferObject form, ConstraintValidatorContext context) {
				String username = (form.getFirstName() + form.getLastName()).toLowerCase();
				form.setUsername(username);
				Optional<UserAccount> loggedIn = authenticationManager.getCurrentUser();
				boolean isValid = !userManagement.nameExists(username) || (loggedIn.isPresent() && loggedIn.get().getUsername().equals(username));
				if (!isValid) {
						context.disableDefaultConstraintViolation(); // Prevent global error registration
						context.buildConstraintViolationWithTemplate("{Dto.username.Taken}")
								.addPropertyNode("username").addConstraintViolation(); // Register error on field "username"
				}
				return isValid;
		}
}