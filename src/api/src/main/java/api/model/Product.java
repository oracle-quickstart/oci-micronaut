package api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public class Product {
    public final String id;
    public final double price;

    @JsonCreator
    public Product(@JsonProperty("id") String id, @JsonProperty("unitPrice") double price) {
        this.id = id;
        this.price = price;
    }
}
