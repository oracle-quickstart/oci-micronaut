package catalogue.model;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.jdbc.annotation.JoinColumn;
import io.micronaut.data.jdbc.annotation.JoinTable;

import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;

@MappedEntity("PRODUCTS")
public class Product {

    @Id
    @Size(max = 20)
    private final String sku;

    @Size(max = 20)
    private final String brand;

    @Size(max = 40)
    private final String title;

    @Size(max = 500)
    private final String description;

    @Size(max = 10)
    private final String weight;

    @Size(max = 25)
    private final String productSize;

    @Size(max = 20)
    private final String colors;

    @MappedProperty("qty")
    private final int quantity;

    private final float price;

    @MappedProperty("image_url_1")
    private final String imageUrl1;

    @MappedProperty("image_url_2")
    private final String imageUrl2;

    @Relation(Relation.Kind.MANY_TO_MANY)
    @JoinTable(
            name = "PRODUCT_CATEGORY",
            joinColumns = @JoinColumn(name = "sku", referencedColumnName = "sku"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    )
    private List<Category> categories = Collections.emptyList();

    public Product(
            String sku,
            String brand,
            String title,
            String description,
            String weight,
            String productSize,
            String colors,
            int quantity,
            float price,
            String imageUrl1,
            String imageUrl2) {
        this.sku = sku;
        this.brand = brand;
        this.title = title;
        this.description = description;
        this.weight = weight;
        this.productSize = productSize;
        this.quantity = quantity;
        this.price = price;
        this.colors = colors;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
    }

    public String getSku() {
        return sku;
    }

    public String getBrand() {
        return brand;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getWeight() {
        return weight;
    }

    public String getProductSize() {
        return productSize;
    }

    public String getColors() {
        return colors;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
