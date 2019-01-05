package flowershop.user.form.validation;

import flowershop.user.UserManager;
import flowershop.user.form.UserDataTransferObject;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, Object> {

	private UserManager userManager;

	public UsernameValidator(UserManager userManager) {
		this.userManager = userManager;
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
			context.unwrap(HibernateConstraintValidatorContext.class).addExpressionVariable("username", username);
			context.buildConstraintViolationWithTemplate("{Dto.username.Taken}")
					.addPropertyNode("username").addConstraintViolation(); // Register error on field "username"
		}
		return isValid;
	}
}