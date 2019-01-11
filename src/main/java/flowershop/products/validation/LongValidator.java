package flowershop.products.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for a String that is supposed to be converted to a Long.
 *
 * @author Cornelius Kummer
 */
public class LongValidator implements ConstraintValidator<IsLong, String> {

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		Long value;
		if (s == null || s.isEmpty()) {
			return true; // NotEmpty takes over
		}
		try {
			value = Long.valueOf(s);
		} catch (Exception e) {
			return false;
		}
		return !(value >= Long.MAX_VALUE || value <= Long.MIN_VALUE);
	}

}
