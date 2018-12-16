package flowershop.events;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

@Controller
public class EventController {
	private final EventRepository events;
	private int beginTimeEdit = 0;
	private int endTimeEdit = 0;

	EventController(EventRepository events) {
		this.events = events;
	}

	@GetMapping("/events")
	public String eventsList(Model model) {
		beginTimeEdit = 0;
		endTimeEdit = 0;
		ArrayList<Event> eventsList = eventsList();
		model.addAttribute("events", eventsList);
		return "event_list";
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
			if (convertedDaysToBegin < 0 || convertedDaysToBegin > 1000000) {
				throw new Exception();
			}
		} catch (Exception e) {
			model.addAttribute("message", "event.add.begin.invalid");
			return "event_add";
		}
		try {
			convertedDaysDuration = Integer.valueOf(duration);
			if (convertedDaysDuration <= 0 || convertedDaysDuration > 1000000) {
				throw new Exception();
			}
		} catch (Exception e) {
			model.addAttribute("message", "event.add.duration.invalid");
			return "event_add";
		}
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime beginTime = currentTime.plusDays(convertedDaysToBegin);
		events.save(new Event(title, text, beginTime, convertedDaysDuration));
		return "redirect:/events";
	}

	@GetMapping("/event/show")
	public String event(@RequestParam(value = "id") long eventId, Model model) {
		Event event = events.findById(eventId).get();
		boolean isActive = false;
		model.addAttribute("event", event);
		if (event.getBeginTime().isBefore(LocalDateTime.now()) && event.getEndTime().isAfter(LocalDateTime.now())) {
			isActive = true;
		}
		model.addAttribute("active", isActive);
		return "event";
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/remove")
	public String remove(@RequestParam(value = "id") long eventId) {
		Event event = events.findById(eventId).get();
		events.delete(event);
		return "redirect:/events";
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit")
	public String editEvent(@RequestParam(value = "id") long eventId, Model model) {
		Event event = events.findById(eventId).get();
		model.addAttribute("event", event);
		model.addAttribute("beginTime", event.getBeginTime().plusDays(beginTimeEdit).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
		model.addAttribute("endTime", event.getEndTime().plusDays(endTimeEdit).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
		return "event_edit";
	}

	@PostMapping("/event/edit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String submitEditEvent(@RequestParam("title") String title, @RequestParam("text") String text, @RequestParam("id") long id,
								  @RequestParam("begin") String beginTime, @RequestParam("end") String endTime) {
		LocalDateTime convertedBeginTime;
		LocalDateTime convertedEndTime;
		beginTimeEdit = 0;
		endTimeEdit = 0;
		try {
			convertedBeginTime = convertToLocalDateTime(beginTime);
			convertedEndTime = convertToLocalDateTime(endTime);
		} catch (Exception e) {
			return "forward:/events/edit?id=" + id;
		}
		Event event = events.findById(id).get();
		event.setTitle(title);
		event.setText(text);
		event.setBeginTime(convertedBeginTime);
		event.setEndTime(convertedEndTime);
		events.save(event);
		return "redirect:/events";
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/btm")
	public String beginTimeMinus(@RequestParam(value = "id") long eventId) {
		beginTimeEdit--;
		return "forward:/events/edit?id=" + eventId;
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/btp")
	public String beginTimePlus(@RequestParam(value = "id") long eventId) {
		beginTimeEdit++;
		return "forward:/events/edit?id=" + eventId;
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/etm")
	public String endTimeMinus(@RequestParam(value = "id") long eventId) {
		endTimeEdit--;
		return "forward:/events/edit?id=" + eventId;
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/etp")
	public String endTimePlus(@RequestParam(value = "id") long eventId) {
		endTimeEdit++;
		return "forward:/events/edit?id=" + eventId;
	}

	private LocalDateTime convertToLocalDateTime(String date) throws Exception {
		String year = "";
		String month = "";
		String day = "";
		for (int i = 0; i < date.length(); i++) {
			if (i < 2) {
				day = day + date.charAt(i);
			}
			if (i > 2 && i < 5) {
				month = month + date.charAt(i);
			}
			if (i > 5) {
				year = year + date.charAt(i);
			}
			if (i > 9) {
				throw new Exception();
			}
		}
		return LocalDateTime.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day), 0, 0);
	}

	public ArrayList<Event> eventsList(){
		return (ArrayList<Event>) events.findAll();
	}
}