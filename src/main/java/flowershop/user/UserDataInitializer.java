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
		if (userAccountManager.findAll().iterator().hasNext()) {
			return;
		}

		UserAccount adminAccount = userAccountManager.create("rosafloris", "pass", Role.of("ROLE_BOSS"));
		UserAccount wholesalerAccount = userAccountManager.create("maxgrosshandel", "pass", Role.of("ROLE_WHOLESALER"));
		UserAccount userAccount1 = userAccountManager.create("johndoe", "pass", Role.of("ROLE_CUSTOMER"));
		UserAccount userAccount2 = userAccountManager.create("lenamaier", "pass", Role.of("ROLE_CUSTOMER"));

		adminAccount.setFirstname("Rosa");
		adminAccount.setLastname("Floris");
		adminAccount.setEmail("rosafloris@web.de");

		wholesalerAccount.setFirstname("Max");
		wholesalerAccount.setLastname("Grosshandel");
		wholesalerAccount.setEmail("max@grosshandel.de");

		userAccount1.setFirstname("John");
		userAccount1.setLastname("Doe");
		userAccount1.setEmail("johndoe@gmail.com");

		userAccount2.setFirstname("Lena");
		userAccount2.setLastname("Maier");
		userAccount2.setEmail("lenamaier@gmx.net");

		userRepository.save(new User(adminAccount, "+49 234 56 789"));
		userRepository.save(new User(wholesalerAccount, "+49 555 43 210"));
		userRepository.save(new User(userAccount1, "+1 345 67 890"));
		userRepository.save(new User(userAccount2, "+49 876 54 321"));
	}

}
