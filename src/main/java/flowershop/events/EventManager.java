package flowershop.events;

import flowershop.events.form.EventDataTransferObject;
import org.salespointframework.order.OrderIdentifier;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
	 * @param eventRepository must not be {@literal null}.
	 */
	public EventManager(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	/**
	 * Creates a new {@link Event} object from the given form data.
	 *
	 * @param form must not be {@literal null}.
	 * @return the created Event
	 */
	public Event create(EventDataTransferObject form) {
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime beginTime = currentTime.plusDays(Integer.valueOf(form.getBegin()));
		Event event = new Event(form.getTitle(), form.getText(), beginTime, Integer.valueOf(form.getDuration()));
		event.setPrivate(Boolean.valueOf(form.getPriv().split(",")[0]));
		return eventRepository.save(event);
	}

	/**
	 * @param id   must not be {@literal null}.
	 * @param date must not be {@literal null}.
	 * @return <code>true</code> if creation of delivery event was successful, <code>false</code> otherwise.
	 */
	public boolean createDeliveryEvent(OrderIdentifier id, String date) {
		try {
			LocalDateTime dateTime = LocalDateTime.parse(date + " 00:00", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
			String title = "%" + id;
			String description = "<a href='/order/" + id + "'>" + id + "</a>";
			Event event = new Event(title, description, dateTime, dateTime.plusDays(1));
			event.setPrivate(true);
			eventRepository.save(event);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Edits the {@link Event} object of the given id with the given form data.
	 *
	 * @param id must not be {@literal null}.
	 * @param form must not be {@literal null}.
	 */
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
		return Streamable.of(StreamSupport.stream(eventRepository.findAll().spliterator(), false).
				sorted(Comparator.comparing(Event::getBeginTime)).collect(Collectors.toList()));
	}

	public Optional<Event> findById(long id) {
		return eventRepository.findById(id);
	}

	public Streamable<Event> findPrivate() {
		return findAll().filter(Event::getIsPrivate);
	}

}
