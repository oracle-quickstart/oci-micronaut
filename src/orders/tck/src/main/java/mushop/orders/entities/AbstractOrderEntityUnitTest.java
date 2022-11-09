package mushop.orders.entities;

import io.micronaut.context.annotation.Property;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import mushop.orders.AbstractTest;
import mushop.orders.repositories.CustomerOrderRepository;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "jpa.default.properties.hibernate.hbm2ddl.auto", value = "create-drop")
abstract class AbstractOrderEntityUnitTest extends AbstractTest {

    @Inject
    private CustomerOrderRepository orderRepository;

    private CustomerOrder createOrderObj() {
        Address address = new Address("id","000","street","city","00000","coutry");
        Card card = new Card("id","0000000000000000","00/00","000");
        Customer customer = new Customer("cust001","first","last","user", Collections.emptyList(),Collections.emptyList());
        return new CustomerOrder(null,
                customer,
                address,
                card,
                null,
                null,
                new Date(),
                0.0f);
    }

    @Test
    public void findOrdersByCustomerId() {
        CustomerOrder order = createOrderObj();
        orderRepository.save(order);
        orderRepository.flush();
        Long orderId = order.getId();
        Page<CustomerOrder> found =  orderRepository.findByCustomerId("cust001", Pageable.unpaged());
        for (CustomerOrder orderFound : found) {
            assertEquals(orderId, orderFound.getId());
        }
    }

    @Test
    public void findOrderByInvalidIdFails(){
        CustomerOrder order = createOrderObj();
        orderRepository.save(order);
        orderRepository.flush();
        Long orderId = order.getId();
        assertFalse(orderRepository.findById(orderId + 50).isPresent());
    }

    @Test
    public void findOrderByIdSucceeds(){
        CustomerOrder order = createOrderObj();
        orderRepository.save(order);
        orderRepository.flush();
        Long orderId = order.getId();
        assertTrue(orderRepository.findById(orderId).isPresent());
    }

    @Test
    public void addressIsPersisted(){
        CustomerOrder order = createOrderObj();
        orderRepository.save(order);
        orderRepository.flush();
        Long orderId = order.getId();
        assertEquals(order.getAddress(), orderRepository.findById(orderId).get().getAddress());
    }
}
