package api.services;

import api.services.annotation.MuService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;

@MuService
@Client(id = ServiceLocator.ASSETS)
@Secured(SecurityRule.IS_ANONYMOUS)
public interface AssetsService {

    @Tag(name="assets")
    @Get(uri = "/assets/image/product/{+path}", processes = MediaType.IMAGE_PNG)
    Flux<HttpResponse<byte[]>> getProductImage(String path);

}
