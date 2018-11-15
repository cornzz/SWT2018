package kickstart.user;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class UserDataInitializer implements DataInitializer {

	private final UserAccountManager userAccountManager;
	private final UserRepository userRepository;

	public UserDataInitializer(UserAccountManager userAccountManager, UserRepository userRepository) {
		this.userAccountManager = userAccountManager;
		this.userRepository = userRepository;
	}

	@Override
	public void initialize() {

		if (userAccountManager.findByUsername("admin").isPresent()) {
			return;
		}

		UserAccount adminAccount = userAccountManager.create("admin", "pass", Role.of("ROLE_BOSS"));
		adminAccount.setFirstname("ad");
		adminAccount.setLastname("min");
		adminAccount.setEmail("ad@mi.n");

		userRepository.save(new User(adminAccount, "+1 234 56 789"));

		userAccountManager.save(adminAccount);

	}
}
