---
title: "HTTP Client"
draft: false
disableBreadcrumb: true
weight: 11
---

Micronaut features an HTTP client that is included by default and automatically instrumented for distributed tracing, service discovery and metrics. The Spring application in the original MuShop features many manually written HTTP calls that [amount to another 100 lines of additional code](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/orders/src/main/java/mushop/orders/services/AsyncGetService.java).

This HTTP client logic was completely removed in the Micronaut application as the existing HTTP client was simply injected via service discovery:

```java
public OrdersService(CustomerOrderRepository customerOrderRepository,
                     MeterRegistry meterRegistry,
                     OrdersPublisher ordersPublisher,
                     PaymentClient paymentClient,
                     OrdersConfiguration ordersConfiguration,
                     @Client("users") RxHttpClient userClient,  // HTTP clients injected here
                     @Client("carts") RxHttpClient cartsClient) { 
    ...
}
```