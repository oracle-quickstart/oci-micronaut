package user.repositories;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import user.model.User;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Join(value = "addresses", type = Join.Type.LEFT_FETCH)
@Join(value = "cards", type = Join.Type.LEFT_FETCH)
@JdbcRepository(dialect = Dialect.H2)
public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    @NonNull
    @Override
    Optional<User> findById(@NonNull @NotNull UUID uuid);
}
