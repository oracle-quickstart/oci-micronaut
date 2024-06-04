package api.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Product {

    private final String id;
    private final double price;

    public Product(String id, double price) {
        this.id = id;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }
}
