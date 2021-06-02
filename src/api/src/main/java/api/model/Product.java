package api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public class Product {

    private final String id;
    private final double unitPrice;

    public Product(String id, double unitPrice) {
        this.id = id;
        this.unitPrice = unitPrice;
    }

    public String getId() {
        return id;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
