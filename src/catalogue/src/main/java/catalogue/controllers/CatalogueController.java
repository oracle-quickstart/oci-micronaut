package catalogue.controllers;

import catalogue.model.Category;
import catalogue.model.Product;
import catalogue.repositories.CategoryRepository;
import catalogue.repositories.ProductRepository;
import io.micrometer.core.annotation.Timed;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.micronaut.tracing.annotation.SpanTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
class CatalogueController implements CatalogueOperations {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    CatalogueController(ProductRepository productRepository,
                        CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Lists the product categories
     *
     * @return The product categories
     */
    @Override
    @Get("/categories")
    @Timed("category.list")
    @CircuitBreaker(reset = "30s")
    @Cacheable("categories")
    public CategoriesDTO listCategories() {
        return new CategoriesDTO(categoryRepository.listName());
    }

    /**
     * Returns the size of the product catalogue
     *
     * @param categories Optionally narrow the size for the given categories
     * @return The size of the product catalogue
     */
    @Override
    @Get("/catalogue/size{?categories}")
    @Timed("catalogue.size")
    @CircuitBreaker(reset = "30s")
    @ContinueSpan
    public CatalogueSizeDTO size(@SpanTag @QueryValue @Nullable List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return new CatalogueSizeDTO(productRepository.countDistinct());
        } else {
            return new CatalogueSizeDTO(productRepository.countDistinctByCategoriesNameInList(categories));
        }
    }

    /**
     * Returns a product from the catalogue for the given ID
     *
     * @param id The id of the product
     * @return A product if it is exists
     */
    @Override
    @Get("/catalogue/{id}")
    @Timed("catalogue.get")
    @CircuitBreaker(reset = "30s")
    @ContinueSpan
    @ApiResponse(responseCode = "404", description = "If the product doesn't exist")
    public Optional<CatalogueItemDTO> find(@SpanTag String id) {
        return productRepository.findById(id).map(this::toDTO);
    }

    /**
     * Lists the available products in the catalogue.
     *
     * @param categories An optional array of categories to include
     * @param pageable   For paginating results
     * @return A list products in the catalogue
     */
    @Override
    @Get("/catalogue{?categories}")
    @Operation(parameters = {
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "sort",
                    description = "Order the products by the given attribute",
                    schema = @Schema(type = "string"),
                    required = false
            ),
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "page",
                    description = "Request the given page number",
                    schema = @Schema(type = "int"),
                    required = false
            ),
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "size",
                    description = "Request the given page size",
                    schema = @Schema(type = "int"),
                    required = false
            ),
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "categories",
                    description = "Order the products by the given attribute",
                    schema = @Schema(type = "array"),
                    required = false
            )
    })
    public List<CatalogueItemDTO> list(
            @Nullable List<String> categories,
            @Parameter(hidden = true) @Nullable Pageable pageable) {
        Stream<Product> productStream;
        if (pageable == null || pageable == Pageable.UNPAGED) {
            productStream = productRepository.findAll().stream();
        } else {
            productStream = productRepository.list(pageable).stream();
        }
        return productStream.map(this::toDTO)
                .collect(Collectors.toList());
    }

    private CatalogueItemDTO toDTO(Product product) {
        return new CatalogueItemDTO(
                product.getSku(),
                product.getBrand(),
                product.getTitle(),
                product.getDescription(),
                product.getWeight(),
                product.getProductSize(),
                product.getColors(),
                product.getQuantity(),
                product.getPrice(),
                getImageUrl(product),
                getCategories(product)
        );
    }

    private String[] getImageUrl(Product product) {
        return new String[]{
                product.getImageUrl1(),
                product.getImageUrl2()
        };
    }

    private String[] getCategories(Product product) {
        return product.getCategories().stream()
                .map((Category::getName))
                .toArray(String[]::new);
    }
}
