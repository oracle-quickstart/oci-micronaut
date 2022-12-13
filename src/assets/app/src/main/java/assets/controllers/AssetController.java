package assets.controllers;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class AssetController {

    @Get("/assets/location")
    public AssetLocationDTO getLocation() {
        return new AssetLocationDTO(getProductImagePath());
    }

    protected String getProductImagePath() {
        return "/assets/image/product/";
    }

}
