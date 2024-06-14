package mushop.orders.services;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.core.type.Argument;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import mushop.orders.OrdersConfiguration;
import mushop.orders.client.PaymentClient;
import mushop.orders.entities.Address;
import mushop.orders.entities.Card;
import mushop.orders.entities.Customer;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.entities.Item;
import mushop.orders.repositories.CustomerOrderRepository;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.resources.OrderUpdate;
import mushop.orders.resources.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static mushop.orders.controllers.OrdersController.OrderFailedException;
import static mushop.orders.controllers.OrdersController.PaymentDeclinedException;

/**
 * Service implements the order execution.
 */
@Singleton
public class OrdersService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final CustomerOrderRepository customerOrderRepository;
    private final MeterRegistry meterRegistry;
    private final OrdersPublisher ordersPublisher;
    private final PaymentClient paymentClient;
    private final OrdersConfiguration ordersConfiguration;
    private final HttpClient userClient;
    private final HttpClient cartsClient;

    public OrdersService(CustomerOrderRepository customerOrderRepository,
                         MeterRegistry meterRegistry,
                         OrdersPublisher ordersPublisher,
                         PaymentClient paymentClient,
                         OrdersConfiguration ordersConfiguration,
                         @Client("users") HttpClient userClient,
                         @Client("carts") HttpClient cartsClient) {
        this.customerOrderRepository = customerOrderRepository;
        this.meterRegistry = meterRegistry;
        this.ordersPublisher = ordersPublisher;
        this.paymentClient = paymentClient;
        this.ordersConfiguration = ordersConfiguration;
        this.userClient = userClient;
        this.cartsClient = cartsClient;
    }

    public Optional<CustomerOrder> getById(Long id) {
        return customerOrderRepository.findById(id);
    }

    public Page<CustomerOrder> searchCustomerOrders(String customerId, Pageable pagable) {
        LOG.info("Searching for {} orders {}", customerId, pagable);
        return customerOrderRepository.findByCustomerId(customerId, pagable);
    }

    public List<CustomerOrder> listOrders() {
        return customerOrderRepository.findAll();
    }

    /**
     * Create order.
     *
     * @param orderPayload order details
     * @return created order
     */
    @Counted("orders.placed")
    public Mono<CustomerOrder> placeOrder(NewOrderResource orderPayload) {
        return Flux.just(new PaymentRequest())
                .doOnNext((request) -> LOG.info("Placing new order {}", orderPayload))
                .switchMap((request) ->
                        Flux.from(userClient.retrieve(HttpRequest.GET(orderPayload.getAddress().getPath()), Address.class))
                                .map(request::setAddress)
                )
                .switchMap((request) ->
                        Flux.from(userClient.retrieve(HttpRequest.GET(orderPayload.getCustomer().getPath()), Customer.class))
                                .map(request::setCustomer)
                )
                .switchMap((request) ->
                        Flux.from(userClient.retrieve(HttpRequest.GET(orderPayload.getCard().getPath()), Card.class))
                                .map(request::setCard)
                )
                .switchMap((request) ->
                        Flux.from(cartsClient.retrieve(HttpRequest.GET(orderPayload.getItems().getPath()), Argument.listOf(Item.class)))
                                .map((orderItems) -> {
                                    //Calculate total
                                    float amount = calculateTotal(orderItems);
                                    request.setAmount(amount);
                                    return new OrderDetail(request, orderItems);
                                })
                )
                .timeout(Duration.of(ordersConfiguration.getTimeout(), ChronoUnit.SECONDS))
                .onErrorResume((throwable -> {
                            if (throwable instanceof TimeoutException) {
                                LOG.error("Timeout exception", throwable);
                                meterRegistry.counter("orders.rejected", "cause", "timeout").increment();
                                return Flux.error(new OrderFailedException("Unable to create order due to timeout from one of the services: " + throwable.getMessage()));
                            }
                            return Flux.error(throwable);
                        })
                )
                .switchMap(orderDetail -> {
                    // authorize payment
                    final PaymentRequest paymentRequest = orderDetail.request;
                    LOG.info("Sending payment request: {}", paymentRequest);
                    return paymentClient.createPayment(paymentRequest)
                            .flatMap((paymentResponse -> {
                                LOG.info("Received payment response: {}", paymentResponse);
                                if (!paymentResponse.isAuthorised()) {
                                    LOG.warn("Payment rejected: {}", paymentResponse);
                                    meterRegistry.counter("orders.rejected", "cause", "payment_declined").increment();
                                    return Mono.error(new PaymentDeclinedException(paymentResponse.getMessage()));
                                } else {
                                    return Mono.just(orderDetail);
                                }
                            }))
                            .timeout(Duration.of(ordersConfiguration.getTimeout(), ChronoUnit.SECONDS))
                            .onErrorResume(throwable -> {
                                if (throwable instanceof TimeoutException) {
                                    meterRegistry.counter("orders.rejected", "cause", "payment_timeout").increment();
                                    return Mono.error(new PaymentDeclinedException("Timeout authorising payment"));
                                }
                                return Mono.error(throwable);
                            });
                }).switchMap(orderDetail -> {
                    final PaymentRequest paymentRequest = orderDetail.request;
                    return Mono.fromCallable(() -> createOrder(paymentRequest, orderDetail.items))
//                            .subscribeOn(Schedulers.io())
                            .flatMap((order) -> {
                                meterRegistry.summary("orders.amount").record(paymentRequest.getAmount());
                                DistributionSummary.builder("order.stats")
                                        .serviceLevelObjectives(10d, 20d, 30d, 40d, 50d, 60d, 70d, 80d, 90d, 100d, 110d)
                                        //.publishPercentileHistogram()
                                        .maximumExpectedValue(120d)
                                        .minimumExpectedValue(5d)
                                        .register(meterRegistry)
                                        .record(paymentRequest.getAmount());

                                OrderUpdate update = new OrderUpdate(order.getId(), null);
                                ordersPublisher.dispatchToFulfillment(update);
                                LOG.info("Order {} sent for fulfillment: {}", order, update);
                                return Mono.just(order);

                            });
                }).singleOrEmpty();
    }

    @Transactional
    CustomerOrder createOrder(PaymentRequest paymentRequest, List<Item> orderItems) {
        CustomerOrder order = new CustomerOrder(
                null,
                paymentRequest.getCustomer(),
                paymentRequest.getAddress(),
                paymentRequest.getCard(),
                orderItems,
                null,
                Calendar.getInstance().getTime(),
                paymentRequest.getAmount());

        LOG.info("Creating order: {}", order);
        CustomerOrder savedOrder = customerOrderRepository.save(order);
        LOG.debug("Saved order: {}", savedOrder);
        return savedOrder;
    }

    private float calculateTotal(List<Item> items) {
        float amount = 0F;
        float shipping = 4.99F;
        amount += items.stream().mapToDouble(i -> i.getQuantity() * i.getUnitPrice()).sum();
        amount += shipping;
        return amount;
    }

    private static final class OrderDetail {
        final PaymentRequest request;
        final List<Item> items;

        public OrderDetail(PaymentRequest request, List<Item> items) {
            this.request = request;
            this.items = items;
        }
    }
}
