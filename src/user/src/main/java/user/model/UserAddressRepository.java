package user.model;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface UserAddressRepository extends CrudRepository<UserAddress, UUID> {

    Optional<UserAddress> findByIdAndUserId(UUID id, UUID userId);

    List<UserAddress> findByUserId(UUID id);

}
