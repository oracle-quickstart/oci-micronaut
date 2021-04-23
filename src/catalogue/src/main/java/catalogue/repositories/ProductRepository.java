package catalogue.repositories;

import catalogue.model.Product;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface ProductRepository extends PageableRepository<Product, String> {

    @NonNull
    @Join("categories.category")
    List<Product> list(@NonNull Pageable pageable);

    @NonNull
    @Override
    @Join("categories.category")
    List<Product> findAll();

    @NonNull
    @Override
    @Join("categories.category")
    Optional<Product> findById(@NonNull @NotNull String id);

    int countDistinct();

    int countDistinctByCategoriesCategoryNameInList(List<String> categories);
}
