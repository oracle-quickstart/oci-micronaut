package catalogue.repositories.dialect.oracle;

import catalogue.repositories.CategoryRepository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface CategoryRepositoryOracleDialect extends CategoryRepository {
}
