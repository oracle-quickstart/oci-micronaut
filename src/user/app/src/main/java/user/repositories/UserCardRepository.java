package user.repositories;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import user.model.UserCard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.H2)
public interface UserCardRepository extends CrudRepository<UserCard, UUID> {

    Optional<UserCard> findByIdAndUserId(UUID id, UUID userId);

    List<UserCard> findByUserId(UUID id);

}
