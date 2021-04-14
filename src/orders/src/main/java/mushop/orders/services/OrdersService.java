package mushop.orders.services;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.http.client.RxHttpClientFactory;
import io.reactivex.Completable;
import io.reactivex.Flowable;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static mushop.orders.controllers.OrdersController.OrderFailedException;
import static mushop.orders.controllers.OrdersController.PaymentDeclinedException;

@Singleton
public class OrdersService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Inject
    private CustomerOrderRepository customerOrderRepository;

    @Inject
    private MeterRegistry meterRegistry;

    @Inject
    private OrdersPublisher ordersPublisher;

    @Inject
    private PaymentClient paymentClient;

    @Inject
    private AsyncGetService asyncGetService;

    @Value("${http.timeout:5}")
    private long timeout;

    public CustomerOrder createNewOrder(NewOrderResource orderPayload) {
        LOG.info("Creating order {}", orderPayload);
        LOG.debug("Starting calls");
        meterRegistry.counter("orders.placed").increment();
        try {
            PaymentRequest paymentRequest = new PaymentRequest();
            AtomicReference<List<Item>> orderItems = new AtomicReference<>();

            boolean finished = Completable.merge(Arrays.asList(
                    Completable.fromPublisher(asyncGetService.getObject(orderPayload.address, Address.class).doOnNext(paymentRequest::setAddress)),
                    Completable.fromPublisher(asyncGetService.getObject(orderPayload.customer, Customer.class).doOnNext(paymentRequest::setCustomer)),
                    Completable.fromPublisher(asyncGetService.getObject(orderPayload.card, Card.class).doOnNext(paymentRequest::setCard)),
                    Completable.fromPublisher(asyncGetService.getDataList(orderPayload.items, Item.class).doOnNext((items -> {
                        orderItems.set(items);
                        //Calculate total
                        float amount = calculateTotal(items);
                        paymentRequest.setAmount(amount);
                    }))))).blockingAwait(timeout, TimeUnit.SECONDS);

            if (!finished) {
                throw new TimeoutException("Unable to create order due to timeout from one of the services.");
            }

            LOG.debug("End of calls.");

            LOG.info("Sending payment request: " + paymentRequest);
            try {
                PaymentResponse paymentResponse = paymentClient.createPayment(paymentRequest)
                        .timeout(timeout, TimeUnit.SECONDS)
                        .blockingSingle();
                LOG.info("Received payment response: " + paymentResponse);

                if (!paymentResponse.isAuthorised()) {
                    meterRegistry.counter("orders.rejected", "cause", "payment_declined").increment();
                    throw new PaymentDeclinedException(paymentResponse.getMessage());
                }
            } catch (NoSuchElementException e) {
                meterRegistry.counter("orders.rejected", "cause", "payment_response_invalid").increment();
                throw new PaymentDeclinedException("Unable to parse authorisation packet");
            }

            //Persist
            CustomerOrder order = new CustomerOrder(
                    null,
                    paymentRequest.getCustomer(),
                    paymentRequest.getAddress(),
                    paymentRequest.getCard(),
                    orderItems.get(),
                    null,
                    Calendar.getInstance().getTime(),
                    paymentRequest.getAmount());
            LOG.debug("Received data: " + order.toString());

            CustomerOrder savedOrder = customerOrderRepository.saveAndFlush(order);
            LOG.debug("Saved order: " + savedOrder);
            meterRegistry.summary("orders.amount").record(paymentRequest.getAmount());
            DistributionSummary.builder("order.stats")
                    .serviceLevelObjectives(10d, 20d, 30d, 40d, 50d, 60d, 70d, 80d, 90d, 100d, 110d)
                    //.publishPercentileHistogram()
                    .maximumExpectedValue(120d)
                    .minimumExpectedValue(5d)
                    .register(meterRegistry)
                    .record(paymentRequest.getAmount());
            OrderUpdate update = new OrderUpdate(savedOrder.getId(), null);
            ordersPublisher.dispatchToFulfillment(update);
            return savedOrder;
        } catch (TimeoutException e) {
            meterRegistry.counter("orders.rejected","cause","timeout").increment();
            throw new OrderFailedException("Unable to create order due to timeout from one of the services.", e);
        } catch (IOException e) {
            meterRegistry.counter("orders.rejected", "cause", "connectivity").increment();
            throw new OrderFailedException("Unable to create order due to unspecified IO error.", e);
        }
    }

    private float calculateTotal(List<Item> items) {
        float amount = 0F;
        float shipping = 4.99F;
        amount += items.stream().mapToDouble(i -> i.getQuantity() * i.getUnitPrice()).sum();
        amount += shipping;
        return amount;
    }
}
