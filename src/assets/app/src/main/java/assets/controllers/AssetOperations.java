package assets.controllers;

import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;

public interface AssetOperations {

    @Get("/assets/location")
    AssetLocationDTO getLocation();

    @Delete("/assets")
    void deleteAssets();

}
