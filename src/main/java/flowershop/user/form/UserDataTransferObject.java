package flowershop.user.form;

import flowershop.user.form.validation.MatchingPassword;
import flowershop.user.form.validation.ValidEmail;
import flowershop.user.form.validation.ValidOldPassword;
import flowershop.user.form.validation.ValidUsername;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Transfer object for {@link flowershop.user.User} data.
 *
 * @author Cornelius Kummer
 */
@MatchingPassword(message = "{Dto.password.MustMatch}", groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.ChangePassProcess.class})
@ValidUsername(groups = {UserDataTransferObject.RegistrationProcess.class})
public class UserDataTransferObject {

	@NotNull(groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.UpdateAccProcess.class})
	@NotEmpty(message = "{Dto.firstName.NotEmpty}", groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.UpdateAccProcess.class})
	private String firstName;

	@NotNull(groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.UpdateAccProcess.class})
	@NotEmpty(message = "{Dto.lastName.NotEmpty}", groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.UpdateAccProcess.class})
	private String lastName;

	private String username;

	@NotNull(groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.UpdateAccProcess.class})
	@NotEmpty(message = "{Dto.email.NotEmpty}", groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.UpdateAccProcess.class})
	@ValidEmail(message = "{Dto.email.Invalid}", groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.UpdateAccProcess.class})
	private String email;

	@NotNull(groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.UpdateAccProcess.class})
	@NotEmpty(message = "{Dto.phone.NotEmpty}", groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.UpdateAccProcess.class})
	private String phone;

	@NotNull(groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.ChangePassProcess.class})
	@NotEmpty(message = "{Dto.password.NotEmpty}", groups = {UserDataTransferObject.RegistrationProcess.class, UserDataTransferObject.ChangePassProcess.class})
	private String password;
	private String passwordRepeat;

	@NotNull(groups = {UserDataTransferObject.ChangePassProcess.class})
	@NotEmpty(message = "{Dto.password.NotEmpty}", groups = {UserDataTransferObject.ChangePassProcess.class})
	@ValidOldPassword(message = "{Dto.oldPassword.Invalid}", groups = {UserDataTransferObject.ChangePassProcess.class})
	private String oldPassword;

	private boolean isEmpty = false;

	private String action = "/account";

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

	public String getOldPassword() {
		return oldPassword;
	}

	public boolean getIsEmpty() {
		return isEmpty;
	}

	public String getAction() {
		return action;
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

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public void setIsEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public void setAction(String action) {
		this.action = action;
	}


	@Override
	public String toString() {
		return "UserDataTransferObject{" +
				"firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", username='" + username + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", password='" + password + '\'' +
				", passwordRepeat='" + passwordRepeat + '\'' +
				", oldPassword='" + oldPassword + '\'' +
				", isEmpty=" + isEmpty +
				'}';
	}

	public interface RegistrationProcess {
	}

	public interface UpdateAccProcess {
	}

	public interface ChangePassProcess {
	}
}
