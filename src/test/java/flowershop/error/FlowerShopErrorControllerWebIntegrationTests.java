package flowershop.error;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the {@link FlowerShopErrorController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Cornelius Kummer
 */
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class AccountingControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired MockMvc mvc;

	@Test
	void handleErrorTest() throws Exception {
		mvc.perform(get("/error")).
				andExpect(status().isOk()).
				andExpect(view().name("error"));
	}

	@Test
	void testErrorTest() throws Exception {
		mvc.perform(get("/test/error")).
				andExpect(status().is5xxServerError()).andDo(print());
	}

	@Test
	void handleAccessDeniedTest() throws Exception {
		mvc.perform(get("/accessDenied")).
				andExpect(status().is4xxClientError());
	}

}
