package kickstart.user;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserManagement {

	private final UserRepository users;
	private final UserAccountManager userAccounts;

	public UserManagement(UserRepository users, UserAccountManager userAccounts) {
		this.users = users;
		this.userAccounts = userAccounts;
	}

	public User createUser(RegistrationForm form) {

		UserAccount userAccount = userAccounts.create((form.getFirstName() + form.getLastName()).toLowerCase(), form.getPassword(), Role.of("ROLE_CUSTOMER"));
		userAccount.setFirstname(form.getFirstName());
		userAccount.setLastname(form.getLastName());
		userAccount.setEmail(form.getEmail());

		System.out.println("User " + userAccount.getUsername() + " registered. " + form.getPassword());
		return users.save(new User(userAccount, form.getPhone()));
	}

	public Streamable<User> findAll() {
		return Streamable.of(users.findAll());
	}

	public Optional<User> findByAccount(UserAccount userAccount) {
		return findByUsername(userAccount.getUsername());
	}

	public Optional<User> findByUsername(String username) {
		return this.findAll().stream().filter(u -> u.getUserAccount().getUsername().equals(username)).findFirst();
	}

}
