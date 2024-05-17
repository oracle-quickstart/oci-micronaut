package mushop.carts;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.json.tree.JsonNode;
import jakarta.inject.Inject;
import mushop.carts.entities.Cart;
import mushop.carts.entities.Item;
abstract class AbstractTestCartService  {

  
    @Inject
    @Client("/")
    HttpClient client;


    @Test
    public void testMetricsJson() {
        JsonNode result = client.toBlocking().retrieve("/metrics", JsonNode.class);
        assertThat(result.get("names").size(), greaterThan(0));
    }



    @Test
    public void testStoreCart() {
        Item i = Item.of("I123", 47, BigDecimal.valueOf(123));
        Cart c = Cart.of("c1", List.of(i));

        HttpResponse<Cart> created = client.toBlocking().exchange(HttpRequest.POST("/carts/" + c.id(), c), Cart.class);
        assertEquals(HttpStatus.CREATED, created.getStatus());
        assertEquals(c.id(), created.body().id());

        HttpResponse<List<Item>> items = client.toBlocking().exchange(HttpRequest.GET("/carts/" + c.id() + "/items"), Argument.listOf(Item.class));
        assertEquals(1, items.body().size());
        assertEquals(i.id(), items.body().get(0).id());

        HttpResponse<Cart> updateItem = client.toBlocking().exchange(HttpRequest.PUT("/carts/" + c.id() + "/items", i.withQuantity(23)), Cart.class);
        assertEquals(HttpStatus.OK, updateItem.getStatus());
        assertEquals(23, updateItem.body().getItems().getFirst().quantity());

        HttpResponse<Cart> deleteItem = client.toBlocking().exchange(HttpRequest.DELETE("/carts/" + c.id() + "/items/" + i.itemId()), Cart.class);
        assertEquals(HttpStatus.OK, deleteItem.getStatus());

        items = client.toBlocking().exchange(HttpRequest.GET("/carts/" + c.id() + "/items"), Argument.listOf(Item.class));
        assertEquals(0, items.body().size());
    }
}
