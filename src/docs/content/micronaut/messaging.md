---
title: "Messaging"
draft: false
disableBreadcrumb: true
weight: 10
---

The original Spring-based application includes [extensive logic to send NATs messages](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/orders/src/main/java/mushop/orders/services/MessagingService.java).

This is greatly simplified in Micronaut with the Micronaut NATS module:

```java
@NatsClient
public interface OrdersPublisher {

    @Subject("mushop-orders")
    void dispatchToFulfillment(OrderUpdate orderUpdate);
}
```

In the above example Micronaut automatically implements the logic to send a message and handle any errors, avoiding 115 lines of additional code.

To Go application that sends events via OCI streaming again features over a [100 lines of code to send messages with the OCI SDK](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/events/service.go). This logic is coupled to OCI and not portable across Clouds.

The Micronaut version is simplified through the use of the Micronaut Kafka module which encapsulates the logic needed to define Kafka producers. 

```java
@KafkaClient(batch = true)
public interface EventProducer {
    @Topic("events")
    void send(EventRecord... eventRecords);
}

```

Since the Kafka API is used to send the message internally, the application can be moved to any Cloud infrastructure that features a Kafka compatible API.