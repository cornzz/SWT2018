package flowershop.user;

import flowershop.user.form.UserDataTransferObject;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages {@link User} instances in the system.
 *
 * @author Cornelius Kummer
 */
@Service
@Transactional
public class UserManager {

	private final UserRepository users;
	private final UserAccountManager userAccountManager;

	/**
	 * Creates a new {@link UserManager} with the given {@link UserRepository} and {@link UserAccountManager}.
	 *
	 * @param users              must not be {@literal null}.
	 * @param userAccountManager must not be {@literal null}.
	 */
	public UserManager(UserRepository users, UserAccountManager userAccountManager) {
		this.users = users;
		this.userAccountManager = userAccountManager;
	}


	/**
	 * Creates a new {@link User} using the data given in the {@link UserDataTransferObject}.
	 *
	 * @param form must not be {@literal null}.
	 * @return the new {@link User} instance.
	 */
	public User createUser(UserDataTransferObject form) {
		UserAccount userAccount = userAccountManager
				.create((form.getFirstName() + form.getLastName()).toLowerCase(), form.getPassword(), Role.of("ROLE_CUSTOMER"));
		userAccount.setFirstname(form.getFirstName());
		userAccount.setLastname(form.getLastName());
		userAccount.setEmail(form.getEmail());

		return users.save(new User(userAccount, form.getPhone()));
	}


	/**
	 * Modifies the {@link User} entity that contains the given {@link UserAccount} instance using the data
	 * given in the {@link UserDataTransferObject}.
	 *
	 * @param form        must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return the modified {@link User} instance.
	 */
	public User modifyUser(UserDataTransferObject form, UserAccount userAccount) {
		User user = findByAccount(userAccount).get();
		userAccount.setFirstname(form.getFirstName());
		userAccount.setLastname(form.getLastName());
		userAccount.setEmail(form.getEmail());
		user.setPhone(form.getPhone());

		return users.save(user);
	}

	/**
	 * Changes the password of the {@link User} entity that contains the given {@link UserAccount} using the data
	 * given in the {@link UserDataTransferObject}.
	 *
	 * @param form        must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 */
	public void changePass(UserDataTransferObject form, UserAccount userAccount) {
		userAccountManager.changePassword(userAccount, form.getPassword());
	}

	/**
	 * @return all {@link User} entities currently registered in the system.
	 */
	public Streamable<User> findAll() {
		return Streamable.of(users.findAll());
	}

	/**
	 * Adds a given {@link Role} to the {@link UserAccount} of the {@link User} entity with the given username
	 *
	 * @param username must not be {@literal null}.
	 * @param role     must not be {@literal null}.
	 * @return <code>true</code> if {@link User} with given username exists and role was added; <code>false</code> otherwise.
	 */
	public boolean addRole(String username, Role role) {
		return userAccountManager.findByUsername(username).map(userAccount -> userAccount.add(role)).orElse(false);
	}

	/**
	 * Sets the roles of the given {@link UserAccount).
	 *
	 * @param user  must not be {@literal null}.
	 * @param roles must not be {@literal null}.
	 */
	public void setRoles(UserAccount user, Streamable<String> roles) {
		List<Role> currentRoles = user.getRoles().get().collect(Collectors.toList());
		currentRoles.forEach(user::remove);
		roles.map(Role::of).forEach(user::add);
		userAccountManager.save(user);
	}


	/**
	 * @param userAccount must not be {@literal null}.
	 * @return an {@link Optional} of the {@link User} entity that contains the given {@link UserAccount} instance.
	 */
	public Optional<User> findByAccount(UserAccount userAccount) {
		return this.findAll().stream().filter(u -> u.getUserAccount().equals(userAccount)).findFirst();
	}

	/**
	 * @param username must not be {@literal null}.
	 * @return an {@link Optional} of the {@link User} entity with the given username.
	 */
	public Optional<User> findByUsername(String username) {
		return this.findAll().stream().filter(u -> u.getUserAccount().getUsername().equals(username)).findFirst();
	}

	/**
	 * @param email must not be {@literal null}.
	 * @return an {@link Optional} of the {@link User} entity with the given Email address.
	 */
	public Optional<User> findByEmail(String email) {
		return this.findAll().stream().filter(u -> u.getUserAccount().getEmail().equals(email)).findFirst();
	}

	/**
	 * @param username must not be {@literal null}.
	 * @return <code>true</code> if a {@link User} instance with the given username exists; <code>false</code> otherwise.
	 */
	public boolean nameExists(String username) {
		return this.findAll().stream().anyMatch(u -> u.getUserAccount().getUsername().equals(username));
	}

	/**
	 * @param email must not be {@literal null}.
	 * @return <code>true</code> if a {@link User} instance with the given email exists; <code>false</code> otherwise.
	 */
	public boolean mailExists(String email) {
		return this.findByEmail(email).isPresent();
	}

}
