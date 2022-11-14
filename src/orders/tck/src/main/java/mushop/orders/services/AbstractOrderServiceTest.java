package mushop.orders.services;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.client.HttpClient;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import mushop.orders.AbstractTest;
import mushop.orders.client.PaymentClient;
import mushop.orders.controllers.OrdersController;
import mushop.orders.entities.Address;
import mushop.orders.entities.Card;
import mushop.orders.entities.Customer;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.entities.Item;
import mushop.orders.repositories.CustomerOrderRepository;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.resources.PaymentRequest;
import mushop.orders.resources.PaymentResponse;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class AbstractOrderServiceTest extends AbstractTest {

    @Inject
    private OrdersService ordersService;

    @Inject
    private PaymentClient paymentClient;

    @Inject
    private CustomerOrderRepository customerOrderRepository;

    @Value(value = "${http.timeout:5}")
    private long timeout;

    @Inject
    private HttpClient httpClient;

    NewOrderResource orderPayload = new NewOrderResource(
            URI.create("http://user/customers/1"),
            URI.create("http://user/customers/1/addresses/1"),
            URI.create("http://user/customers/1/cards/1"),
            URI.create("http://carts/carts/1/items"));
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
    List<Item> items = Collections.singletonList(new Item("001", "001", 1, 100f));
    PaymentRequest paymentRequest = new PaymentRequest(address, card, customer, 104.99f);
    PaymentResponse payment_authorized = new PaymentResponse(true, "Payment authorized");

    @Test
    public void normalOrdersSucceed() {

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Mono.just(address));

        when(paymentClient.createPayment(paymentRequest))
                .thenReturn(Mono.just(payment_authorized));

        when(httpClient.retrieve(any(), eq(Card.class)))
                .thenReturn(Mono.just(card));

        when(httpClient.retrieve(any(), eq(Customer.class)))
                .thenReturn(Mono.just(customer));

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Mono.just(address));

        when(httpClient.retrieve(any(), eq(Argument.listOf(Item.class))))
                .thenReturn(Mono.just(items));

        when(customerOrderRepository.save(any(CustomerOrder.class)))
                .then(returnsFirstArg());

        assertNotNull(ordersService.placeOrder(orderPayload));
    }

    @Test
    public void highValueOrdersDeclined() {
        List<Item> expensiveItems = Collections.singletonList(new Item("001", "001", 1, 200f));
        PaymentRequest priceyRequest = new PaymentRequest(address, card, customer, 204.99f);
        PaymentResponse payment_unauthorized = new PaymentResponse(false, "Payment unauthorized");

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Mono.just(address));

        when(paymentClient.createPayment(priceyRequest))
                .thenReturn(Mono.just(payment_unauthorized));

        when(httpClient.retrieve(any(), eq(Card.class)))
                .thenReturn(Mono.just(card));

        when(httpClient.retrieve(any(), eq(Customer.class)))
                .thenReturn(Mono.just(customer));

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Mono.just(address));

        when(httpClient.retrieve(any(), eq(Argument.listOf(Item.class))))
                .thenReturn(Mono.just(expensiveItems));

        when(customerOrderRepository.save(any(CustomerOrder.class)))
                .then(returnsFirstArg());

        assertThrows(OrdersController.PaymentDeclinedException.class,
                () -> ordersService.placeOrder(orderPayload).block());
    }

    @Test
    public void paymentTimeoutOrdersDeclined() {

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Mono.just(address));

        when(paymentClient.createPayment(paymentRequest))
                .thenReturn(Mono.just(payment_authorized).delayElement(Duration.of(timeout + 1, ChronoUnit.SECONDS)));

        when(httpClient.retrieve(any(), eq(Card.class)))
                .thenReturn(Mono.just(card));

        when(httpClient.retrieve(any(), eq(Customer.class)))
                .thenReturn(Mono.just(customer));

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Mono.just(address));

        when(httpClient.retrieve(any(), eq(Argument.listOf(Item.class))))
                .thenReturn(Mono.just(items));

        when(customerOrderRepository.save(any(CustomerOrder.class)))
                .then(returnsFirstArg());

        assertThrows(OrdersController.PaymentDeclinedException.class,
                () -> ordersService.placeOrder(orderPayload).block());
    }

    @Test
    public void timeoutException_rethrown_as_OrderFailedException() {

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Mono.just(address));

        when(paymentClient.createPayment(paymentRequest))
                .thenReturn(Mono.just(payment_authorized));

        when(httpClient.retrieve(any(), eq(Card.class)))
                .thenReturn(Mono.just(card));

        when(httpClient.retrieve(any(), eq(Customer.class)))
                .thenReturn(Mono.just(customer));

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Mono.just(address));

        when(httpClient.retrieve(any(), eq(Argument.listOf(Item.class))))
                .thenReturn(Mono.just(items).delayElement(Duration.of(timeout + 1, ChronoUnit.SECONDS)));

        when(customerOrderRepository.save(any(CustomerOrder.class)))
                .then(returnsFirstArg());

        assertThrows(OrdersController.OrderFailedException.class,
                () -> ordersService.placeOrder(orderPayload).block());
    }

    @MockBean(OrdersPublisher.class)
    OrdersPublisher ordersPublisher() {
        return mock(OrdersPublisher.class);
    }

    @MockBean(ShipmentsListener.class)
    ShipmentsListener shipmentsListener() {
        return mock(ShipmentsListener.class);
    }

    @MockBean(HttpClient.class)
    HttpClient httpClient() {
        return mock(HttpClient.class);
    }

    @MockBean(PaymentClient.class)
    PaymentClient paymentClient() {
        return mock(PaymentClient.class);
    }

    @MockBean(CustomerOrderRepository.class)
    CustomerOrderRepository customerOrderRepository() {
        return mock(CustomerOrderRepository.class);
    }
}
