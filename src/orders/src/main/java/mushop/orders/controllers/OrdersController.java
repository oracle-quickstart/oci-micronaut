/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.controllers;

import io.micrometer.core.lang.Nullable;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
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
import mushop.orders.controllers.dto.CustomerOrderDto;
import mushop.orders.controllers.dto.CustomerOrdersDto;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.services.OrdersService;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Orders controller.
 */
@Controller("/orders")
public class OrdersController {

    private final OrdersService ordersService;
    private final DtoMapper dtoMapper;

    public OrdersController(OrdersService ordersService, DtoMapper dtoMapper) {
        this.ordersService = ordersService;
        this.dtoMapper = dtoMapper;
    }

    @Status(HttpStatus.CREATED)
    @Post
    public Single<CustomerOrder> newOrder(@Body NewOrderResource newOrderResource) {
        if (newOrderResource.address == null || newOrderResource.customer == null || newOrderResource.card == null || newOrderResource.items == null) {
            throw new InvalidOrderException("Invalid order request. Order requires customer, address, card and items.");
        }
        return ordersService.placeOrder(newOrderResource);
    }

    @Transactional
    @ReadOnly
    @Get
    public CustomerOrder getOrder(Long orderId) {
        return ordersService.getById(orderId);
    }

    @Transactional
    @ReadOnly
    @Get("/search/customer{?custId}{&sort}")
    public CustomerOrdersDto searchCustomerOrders(Optional<String> custId, Optional<String> sort) {

        if (custId.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Missing custId parameter.");
        }

        Sort sorted = null;
        if (sort.isPresent()) {
            String[] arr = sort.get().split(",");
            if (arr.length == 1) {
                sorted = Sort.of(Sort.Order.desc(arr[0]));
            } else if (arr.length == 2) {
                try {
                    Sort.Order.Direction d = Sort.Order.Direction.valueOf(arr[1].toUpperCase());
                    sorted = Sort.of(new Sort.Order(arr[0], d, true));
                } catch (IllegalArgumentException e) {
                    sorted = Sort.of(Sort.Order.desc(arr[0]));
                }
            }
        } else {
            sorted = Sort.unsorted();
        }

        Page<CustomerOrder> customerOrders = ordersService.searchCustomerOrders(custId.get(), Pageable.from(0, -1, sorted));
        return dtoMapper.toCustomerOrdersDto(customerOrders);
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
