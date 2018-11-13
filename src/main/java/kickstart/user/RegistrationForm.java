package kickstart.user;

import javax.validation.constraints.NotEmpty;

public interface RegistrationForm {

	@NotEmpty(message = "{RegistrationForm.firstName.NotEmpty}")
	String getFirstName();

	@NotEmpty(message = "{RegistrationForm.lastName.NotEmpty}")
	String getLastName();

	@NotEmpty(message = "{RegistrationForm.email.NotEmpty}")
	String getEmail();

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}")
	String getPassword();

	@NotEmpty(message = "{RegistrationForm.phone.NotEmpty}")
	String getPhone();

}
