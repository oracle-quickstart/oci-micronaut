package mushop.carts.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import mushop.carts.entities.Cart;
import mushop.carts.entities.Item;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCartService implements TestPropertyProvider {

    @Container
    final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @Inject
    @Client("/")
    HttpClient httpClient;

    @NonNull
    @Override
    public Map<String, String> getProperties() {
        mongoDBContainer.start();
        return Map.of(
            "mongodb.uri", mongoDBContainer.getReplicaSetUrl(),
            "mongodb.package-names", "mushop.carts"
        );
    }

    @Test
    public void testMetricsJson() {
        JsonNode result = httpClient.toBlocking().retrieve("/metrics", JsonNode.class);
        assertThat(result.get("names").size(), greaterThan(0));
    }

    @Test
    public void testHealthCheck(){
        Arrays.asList("/health", "/health/liveness", "/health/readiness").forEach(uri -> {
            HttpResponse<JsonNode> result = httpClient.toBlocking().exchange(uri, JsonNode.class);
            assertThat(result.status(), equalTo(HttpStatus.OK));
            assertThat(result.body().get("status").asText(),
                    Matchers.either(Matchers.is("UP")).or(Matchers.is("UNKNOWN")));
        });
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
