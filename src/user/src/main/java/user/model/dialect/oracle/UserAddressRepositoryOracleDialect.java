package user.model.dialect.oracle;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import user.model.UserAddressRepository;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface UserAddressRepositoryOracleDialect  extends UserAddressRepository {
}
