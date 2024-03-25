package assets.controllers;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class AssetLocationDTO {

    private final String productImagePath;

    public AssetLocationDTO(String productImagePath) {
        this.productImagePath = productImagePath;
    }

    public String getProductImagePath() {
        return productImagePath;
    }
}
