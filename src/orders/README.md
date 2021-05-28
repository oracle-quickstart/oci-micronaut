# Orders

Orders is a microservice implemented as a Micronaut application, using Oracle Autonomous Transaction Processing (ATP) as its data store. 

The API is exposed over HTTP, and when deployed over Kubernetes, can be scaled horizontally while other services discover and interact with this microservice over its Service abstraction.

## Oracle ATP 

Oracle Autonomous Database is a family of products with each member of the family optimized by workload. Autonomous Data Warehouse (ADW) and Autonomous Transaction Processing (ATP) are the two products that have been released in 2018.

# Micronaut Features

* [Micronaut Oracle Cloud](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/)
* [Micronaut Data JPA](https://micronaut-projects.github.io/micronaut-data/latest/guide/)
* [NATS Messaging](https://micronaut-projects.github.io/micronaut-nats/latest/guide/)
* [Kubernetes Service Discovery](https://micronaut-projects.github.io/micronaut-kubernetes/latest/guide/)
* Monitoring with [Micrometer](https://micrometer.io/) and [Prometheus](https://prometheus.io/)
* Tracing with [Zipkin](https://zipkin.io/)
* [Swagger API documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/)

# Usage

The MuShop application deploys this service using Helm, Kubernetes, and Docker. (See
[/deploy/complete/helm-chart/](https://github.com/pgressa/oraclecloud-cloudnative/tree/master/deploy/complete/helm-chart)).

# Running Locally

This application uses Oracle Autonomous Database and NATs for messaging. To run the application locally you can use a local Oracle database and modify the `datasources` configuration found in `src/main/resources/application.yml` accordingly.

Alternatively you can run Oracle in a container with the following command:

```bash
$ docker run -p 1521:1521 -e ORACLE_PASSWORD=oracle gvenzl/oracle-xe
```
You can then start NATs locally for messaging:

```bash
docker run -p 4222:4222 -p 6222:6222 -p 8222:8222 nats
```

Then run the application with:

```bash
./gradlew run
```

The available endpoints can be browsed at http://localhost:8082/swagger/views/swagger-ui

