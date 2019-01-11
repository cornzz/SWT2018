package flowershop.events;

import flowershop.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests interacting with the {@link Event} class.
 *
 * @author Tomasz Ludyga
 */
class EventIntegrationTests extends AbstractIntegrationTests {

	// TODO: Could not autowire. No beans of 'EventRepository' type found.
	@Autowired
	EventRepository eventRepository;

	@Test
	void basicTest() {
		Event event1 = new Event("Titel", "Inhalt", LocalDateTime.of(2018, 12, 31, 1, 1), 31);
		Event event2 = new Event("Titel", "Inhalt", LocalDateTime.of(2018, 1, 1, 1, 1), LocalDateTime.of(2018, 1, 8, 1, 1));
		assertNotNull(event1, "Event1 is null");
		assertNotNull(event2, "Event2 is null");
		assertNotNull(eventRepository, "Event repository is null");
		assertEquals("Titel", event1.toString());
	}

	@Test
	void gettersTest() {
		Event event1 = new Event("Titel", "Inhalt", LocalDateTime.of(2018, 12, 31, 1, 1), 31);
		Event event2 = new Event("Titel", "Inhalt", LocalDateTime.of(2018, 1, 1, 1, 1), LocalDateTime.of(2018, 1, 8, 1, 1));

		assertEquals("Titel", event1.getTitle(), "Title wrong or null");
		assertEquals("Titel", event2.getTitle(), "Title wrong or null");

		assertEquals("Inhalt", event1.getText(), "Text wrong or null");
		assertEquals("Inhalt", event2.getText(), "Text wrong or null");

		assertNotNull("Create time wrong or null or wrong formated", event1.getFormatedCreatedTime());
		assertNotNull("Create time wrong or null or wrong formated", event2.getFormatedCreatedTime());

		assertNotNull("Formated begin time wrong or null", event1.getFormatedBeginTime());
		assertNotNull("Formated begin time wrong or null", event2.getFormatedBeginTime());

		assertNotNull("Formated end time wrong or null", event1.getFormatedEndTime());
		assertNotNull("Formated end time wrong or null", event2.getFormatedEndTime());

		assertEquals(LocalDate.of(2018, 12, 31), event1.getBeginTime().toLocalDate(), "Unformated begin time wrong or null");
		assertEquals(LocalDate.of(2018, 1, 1), event2.getBeginTime().toLocalDate(), "Unformated begin time wrong or null");

		assertEquals(LocalDate.of(2019, 1, 31), event1.getEndTime().toLocalDate(), "Unformated end time wrong or null");
		assertEquals(LocalDate.of(2018, 1, 8), event2.getEndTime().toLocalDate(), "Unformated end time wrong or null");

	}

	@Test
	void settersTest() {
		Event event1 = new Event("Titel", "Inhalt", LocalDateTime.of(2018, 12, 31, 1, 1), 31);

		String newTitle = "New title";
		event1.setTitle(newTitle);
		assertEquals(newTitle, event1.getTitle(), "Title wrong or null");

		String newText = "New short content";
		event1.setText(newText);
		assertEquals(newText, event1.getText(), "Short text content wrong or null");
		newText = "New large content new large content new large content new large content new large content new large content\n" +
				"new large content new large content new large content new large content new large content new large content \n" +
				"new large content new large content new large content new large content new large content new large content \n" +
				"new large content new large content new large content new large content new large content new large content \n" +
				"\n\n\n\n\n\n new large content new large content new large content new large content new large content \n" +
				"new large content new large content new large content new large content new large content new large content.";
		event1.setText(newText);
		assertEquals(newText, event1.getText(), "Lange text content wrong or null");

		LocalDateTime newTime = LocalDateTime.of(2018, 02, 1, 1, 1);
		event1.setBeginTime(newTime);
		assertEquals(newTime, event1.getBeginTime(), "Begin time wrong or null");

		newTime = LocalDateTime.of(2018, 02, 1, 1, 1);
		event1.setEndTime(newTime);
		assertEquals(newTime, event1.getEndTime(), "End time wrong or null");
	}

	@Test
	void idTest() {
		Event event1 = new Event("Titel", "Inhalt", LocalDateTime.of(2018, 12, 31, 1, 1), 31);
		eventRepository.save(event1);
		Event event2 = new Event("Titel", "Inhalt", LocalDateTime.of(2018, 1, 1, 1, 1), LocalDateTime.of(2018, 1, 8, 1, 1));
		eventRepository.save(event2);
		long id1 = event1.getId();
		long id2 = event2.getId();

		assertNotNull(event1.getId(), "EventId wrong or null");
		assertNotNull(event2.getId(), "EventId wrong or null");
		System.out.println(id1);
		System.out.println(id2);
		assertNotSame(event1.getId(), event2.getId(), "Two events has the same id");
	}

	@Test
	void TextLinesTest() {
		Event event = new Event("Titel", "Inhalt", LocalDateTime.of(2018, 12, 31, 1, 1), 31);
		List<String> list = new ArrayList();
		list.add("Inhalt");
		assertEquals(list, event.getTextLines(), "Text line wrong or null");
		list.add("new content");
		list.add("content");
		list.add("");
		list.add("");
		list.add("new content content content");
		event.setText("Inhalt\nnew content\ncontent\n\n\nnew content content content");
		assertEquals(list, event.getTextLines(), "Text lines wrong or null");
	}
}