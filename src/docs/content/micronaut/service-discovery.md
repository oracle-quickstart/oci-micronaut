---
title: "Service Discovery"
draft: false
disableBreadcrumb: true
weight: 2
---

In a Microservice architecture services need to discover each other in a decoupled manner that is independent of the service discovery mechanism.

Micronaut features a [Service Discovery abstraction](https://docs.micronaut.io/latest/guide/#serviceDiscovery) that allows service discovery to be backed onto different implementations including Kubernetes, HashiCorp Consul, Eureka and more.

The original MuShop application featured extensive handcrafted service discovery code in both the Spring application:

* [OrdersConfigurationProperties](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/orders/src/main/java/mushop/orders/config/OrdersConfigurationProperties.java) & [RestProxyTemplate](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/orders/src/main/java/mushop/orders/config/RestProxyTemplate.java) - hand crafted Spring service discovery configuration handling
* [OrdersConfigurationProperties](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/orders/src/main/java/mushop/orders/config/OrdersConfigurationProperties.java) & [RestProxyTemplate](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/orders/src/main/java/mushop/orders/config/RestProxyTemplate.java) - hand crafted Spring service discovery configuration handling

And Node/Express JavaScript API:

* [common/index.js](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/api/api/common/index.js) - Manual service discovery implementation
* [endpoints.js](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/api/api/endpoints.js) - Manual service discovery configuration
* [helpers/index.js](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/api/helpers/index.js#L145-L174) - More manual service discovery routines

By migrating the code to Micronaut all this code could be deleted and instead encapsulated by the defining of a simple [service ID](https://github.com/pgressa/oraclecloud-cloudnative/blob/97faa619b4f297e72ec6de8e9d4dfe81ffee6493/src/api/src/main/java/api/services/CartsService.java#L166-L170) and declarative client interfaces:

```java
@Client(id = "mushop-catalogue", path = "/catalogue")
public interface CatalogueClient {
    @Get("/{id}")
    Maybe<Product> getItem(String id);
}
```

Using the service ID Micronaut provides client side lookup and load balancing and the target service to invoke is abstracted such that it can be found via explicit declaration in configuration (for example when working locally):

```yaml
micronaut:
	http:
		services:
			mushop-catalogue:
				url: http://localhost:8082
```

Or automatically via [Micronaut's integration with Kubernetes](https://micronaut-projects.github.io/micronaut-kubernetes/snapshot/guide/#service-discovery) when deployed to a cluster.






