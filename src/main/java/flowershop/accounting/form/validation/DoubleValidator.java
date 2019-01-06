package flowershop.accounting.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for a String that is supposed to be converted to a Double
 *
 * @author Cornelius Kummer
 */
public class DoubleValidator implements ConstraintValidator<IsDouble, String> {

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		Double value;
		if (s.isEmpty()) {
			return true; // NotEmpty takes over
		}
		try {
			value = Double.valueOf(s);
		} catch (Exception e) {
			return false;
		}
		return !(value >= Double.POSITIVE_INFINITY) && !(value <= Double.NEGATIVE_INFINITY);
	}

}
