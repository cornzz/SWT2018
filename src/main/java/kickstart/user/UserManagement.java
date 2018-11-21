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
		private final UserAccountManager userAccountManager;

		public UserManagement(UserRepository users, UserAccountManager userAccountManager) {
				this.users = users;
				this.userAccountManager = userAccountManager;
		}

		public User createUser(UserDto form) {
				UserAccount userAccount = userAccountManager
						.create((form.getFirstName() + form.getLastName()).toLowerCase(), form.getPassword(), Role.of("ROLE_CUSTOMER"));
				userAccount.setFirstname(form.getFirstName());
				userAccount.setLastname(form.getLastName());
				userAccount.setEmail(form.getEmail());

				return users.save(new User(userAccount, form.getPhone()));
		}

		public User modifyUser(UserDto form, UserAccount userAccount) {
				User user = findByAccount(userAccount).get();
				userAccount.setFirstname(form.getFirstName());
				userAccount.setLastname(form.getLastName());
				userAccount.setEmail(form.getEmail());
				user.setPhone(form.getPhone());

				return users.save(user);
		}

		public void changePass(UserDto form, UserAccount userAccount) {
				userAccountManager.changePassword(userAccount, form.getPassword());
		}

		public Streamable<User> findAll() {
				return Streamable.of(users.findAll());
		}

		public Optional<User> findByAccount(UserAccount userAccount) {
				return this.findAll().stream().filter(u -> u.getUserAccount().equals(userAccount)).findFirst();
		}

		public boolean nameExists(String username) {
				return this.findAll().stream().anyMatch(u -> u.getUserAccount().getUsername().equals(username));
		}

		public boolean mailExists(String email) {
				return this.findAll().stream().anyMatch(u -> u.getUserAccount().getEmail().equals(email));
		}

}
