package mushop.orders.services;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Flowable;
import io.reactivex.Single;
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
import mushop.orders.resources.OrderUpdate;
import mushop.orders.resources.PaymentRequest;
import mushop.orders.resources.PaymentResponse;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
public class OrderServiceTest extends AbstractTest {
    @Inject
    private OrdersService ordersService;

    @Inject
    private PaymentClient paymentClient;

    @Inject
    private OrdersPublisher ordersPublisher;

    @Inject
    private CustomerOrderRepository customerOrderRepository;

    @Value(value = "${http.timeout:5}")
    private long timeout;

    @Inject
    private RxHttpClient httpClient;

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
            Arrays.asList(address),
            Arrays.asList(card));
    List<Item> items = Arrays.asList(new Item("001", "001", 1, 100f));
    PaymentRequest paymentRequest = new PaymentRequest(address, card, customer, 104.99f);
    PaymentResponse payment_authorized = new PaymentResponse(true, "Payment authorized");

    @Test
    public void normalOrdersSucceed() {

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Flowable.just(address));

        when(paymentClient.createPayment(paymentRequest))
                .thenReturn(Flowable.just(payment_authorized));

        when(httpClient.retrieve(any(), eq(Card.class)))
                .thenReturn(Flowable.just(card));

        when(httpClient.retrieve(any(), eq(Customer.class)))
                .thenReturn(Flowable.just(customer));

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Flowable.just(address));

        when(httpClient.retrieve(any(), eq(Argument.listOf(Item.class))))
                .thenReturn(Flowable.just(items));

        when(customerOrderRepository.save(any(CustomerOrder.class)))
                .then(returnsFirstArg());

        assertNotNull(ordersService.placeOrder(orderPayload));
    }

    @Test
    public void highValueOrdersDeclined() {
        List<Item> expensiveItems = Arrays.asList(new Item("001", "001", 1, 200f));
        PaymentRequest priceyRequest = new PaymentRequest(address, card, customer, 204.99f);
        PaymentResponse payment_unauthorized = new PaymentResponse(false, "Payment unauthorized");

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Flowable.just(address));

        when(paymentClient.createPayment(priceyRequest))
                .thenReturn(Flowable.just(payment_unauthorized));

        when(httpClient.retrieve(any(), eq(Card.class)))
                .thenReturn(Flowable.just(card));

        when(httpClient.retrieve(any(), eq(Customer.class)))
                .thenReturn(Flowable.just(customer));

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Flowable.just(address));

        when(httpClient.retrieve(any(), eq(Argument.listOf(Item.class))))
                .thenReturn(Flowable.just(expensiveItems));


        when(customerOrderRepository.save(any(CustomerOrder.class)))
                .then(returnsFirstArg());

        assertThrows(OrdersController.PaymentDeclinedException.class,
                () -> ordersService.placeOrder(orderPayload).blockingGet());
    }

    @Test
    public void paymentTimeoutOrdersDeclined() {

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Flowable.just(address));

        when(paymentClient.createPayment(paymentRequest))
                .thenReturn(Flowable.just(payment_authorized).delay(timeout + 1, TimeUnit.SECONDS));

        when(httpClient.retrieve(any(), eq(Card.class)))
                .thenReturn(Flowable.just(card));

        when(httpClient.retrieve(any(), eq(Customer.class)))
                .thenReturn(Flowable.just(customer));

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Flowable.just(address));

        when(httpClient.retrieve(any(), eq(Argument.listOf(Item.class))))
                .thenReturn(Flowable.just(items));

        when(customerOrderRepository.save(any(CustomerOrder.class)))
                .then(returnsFirstArg());


        assertThrows(OrdersController.PaymentDeclinedException.class,
                () -> ordersService.placeOrder(orderPayload).blockingGet());
    }

    @Test
    public void timeoutException_rethrown_as_OrderFailedException() {

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Flowable.just(address));

        when(paymentClient.createPayment(paymentRequest))
                .thenReturn(Flowable.just(payment_authorized));

        when(httpClient.retrieve(any(), eq(Card.class)))
                .thenReturn(Flowable.just(card));

        when(httpClient.retrieve(any(), eq(Customer.class)))
                .thenReturn(Flowable.just(customer));

        when(httpClient.retrieve(any(), eq(Address.class)))
                .thenReturn(Flowable.just(address));

        when(httpClient.retrieve(any(), eq(Argument.listOf(Item.class))))
                .thenReturn(Flowable.just(items).delay(timeout + 1, TimeUnit.SECONDS));

        when(customerOrderRepository.save(any(CustomerOrder.class)))
                .then(returnsFirstArg());

        when(ordersPublisher.dispatchToFulfillment(any(OrderUpdate.class)))
                .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

        assertThrows(OrdersController.OrderFailedException.class,
                () -> ordersService.placeOrder(orderPayload).blockingGet());
    }

    @MockBean(OrdersPublisher.class)
    OrdersPublisher ordersPublisher() {
        return mock(OrdersPublisher.class);
    }

    @MockBean(ShipmentsListener.class)
    ShipmentsListener shipmentsListener() {
        return mock(ShipmentsListener.class);
    }

    @MockBean(RxHttpClient.class)
    RxHttpClient httpClient(){
        return mock(RxHttpClient.class);
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
