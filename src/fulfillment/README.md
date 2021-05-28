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

# Running Locally

To run the application locally first start a NATS server:

```bash
docker run -p 4222:4222 -p 6222:6222 -p 8222:8222 nats
```

Then start the application with:

```bash
./gradlew run
```

The available endpoints can be browsed at http://localhost:8082/swagger/views/swagger-ui

# Building and Running a GraalVM Native Image

To build the application into a GraalVM native image you can run:

```bash
./gradlew nativeImage
```

Once the native image is built you can run it with:

```bash
./build/native-image/application
```

# Deployment to Oracle Cloud

The entire MuShop application can be deployed with the [Helm Chart](../../deploy/complete/helm-chart).

However, if you wish to deploy the fulfillment service manually you can do so.

First you need to [Login to Oracle Cloud Container Registry](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionslogintoocir.htm) then you can deploy the container image with:

```bash
./gradlew dockerPush
```

Or the native version with:

```bash
./gradlew dockerPushNative
```

The Docker image names to push to can be altered by editing the following lines in [build.gradle](https://github.com/pgressa/oraclecloud-cloudnative/blob/983c78a8cd55ecc33b1b3aac6a2d68524683a5b3/src/fulfillment/build.gradle#L70-L76):

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

In addition [instance principal needs to be configured](https://docs.oracle.com/en-us/iaas/Content/Identity/Tasks/callingservicesfrominstances.htm) to ensure the VM or container has access to the necessary Oracle Cloud resources.
