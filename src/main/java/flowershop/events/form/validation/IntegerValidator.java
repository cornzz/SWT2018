package flowershop.events.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for a String that is supposed to be converted to an Integer
 *
 * @author Cornelius Kummer
 */
public class IntegerValidator implements ConstraintValidator<IsInteger, String> {

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		Integer value;
		if (s == null || s.isEmpty()) {
			return true; // NotEmpty takes over
		}
		try {
			value = Integer.valueOf(s);
			if (value < 0 || value > 1000000) {
				throw new Exception();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
