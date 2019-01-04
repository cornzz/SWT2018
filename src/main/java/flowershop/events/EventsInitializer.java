package flowershop.events;

import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class EventsInitializer implements DataInitializer {
	private final EventRepository events;

	public EventsInitializer(EventRepository events) {
		this.events = events;
	}

	@Override
	public void initialize() {
		Event event1 = new Event("Blumentag", "Blumentag - 20% Rabatt auf alle Produkte!", LocalDateTime.now(), 10);
		Event event2 = new Event("Frühjahrsputz", "Frühjahrsputz", LocalDateTime.now(), 1);
		event2.setPrivate(true);
		events.saveAll(Arrays.asList(event1, event2));
	}
}
