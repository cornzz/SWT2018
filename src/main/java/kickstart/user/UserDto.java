package kickstart.user;

import kickstart.validation.MatchingPassword;
import kickstart.validation.ValidEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@MatchingPassword(message = "{Dto.password.MustMatch}", groups = {UserDto.RegistrationProcess.class, UserDto.ChangePassProcess.class})
public class UserDto {

	@NotNull(groups = {UserDto.RegistrationProcess.class, UserDto.UpdateAccProcess.class})
	@NotEmpty(message = "{Dto.firstName.NotEmpty}", groups = {UserDto.RegistrationProcess.class, UserDto.UpdateAccProcess.class})
	private String firstName;

	@NotNull(groups = {UserDto.RegistrationProcess.class, UserDto.UpdateAccProcess.class})
	@NotEmpty(message = "{Dto.lastName.NotEmpty}", groups = {UserDto.RegistrationProcess.class, UserDto.UpdateAccProcess.class})
	private String lastName;

	private String username;

	@ValidEmail(message = "{Dto.email.Invalid}", groups = {UserDto.RegistrationProcess.class, UserDto.UpdateAccProcess.class})
	@NotNull(groups = {UserDto.RegistrationProcess.class, UserDto.UpdateAccProcess.class})
	@NotEmpty(message = "{Dto.email.NotEmpty}", groups = {UserDto.RegistrationProcess.class, UserDto.UpdateAccProcess.class})
	private String email;

	@NotNull(groups = {UserDto.RegistrationProcess.class, UserDto.UpdateAccProcess.class})
	@NotEmpty(message = "{Dto.phone.NotEmpty}", groups = {UserDto.RegistrationProcess.class, UserDto.UpdateAccProcess.class})
	private String phone;

	@NotNull(groups = {UserDto.RegistrationProcess.class, UserDto.ChangePassProcess.class})
	@NotEmpty(message = "{Dto.password.NotEmpty}", groups = {UserDto.RegistrationProcess.class, UserDto.ChangePassProcess.class})
	private String password;
	private String passwordRepeat;


	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPhone() {
		return this.phone;
	}

	public String getPassword() {
		return this.password;
	}

	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}

	@Override
	public String toString() {
		return "UserDto{" +
				"firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", username='" + username + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", password='" + password + '\'' +
				", passwordRepeat='" + passwordRepeat + '\'' +
				'}';
	}

	interface RegistrationProcess {
	}

	interface UpdateAccProcess {
	}

	interface ChangePassProcess {
	}
}
