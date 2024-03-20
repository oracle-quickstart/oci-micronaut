package catalogue.repositories;

import catalogue.model.Product;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.H2)
public interface ProductRepository extends PageableRepository<Product, String> {

    @NonNull
    @Join("categories")
    List<Product> list(@NonNull Pageable pageable);

    @NonNull
    @Override
    @Join("categories")
    List<Product> findAll();

    @NonNull
    @Override
    @Join("categories")
    Optional<Product> findById(@NonNull @NotNull String id);

    int countDistinct();

    int countDistinctByCategoriesNameInList(List<String> categories);
}
