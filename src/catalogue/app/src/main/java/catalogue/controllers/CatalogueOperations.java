package catalogue.controllers;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

import java.util.List;
import java.util.Optional;

public interface CatalogueOperations {

    @Get("/categories")
    CategoriesDTO listCategories();

    @Get("/catalogue/size{?categories}")
    CatalogueSizeDTO size(@Nullable @QueryValue List<String> categories);

    @Get("/catalogue/{id}")
    Optional<CatalogueItemDTO> find(String id);

    @Get("/catalogue{?categories}")
    List<CatalogueItemDTO> list(@Nullable @QueryValue List<String> categories,
                                Pageable pageable);
}
