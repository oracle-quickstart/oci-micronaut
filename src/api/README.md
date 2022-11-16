# MuShop Storefront API

---
Storefront backend written as a Micronaut application in Java orchestrating services for consumption by the microservices [web application](../storefront)

> Modified from original source by Weaveworks [microservices-demo](https://github.com/microservices-demo/front-end)

The `app` subproject contains the application code with no Cloud specific dependencies or configuration.

The `aws` subproject depends on the `app` project and introduces configuration (defined in `aws/src/main/resources/application-ec2.yml`) and dependencies (defined in `aws/build.gradle`) that integrate the application with services of AWS:

* AWS CloudWatch Metrics
* AWS CloudWatch Tracing

The `oci` subproject depends on the `app` project and introduces configuration (defined in `oci/src/main/resources/application-oraclecloud.yml`) and dependencies (defined in `oci/build.gradle`) that integrate the application with services of Oracle Cloud:

* Oracle Cloud Application Monitoring (Metrics)
* Oracle Cloud Application Performance Monitoring (Tracing)

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
[/deploy/complete/helm-chart/](https://oracle-quickstart.github.io/oci-micronaut/quickstart/).


# Running Locally

This application is a gateway service that communicates with other backend services that form part of the MuShop topology.

To start the application you can run:

```bash
./gradlew :app:run
```

The available endpoints can be browsed at http://localhost:8080/swagger/views/swagger-ui/

Note that in order for the application to be functional it may require you to start an associated backend service. For example accessing the catalogue via:

```bash
curl -i http://localhost:8080/api/catalogue
```

Will result in a `No available services for ID` error unless the catalogue service is running. You can start the entire topology using the `docker-compose.yml` located in [this directory](https://github.com/oracle-quickstart/oci-micronaut/tree/main/deploy/complete/docker-compose).

Alternatively you can start an individual backend service on another port:

```bash
cd ../catalogue
./gradlew :app:run --args="-Dmicronaut.server.port=8082"
```

In the above case catalogue is run on port 8082, then you can run the API service and link service discovery to the backend service:

```bash
cd ../api
./gradlew :app:run --args="-Dmicronaut.http.services.mushop-catalogue.url=http://localhost:8082"
```

Here the `mushop-catalogue` service is bound as a specific URL. In production and when deployed on OKE the application uses Kubernetes based service discovery and thanks to the following configuration specified in `app/src/main/resources/bootstrap.yml`:

```yaml
kubernetes:
  client:
    discovery:
      mode: service
      enabled: true

```

The following table lists the service IDs the API application uses to discover services:

| Service ID | Description |
| --- | --- |
| `mushop-carts` | [The Carts Application](../carts) |
| `mushop-user` | [The Users Application](../user) |
| `mushop-orders` | [The Orders Application](../orders) |
| `mushop-catalogue` | [The Catalogue Application](../catalogue) |
| `mushop-events` | [The Catalogue Application](../events) |
| `mushop-newsletter` | [The NewsLetter Function](../functions/newsletter-subscription) |


# Building and Running a GraalVM Native Image

To build the application into a GraalVM native image you can run:

```bash
./gradlew :app:nativeCompile
```

Once the native image is built you can run it with:

```bash
./app/build/native/nativeCompile/app
```

# Deployment to Oracle Cloud

The entire MuShop application can be deployed with the [Helm Chart](../../deploy/complete/helm-chart).

However, if you wish to deploy the api service manually you can do so.

First you need to [Login to Oracle Cloud Container Registry](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionslogintoocir.htm), then you can deploy the container image with:

```bash
./gradlew :oci:dockerPush
```

Or the native version with:

```bash
./gradlew :oci:dockerPushNative
```

The Docker image names to push to can be altered by editing the following lines in subproject build.gradle files.

```groovy
dockerBuild {
    images = ["phx.ocir.io/oraclelabs/micronaut-showcase/mushop/$project.parent.name-$project.name-${javaBaseImage}:$project.version"]
}


dockerBuildNative {
    images = ["phx.ocir.io/oraclelabs/micronaut-showcase/mushop/${project.parent.name}-${project.name}-native:$project.version"]
}
```

When running the container image on an Oracle Compute Instance VM or via OKE the following environment variables need to be set as defined in the [application-oraclecloud.yml](oci/src/main/resources/application-oraclecloud.yml) configuration file:


| Env Var | Description |
| --- | --- |
| `ORACLECLOUD_METRICS_NAMESPACE` | The Oracle Cloud Monitoring Namespace. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#micrometer). |
| `ORACLECLOUD_METRICS_RESOURCEGROUP` | [The Oracle Cloud Monitoring Resource Group. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#micrometer). |
| `ORACLECLOUD_METRICS_COMPARTMENT_ID` | The Oracle Cloud Monitoring Compartment ID. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#micrometer). |
| `ORACLECLOUD_TRACING_ZIPKIN_HTTP_URL` | The Oracle Cloud Application Performance Monitoring Zipkin URL. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#tracing). |
| `ORACLECLOUD_TRACING_ZIPKIN_HTTP_PATH` | The Oracle Cloud Application Performance Monitoring Zipkin HTTP Path. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#tracing). |
| `REDIS_URI` | The URI of the Redis instance used to manage HTTP sessions |

In addition [instance principal needs to be configured](https://docs.oracle.com/en-us/iaas/Content/Identity/Tasks/callingservicesfrominstances.htm) to ensure the VM or container has access to the necessary Oracle Cloud resources.
