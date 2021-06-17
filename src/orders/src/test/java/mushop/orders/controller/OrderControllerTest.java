package mushop.orders.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Single;
import mushop.orders.AbstractTest;
import mushop.orders.controllers.OrdersController;
import mushop.orders.controllers.OrdersController.OrderFailedException;
import mushop.orders.entities.Address;
import mushop.orders.entities.Card;
import mushop.orders.entities.Customer;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.services.OrdersService;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
public class OrderControllerTest extends AbstractTest {

    @Inject
    private OrdersService ordersService;

    @Inject
    @Client("/")
    private RxHttpClient httpClient;

    Address address = new Address(
            "001",
            "000",
            "street",
            "city",
            "postcode",
            "country");
    Card card = new Card(
            "001",
            "0000000000000000",
            "00/00",
            "000");
    Customer customer = new Customer(
            "001",
            "firstname",
            "lastName",
            "username",
            Collections.singletonList(address),
            Collections.singletonList(card));

    URI customerURI = URI.create("http://user/customers/1");
    URI addressURI = URI.create("http://user/customers/1/addresses/1");
    URI cardURI = URI.create("http://user/customers/1/cards/1");
    URI itemsURI = URI.create("http://carts/carts/1/items");
    CustomerOrder order = new CustomerOrder(1L, customer, address, card, null, null, null, 00f);

    @Test
    void orderPayload_returns_201() {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, addressURI, cardURI, itemsURI);

        when(ordersService.placeOrder(orderPayload))
                .thenReturn(Single.just(order));

        assertEquals(HttpStatus.CREATED, httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class)
                        .body(orderPayload)).blockingSingle().getStatus());
    }

    @Test
    void loadPayload_returns_200() {
        when(ordersService.getById(eq(123L))).thenReturn(Optional.of(order));

        HttpResponse<JsonNode> response = httpClient.exchange(HttpRequest.GET("/orders/123"), JsonNode.class).blockingSingle();
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void missingCustomer_returns_406() {
        NewOrderResource orderPayload = new NewOrderResource(null, addressURI, cardURI, itemsURI);

        when(ordersService.placeOrder(orderPayload))
                .thenReturn(Single.just(order));

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatus());
    }

    @Test
    void missingAddress_returns_406() {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, null, cardURI, itemsURI);

        when(ordersService.placeOrder(orderPayload))
                .thenReturn(Single.just(order));

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatus());
    }

    @Test
    void missingCard_returns_406() {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, addressURI, null, itemsURI);

        when(ordersService.placeOrder(orderPayload))
                .thenReturn(Single.just(order));

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatus());
    }

    @Test
    void missingCartItems_returns_406() {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, addressURI, cardURI, null);

        when(ordersService.placeOrder(orderPayload))
                .thenReturn(Single.just(order));

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatus());
    }

    @Test
    void paymentDeclined_returns_406() {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, addressURI, cardURI, itemsURI);
        when(ordersService.placeOrder(orderPayload))
                .thenThrow(new OrdersController.PaymentDeclinedException("test"));

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatus());
    }

    @Test
    void illegalState_returns_503() {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, addressURI, cardURI, itemsURI);

        when(ordersService.placeOrder(orderPayload))
                .thenThrow(new OrderFailedException("test"));

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, e.getStatus());
    }

    @Test
    void customerOrdersWithoutSort_returns_200() {
        Page<CustomerOrder> p = Page.of(List.of(order), Pageable.UNPAGED, 1);

        when(ordersService.searchCustomerOrders(eq("123"), any(Pageable.class)))
                .thenReturn(p);

        HttpResponse<JsonNode> lists = httpClient.exchange(HttpRequest.GET("/orders/search/customer?custId=123"), JsonNode.class).blockingSingle();
        assertEquals(HttpStatus.OK, lists.getStatus());
    }

    @Test
    void customerOrdersWithSort_returns_200() {
        Page<CustomerOrder> p = Page.of(List.of(order), Pageable.UNPAGED, 1);

        when(ordersService.searchCustomerOrders(
                eq("123"),
                eq(Pageable.from(0, -1, Sort.of(Sort.Order.desc("orderDate", true))))))
                .thenReturn(p);

        HttpResponse<JsonNode> response = httpClient.exchange(HttpRequest.GET("/orders/search/customer?custId=123&sort=orderDate,desc"), JsonNode.class).blockingSingle();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.body().has("_embedded"));
        assertTrue(response.body().has("page"));
        JsonNode jsonNode = response.body().get("_embedded");
        assertTrue(jsonNode.has("customerOrders"));
    }

    @MockBean(OrdersService.class)
    OrdersService ordersService() {
        return mock(OrdersService.class);
    }
}
