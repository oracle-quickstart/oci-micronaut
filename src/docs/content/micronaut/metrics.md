---
title: "Application Metrics and Monitoring"
draft: false
disableBreadcrumb: true
weight: 4
---

The original MuShop applications implemented exposing metrics inconsistently with some applications exporting metrics and others not. The applications that did export metrics provided support only for Prometheus, for example in the Go code:

```golang
r.Handle("/metrics", promhttp.Handler())
```

Micronaut builds on [Micrometer](https://micrometer.io) and supports exporting Metrics to over a dozen metrics backends including Prometheus and Oracle Cloud.

Every single application in the Micronaut MuShop demonstration exports metrics consistently and uniformally through the addition of this simple configuration:

```yaml
micronaut:
  metrics:
    export:
      prometheus:
        enabled: true
        descriptions: true
        step: PT1M    	
      oraclecloud:
        enabled: true
        namespace: ${ORACLECLOUD_METRICS_NAMESPACE:micronaut_mushop}
        resourceGroup: ${ORACLECLOUD_METRICS_RESOURCEGROUP:user}
        compartmentId: ${ORACLECLOUD_METRICS_COMPARTMENT_ID}

```

In addition, timers and counters can easily be added to any code in the Micronaut application simply by annotating a method with `@Timed` or `@Counted`, for example the original Helidon/Java code was written with explicit calls to add timings and meters:

```java
public void deleteCartItem(ServerRequest request, ServerResponse response) {

    String cartId = request.path().param("cartId");
    String itemId = request.path().param("itemId");
    try {
        Cart cart = carts.getById(cartId);
        if (cart == null || !cart.removeItem(itemId)) {
            response.status(404).send();
            return;
        }
        Timer.Context context = saveCartTimer.time();
        carts.save(cart);
        context.close();
        response.status(200).send();
    } catch (Exception e) {
        log.log(Level.SEVERE, "deleteCartItem failed.", e);
        sendError(response, e.getMessage());
        return;
    }
}
```

This error prone logic is greatly simplified by simply adding `@Timed` to method in Micronaut:

```java
@Delete("/{cartId}/items/{itemId}")
@Timed("carts.updated.timer")
Cart deleteCartItem(String cartId, String itemId) {
    Cart cart = cartRepository.getById(cartId);
    if (cart == null || !cart.removeItem(itemId)) {
        throw new HttpStatusException(HttpStatus.NOT_FOUND,
                "Cart with id " + cartId + " not found");
    }
    cartRepository.save(cart);
    return cart;
}
```