package catalogue.repositories.dialect.mysql;

import catalogue.repositories.CategoryRepository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface CategoryRepositoryMysqlDialect extends CategoryRepository {
}
