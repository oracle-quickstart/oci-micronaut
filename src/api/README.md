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
