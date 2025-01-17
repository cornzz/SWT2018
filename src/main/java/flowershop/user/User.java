package flowershop.user;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.stream.Collectors;

/**
 * @author Cornelius Kummer
 */
@Entity
public class User {

	private @Id
	@GeneratedValue
	long id;

	private String phone;

	@OneToOne
	private UserAccount userAccount;

	@SuppressWarnings("unused")
	private User() {
	}

	public User(UserAccount userAccount, String phone) {
		this.userAccount = userAccount;
		this.phone = phone;
	}

	public long getId() {
		return id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public Streamable<Role> getRoles() {
		return this.userAccount.getRoles();
	}

	public String getRoleString() {
		return getRoles().get().map(r -> r.toString().replace("ROLE_", "")).collect(Collectors.joining(", "));
	}

}
