package api.services;

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

import java.util.List;
import java.util.Map;

@MuService
@Client(id = ServiceLocator.CATALOGUE)
@Secured(SecurityRule.IS_ANONYMOUS)
public interface CatalogueService {
    @Get(uri = "/catalogue/images/{+path}", processes = MediaType.IMAGE_PNG)
    Flowable<HttpResponse<byte[]>> getImage(String path);

    @Get("/catalogue/{id}")
    Single<HttpResponse<Map<String, Object>>> getItem(String id);

    @Get("/catalogue{?categories,size}")
    Single<HttpResponse<List<?>>> getCatalogue(@Nullable List<String> categories, @Nullable String size);

    @Get("/categories")
    Single<HttpResponse<Map<String, Object>>> getCategories();
}
