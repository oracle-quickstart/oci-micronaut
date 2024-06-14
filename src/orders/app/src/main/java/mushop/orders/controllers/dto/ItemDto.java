package mushop.orders.controllers.dto;

import io.micronaut.serde.annotation.Serdeable;

/**
 * The {@link mushop.orders.entities.Item} DTO.
 */
@Serdeable
public class ItemDto {

    private final String id;
    private final String itemId;
    private final int quantity;
    private final float unitPrice;

    public ItemDto(String id, String itemId, int quantity, float unitPrice) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getUnitPrice() {
        return unitPrice;
    }
}
