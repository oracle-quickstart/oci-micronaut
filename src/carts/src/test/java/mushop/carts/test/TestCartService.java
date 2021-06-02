package mushop.carts.test;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mushop.carts.entities.Cart;
import mushop.carts.entities.Item;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCartService implements OracleSodaTest {

    @Container
    static OracleContainer oracleContainer = new OracleContainer("registry.gitlab.com/micronaut-projects/micronaut-graal-tests/oracle-database:18.4.0-xe");

    @Inject
    @Client("/")
    RxHttpClient httpClient;

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

        HttpResponse<Cart> created = httpClient.exchange(HttpRequest.POST("/carts/" + c.getId(), c), Cart.class).blockingSingle();
        assertEquals(HttpStatus.CREATED, created.getStatus());
        assertEquals(c.getId(), created.body().getId());

        HttpResponse<List<Item>> items = httpClient.exchange(HttpRequest.GET("/carts/" + c.getId() + "/items"), Argument.listOf(Item.class)).blockingSingle();
        assertEquals(1, items.body().size());
        assertEquals(i.getId(), items.body().get(0).getId());

        HttpResponse<Cart> deleteItem = httpClient.exchange(HttpRequest.DELETE("/carts/" + c.getId() + "/items/" + i.getItemId()), Cart.class).blockingSingle();
        assertEquals(HttpStatus.OK, deleteItem.getStatus());

        items = httpClient.exchange(HttpRequest.GET("/carts/" + c.getId() + "/items"), Argument.listOf(Item.class)).blockingSingle();
        assertEquals(0, items.body().size());
    }

    @Override
    public OracleContainer getOracleContainer() {
        return oracleContainer;
    }
}
