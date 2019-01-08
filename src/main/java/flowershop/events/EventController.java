package flowershop.events;


import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class EventController {
	private final EventRepository eventRepository;
	private int beginTimeEdit = 0;
	private int endTimeEdit = 0;

	EventController(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@GetMapping("/events")
	public String eventsList(Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		beginTimeEdit = 0;
		endTimeEdit = 0;
		Streamable<Event> events = Streamable.of(eventRepository.findAll());
		Streamable<Event> privateEvents = loggedIn.filter(userAccount -> userAccount.hasRole(Role.of("ROLE_BOSS")))
				.map(userAccount -> events.filter(Event::getIsPrivate)).orElse(Streamable.empty());
		model.addAttribute("events", events.filter(event -> !event.getIsPrivate()));
		model.addAttribute("privateEvents", privateEvents);
		return "event_list";
	}

	@GetMapping("/event/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addEvent() {
		return "event_add";
	}

	@PostMapping("/event/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addEvent(Model model, @RequestParam("title") String title, @RequestParam("text") String text, @RequestParam("begin") String daysToBegin,
						   @RequestParam("duration") String duration, @RequestParam("isPrivate") boolean isPrivate) {
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
		Event event = new Event(title, text, beginTime, convertedDaysDuration);
		event.setPrivate(isPrivate);
		eventRepository.save(event);

		return "redirect:/events";
	}

	@GetMapping("/event/show")
	public String event(@RequestParam(value = "id") long eventId, Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		return eventRepository.findById(eventId).map(event -> {
			if (event.getIsPrivate() && (!loggedIn.isPresent() || !loggedIn.get().hasRole(Role.of("ROLE_BOSS")))) {
				return "redirect:/events";
			}
			int state = -1; // -1 = scheduled, 0 = active, 1 = over
			if (event.getBeginTime().isBefore(LocalDateTime.now()) && event.getEndTime().isAfter(LocalDateTime.now())) {
				state = 0;
			} else if (event.getEndTime().isBefore(LocalDateTime.now())) {
				state = 1;
			}
			model.addAttribute("event", event).addAttribute("state", state);
			return "event";
		}).orElse("event");
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/remove")
	public String remove(@RequestParam(value = "id") long eventId) {
		return eventRepository.findById(eventId).map(event -> {
			eventRepository.delete(event);
			return "redirect:/events";
		}).orElse("redirect:/events");
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit")
	public String editEvent(@RequestParam(value = "id") long eventId, Model model) {
		return eventRepository.findById(eventId).map(event -> {
			model.addAttribute("event", event);
			model.addAttribute("beginTime", event.getBeginTime().plusDays(beginTimeEdit));
			model.addAttribute("endTime", event.getEndTime().plusDays(endTimeEdit));
			return "event_edit";
		}).orElse("event_edit");
	}

	@PostMapping("/event/edit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String submitEditEvent(@RequestParam("title") String title, @RequestParam("text") String text, @RequestParam("id") long id,
								  @RequestParam("begin") String beginTime, @RequestParam("end") String endTime) {
		LocalDateTime convertedBeginTime;
		LocalDateTime convertedEndTime;
		beginTimeEdit = 0;
		endTimeEdit = 0;
		return eventRepository.findById(id).map(event -> {
			event.setTitle(title);
			event.setText(text);
			event.setBeginTime(LocalDateTime.parse(beginTime));
			event.setEndTime(LocalDateTime.parse(endTime));
			eventRepository.save(event);
			return "redirect:/events";
		}).orElse("redirect:/events");
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/btm")
	public String beginTimeMinus(@RequestParam(value = "id") long eventId) {
		beginTimeEdit--;
		return "forward:/event/edit?id=" + eventId;
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/btp")
	public String beginTimePlus(@RequestParam(value = "id") long eventId) {
		beginTimeEdit++;
		return "forward:/event/edit?id=" + eventId;
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/etm")
	public String endTimeMinus(@RequestParam(value = "id") long eventId) {
		endTimeEdit--;
		return "forward:/event/edit?id=" + eventId;
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/etp")
	public String endTimePlus(@RequestParam(value = "id") long eventId) {
		endTimeEdit++;
		return "forward:/event/edit?id=" + eventId;
	}

}