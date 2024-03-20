package catalogue.controllers;


import io.micronaut.serde.annotation.Serdeable;

/**
 * Returns the size of the catalogue
 */
@Serdeable
public class CatalogueSizeDTO {

    private final int size;

    public CatalogueSizeDTO(int size) {
        this.size = size;
    }

    /**
     * The size of the catalogue
     * @return The size of the catalogue
     */
    public int getSize() {
        return size;
    }
}
