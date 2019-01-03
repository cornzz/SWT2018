package flowershop.user.form.validation;

import flowershop.user.UserManager;
import flowershop.user.form.UserDataTransferObject;
import org.salespointframework.useraccount.AuthenticationManager;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, Object> {

		private AuthenticationManager authenticationManager;
		private UserManager userManager;

		public UsernameValidator(AuthenticationManager authenticationManager, UserManager userManager) {
				this.authenticationManager = authenticationManager;
				this.userManager = userManager;
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
				boolean isValid = !userManager.nameExists(username);
				if (!isValid) {
						context.disableDefaultConstraintViolation(); // Prevent global error registration
						context.buildConstraintViolationWithTemplate("{Dto.username.Taken}")
								.addPropertyNode("username").addConstraintViolation(); // Register error on field "username"
				}
				return isValid;
		}
}