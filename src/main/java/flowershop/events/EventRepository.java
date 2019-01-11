package flowershop.events;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository interface to manage {@link Event} instances.
 *
 * @author Tomasz Ludyga
 */
public interface EventRepository extends CrudRepository<Event, Long> {

}
