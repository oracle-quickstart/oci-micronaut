package assets.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;

public class AbstractAssetControllerTest {

    @Inject
    private AssetClient client;

    AssetLocationDTO getAssetLocation() {
        return client.getLocation();
    }

    void deleteAssets() {
        client.deleteAssets();
    }

    HttpResponse<byte[]> getImage(String name) {
        return client.getImage(name);
    }

    @Client("/")
    interface AssetClient extends AssetOperations {
        @Get("/assets/image/product/{name}")
        HttpResponse<byte[]> getImage(String name);
    }
}
