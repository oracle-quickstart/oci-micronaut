# Catalogue

A microservices demo service written as a Micronaut application in Java that provides catalogue/product information stored on [Oracle Autonomous Database](https://www.oracle.com/autonomous-database/).

### API Spec

Checkout the API Spec [here](https://mushop.docs.apiary.io)

# Micronaut Features

* [Micronaut Oracle Cloud](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/)
* [Micronaut Data JDBC](https://micronaut-projects.github.io/micronaut-data/latest/guide/)
* [Flyway Database Migration](https://micronaut-projects.github.io/micronaut-flyway/latest/guide/)
* Monitoring with [Micrometer](https://micrometer.io/) and [Prometheus](https://prometheus.io/)
* Tracing with [Zipkin](https://zipkin.io/)
* [Swagger API documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/)

# Usage

The MuShop application deploys this service using Helm, Kubernetes, and Docker. (See
[/deploy/complete/helm-chart/](https://github.com/pgressa/oraclecloud-cloudnative/tree/master/deploy/complete/helm-chart)).

# Running Locally

This application uses Oracle Autonomous Database when running in Oracle Cloud. To run the application locally you can use a local Oracle database and modify the `datasources` configuration found in `src/main/resources/application.yml` accordingly.

Alternatively you can run Oracle in a container with the following command:

```bash
$ docker run -p 1521:1521 -e ORACLE_PASSWORD=oracle gvenzl/oracle-xe
```

Then start the application with:

```bash
./gradlew run
```

The available endpoints can be browsed at http://localhost:8080/swagger/views/swagger-ui