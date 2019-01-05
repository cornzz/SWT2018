package flowershop.user.form.validation;

import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for a given password that is supposed to be the password of a {@link flowershop.user.User} entity.
 *
 * @author Cornelius Kummer
 */
public class OldPasswordValidator implements ConstraintValidator<ValidOldPassword, String> {

	private AuthenticationManager authMgr;

	public OldPasswordValidator(AuthenticationManager authenticationManager) {
		this.authMgr = authenticationManager;
	}

	/**
	 * @param oldPasswordCandidate
	 * @param constraintValidatorContext must not be {@literal null}.
	 * @return <code>true</code> if given password matches the currently logged in users password; <code>false</code> otherwise.
	 */
	@Override
	public boolean isValid(String oldPasswordCandidate, ConstraintValidatorContext constraintValidatorContext) {
		if (oldPasswordCandidate == null || oldPasswordCandidate.isEmpty()) {
			return true; // In this case @NotNull / @NotEmpty takes over
		}
		return authMgr.getCurrentUser().isPresent() && authMgr.
				matches(Password.unencrypted(oldPasswordCandidate), authMgr.getCurrentUser().get().getPassword());
	}
}
