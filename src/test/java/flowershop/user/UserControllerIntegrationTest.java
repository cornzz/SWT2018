package flowershop.user;

import flowershop.AbstractIntegrationTests;
import flowershop.user.form.UserDataTransferObject;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests interacting with the {@link UserController} directly.
 *
 * @author Cornelius Kummer
 */
class UserControllerIntegrationTest extends AbstractIntegrationTests {

	@Autowired UserController controller;
	@Autowired UserManager userManager;


	@Test
	void returnXIfNotAuthenticated() {
		assertThat(controller.login(Optional.empty())).isEqualTo("login");
		assertThat(controller.register(new UserDataTransferObject(), Optional.empty()).getViewName()).isEqualTo("register");
	}

	@Test
	void returnXIfAuthenticated() {
		assertThat(controller.login(Optional.of(new UserAccount()))).isEqualTo("redirect:/products");
		assertThat(controller.register(new UserDataTransferObject(), Optional.of(new UserAccount())).getViewName()).isEqualTo("redirect:/account");
	}

	@Test
	void rejectsUnauthenticatedAccess() {
		assertThatExceptionOfType(AuthenticationException.class)
				.isThrownBy(() -> controller.users(new ModelAndView()));
	}

	@Test
	@WithMockUser(roles = "BOSS")
	void allowsAuthenticatedAccess() {
		ModelAndView model = new ModelAndView();
		controller.users(model);
		assertThat(model.getModel().get("userList")).isNotNull();
	}

	@Test
	void populateFormTest() {
		UserDataTransferObject form = new UserDataTransferObject();
		UserAccount userAccount = userManager.findByUsername("test").get().getUserAccount();
		controller.populateForm(form, userAccount);
		assertEquals(form.getEmail(), "te@st.te");
	}

}
