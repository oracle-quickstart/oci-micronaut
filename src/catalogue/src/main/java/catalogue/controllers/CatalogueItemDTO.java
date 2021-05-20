package catalogue.controllers;

import io.micronaut.core.annotation.Introspected;

/**
 * Represents a product in the catalog
 */
@Introspected
public class CatalogueItemDTO {

    private final String id;
    private final String brand;
    private final String title;
    private final String description;
    private final String weight;
    private final String productSize;
    private final String colors;
    private final int qty;
    private final double price;
    private final String[] imageUrl;
    private final String[] category;

    public CatalogueItemDTO(
            String id,
            String brand,
            String title,
            String description,
            String weight,
            String productSize,
            String colors,
            int qty,
            double price,
            String[] imageUrl,
            String[] category) {
        this.id = id;
        this.brand = brand;
        this.title = title;
        this.description = description;
        this.weight = weight;
        this.productSize = productSize;
        this.colors = colors;
        this.qty = qty;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    /**
     * The id of the product
     */
    public String getId() {
        return id;
    }

    /**
     * The brand of the product
     */
    public String getBrand() {
        return brand;
    }

    /**
     * The title of the product
     */
    public String getTitle() {
        return title;
    }

    /**
     * The description of the product
     */
    public String getDescription() {
        return description;
    }

    /**
     * The weight of the product
     */
    public String getWeight() {
        return weight;
    }

    /**
     * The size of the product
     */
    public String getProductSize() {
        return productSize;
    }

    /**
     * The available colors of the product
     */
    public String getColors() {
        return colors;
    }

    /**
     * The quantity of the product
     */
    public int getQty() {
        return qty;
    }

    /**
     * The price of the product
     */
    public double getPrice() {
        return price;
    }

    /**
     * An array of size 2 with the first image being the thumbnail and the second the full size image
     */
    public String[] getImageUrl() {
        return imageUrl;
    }

    /**
     * The categories of the image
     */
    public String[] getCategory() {
        return category;
    }
}
