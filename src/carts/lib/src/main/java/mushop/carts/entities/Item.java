package mushop.carts.entities;

import java.math.BigDecimal;
import java.util.UUID;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public record Item(@Id String id, String itemId, int quantity, BigDecimal unitPrice) {

    public static Item of(String itemId, int quantity, BigDecimal unitPrice) {
        return new Item(UUID.randomUUID().toString(), itemId, quantity, unitPrice);

    }

    public Item withQuantity(int newQuantity) {
        return new Item(id, itemId, newQuantity, unitPrice);
    }
}
