package flowershop.user;

import flowershop.AbstractIntegrationTests;
import flowershop.user.form.UserDataTransferObject;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests interacting with the {@link UserManager} directly.
 *
 * @author Cornelius Kummer
 */
class UserManagerIntegrationTests extends AbstractIntegrationTests {

	@Autowired UserManager userManager;
	@Autowired UserAccountManager userAccountManager;
	@Autowired UserRepository userRepository;
	@Autowired AuthenticationManager authenticationManager;
	private UserDataTransferObject form = new UserDataTransferObject();


	@BeforeAll
	void setUp() {
		form.setFirstName("Dave");
		form.setLastName("Smith");
		form.setPassword("pass");
		form.setEmail("email");
		form.setPhone("123");

		assertThat(userManager.createUser(form)).isNotNull();
	}

	@Test
	void testFindAll() {
		Streamable<User> result = userManager.findAll();
		assertThat(result).isNotEmpty();
		result.forEach(System.out::println);
	}

	@Test
	void testFindByAccount() {
		UserAccount user = userAccountManager.findAll().stream().findFirst().get();
		Optional<User> result = userManager.findByAccount(user);
		assertThat(result).isNotEmpty();
	}

	@Test
	void testFindByUsername() {
		Optional<User> result = userManager.findByUsername("davesmith");
		assertThat(result).isNotEmpty();
	}

	@Test
	void testNameExists() {
		boolean result = userManager.nameExists("davesmith");
		assertThat(result).isTrue();
	}

	@Test
	void mailExists() {
		boolean result = userManager.mailExists("email");
		assertThat(result).isTrue();
	}

	@Test
	void testModifyUser() {
		form.setFirstName("Mike");
		UserAccount user = userAccountManager.findByUsername("davesmith").get();
		User result = userManager.modifyUser(form, user);
		assertThat(result.getUserAccount().getFirstname()).isEqualTo("Mike");
	}

	@Test
	void testChangePassword() {
		form.setPassword("newpass");
		UserAccount user = userAccountManager.findByUsername("davesmith").get();
		userManager.changePass(form, user);
		boolean result = authenticationManager.matches(Password.unencrypted("newpass"), user.getPassword());
		assertThat(result).isTrue();
	}

}
