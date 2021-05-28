# MuShop User Service

Represents a microservice for customer information, authentication, and metadata, implemented as a Micronaut application.

The service uses [Oracle Autonomous Database](https://www.oracle.com/autonomous-database/) as its data store.

### REST

The following table describes the high-level REST endpoints provided
by this service.

| Endpoint | Description | Verb |
|---|---|---|
| `/register` | User registration | `POST` |
| `/login` | User authentication | `POST` |
| `/customers[{userId}]` | CRUD endpoints for **customer** | `GET`, `PUT`, `DELETE` |
| `/customers/{userId}/cards[/{cardId}]` | CRUD endpoints for customer **cards** | `GET`, `POST`, `DELETE` |
| `/customers/{userId}/addresses[/{addressId}]` | CRUD endpoints for customer **addresses** | `GET`, `POST`, `DELETE` |

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