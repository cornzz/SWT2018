package flowershop.accounting.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DoubleValidator implements ConstraintValidator<IsDouble, String> {

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		Double value;
		try {
			value = Double.valueOf(s);
		} catch (Exception e) {
			return false;
		}
		return !(value >= Double.POSITIVE_INFINITY) && !(value <= Double.NEGATIVE_INFINITY);
	}

}
