package flowershop.events;

import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventsInitializer implements DataInitializer {

	public EventsInitializer() {
	}

	@Override
	public void initialize() {
		Event event = new Event("Blumentag", "Blumentag - für jede Produkt gilt Preissenkung 20%.", LocalDateTime.now(), LocalDateTime.now().plusHours(10));
		EventController.saveEvent(event);
	}
}
