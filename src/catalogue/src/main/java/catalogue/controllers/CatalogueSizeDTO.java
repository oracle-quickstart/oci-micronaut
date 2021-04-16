package catalogue.controllers;

import io.micronaut.core.annotation.Introspected;

/**
 * Returns the size of the catalogue
 */
@Introspected
public class CatalogueSizeDTO {
    private final int size;

    public CatalogueSizeDTO(int size) {
        this.size = size;
    }

    /**
     * The size of the catalogue
     */
    public int getSize() {
        return size;
    }
}
