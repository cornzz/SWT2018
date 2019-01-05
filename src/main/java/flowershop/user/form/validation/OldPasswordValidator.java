package flowershop.user.form.validation;

import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OldPasswordValidator implements ConstraintValidator<ValidOldPassword, String> {

	private AuthenticationManager authMgr;

	public OldPasswordValidator(AuthenticationManager authenticationManager) {
		this.authMgr = authenticationManager;
	}

	@Override
	public boolean isValid(String oldPasswordCandidate, ConstraintValidatorContext constraintValidatorContext) {
		if (oldPasswordCandidate == null || oldPasswordCandidate.isEmpty()) {
			return true; // In this case @NotNull / @NotEmpty takes over
		}
		return authMgr.getCurrentUser().isPresent() && authMgr.
				matches(Password.unencrypted(oldPasswordCandidate), authMgr.getCurrentUser().get().getPassword());
	}
}
