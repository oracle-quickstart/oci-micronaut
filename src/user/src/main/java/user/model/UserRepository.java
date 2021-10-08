package user.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

/*
 * Allows querying the database for results from the USER table using Micronaut Data JDBC.
 *
 * @see https://micronaut-projects.github.io/micronaut-data/latest/guide/#sql
 */

 /*
  * These two {@code Join} annotations configure the repository so that 
  * every query include a {@code LEFT JOIN} statement to fetch the 
  * "addresses" and "cards" when querying the {@code USER} table
  */
@Join(value = "addresses", type = Join.Type.LEFT_FETCH)
@Join(value = "cards", type = Join.Type.LEFT_FETCH)
public interface UserRepository extends CrudRepository<User, UUID> {

    /*
     * Perform a {@code SELECT * FROM USER WHERE USERNAME = ? query}
     * 
     * @see https://micronaut-projects.github.io/micronaut-data/latest/guide/#querying
     */
    Optional<User> findByUsername(String username);

    /*
     * Perform a {@code SELECT * FROM USER WHERE ID = ? query}
     * 
     * @see https://micronaut-projects.github.io/micronaut-data/latest/guide/#querying
     */
    @NonNull
    @Override
    Optional<User> findById(@NonNull @NotNull UUID uuid);
}
