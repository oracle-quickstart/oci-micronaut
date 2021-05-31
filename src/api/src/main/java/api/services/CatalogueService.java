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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@MuService
@Client(id = ServiceLocator.CATALOGUE)
@Secured(SecurityRule.IS_ANONYMOUS)
public interface CatalogueService {

    /**
     * Get catalogue item image.
     * @param path The path to the image
     * @return The resulting image
     */
    @Tag(name="catalogue")
    @Get(uri = "/catalogue/images/{+path}", processes = MediaType.IMAGE_PNG)
    Flowable<HttpResponse<byte[]>> getImage(String path);

    /**
     * Get catalogue item.
     *
     * @param id Catalogue item ID.
     * @return Returns catalogue item details.
     */    
    @Tag(name="catalogue")
    @Get("/catalogue/{id}")
    Single<HttpResponse<CatalogueItem>> getItem(
            @Parameter(example = "MU-US-001") String id);

    /**
     * Gets the items in the catalogue.
     * @param categories Filter categories. By default returns all categories items.
     * @param size Number of items to return. By default returns all items.
     * @return Returns list of catalogue items.    
     */    
    @Tag(name="catalogue")
    @Get("/catalogue{?categories,size}")
    Single<HttpResponse<List<CatalogueItem>>> getCatalogue(
            @Parameter(example = "Food Pouches,Dry Food") @Nullable List<String> categories,
            @Parameter(example = "5") @Nullable Integer size);

    /**
     * Get catalogue items categories.
     * @return The list of categories.
     */    
    @Tag(name="catalogue")
    @Get("/categories")
    Single<Categories> getCategories();
}
