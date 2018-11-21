package kickstart.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

		private static final String EMAIL_PATTERN = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-zA-Z0-9-]*[a-z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])+$";

		@Override
		public void initialize(ValidEmail constraintAnnotation) {
		}

		@Override
		public boolean isValid(String email, ConstraintValidatorContext context) {
				if (email == null) {
						return false;
				}
				return validateEmail(email);
		}

		private boolean validateEmail(String email) {
				Pattern pattern = Pattern.compile(EMAIL_PATTERN);
				Matcher matcher = pattern.matcher(email);
				return matcher.matches();
		}
}