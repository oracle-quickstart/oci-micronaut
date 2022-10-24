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
[/deploy/complete/helm-chart/](https://github.com/oracle-quickstart/oci-micronaut/tree/master/deploy/complete/helm-chart)).

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

The available endpoints can be browsed at http://localhost:8080/swagger/views/swagger-ui/

# Building and Running a GraalVM Native Image

To build the application into a GraalVM native image you can run:

```bash
./gradlew nativeCompile
```

Once the native image is built you can run it with:

```bash
./build/native-image/application
```

# Deployment to Oracle Cloud

The entire MuShop application can be deployed with the [Helm Chart](../../deploy/complete/helm-chart).

However, if you wish to deploy the events service manually you can do so.

First you need to [Login to Oracle Cloud Container Registry](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionslogintoocir.htm) then you can deploy the container image with:

```bash
./gradlew dockerPush
```

Or the native version with:

```bash
./gradlew dockerPushNative
```

The Docker image names to push to can be altered by editing the following lines in [build.gradle](https://github.com/oracle-quickstart/oci-micronaut/blob/983c78a8cd55ecc33b1b3aac6a2d68524683a5b3/src/events/build.gradle#L66-L72):

```groovy
dockerBuild {
    images = ["iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/$project.name-${javaBaseImage}:$project.version"]
}


dockerBuildNative {
    images = ["iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/${project.name}-native:$project.version"]
}
```

When running the container image on an Oracle Compute Instance VM or via OKE the following environment variables need to be set as defined in the [application-oraclecloud.yml](src/main/resources/application-oraclecloud.yml) configuration file:



| Env Var | Description |
| --- | --- |
| `ORACLECLOUD_METRICS_NAMESPACE` | The Oracle Cloud Monitoring Namespace. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#micrometer). |
| `ORACLECLOUD_METRICS_RESOURCEGROUP` | [The Oracle Cloud Monitoring Resource Group. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#micrometer). |
| `ORACLECLOUD_METRICS_COMPARTMENT_ID` | The Oracle Cloud Monitoring Compartment ID. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#micrometer). |
| `ORACLECLOUD_TRACING_ZIPKIN_HTTP_URL` | The Oracle Cloud Application Performance Monitoring Zipkin URL. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#tracing). |
| `ORACLECLOUD_TRACING_ZIPKIN_HTTP_PATH` | The Oracle Cloud Application Performance Monitoring Zipkin HTTP Path. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#tracing). |
| `ORACLECLOUD_KAFKA_BOOTSTRAP_SERVERS` | The Kafka bootstrap servers config to connect to OCI Streaming. See the [this blog post](https://blogs.oracle.com/developers/easy-messaging-with-micronauts-kafka-support-and-oracle-streaming-service) for an example.  |
| `ORACLECLOUD_KAFKA_SASL_JAAS_CONFIG` | he Kafka SASL JAAS config to connect to OCI Streaming. See the [this blog post](https://blogs.oracle.com/developers/easy-messaging-with-micronauts-kafka-support-and-oracle-streaming-service) for an example. |
