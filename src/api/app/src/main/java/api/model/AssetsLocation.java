package api.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class AssetsLocation {

    private final String productImagePath;

    public AssetsLocation(String productImagePath) {
        this.productImagePath = productImagePath;
    }

    public String getProductImagePath() {
        return productImagePath;
    }
}
