# MuShop Storefront API

---
Storefront backend written as a Micronaut application in Java orchestrating services for consumption by the microservices [web application](../storefront)

> Modified from original source by Weaveworks [microservices-demo](https://github.com/microservices-demo/front-end)

# Micronaut Features

* [Micronaut Oracle Cloud](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/)
* Monitoring with [Micrometer](https://micrometer.io/) and [Prometheus](https://prometheus.io/)
* Tracing with [Zipkin](https://zipkin.io/)
* Session-based and HTTP Basic [security](https://micronaut-projects.github.io/micronaut-security/latest/guide/)
* [Kubernetes service discovery](https://micronaut-projects.github.io/micronaut-kubernetes/latest/guide/)
* [Redis HTTP session store](https://micronaut-projects.github.io/micronaut-redis/latest/guide/)
* [Swagger API documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/)

# Usage

The MuShop application deploys this service using Helm, Kubernetes, and Docker. (See
[/deploy/complete/helm-chart/](https://github.com/pgressa/oraclecloud-cloudnative/tree/master/deploy/complete/helm-chart)).


# Running Locally

This application is a gateway service that communicates with other backend services that form part of the MuShop topology.

To start the application you can run:

```bash
./gradlew run
```

The available endpoints can be browsed at http://localhost:8080/swagger/views/swagger-ui

Note that in order for the application to be functional it may require you to start an associated backend service. For example accessing the catalogue via:

```bash
curl -i http://localhost:8080/api/catalogue
```

Will result in a `No available services for ID` error unless the catalogue service is running. You can start the entire topology using the `docker-compose.yml` located in [this directory](https://github.com/pgressa/oci-cloudnative/blob/master/deploy/complete/docker-compose).

Alternatively you can start an individual backend service on another port:

```
cd ../catalogue
docker run -d -p 1521:1521 -e ORACLE_PASSWORD=oracle gvenzl/oracle-xe
./gradlew run --args="-Dmicronaut.server.port=8082"
```

In the above case catalogue is run on port 8082, then you can run the API service and link service discovery to the backend service:

```bash
cd ../api
./gradlew run --args="-Dmicronaut.http.services.mushop-catalogue.url=http://localhost:8082"
```

Here the `mushop-catalogue` service is bound as a specific URL. In production and when deployed on OKE the application uses Kubernetes based service discovery and thanks to the following configuration specified in `src/main/resources/bootstrap.yml`:

```yaml
kubernetes:
  client:
    discovery:
      mode: service
      enabled: true

```

The following table lists the service IDs the API application uses to discovery services:

| Service ID | Description |
| --- | --- |
| `mushop-carts` | [The Carts Application](../carts) |
| `mushop-user` | [The Users Application](../user) |
| `mushop-orders` | [The Orders Application](../orders) |
| `mushop-catalogue` | [The Catalogue Application](../catalogue) |
| `mushop-events` | [The Catalogue Application](../events) |
| `mushop-newsletter` | [The NewsLetter Function](../functions/newsletter-subscription) |