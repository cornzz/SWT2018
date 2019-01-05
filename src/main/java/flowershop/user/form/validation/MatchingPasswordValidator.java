package flowershop.user.form.validation;

import flowershop.user.form.UserDataTransferObject;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for pair of passwords.
 *
 * @author Cornelius Kummer
 */
public class MatchingPasswordValidator implements ConstraintValidator<MatchingPassword, Object> {

	/**
	 * Extracts a {@link UserDataTransferObject} from given {@link Object} and validates equality the <code>password</code>
	 * and <code>passwordRepeat</code> fields of said {@link UserDataTransferObject}.
	 *
	 * @param obj     must not be {@literal null}.
	 * @param context must not be {@literal null}.
	 * @return <code>true</code> if the entered password pair is equal; <code>false</code> otherwise.
	 */
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