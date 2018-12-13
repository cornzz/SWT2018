package flowershop.events;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
		return "event_list";
	}

	public static void saveEvent(Event event) {
		events.add(event);
	}

	@GetMapping("/event/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addEvent() {
		return "event_add";
	}

	@PostMapping("/event/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addEvent(Model model, @RequestParam("title") String title, @RequestParam("text") String text, @RequestParam("begin") String daysToBegin, @RequestParam("end") String duration) {
		int convertedDaysToBegin;
		int convertedDaysDuration;
		try {
			convertedDaysToBegin = Integer.valueOf(daysToBegin);
			if (convertedDaysToBegin < 0) {
				throw new Exception();
			}
		} catch (Exception e) {
			model.addAttribute("message", "event.add.begin.invalid");
			return "event_add";
		}
		try {
			convertedDaysDuration = Integer.valueOf(duration);
			if (convertedDaysDuration <= 0) {
				throw new Exception();
			}
		} catch (Exception e) {
			model.addAttribute("message", "event.add.duration.invalid");
			return "event_add";
		}
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime beginTime = currentTime.plusDays(convertedDaysToBegin);
		LocalDateTime endTime = beginTime.plusDays(convertedDaysDuration);
		events.add(new Event(title, text, beginTime, endTime));
		return "redirect:/events";
	}

	@GetMapping("/event/show")
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