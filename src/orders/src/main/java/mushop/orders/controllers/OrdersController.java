/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.controllers;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.transaction.annotation.ReadOnly;
import io.reactivex.Single;

import java.util.Optional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import mushop.orders.controllers.dto.CustomerOrderDto;
import mushop.orders.controllers.dto.CustomerOrdersDto;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.services.OrdersService;

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
    Single<CustomerOrderDto> newOrder(@Body @Valid NewOrderResource newOrderResource) {
        return ordersService.placeOrder(newOrderResource)
                .map(dtoMapper::toCustomerOrderDto);
    }

    @ReadOnly
    @Get("/{orderId}")
    Optional<CustomerOrderDto> getOrder(Long orderId) {
        Optional<CustomerOrder> order = ordersService.getById(orderId);
        CustomerOrderDto ordersDto = null;
        if (order.isPresent()) {
            ordersDto = dtoMapper.toCustomerOrderDto(order.get());
        }
        return Optional.ofNullable(ordersDto);
    }

    @ReadOnly
    @Get("/search/customer")
    CustomerOrdersDto searchCustomerOrders(@QueryValue String custId, Pageable pageable) {
        return dtoMapper.toCustomerOrdersDto(
                ordersService.searchCustomerOrders(custId, Pageable.from(0, -1, pageable.getSort()))
        );
    }

    @Error(ConstraintViolationException.class)
    public HttpResponse<JsonError> constraintError(HttpRequest request, ConstraintViolationException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.status(HttpStatus.NOT_ACCEPTABLE)
                .body(error);
    }

    public static class PaymentDeclinedException extends HttpStatusException {
        public PaymentDeclinedException(String s) {
            super(HttpStatus.NOT_ACCEPTABLE, s);
        }
    }

    public static class OrderFailedException extends HttpStatusException {
        public OrderFailedException(String s) {
            super(HttpStatus.SERVICE_UNAVAILABLE, s);
        }
    }
}
