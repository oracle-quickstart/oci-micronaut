package api.services;

import api.model.CatalogueItem;
import api.model.Categories;
import api.services.annotation.MuService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@MuService
@Client(id = ServiceLocator.CATALOGUE)
@Secured(SecurityRule.IS_ANONYMOUS)
public interface CatalogueService {

    @Operation(
            summary = "Get image",
            description = "Get catalogue item image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns image."),
            },
            tags = {"catalogue"}
    )
    @Get(uri = "/catalogue/images/{+path}", processes = MediaType.IMAGE_PNG)
    Flowable<HttpResponse<byte[]>> getImage(String path);

    @Operation(
            summary = "Catalogue item",
            description = "Get catalogue item.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns catalogue item details."),
            },
            tags = {"catalogue"}
    )
    @Get("/catalogue/{id}")
    Single<HttpResponse<CatalogueItem>> getItem(
            @Parameter(description = "Catalogue item id.", example = "MU-US-001", in = ParameterIn.PATH) String id);

    @Operation(
            summary = "Get catalogue",
            description = "Get items catalogue.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns list of catalogue items."),
            },
            tags = {"catalogue"}
    )
    @Get("/catalogue{?categories,size}")
    Single<HttpResponse<List<CatalogueItem>>> getCatalogue(
            @Parameter(description = "Filter categories. By default returns all categories items.", example = "Food Pouches,Dry Food") @Nullable List<String> categories,
            @Parameter(description = "Number of items to return. By default returns all items.", example = "5") @Nullable String size);

    @Operation(
            summary = "Get categories",
            description = "Get catalogue items categories.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns list of user orders."),
            },
            tags = {"catalogue"}
    )
    @Get("/categories")
    Single<Categories> getCategories();
}
