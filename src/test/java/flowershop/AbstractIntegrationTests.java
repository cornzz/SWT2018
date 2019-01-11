package flowershop;

import flowershop.user.UserManager;
import flowershop.user.form.UserDataTransferObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
public abstract class AbstractIntegrationTests {

	@Autowired UserManager userManager;
	@Autowired UserAccountManager userAccountManager;

	@BeforeAll
	void setup() {
		System.out.println("Setup...");
		if (userAccountManager.findByUsername("test").isPresent()) {
			return;
		}
		UserDataTransferObject form = new UserDataTransferObject();
		form.setFirstName("te");
		form.setLastName("st");
		form.setPassword("test");
		form.setEmail("te@st.te");
		form.setPhone("test");

		assertThat(userManager.createUser(form)).isNotNull();
		userManager.addRole("test", Role.of("ROLE_BOSS"));
	}

}
