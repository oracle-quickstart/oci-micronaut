package mushop.carts.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void testRemoveItem() {
        String itemId = "itemId";

        Item item = Item.of(itemId, 0, new BigDecimal(0));
        Cart cart = new Cart(null, null, List.of(item));

        assertEquals(1, cart.items().size());
        assertTrue(cart.removeItem(itemId));
        assertEquals(0, cart.items().size());
        assertFalse(cart.removeItem(itemId));
    }

    @Test
    void testMergeCartsSameItems() {
        String itemId = "itemId";

        Item cart1Item = Item.of(itemId, 1, new BigDecimal(10));
        Cart cart1 = new Cart(null, null, List.of(cart1Item));

        Item cart2Item = Item.of(itemId, 3, new BigDecimal(20));
        Cart cart2 = new Cart(null, null, List.of(cart2Item));

        Cart merged = cart1.merge(cart2);
        assertEquals(1, merged.items().size());

        Item first = merged.getItems().getFirst();
        assertEquals(4, first.quantity());
        assertEquals(10, first.unitPrice().intValue());
    }

    @Test
    void testMergeCartsMoreItems() {
        Item cart1item1 = Item.of("1", 1, new BigDecimal(1));
        Item cart2item1 = Item.of("1", 2, new BigDecimal(1));

        Item item2 = Item.of("2", 2, new BigDecimal(2));

        Cart cart1 = new Cart(null, null, List.of(cart1item1));
        Cart cart2 = new Cart(null, null, List.of(cart2item1, item2));

        Cart merged = cart1.merge(cart2);
        assertEquals(2, merged.items().size());

        Item mergedItem1 = merged.getItems().stream().filter(item -> item.itemId().equals("1")).findFirst().get();
        assertEquals(3, mergedItem1.quantity());

        Item mergedItem2 = merged.getItems().stream().filter(item -> item.itemId().equals("2")).findFirst().get();
        assertEquals(2, mergedItem2.quantity());
    }

    @Test
    void testMergeCartsLessItems() {
        Item cart1item1 = Item.of("1", 1, new BigDecimal(1));
        Item cart2item1 = Item.of("1", 2, new BigDecimal(1));

        Item item2 = Item.of("2", 2, new BigDecimal(2));

        Cart cart1 = new Cart(null, null, List.of(cart1item1, item2));
        Cart cart2 = new Cart(null, null, List.of(cart2item1));

        Cart merged = cart1.merge(cart2);
        assertEquals(2, merged.items().size());

        Item mergedItem1 = merged.getItems().stream().filter(item -> item.itemId().equals("1")).findFirst().get();
        assertEquals(3, mergedItem1.quantity());

        Item mergedItem2 = merged.getItems().stream().filter(item -> item.itemId().equals("2")).findFirst().get();
        assertEquals(2, mergedItem2.quantity());
    }
}