package assets.controllers;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class AssetController {

    static final String PRODUCT_IMAGE_BASE_PATH = "image/product/";

    @Get("/location")
    public AssetLocationDTO getLocation() {
        return new AssetLocationDTO(getProductImagePath());
    }

    protected String getProductImagePath() {
        return PRODUCT_IMAGE_BASE_PATH;
    }

}
