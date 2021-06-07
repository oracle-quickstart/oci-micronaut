package user.model.dialect.mysql;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import user.model.UserRepository;
import user.model.dialect.oracle.UserRepositoryOracleDialect;

@Requires(env = Environment.AMAZON_EC2)
@Replaces(UserRepositoryOracleDialect.class)
@JdbcRepository(dialect = Dialect.MYSQL)
public interface UserRepositoryMysqlDialect extends UserRepository {
}
