package flowershop.help;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


/**
 * Integration test for the {@link FlowerShopHelpController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Cornelius Kummer
 */
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class FlowerShopHelpControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void helpTest() throws Exception {
		mvc.perform(get("/help")).
				andExpect(status().isOk()).
				andExpect(view().name("help"));
	}

}
