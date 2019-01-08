package flowershop.user.form.validation;

import flowershop.user.UserManager;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for modern e-mail addresses.
 *
 * @author Cornelius Kummer
 */
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
			return true; // In this case @NotNull / @NotEmpty takes over
		}
		return validateEmail(email);
	}

	/**
	 * Checks if a {@link flowershop.user.User} instance with given e-mail is already registered, if so, returns <code>false</code>
	 * if that {@link flowershop.user.User} instance doesn't belong to the currently logged in user. Furthermore checks,
	 * if the given e-mail matches the pattern of a common e-mail address.
	 *
	 * @param email must not be {@literal null}.
	 * @return <code>true</code> if given e-mail is valid; <code>false</code> otherwise.
	 */
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