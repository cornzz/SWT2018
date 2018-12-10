package flowershop.user.form.validation;

import flowershop.user.form.UserDataTransferObject;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MatchingPasswordValidator implements ConstraintValidator<MatchingPassword, Object> {

		@Override
		public void initialize(MatchingPassword constraintAnnotation) {
		}

		@Override
		public boolean isValid(Object obj, ConstraintValidatorContext context) {
				UserDataTransferObject form = (UserDataTransferObject) obj;
				if (form.getPassword() == null || form.getPasswordRepeat() == null) {
						return false;
				}
				boolean isValid = form.getPassword().equals(form.getPasswordRepeat());
				if (!isValid) {
						context.disableDefaultConstraintViolation(); // Prevent global error registration
						context.buildConstraintViolationWithTemplate("{Dto.password.MustMatch}")
								.addPropertyNode("passwordRepeat").addConstraintViolation(); // Register error on field "username"
				}
				return isValid;
		}

}