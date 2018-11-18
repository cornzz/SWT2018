package kickstart.user;

import kickstart.validation.MatchingPassword;
import kickstart.validation.ValidEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@MatchingPassword(message = "{UserDto.password.MustMatch}")
public class UserDto {

	@NotNull
	@NotEmpty(message = "{UserDto.firstName.NotEmpty}")
	private String firstName;

	@NotNull
	@NotEmpty(message = "{UserDto.lastName.NotEmpty}")
	private String lastName;

	private String username;

	@ValidEmail(message = "{UserDto.email.Invalid}")
	@NotNull
	@NotEmpty(message = "{UserDto.email.NotEmpty}")
	private String email;

	@NotNull
	@NotEmpty(message = "{UserDto.phone.NotEmpty}")
	private String phone;

	@NotNull
	@NotEmpty(message = "{UserDto.password.NotEmpty}")
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
}
