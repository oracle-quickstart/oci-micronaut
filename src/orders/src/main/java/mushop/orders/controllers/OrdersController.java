/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.controllers;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.transaction.annotation.ReadOnly;
import io.reactivex.Single;
import mushop.orders.controllers.dto.CustomerOrdersDto;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.services.OrdersService;

import javax.transaction.Transactional;

/**
 * Orders controller.
 */
@Controller("/orders")
public class OrdersController {

    private final OrdersService ordersService;
    private final DtoMapper dtoMapper;

    OrdersController(OrdersService ordersService,
                     DtoMapper dtoMapper) {
        this.ordersService = ordersService;
        this.dtoMapper = dtoMapper;
    }

    @Status(HttpStatus.CREATED)
    @Post
    Single<CustomerOrder> newOrder(@Body NewOrderResource newOrderResource) {
        if (newOrderResource.getAddress() == null ||
                newOrderResource.getCustomer() == null ||
                newOrderResource.getCard() == null ||
                newOrderResource.getItems() == null) {
            throw new InvalidOrderException("Invalid order request. Order requires customer, address, card and items.");
        }
        return ordersService.placeOrder(newOrderResource);
    }

    @Transactional
    @ReadOnly
    @Get("/{orderId}")
    CustomerOrder getOrder(Long orderId) {
        return ordersService.getById(orderId);
    }

    @Transactional
    @ReadOnly
    @Get("/search/customer")
    CustomerOrdersDto searchCustomerOrders(@QueryValue String custId, Pageable pageable) {
        return dtoMapper.toCustomerOrdersDto(
                ordersService.searchCustomerOrders(custId, Pageable.from(0, -1, pageable.getSort()))
        );
    }

    public static class PaymentDeclinedException extends HttpStatusException {
        public PaymentDeclinedException(String s) {
            super(HttpStatus.NOT_ACCEPTABLE, s);
        }
    }

    public static class InvalidOrderException extends HttpStatusException {
        public InvalidOrderException(String s) {
            super(HttpStatus.NOT_ACCEPTABLE, s);
        }
    }

    public static class OrderFailedException extends HttpStatusException {
        public OrderFailedException(String s) {
            super(HttpStatus.SERVICE_UNAVAILABLE, s);
        }
    }
}
