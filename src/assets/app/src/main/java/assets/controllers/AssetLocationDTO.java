package assets.controllers;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class AssetLocationDTO {

    private final String productImagePath;

    public AssetLocationDTO(String productImagePath) {
        this.productImagePath = productImagePath;
    }

    public String getProductImagePath() {
        return productImagePath;
    }
}
