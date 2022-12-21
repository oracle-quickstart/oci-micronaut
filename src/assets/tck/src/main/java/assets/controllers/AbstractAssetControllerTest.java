package assets.controllers;

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

    @Client("/")
    interface AssetClient extends AssetOperations {
    }
}
