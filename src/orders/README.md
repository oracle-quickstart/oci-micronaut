# Orders

Orders is a microservice implemented as a Micronaut application, using Oracle Autonomous Transaction Processing (ATP) as its data store. 

The API is exposed over HTTP, and when deployed over Kubernetes, can be scaled horizontally while other services discover and interact with this microservice over its Service abstraction.

## Oracle ATP 

Oracle Autonomous Database is a family of products with each member of the family optimized by workload. Autonomous Data Warehouse (ADW) and Autonomous Transaction Processing (ATP) are the two products that have been released in 2018.

# Micronaut Features

* [Micronaut Oracle Cloud](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/)
* [Micronaut Data JPA](https://micronaut-projects.github.io/micronaut-data/latest/guide/)
* [NATS Messaging](https://micronaut-projects.github.io/micronaut-nats/latest/guide/)
* [Kubernetes service discovery](https://micronaut-projects.github.io/micronaut-kubernetes/latest/guide/)
* Monitoring with [Micrometer](https://micrometer.io/) and [Prometheus](https://prometheus.io/)
* Tracing with [Zipkin](https://zipkin.io/)
* [Swagger API documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/)

# Usage

The MuShop application deploys this service using Helm, Kubernetes, and Docker. (See
[/deploy/complete/helm-chart/](https://github.com/pgressa/oraclecloud-cloudnative/tree/master/deploy/complete/helm-chart)).
