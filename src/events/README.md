# Events

A microservices-demo service written as a Micronaut application in Java that provides event streaming services.

# Micronaut Features

* [Micronaut Oracle Cloud](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/)
* [Event streaming with Kafka](https://micronaut-projects.github.io/micronaut-kafka/latest/guide/)
* Monitoring with [Micrometer](https://micrometer.io/) and [Prometheus](https://prometheus.io/)
* Tracing with [Zipkin](https://zipkin.io/)
* [Swagger API documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/)


# Usage

The MuShop application deploys this service using Helm, Kubernetes, and Docker. (See
[/deploy/complete/helm-chart/](https://github.com/pgressa/oraclecloud-cloudnative/tree/master/deploy/complete/helm-chart)).

# Running Locally

This service uses Oracle Cloud Streaming which provides a Kafka-compatible API for streaming applications.

To run the application locally you need either Kafka or a Kafka-compatible server such as Redpanda running locally. 

For example to run Redpanda locally execute:

```bash
docker run --pull=always --name=redpanda-1 --rm \
-p 9092:9092 \
vectorized/redpanda:latest \
start \
--overprovisioned \
--smp 1  \
--memory 1G \
--reserve-memory 0M \
--node-id 0 \
--check=false
```

Then start the application with:

```bash
./gradlew run
```

The available endpoints can be browsed at http://localhost:8080/swagger/views/swagger-ui