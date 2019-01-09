package flowershop.events;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class EventControllerIntegrationTest extends AbstractIntegrationTests {

	private Model model = new ExtendedModelMap();

	@Autowired
	EventController controller;
	@Autowired
	EventRepository repository;
	@Autowired
	UserAccountManager userAccountManager;


	@Test
	void returnXIfNotPrivate() {
		Event event = new Event("title", "content", LocalDateTime.now(), 1);
		event.setPrivate(false);
		repository.save(event);
		UserAccount adminAccount = userAccountManager.create("admin2", "pass", Role.of("ROLE_CUSTOMER"));
		assertThat(controller.event(event.getId(), model, Optional.of(adminAccount))).isEqualTo("event");
	}

	@Test
	void returnXIfPrivate() {
		Event event = new Event("title", "content", LocalDateTime.now(), 10);
		event.setPrivate(true);
		repository.save(event);
		UserAccount adminAccount = userAccountManager.create("admin2", "pass", Role.of("ROLE_CUSTOMER"));
		assertThat(controller.event(event.getId(), model, Optional.of(adminAccount))).isEqualTo("redirect:/events");
	}

	@Test
	void rejectsUnauthenticatedAccess() {
		assertThatExceptionOfType(AuthenticationException.class)
				.isThrownBy(() -> controller.addEvent());
		Event event = new Event("title", "content", LocalDateTime.now(), 1);
		repository.save(event);
		long id = event.getId();
		assertThatExceptionOfType(AuthenticationException.class)
				.isThrownBy(() -> controller.remove(id));
		assertThatExceptionOfType(AuthenticationException.class)
				.isThrownBy(() -> controller.editEvent(id, model));
		assertThatExceptionOfType(AuthenticationException.class)
				.isThrownBy(() -> controller.beginTimeMinus(id));
		assertThatExceptionOfType(AuthenticationException.class)
				.isThrownBy(() -> controller.beginTimePlus(id));
		assertThatExceptionOfType(AuthenticationException.class)
				.isThrownBy(() -> controller.endTimePlus(id));
		assertThatExceptionOfType(AuthenticationException.class)
				.isThrownBy(() -> controller.endTimeMinus(id));
	}

	@Test
	@WithMockUser(roles = "BOSS")
	void allowsAuthenticatedAccess() {
		String returnedValue = controller.addEvent();
		assertThat(returnedValue).isEqualTo("event_add");
		Event event = new Event("title", "content", LocalDateTime.now(), 1);
		repository.save(event);
		long id = event.getId();
		returnedValue = controller.remove(id);
		assertThat(returnedValue).isEqualTo("redirect:/events");
		returnedValue = controller.editEvent(id, model);
		assertThat(returnedValue).isEqualTo("event_edit");
		controller.beginTimeMinus(id);
		controller.beginTimePlus(id);
		controller.endTimeMinus(id);
		controller.endTimePlus(id);
	}
}

