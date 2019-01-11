package flowershop.user;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the {@link UserController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Cornelius Kummer
 */
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class UserControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired MockMvc mvc;
	@Autowired UserManager userManager;

	@Test
	void redirectsToLoginPageForSecuredResource() throws Exception {
		String[] routes = {"/account", "/account/id", "/account/changepass", "/account/id/changepass", "/users"};
		mvc.perform(get("/login")).
				andExpect(status().isOk()).
				andExpect(view().name("login"));
		for (String route : routes) {
			mvc.perform(get(route)).
					andExpect(status().isFound()).
					andExpect(header().string("Location", endsWith("/login")));
		}
	}

	@Test
	void returnsModelAndViewForSecuredUriAfterAuthentication() throws Exception {
		mvc.perform(get("/users").with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(view().name("users")).
				andExpect(model().attributeExists("userList"));
	}

	@Test
	void loginEmptyTest() throws Exception {
		mvc.perform(post("/login")).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/login?error"));
	}

	@Test
	void loginInvalidTest() throws Exception {
		mvc.perform(post("/login").
				param("username", "").
				param("password", "")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/login?error"));
	}

	@Test
	void loginTest() throws Exception {
		mvc.perform(post("/login").
				param("username", "test").
				param("password", "test")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/"));
	}

	@Test
	void registerNewLoggedInTest() throws Exception {
		mvc.perform(post("/register").with(user("admin"))).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/account"));
	}

	@Test
	void registerNewEmptyTest() throws Exception {
		mvc.perform(post("/register")).
				andExpect(status().isOk()).
				andExpect(view().name("register"));
	}

	@Test
	void registerNewPopulatedTest() throws Exception {
		mvc.perform(post("/register").
				param("firstName", "aaa").
				param("lastName", "bbb").
				param("email", "aa@bb.cc").
				param("phone", "123123").
				param("password", "asd").
				param("passwordRepeat", "asd")
		).
				andExpect(status().isOk()).
				andExpect(forwardedUrl("/"));

		boolean result = userManager.nameExists("aaabbb");
		assertThat(result).isTrue();
	}

	@Test
	void registerNewInvalidTest() throws Exception {
		mvc.perform(post("/register").
				param("firstName", "te").
				param("lastName", "st").
				param("email", "te@st.te").
				param("password", "asd").
				param("passwordRepeat", "as")
		).
				andExpect(status().isOk()).
				andExpect(view().name("register"));
	}

	@Test
	@WithMockUser(username = "test", password = "test")
	void myAccountTest() throws Exception {
		mvc.perform(get("/account")).
				andExpect(status().isOk()).
				andExpect(view().name("account")).
				andExpect(model().attributeExists("form"));
	}

	@Test
	@WithMockUser(username = "test", password = "test")
	void updateMyAccountTest() throws Exception {
		mvc.perform(post("/account").
				param("firstName", "ts").
				param("lastName", "et").
				param("email", "ts@et.te").
				param("phone", "tset").
				param("password", "tset").
				param("passwordRepeat", "tset")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/account?success"));

		String result = userManager.findByUsername("test").get().getPhone();
		assertEquals(result, "tset");
	}

	@Test
	@WithMockUser(username = "test", password = "test")
	void updateMyAccountEmptyTest() throws Exception {
		mvc.perform(post("/account")).
				andExpect(status().isOk()).
				andExpect(view().name("account")).
				andExpect(model().attributeExists("form"));
	}

	@Test
	void accountTest() throws Exception {
		mvc.perform(get("/account/test").with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(view().name("account")).
				andExpect(model().attributeExists("form"));
	}

	@Test
	void updateAccountTest() throws Exception {
		mvc.perform(post("/account/test").with(user("admin").roles("BOSS")).
				param("firstName", "ts").
				param("lastName", "et").
				param("email", "ts@et.te").
				param("phone", "tset").
				param("password", "tset").
				param("passwordRepeat", "tset")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/account/test?success"));

		String result = userManager.findByUsername("test").get().getPhone();
		assertEquals(result, "tset");
	}

	@Test
	@WithMockUser
	void changeMyPasswordTest() throws Exception {
		mvc.perform(get("/account/changepass").with(user("admin"))).
				andExpect(status().isOk()).
				andExpect(view().name("account_changepass"));
	}

	@Test
	@WithMockUser(username = "test", password = "test")
	void changeMyPasswordEmptyTest() throws Exception {
		mvc.perform(post("/account/changepass")).
				andExpect(status().isOk()).
				andExpect(view().name("account_changepass"));
	}

	@Test
	@WithMockUser(username = "test", password = "test")
	void changeMyPasswordPopulatedTest() throws Exception {
		mvc.perform(post("/account/changepass").
				param("oldPassword", "test").
				param("password", "tset").
				param("passwordRepeat", "tset")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/account?success"));
	}

	@Test
	void changePasswordEmptyTest() throws Exception {
		mvc.perform(get("/account/test/changepass").with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(view().name("account_changepass_admin")).
				andExpect(model().attributeExists("form"));
	}

	@Test
	void changePasswordPopulatedTest() throws Exception {
		mvc.perform(post("/account/test/changepass").with(user("admin").roles("BOSS")).
				param("password", "tset")
		).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/account/test?success"));
	}

}