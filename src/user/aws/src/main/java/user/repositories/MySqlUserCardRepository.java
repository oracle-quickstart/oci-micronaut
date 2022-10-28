package user.repositories;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@Replaces(UserCardRepository.class)
@JdbcRepository(dialect = Dialect.MYSQL)
public interface MySqlUserCardRepository extends UserCardRepository {
}
