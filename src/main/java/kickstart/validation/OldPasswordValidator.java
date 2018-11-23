package kickstart.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;

public class OldPasswordValidator implements ConstraintValidator<ValidOldPassword, String> {

		private AuthenticationManager authMgr;

		public OldPasswordValidator(AuthenticationManager authenticationManager) {
				this.authMgr = authenticationManager;
		}

		@Override
		public void initialize(ValidOldPassword constraintAnnotation) {
		}

		@Override
		public boolean isValid(String oldPasswordCandidate, ConstraintValidatorContext constraintValidatorContext) {
				return authMgr.getCurrentUser().isPresent() && authMgr
						.matches(Password.unencrypted(oldPasswordCandidate), authMgr.getCurrentUser().get().getPassword());
		}
}
