package flowershop.events;

import flowershop.events.form.EventDataTransferObject;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Manages {@link Event} instances in the system.
 *
 * @author Cornelius Kummer
 */
@Service
@Transactional
public class EventManager {

	private final EventRepository eventRepository;

	/**
	 * Creates a new {@link EventManager} with the given {@link EventRepository}.
	 *
	 * @param eventRepository              must not be {@literal null}.
	 */
	public EventManager(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	public Event create(EventDataTransferObject form) {
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime beginTime = currentTime.plusDays(Integer.valueOf(form.getBegin()));
		Event event = new Event(form.getTitle(), form.getText(), beginTime, Integer.valueOf(form.getDuration()));
		event.setPrivate(Boolean.valueOf(form.getPriv().split(",")[0]));
		return eventRepository.save(event);
	}

	public void edit(long id, EventDataTransferObject form) {
		eventRepository.findById(id).ifPresent(event -> {
			event.setTitle(form.getTitle());
			event.setText(form.getText());
			event.setBeginTime(LocalDateTime.parse(form.getBeginDate()));
			event.setEndTime(LocalDateTime.parse(form.getEndDate()));
			eventRepository.save(event);
		});
	}

	public void delete(long id) {
		eventRepository.findById(id).ifPresent(eventRepository::delete);
	}

	public Streamable<Event> findAll() {
		return Streamable.of(eventRepository.findAll());
	}

	public Optional<Event> findById(long id) {
		return eventRepository.findById(id);
	}

	public Streamable<Event> findPrivate(Optional<UserAccount> loggedIn) {
		return loggedIn.filter(u -> u.hasRole(Role.of("ROLE_BOSS"))).
				map(u -> findAll().filter(Event::getIsPrivate)).orElse(Streamable.empty());
	}

}
