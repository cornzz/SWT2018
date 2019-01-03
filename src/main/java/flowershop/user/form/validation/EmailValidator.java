package flowershop.user.form.validation;

import flowershop.user.UserManager;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

		private static final String EMAIL_PATTERN = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-zA-Z0-9-]*[a-z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])+$";
		private AuthenticationManager authenticationManager;
		private UserManager userManager;

		public EmailValidator(AuthenticationManager authenticationManager, UserManager userManager) {
				this.authenticationManager = authenticationManager;
				this.userManager = userManager;
		}

		@Override
		public boolean isValid(String email, ConstraintValidatorContext context) {
				if (email == null || email.isEmpty()) {
						// In this case @NotNull / @NotEmpty takes over
						return true;
				}
				return validateEmail(email);
		}

		private boolean validateEmail(String email) {
				Optional<UserAccount> loggedIn = authenticationManager.getCurrentUser();
				if (userManager.mailExists(email) && (!loggedIn.isPresent() || !loggedIn.get().getEmail().equals(email))) {
						return false;
				}
				Pattern pattern = Pattern.compile(EMAIL_PATTERN);
				Matcher matcher = pattern.matcher(email);
				return matcher.matches();
		}
}