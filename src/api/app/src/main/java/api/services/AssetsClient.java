package api.services;

import api.model.AssetsLocation;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client(id = ServiceLocator.ASSETS, path = "/assets")
public interface AssetsClient {

    @Get("/location")
    Mono<AssetsLocation> getAssetsLocation();
}
