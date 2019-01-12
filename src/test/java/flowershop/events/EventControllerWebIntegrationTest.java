package flowershop.events;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the {@link EventController} on the web layer, i.e. simulating HTTP requests.
 *
 * @author Tomasz Ludyga
 */
@AutoConfigureMockMvc
public class EventControllerWebIntegrationTest extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	EventRepository repository;

	@Test
	void addInvalidEventTest() throws Exception {
		//valid inputs
		mvc.perform(post("/event/add?title=asd&text=foo&begin=1&duration=10&priv=true").with(user("admin").roles("BOSS"))).
				andExpect(status().isFound()).
				andExpect(redirectedUrl("/events"));
		//invalid inputs
		mvc.perform(post("/event/add?title=foo&text=asd&begin=TEXT&duration=10&priv=true").with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(view().name("event_add"));
		mvc.perform(post("/event/add?title=asd&text=foo&begin=&duration=TEXT&priv=true").with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(view().name("event_add"));
		mvc.perform(post("/event/add?title=asd&text=foo&begin=-1&duration=10&priv=true").with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(view().name("event_add"));
		mvc.perform(post("/event/add?title=asd&text=foo&begin=1&duration=-10&priv=true").with(user("admin").roles("BOSS"))).
				andExpect(status().isOk()).
				andExpect(view().name("event_add"));
	}

	@Test
	void markEventStateTest() throws Exception {
		Event event = new Event("title", "content", LocalDateTime.of(2000, 1, 1, 1, 1), 10);
		repository.save(event);
		long id = event.getId();
		mvc.perform(get("/event/show?id=" + id)).
				andExpect(status().isOk()).
				andExpect(model().attribute("state", 1));

		event = new Event("title", "content", LocalDateTime.of(2040, 1, 1, 1, 1), 10);
		repository.save(event);
		id = event.getId();
		mvc.perform(get("/event/show?id=" + id)).
				andExpect(status().isOk()).
				andExpect(model().attribute("state", -1));

		event = new Event("title", "content", LocalDateTime.of(2018, 1, 1, 1, 1), 9999);
		repository.save(event);
		id = event.getId();
		mvc.perform(get("/event/show?id=" + id)).
				andExpect(status().isOk()).
				andExpect(model().attribute("state", 0));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void eventsListTest() throws Exception {
		mvc.perform(get("/events")).
				andExpect(status().isOk()).
				andExpect(model().attributeExists("events", "privateEvents"));
	}

	@Test
	@WithMockUser(username = "test", roles = "BOSS")
	void editEventTest() throws Exception {
		Event event = new Event("title", "content", LocalDateTime.of(2018, 1, 1, 1, 1), 9999);
		repository.save(event);
		long id = event.getId();
		mvc.perform(get("/event/edit?id=" + id)).
				andExpect(status().isOk());
		mvc.perform(post("/event/edit?title=asd&text=foo&id=" + id + "&beginDate=" + event.getBeginTime() + "&endDate=" + event.getEndTime())).
				andExpect(status().isFound()).
				andExpect(redirectedUrl("/events"));
		mvc.perform(post("/event/edit?title=asd&text=foo&id=" + id + "&beginDate=TEST&endDate=TEST")).
				andExpect(status().isOk()).
				andExpect(view().name("event_edit"));
		mvc.perform(post("/event/edit?id=0")).
				andExpect(status().is3xxRedirection()).
				andExpect(redirectedUrl("/events"));
	}

}
