package flowershop.events;


import org.salespointframework.catalog.Product;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class EventController {

	private static ArrayList<Event> events;

	EventController() {
		this.events = new ArrayList<Event>();
	}

	@GetMapping("/events")
	public String eventsList(Model model) {
		model.addAttribute("events", events);
		return "events_list";
	}

	public static void saveEvent(Event event) {
		events.add(event);
	}

	@GetMapping("/events/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addEvent() {
		return "event_add";
	}

	@PostMapping("/events/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addEvent(Model model, @RequestParam("title") String title, @RequestParam("text") String text, @RequestParam("begin") String daysToBegin, @RequestParam("end") String duration) {
		int convertedDaysToBegin;
		int convertedDaysDuration;
		try {
			convertedDaysToBegin = Integer.valueOf(daysToBegin);
		} catch (Exception e) {
			model.addAttribute("message", "Invalide Tagen zum Beginn Angabe");
			return "event_add";
		}
		try {
			convertedDaysDuration = Integer.valueOf(duration);
		} catch (Exception e) {
			model.addAttribute("message", "Invalide Dauer Angabe");
			return "event_add";
		}
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime beginTime = currentTime.plusDays(convertedDaysToBegin);
		LocalDateTime endTime = beginTime.plusDays(convertedDaysDuration);
		events.add(new Event(title, text, beginTime, endTime));
		return "redirect:/events";
	}

	@GetMapping("/events/show")
	public String event(@RequestParam(value = "id") String eventId, Model model) {
		Event event = findById(eventId);
		boolean isActive = false;
		model.addAttribute("event", event);
		if (event.getBeginTime().isBefore(LocalDateTime.now()) && event.getEndTime().isAfter(LocalDateTime.now())) {
			isActive = true;
		}
		model.addAttribute("active", isActive);
		return "event";
	}

	public Event findById(String id) {
		Event event;
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).getId().equals(id)) {
				event = events.get(i);
				return event;
			}
		}
		return null;
	}
}