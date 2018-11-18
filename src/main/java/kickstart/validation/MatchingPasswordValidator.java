package kickstart.validation;

import kickstart.user.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MatchingPasswordValidator implements ConstraintValidator<MatchingPassword, Object> {

	@Override
	public void initialize(MatchingPassword constraintAnnotation) {
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context){
		UserDto form = (UserDto) obj;
		// System.out.println(form.getPassword() + ' ' + form.getPasswordRepeat());
		// System.out.println(form.getPassword().equals(form.getPasswordRepeat()));
		return form.getPassword().equals(form.getPasswordRepeat());
	}

}