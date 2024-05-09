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
    HttpClient httpClient;


    @Test
    public void testMetricsJson() {
        JsonNode result = httpClient.toBlocking().retrieve("/metrics", JsonNode.class);
        assertThat(result.get("names").size(), greaterThan(0));
    }



    @Test
    public void testStoreCart() {
        Item i = new Item();
        i.setUnitPrice(BigDecimal.valueOf(123));
        i.setQuantity(47);
        i.setItemId("I123");

        Cart c = new Cart();
        c.setCustomerId("c1");
        c.getItems().add(i);

        HttpResponse<Cart> created = httpClient.toBlocking().exchange(HttpRequest.POST("/carts/" + c.getId(), c), Cart.class);
        assertEquals(HttpStatus.CREATED, created.getStatus());
        assertEquals(c.getId(), created.body().getId());

        HttpResponse<List<Item>> items = httpClient.toBlocking().exchange(HttpRequest.GET("/carts/" + c.getId() + "/items"), Argument.listOf(Item.class));
        assertEquals(1, items.body().size());
        assertEquals(i.getId(), items.body().get(0).getId());

        HttpResponse<Cart> deleteItem = httpClient.toBlocking().exchange(HttpRequest.DELETE("/carts/" + c.getId() + "/items/" + i.getItemId()), Cart.class);
        assertEquals(HttpStatus.OK, deleteItem.getStatus());

        items = httpClient.toBlocking().exchange(HttpRequest.GET("/carts/" + c.getId() + "/items"), Argument.listOf(Item.class));
        assertEquals(0, items.body().size());
    }
}
