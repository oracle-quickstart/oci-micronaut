/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.controllers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.services.OrdersService;

import javax.inject.Inject;

@Controller("/orders")
@ExecuteOn(TaskExecutors.IO)
public class OrdersController {

    @Inject
    private OrdersService ordersService;

    @Status(HttpStatus.CREATED)
    @Post(processes = MediaType.APPLICATION_JSON)
    public CustomerOrder newOrder(@Body NewOrderResource newOrderResource) {
        if (newOrderResource.address == null || newOrderResource.customer == null || newOrderResource.card == null || newOrderResource.items == null) {
            throw new InvalidOrderException("Invalid order request. Order requires customer, address, card and items.");
        }
        return ordersService.createNewOrder(newOrderResource);
    }

    public static class PaymentDeclinedException extends IllegalStateException {
        public PaymentDeclinedException(String s) {
            super(s);
        }
    }

    public class InvalidOrderException extends IllegalStateException {
        public InvalidOrderException(String s) {
            super(s);
        }
    }

    public static class OrderFailedException extends IllegalStateException {
        public OrderFailedException(String s, Throwable e) {
            super(s, e);
        }
    }

    @Error
    HttpResponse<JsonError> failedOrderException(HttpRequest request, InvalidOrderException invalidOrderException) {
        JsonError error = new JsonError("Invalid Order: " + invalidOrderException.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>status(HttpStatus.NOT_ACCEPTABLE, "Invalid order")
                .body(error);
    }

    @Error
    HttpResponse<JsonError> orderFailedException(HttpRequest request, OrderFailedException orderFailedException) {
        JsonError error = new JsonError("Order failed: " + orderFailedException.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>status(HttpStatus.SERVICE_UNAVAILABLE, "Order failed")
                .body(error);
    }

    @Error
    HttpResponse<JsonError> paymentDeclinedException(HttpRequest request, PaymentDeclinedException paymentDeclinedException) {
        JsonError error = new JsonError("Payment declined: " + paymentDeclinedException.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>status(HttpStatus.NOT_ACCEPTABLE, "Payment declined")
                .body(error);
    }

}
