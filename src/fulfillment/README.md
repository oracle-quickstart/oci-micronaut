# Fulfillment

This service represents a fulfillment system in an e-commerce platform. The key idea demonstrated by this service is asynchronous, message-based application design. This allows us to scale the order management and fulfillment systems independently and provides for better fault tolerance. A messaging system also makes it easy for programs to communicate across different environments, languages, cloud providers and on-premise systems. Generally more throughput can be achieved with these designs than traditional synchronous, blocking models.

The service is implemented as a Micronaut application written in Java.

## Messaging with NATS.io

The messaging system used is [nats.io](https://nats.io). NATS is a production ready messaging system that is extremely lightweight. It's also a cloud-native CNCF project with Kubernetes and Prometheus integrations.

# Micronaut Features

* [Micronaut Oracle Cloud](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/)
* [NATS Messaging](https://micronaut-projects.github.io/micronaut-nats/latest/guide/)
* Monitoring with [Micrometer](https://micrometer.io/) and [Prometheus](https://prometheus.io/)
* Tracing with [Zipkin](https://zipkin.io/)
* [Swagger API documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/)

# Usage

The MuShop application deploys this service using Helm, Kubernetes, and Docker. (See
[/deploy/complete/helm-chart/](https://github.com/pgressa/oraclecloud-cloudnative/tree/master/deploy/complete/helm-chart)).
