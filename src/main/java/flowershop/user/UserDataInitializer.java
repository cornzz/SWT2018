package flowershop.user;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Initializes {@link User}s
 *
 * @author Cornelius Kummer
 */
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
		if (userRepository.findAll().iterator().hasNext()) {
			return;
		}

		UserAccount adminAccount = userAccountManager.create("admin", "pass", Role.of("ROLE_BOSS"));
		UserAccount wholesalerAccount = userAccountManager.create("wholesaler", "pass", Role.of("ROLE_WHOLESALER"));
		UserAccount userAccount1 = userAccountManager.create("johndoe", "pass", Role.of("ROLE_CUSTOMER"));
		UserAccount userAccount2 = userAccountManager.create("larrybird", "pass", Role.of("ROLE_CUSTOMER"));

		adminAccount.setFirstname("Ad");
		adminAccount.setLastname("Min");
		adminAccount.setEmail("ad@m.in");

		wholesalerAccount.setFirstname("Whole");
		wholesalerAccount.setLastname("Sale");
		wholesalerAccount.setEmail("whole@sa.le");

		userAccount1.setFirstname("John");
		userAccount1.setLastname("Doe");
		userAccount1.setEmail("john@doe.com");

		userAccount2.setFirstname("Larry");
		userAccount2.setLastname("Bird");
		userAccount2.setEmail("larry@bird.biz");

		userRepository.save(new User(adminAccount, "+1 234 56 789"));
		userRepository.save(new User(wholesalerAccount, "+6 555 43 210"));
		userRepository.save(new User(userAccount1, "+2 345 67 890"));
		userRepository.save(new User(userAccount2, "+9 876 54 321"));
	}

}
