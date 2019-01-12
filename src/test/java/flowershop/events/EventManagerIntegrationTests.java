package flowershop.events;

import flowershop.AbstractIntegrationTests;
import flowershop.order.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventManagerIntegrationTests extends AbstractIntegrationTests {

	@Autowired EventManager eventManager;

	@Test
	void createDeliveryEventInvalidTest() {
		Transaction order = new Transaction();

		eventManager.createDeliveryEvent(order.getId(), "test");
		assertTrue(eventManager.findAll().stream().noneMatch(e -> e.getTitle().equals("%" + order.getId())));
	}

	@Test
	void createDeliveryEventValidTest() {
		Transaction order = new Transaction();

		eventManager.createDeliveryEvent(order.getId(), "11.11.2000");
		assertTrue(eventManager.findAll().stream().anyMatch(e -> e.getTitle().equals("%" + order.getId())));
	}

}
