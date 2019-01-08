package flowershop.user;

import flowershop.AbstractIntegrationTests;
import flowershop.user.form.UserDataTransferObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests interacting with the {@link UserManager} directly.
 *
 * @author Cornelius Kummer
 */
class UserManagerIntegrationTests extends AbstractIntegrationTests {

	@Autowired AuthenticationManager authenticationManager;
	private UserDataTransferObject form = new UserDataTransferObject();
	@Autowired UserManager userManager;
	@Autowired UserAccountManager userAccountManager;

	@BeforeAll
	void setup2() {
		form.setFirstName("Dave");
		form.setLastName("Smith");
		form.setPassword("pass");
		form.setEmail("email");
		form.setPhone("123");

		assertThat(userManager.createUser(form)).isNotNull();
	}

	@Test
	void findAllTest() {
		Streamable<User> result = userManager.findAll();
		assertThat(result).isNotEmpty();
		result.forEach(System.out::println);
	}

	@Test
	void findByAccountTest() {
		UserAccount user = userAccountManager.findAll().stream().findFirst().get();
		Optional<User> result = userManager.findByAccount(user);
		assertThat(result).isNotEmpty();
	}

	@Test
	void findByUsernameTest() {
		Optional<User> result = userManager.findByUsername("davesmith");
		assertThat(result).isNotEmpty();
	}

	@Test
	void nameExistsTest() {
		boolean result = userManager.nameExists("davesmith");
		assertThat(result).isTrue();
	}

	@Test
	void mailExistsTest() {
		boolean result = userManager.mailExists("email");
		assertThat(result).isTrue();
	}

	@Test
	void modifyUserTest() {
		form.setFirstName("Mike");
		UserAccount user = userAccountManager.findByUsername("davesmith").get();
		User result = userManager.modifyUser(form, user);
		assertThat(result.getUserAccount().getFirstname()).isEqualTo("Mike");
	}

	@Test
	void changePasswordTest() {
		form.setPassword("newpass");
		UserAccount user = userAccountManager.findByUsername("davesmith").get();
		userManager.changePass(form, user);
		boolean result = authenticationManager.matches(Password.unencrypted("newpass"), user.getPassword());
		assertTrue(result);
	}

	@Test
	void addRoleTest() {
		Role role = Role.of("ROLE_BOSS");
		userManager.addRole("davesmith", role);
		boolean result = userManager.findByUsername("davesmith").get().getUserAccount().hasRole(role);
		assertTrue(result);
	}

}
