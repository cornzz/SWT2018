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

/**
 * A Spring MVC controller to manage {@link Event}s.
 *
 * @author Tomasz Ludyga
 */
@Controller
public class EventController {
	private final EventRepository eventRepository;
	/**
	 * Dynamic variables representing current event time edition, package private because of tests.
	 */
	int beginTimeEdit = 0;
	int endTimeEdit = 0;

	EventController(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	/**
	 * Displays events list.
	 *
	 * @param model will never be {@literal null}.
	 * @param loggedIn can be empty {@link Optional}.
	 * @return the view name.
	 */
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

	/**
	 * Displays event add form.
	 *
	 * @return the view name.
	 */
	@GetMapping("/event/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String addEvent() {
		return "event_add";
	}

	/**
	 * Adds new event and saves in eventRepository.
	 *
	 * @param model will never be {@literal null}.
	 * @param title must not be {@literal null}.
	 * @param text must not be {@literal null}.
	 * @param daysToBegin must not be {@literal null}.
	 * @param duration must not be {@literal null}.
	 * @param isPrivate must not be {@literal null}.
	 * @return {@link String} redirect to event list if params valid, otherwise event add view name
	 */
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

	/**
	 * Displays an event with his content.
	 *
	 * @param eventId must not be {@literal null}.
	 * @param model will not be {@literal null}.
	 * @param loggedIn will not be {@literal null}.
	 * @return the view name or (if no access) {@link String} redirect to events list
	 */
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

	/**
	 * Removes selected event from eventRepository.
	 *
	 * @param eventId must not be {@literal null}.
	 * @return {@link String} redirect to events list.
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/remove")
	public String remove(@RequestParam(value = "id") long eventId) {
		return eventRepository.findById(eventId).map(event -> {
			eventRepository.delete(event);
			return "redirect:/events";
		}).orElse("redirect:/events");
	}

	/**
	 * Displays event edit form
	 *
	 * @param eventId must not be {@literal null}.
	 * @param model will not be {@literal null}.
	 * @return the view name
	 */
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

	/**
	 * Changes attributes of selected event and saves it in eventRepository.
	 *
	 * @param title must not be {@literal null}.
	 * @param text must not be {@literal null}.
	 * @param id must not be {@literal null}.
	 * @param beginTime must not be {@literal null}.
	 * @param endTime must not be {@literal null}.
	 * @return {@link String} redirect to events list.
	 */
	@PostMapping("/event/edit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String submitEditEvent(@RequestParam("title") String title, @RequestParam("text") String text, @RequestParam("id") long id,
								  @RequestParam("begin") String beginTime, @RequestParam("end") String endTime) {
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

	/**
	 * Reduces by 1 the beginTimeEdit value
	 *
	 * @param eventId must not be {@literal null}.
	 * @return {@link String} forward to event edit form.
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/btm")
	public String beginTimeMinus(@RequestParam(value = "id") long eventId) {
		beginTimeEdit--;
		return "forward:/event/edit?id=" + eventId;
	}

	/**
	 * Increases by 1 the beginTimeEdit value
	 *
	 * @param eventId must not be {@literal null}.
	 * @return {@link String} forward to event edit form.
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/btp")
	public String beginTimePlus(@RequestParam(value = "id") long eventId) {
		beginTimeEdit++;
		return "forward:/event/edit?id=" + eventId;
	}

	/**
	 * Reduces by 1 the endTimeEdit value
	 *
	 * @param eventId must not be {@literal null}.
	 * @return {@link String} forward to event edit form.
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/etm")
	public String endTimeMinus(@RequestParam(value = "id") long eventId) {
		endTimeEdit--;
		return "forward:/event/edit?id=" + eventId;
	}

	/**
	 * Increases by 1 the endTimeEdit value
	 *
	 * @param eventId must not be {@literal null}.
	 * @return {@link String} forward to event edit form.
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/event/edit/etp")
	public String endTimePlus(@RequestParam(value = "id") long eventId) {
		endTimeEdit++;
		return "forward:/event/edit?id=" + eventId;
	}

}