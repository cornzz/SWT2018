package flowershop.events;


import flowershop.events.form.EventDataTransferObject;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A Spring MVC controller to manage {@link Event}s.
 *
 * @author Tomasz Ludyga
 */
@Controller
public class EventController {
	private final EventManager eventManager;
	/**
	 * Dynamic variables representing current event time edition, package private because of tests.
	 */
	private int beginTimeEdit = 0;
	private int endTimeEdit = 0;

	EventController(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	/**
	 * Displays events list.
	 *
	 * @param model    will never be {@literal null}.
	 * @param loggedIn can be empty {@link Optional}.
	 * @return the view name.
	 */
	@GetMapping("/events")
	public String eventsList(Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		beginTimeEdit = 0;
		endTimeEdit = 0;
		Streamable<Event> events = eventManager.findAll();
		Streamable<Event> privateEvents = eventManager.findPrivate(loggedIn);
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
	public String addEvent(EventDataTransferObject form, Model model) {
		model.addAttribute("form", form);
		return "event_add";
	}

	/**
	 * Adds new event and saves in eventRepository.
	 *
	 * @param model  will never be {@literal null}.
	 * @param form   will never be {@literal null}.
	 * @param result will never be {@literal null}.
	 * @return {@link String} redirect to event list if params valid, otherwise event add view name
	 */
	@PostMapping("/event/add")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public ModelAndView addEvent(Model model,
								 @ModelAttribute("form") @Validated(EventDataTransferObject.CreateEvent.class) EventDataTransferObject form,
								 BindingResult result) {
		if (result.hasErrors()) {
			model.addAttribute("form", form);
			return new ModelAndView("event_add", "form", form);
		}
		eventManager.create(form);

		return new ModelAndView("redirect:/events");
	}

	/**
	 * Displays an event with his content.
	 *
	 * @param eventId  must not be {@literal null}.
	 * @param model    will not be {@literal null}.
	 * @param loggedIn will not be {@literal null}.
	 * @return the view name or (if no access) {@link String} redirect to events list
	 */
	@GetMapping("/event/show")
	public String event(@RequestParam(value = "id") long eventId, Model model, @LoggedIn Optional<UserAccount> loggedIn) {
		return eventManager.findById(eventId).map(event -> {
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
		eventManager.delete(eventId);
		return "redirect:/events";
	}

	/**
	 * Displays event edit form
	 *
	 * @param eventId must never be {@literal null}.
	 * @param model   will never be {@literal null}.
	 * @param form    will never be {@literal null}.
	 * @return the view name
	 */
	@GetMapping("/event/edit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String editEvent(@RequestParam(value = "id") long eventId, Model model, EventDataTransferObject form) {
		return eventManager.findById(eventId).map(event -> {
			model.addAttribute("event", event);
			model.addAttribute("form", form);
			model.addAttribute("beginTime", event.getBeginTime().plusDays(beginTimeEdit));
			model.addAttribute("endTime", event.getEndTime().plusDays(endTimeEdit));
			return "event_edit";
		}).orElse("event_edit");
	}

	/**
	 * Changes attributes of selected event and saves it in eventRepository.
	 *
	 * @param id must not be {@literal null}.
	 * @return {@link String} redirect to events list.
	 */
	@PostMapping("/event/edit")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public ModelAndView submitEditEvent(@RequestParam("id") long id,
										@ModelAttribute("form") @Validated(EventDataTransferObject.EditEvent.class) EventDataTransferObject form,
										BindingResult result) {
		return eventManager.findById(id).map(event -> {
			if (result.hasErrors()) {
				return new ModelAndView("event_edit", "event", event).
						addObject("form", form).
						addObject("beginTime", form.getBeginDate()).
						addObject("endTime", form.getEndDate());
			}
			beginTimeEdit = 0;
			endTimeEdit = 0;
			eventManager.edit(id, form);
			return new ModelAndView("redirect:/events");
		}).orElse(new ModelAndView("redirect:/events"));
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