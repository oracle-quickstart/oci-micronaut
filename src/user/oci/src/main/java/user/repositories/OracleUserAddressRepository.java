package user.repositories;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@Replaces(UserAddressRepository.class)
@JdbcRepository(dialect = Dialect.ORACLE)
public interface OracleUserAddressRepository extends UserAddressRepository {
}
