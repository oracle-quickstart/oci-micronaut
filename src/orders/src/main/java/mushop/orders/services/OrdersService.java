package mushop.orders.services;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.reactivex.Completable;
import mushop.orders.OrdersConfiguration;
import mushop.orders.client.PaymentClient;
import mushop.orders.entities.Address;
import mushop.orders.entities.Card;
import mushop.orders.entities.Customer;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.entities.Item;
import mushop.orders.repositories.CustomerOrderRepository;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.values.OrderUpdate;
import mushop.orders.values.PaymentRequest;
import mushop.orders.values.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static mushop.orders.controllers.OrdersController.OrderFailedException;
import static mushop.orders.controllers.OrdersController.PaymentDeclinedException;

@Singleton
@ExecuteOn(TaskExecutors.IO)
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
    public CustomerOrder placeOrder(NewOrderResource orderPayload) {
        try {
            LOG.info("Placing new order {}", orderPayload);

            LOG.debug("Preparing payment request");
            PaymentRequest paymentRequest = new PaymentRequest();
            AtomicReference<List<Item>> orderItems = new AtomicReference<>();

            boolean finished = Completable.merge(Arrays.asList(
                    Completable.fromPublisher(userClient.retrieve(HttpRequest.GET(orderPayload.address.getPath()), Address.class)
                            .doOnError(t -> LOG.error("Failed to fetch address " + orderPayload.address.getPath() + ":" + t.getMessage()))
                            .doOnNext(paymentRequest::setAddress)),
                    Completable.fromPublisher(userClient.retrieve(HttpRequest.GET(orderPayload.customer.getPath()), Customer.class)
                            .doOnError(t -> LOG.error("Failed to fetch customer " + orderPayload.address.getPath() + ":" + t.getMessage()))
                            .doOnNext(paymentRequest::setCustomer)),
                    Completable.fromPublisher(userClient.retrieve(HttpRequest.GET(orderPayload.card.getPath()), Card.class)
                            .doOnError(t -> LOG.error("Failed to fetch payment card " + orderPayload.card.getPath() + ":" + t.getMessage()))
                            .doOnNext(paymentRequest::setCard)),
                    Completable.fromPublisher(cartsClient.retrieve(HttpRequest.GET(orderPayload.items.getPath()), Argument.listOf(Item.class))
                            .doOnError(t -> LOG.error("Failed to fetch carts items " + orderPayload.items.getPath() + ":" + t.getMessage()))
                            .doOnNext((items -> {
                        orderItems.set(items);
                        //Calculate total
                        float amount = calculateTotal(items);
                        paymentRequest.setAmount(amount);
                    }))))).blockingAwait(ordersConfiguration.getTimeout(), TimeUnit.SECONDS);

            if (!finished) {
                throw new TimeoutException("Unable to create order due to timeout from one of the services.");
            }

            authorisePayment(paymentRequest);

            CustomerOrder order = createOrder(paymentRequest, orderItems.get());

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
            return order;
        } catch (TimeoutException e) {
            LOG.error("Timeout exception", e);
            meterRegistry.counter("orders.rejected", "cause", "timeout").increment();
            throw new OrderFailedException("Unable to create order due to timeout from one of the services: " + e.getMessage());
        } catch (Exception e){
            LOG.error("Unexpected failure", e);
            throw new OrderFailedException("Unexpected failure");
        }
    }

    private void authorisePayment(PaymentRequest paymentRequest){
        try {
            LOG.info("Sending payment request: " + paymentRequest);
            PaymentResponse paymentResponse = paymentClient.createPayment(paymentRequest)
                    .timeout(ordersConfiguration.getTimeout(), TimeUnit.SECONDS)
                    .blockingSingle();
            LOG.info("Received payment response: " + paymentResponse);

            if (!paymentResponse.isAuthorised()) {
                LOG.warn("Payment rejected: " + paymentResponse);
                meterRegistry.counter("orders.rejected", "cause", "payment_declined").increment();
                throw new PaymentDeclinedException(paymentResponse.getMessage());
            }
        } catch (NoSuchElementException e) {
            meterRegistry.counter("orders.rejected", "cause", "payment_response_invalid").increment();
            throw new PaymentDeclinedException("Unable to parse authorisation packet");
        }
    }

    @Transactional
    CustomerOrder createOrder(PaymentRequest paymentRequest, List<Item> orderItems) throws TimeoutException {
        CustomerOrder order = new CustomerOrder(
                null,
                paymentRequest.getCustomer(),
                paymentRequest.getAddress(),
                paymentRequest.getCard(),
                orderItems,
                null,
                Calendar.getInstance().getTime(),
                paymentRequest.getAmount());

        LOG.info("Creating order: " + order);
        CustomerOrder savedOrder = customerOrderRepository.save(order);
        LOG.debug("Saved order: " + savedOrder);
        return savedOrder;
    }

    private float calculateTotal(List<Item> items) {
        float amount = 0F;
        float shipping = 4.99F;
        amount += items.stream().mapToDouble(i -> i.getQuantity() * i.getUnitPrice()).sum();
        amount += shipping;
        return amount;
    }
}
