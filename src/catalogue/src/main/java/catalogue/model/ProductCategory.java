package catalogue.model;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.*;

@MappedEntity("PRODUCT_CATEGORY")
public class ProductCategory {
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    @Id
    @Nullable
    private final Long productCategoryId;
    @MappedProperty("sku")
    @Relation(Relation.Kind.MANY_TO_ONE)
    private final Product product;
    @MappedProperty("category_id")
    @Relation(Relation.Kind.MANY_TO_ONE)
    private final Category category;

    public ProductCategory(@Nullable Long productCategoryId, Product product, Category category) {
        this.productCategoryId = productCategoryId;
        this.product = product;
        this.category = category;
    }

    @Nullable
    public Long getProductCategoryId() {
        return productCategoryId;
    }

    public Product getProduct() {
        return product;
    }

    public Category getCategory() {
        return category;
    }
}
