package flowershop.user.validation;

import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import flowershop.user.UserDataTransferObject;
import flowershop.user.UserManagement;
import org.salespointframework.useraccount.AuthenticationManager;

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
		public boolean isValid(Object obj, ConstraintValidatorContext context) {
				return validateUsername((UserDataTransferObject) obj, context);
		}

		private boolean validateUsername(UserDataTransferObject form, ConstraintValidatorContext context) {
				String username = (form.getFirstName() + form.getLastName()).replaceAll("\\s", "").toLowerCase();
				form.setUsername(username);
				boolean isValid = !userManagement.nameExists(username);
				if (!isValid) {
						context.disableDefaultConstraintViolation(); // Prevent global error registration
						context.buildConstraintViolationWithTemplate("{Dto.username.Taken}")
								.addPropertyNode("username").addConstraintViolation(); // Register error on field "username"
				}
				return isValid;
		}
}