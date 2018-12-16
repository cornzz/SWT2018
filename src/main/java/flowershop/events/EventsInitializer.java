package flowershop.events;

import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventsInitializer implements DataInitializer {
	private final EventRepository events;

	public EventsInitializer(EventRepository events) {
		this.events = events;
	}

	@Override
	public void initialize() {
		Event event = new Event("Blumentag", "Blumentag - für jede Produkt gilt Preissenkung 20%. \n\nFür jede Produkt gilt Preissenkung 20%. Für jede Produkt gilt Preissenkung 20%. Für jede Produkt gilt Preissenkung 20%. Für jede Produkt gilt Preissenkung 20%.", LocalDateTime.now(), 10);
		events.save(event);
	}
}
