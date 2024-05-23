package mushop.carts.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public record Cart(@Id String id, String customerId, Map<String, Item> items) {

    public static Cart of(String customerId, List<Item> items) {
        return new Cart(UUID.randomUUID().toString(), customerId, items);
    }

    public Cart(@Id String id, String customerId, List<Item> itemList) {
        this(id, customerId, itemList.stream().collect(Collectors.toMap(Item::itemId, Function.identity())));
    }

    public boolean removeItem(String itemId) {
        Item removed = items.remove(itemId);
        return removed != null;
    }

    public void updateItem(Item item) {
        items.put(item.itemId(), item);
    }

    public Cart merge(Cart cart) {
        return new Cart(id, cart.customerId(), mergeItems(cart.items));
    }

    private Map<String, Item> mergeItems(Map<String, Item> newItems) {
        Map<String, Item> result = new HashMap<>(items);
        for (Item newItem : newItems.values()) {
            result.computeIfPresent(newItem.itemId(), (itemId, existing) -> new Item(existing.id(), itemId, existing.quantity() + newItem.quantity(), existing.unitPrice()));
            result.putIfAbsent(newItem.itemId(), newItem);
        }
        return result;
    }

    public Cart withId(String cartId) {
        return new Cart(cartId, customerId, items);
    }

    public List<Item> getItems() {
        return Optional.ofNullable(items)
            .map(Map::values)
            .map(ArrayList::new)
            .orElseGet(ArrayList::new);
    }
}
