package mushop.orders.services;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.core.type.Argument;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import mushop.orders.OrdersConfiguration;
import mushop.orders.client.PaymentClient;
import mushop.orders.entities.*;
import mushop.orders.repositories.CustomerOrderRepository;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.resources.OrderUpdate;
import mushop.orders.resources.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static mushop.orders.controllers.OrdersController.OrderFailedException;
import static mushop.orders.controllers.OrdersController.PaymentDeclinedException;

@Singleton
public class OrdersService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final CustomerOrderRepository customerOrderRepository;

    private final MeterRegistry meterRegistry;

    private final OrdersPublisher ordersPublisher;

    private final PaymentClient paymentClient;

    private final OrdersConfiguration ordersConfiguration;

    private final RxHttpClient userClient;

    private final RxHttpClient cartsClient;

    public OrdersService(CustomerOrderRepository customerOrderRepository,
                         MeterRegistry meterRegistry,
                         OrdersPublisher ordersPublisher,
                         PaymentClient paymentClient,
                         OrdersConfiguration ordersConfiguration,
                         @Client("users") RxHttpClient userClient,
                         @Client("carts") RxHttpClient cartsClient) {
        this.customerOrderRepository = customerOrderRepository;
        this.meterRegistry = meterRegistry;
        this.ordersPublisher = ordersPublisher;
        this.paymentClient = paymentClient;
        this.ordersConfiguration = ordersConfiguration;
        this.userClient = userClient;
        this.cartsClient = cartsClient;
    }

    public CustomerOrder getById(Long id) {
        Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(id);
        if (customerOrder.isPresent()) {
            return customerOrder.get();
        } else {
            LOG.info("Order with id {} not found", id);
            return null;
        }
    }

    public Page<CustomerOrder> searchCustomerOrders(String customerId, Pageable pagable){
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
    public Single<CustomerOrder> placeOrder(NewOrderResource orderPayload) {
        return Flowable.just(new PaymentRequest())
                .doOnNext((request) -> LOG.info("Placing new order {}", orderPayload))
                .switchMap((request) ->
                        userClient.retrieve(HttpRequest.GET(orderPayload.address.getPath()), Address.class)
                                .map(request::setAddress)
                ).switchMap((request) ->
                        userClient.retrieve(HttpRequest.GET(orderPayload.customer.getPath()), Customer.class)
                                .map(request::setCustomer)
                ).switchMap((request) ->
                        userClient.retrieve(HttpRequest.GET(orderPayload.card.getPath()), Card.class)
                                .map(request::setCard)
                ).switchMap((request) ->
                        cartsClient.retrieve(HttpRequest.GET(orderPayload.items.getPath()), Argument.listOf(Item.class))
                                .map((orderItems) -> {
                                    //Calculate total
                                    float amount = calculateTotal(orderItems);
                                    request.setAmount(amount);
                                    return new OrderDetail(request, orderItems);
                                })
                )
                .timeout(ordersConfiguration.getTimeout(), TimeUnit.SECONDS)
                .onErrorResumeNext((throwable -> {
                            if (throwable instanceof TimeoutException) {
                                LOG.error("Timeout exception", throwable);
                                meterRegistry.counter("orders.rejected", "cause", "timeout").increment();
                                return Flowable.error(new OrderFailedException("Unable to create order due to timeout from one of the services: " + throwable.getMessage()));
                            }
                            return Flowable.error(throwable);
                        })
                )
                .switchMap((orderDetail -> {
                    // authorize payment
                    final PaymentRequest paymentRequest = orderDetail.request;
                    LOG.info("Sending payment request: {}", paymentRequest);
                    return paymentClient.createPayment(paymentRequest)
                            .flatMap((paymentResponse -> {
                                LOG.info("Received payment response: {}", paymentResponse);

                                if (!paymentResponse.isAuthorised()) {
                                    LOG.warn("Payment rejected: {}", paymentResponse);
                                    meterRegistry.counter("orders.rejected", "cause", "payment_declined").increment();
                                    return Flowable.error(new PaymentDeclinedException(paymentResponse.getMessage()));
                                } else {
                                    return Flowable.just(orderDetail);
                                }
                            }))
                            .timeout(ordersConfiguration.getTimeout(), TimeUnit.SECONDS)
                            .onErrorResumeNext((throwable -> {
                                if (throwable instanceof TimeoutException) {
                                    meterRegistry.counter("orders.rejected", "cause", "payment_timeout").increment();
                                    return Flowable.error(new PaymentDeclinedException("Timeout authorising payment"));
                                }
                                return Flowable.error(throwable);
                            }));
                })).switchMap((orderDetail -> {
                    final PaymentRequest paymentRequest = orderDetail.request;
                    return Single.fromCallable(() -> createOrder(paymentRequest, orderDetail.items))
                            .subscribeOn(Schedulers.io())
                            .flatMapPublisher((order) -> {
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
                                return Flowable.just(order);

                            });
                })).firstOrError();
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
