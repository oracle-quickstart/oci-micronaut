package assets.controllers;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.exceptions.HttpStatusException;

@Controller
public class AssetController {

    @Get("/assets/location")
    public AssetLocationDTO getLocation() {
        return new AssetLocationDTO(getProductImagePath());
    }

    @Delete("/assets")
    public void deleteAssets() {
        deleteUploadedAssets();
    }

    protected String getProductImagePath() {
        return "/assets/image/product/";
    }

    protected void deleteUploadedAssets() {
        throw new HttpStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Not allowed when service running locally");
    }

}
