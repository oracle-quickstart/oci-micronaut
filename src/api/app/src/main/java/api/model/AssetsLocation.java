package api.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class AssetsLocation {

    private final String productImagePath;

    public AssetsLocation(String productImagePath) {
        this.productImagePath = productImagePath;
    }

    public String getProductImagePath() {
        return productImagePath;
    }
}
