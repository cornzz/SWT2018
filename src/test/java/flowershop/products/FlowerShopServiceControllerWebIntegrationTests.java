package flowershop.products;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the {@link FlowerShopServiceControllerWebIntegrationTests} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Friedrich Bethke
 */

@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class FlowerShopServiceControllerWebIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	FlowerShopServiceCatalog serviceCatalog;

	@Test
	void preventsPublicAccessForServiceView() throws Exception {

		mvc.perform(get("/services"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));//
	}

	@Test
	void serviceViewIsAccessibleForAdmin() throws Exception {
		mvc.perform(get("/services")
				.with(user("admin").roles("BOSS")))
				.andExpect(status().isOk());
	}

	@Test
	void preventsPublicAccessForAddServiceView() throws Exception {
		mvc.perform(get("/services/add"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void addServiceViewIsAccessibleForAdmin() throws Exception {
		mvc.perform(get("/services/add")
				.with(user("admin").roles("BOSS")))
				.andExpect(status().isOk());
	}

	@Test
	void servicesAreAvailableToModel() throws Exception {
		mvc.perform(get("/services")
				.with(user("admin").roles("BOSS")))
				.andExpect(status().isOk())
				.andExpect(model().attribute("services", is(serviceCatalog.findAll())));
	}
}