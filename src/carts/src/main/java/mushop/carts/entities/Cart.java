package mushop.carts.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public class Cart {

    @Id
    private String id;

    private String customerId;

    private List<Item> items = new ArrayList<>();

    public Cart() {
        id = UUID.randomUUID().toString();
    }

    public Cart(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public boolean removeItem(String itemId) {
        return items.removeIf(item -> itemId.equalsIgnoreCase(item.getItemId()));
    }

    public void merge(Cart cart) {
        customerId = cart.getCustomerId();
        for (Item item : cart.items) {
            mergeItem(item);
        }
    }

    private void mergeItem(Item item) {
        for (Item existing : items) {
            if (existing.getItemId().equals(item.getItemId())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    @Override
    public String toString() {
        return "Cart [customerId=" + customerId +
                ", id=" + id +
                ", items=" + items +
                "]";
    }
}
