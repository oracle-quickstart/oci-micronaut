package catalogue.repositories.dialect.mysql;

import catalogue.repositories.CategoryRepository;
import catalogue.repositories.dialect.oracle.CategoryRepositoryOracleDialect;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@Requires(env = Environment.AMAZON_EC2)
@Replaces(CategoryRepositoryOracleDialect.class)
@JdbcRepository(dialect = Dialect.MYSQL)
public interface CategoryRepositoryMysqlDialect extends CategoryRepository {
}
