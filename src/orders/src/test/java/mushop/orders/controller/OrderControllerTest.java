package mushop.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
public class OrderControllerTest extends AbstractTest {

    @Inject
    private OrdersService ordersService;

    @Inject
    @Client("/")
    private RxHttpClient httpClient;

    @Inject
    private ObjectMapper objectMapper;

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
            Arrays.asList(address),
            Arrays.asList(card));

    URI customerURI = URI.create("http://user/customers/1");
    URI addressURI = URI.create("http://user/customers/1/addresses/1");
    URI cardURI = URI.create("http://user/customers/1/cards/1");
    URI itemsURI = URI.create("http://carts/carts/1/items");
    CustomerOrder order = new CustomerOrder(001l, customer, address, card, null, null, null, 00f);

    @Test
    void orderPayload_returns_201() throws Exception {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, addressURI, cardURI, itemsURI);

        when(ordersService.placeOrder(orderPayload).blockingGet())
                .thenReturn(order);

        assertEquals(HttpStatus.CREATED, httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class)
                        .body(orderPayload)).blockingSingle().getStatus());
    }

    @Test
    void missingCustomer_returns_406(){
        NewOrderResource orderPayload = new NewOrderResource(null, addressURI, cardURI, itemsURI);

        when(ordersService.placeOrder(orderPayload).blockingGet())
                .thenReturn(order);

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatus());
    }

    @Test
    void missingAddress_returns_406() {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, null, cardURI, itemsURI);

        when(ordersService.placeOrder(orderPayload).blockingGet())
                .thenReturn(order);

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatus());
    }

    @Test
    void missingCard_returns_406() {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, addressURI, null, itemsURI);

        when(ordersService.placeOrder(orderPayload).blockingGet())
                .thenReturn(order);

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatus());
    }

    @Test
    void missingCartItems_returns_406() {
        NewOrderResource orderPayload = new NewOrderResource(customerURI, addressURI, cardURI, null);

        when(ordersService.placeOrder(orderPayload).blockingGet())
                .thenReturn(order);

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.exchange(HttpRequest.POST("/orders", NewOrderResource.class).body(orderPayload)).blockingSingle();
        });

        assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatus());
    }

    @Test
    void paymentDeclined_returns_406(){
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

    @MockBean(OrdersService.class)
    OrdersService ordersService(){
        return mock(OrdersService.class);
    }

}
