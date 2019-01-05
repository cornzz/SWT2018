package flowershop.user;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository interface to manage {@link User} instances.
 *
 * @author Cornelius Kummer
 */
public interface UserRepository extends CrudRepository<User, Long> {

}
