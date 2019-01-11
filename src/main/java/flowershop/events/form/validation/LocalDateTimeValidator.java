package flowershop.events.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

/**
 * Validator for a String that is supposed to be converted to a {@link LocalDateTime}.
 *
 * @author Cornelius Kummer
 */
public class LocalDateTimeValidator implements ConstraintValidator<ValidLocalDateTime, String> {

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		LocalDateTime value;
		if (s == null || s.isEmpty()) {
			return true; // NotEmpty takes over
		}
		try {
			value = LocalDateTime.parse(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}