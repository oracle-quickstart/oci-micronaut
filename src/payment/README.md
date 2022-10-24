# Payment

A microservices demo service implemented as a Micronaut application that provides payment services.

# Micronaut Features

* [Micronaut Oracle Cloud](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/)
* Monitoring with [Micrometer](https://micrometer.io/) and [Prometheus](https://prometheus.io/)
* Tracing with [Zipkin](https://zipkin.io/)
* [Swagger API documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/)

# Usage

The MuShop application deploys this service using Helm, Kubernetes, and Docker. (See
[/deploy/complete/helm-chart/](https://github.com/oracle-quickstart/oci-micronaut/tree/master/deploy/complete/helm-chart)).

# Running Locally

You can start the application with:

```bash
./gradlew run
```

The available endpoints can then be browsed at http://localhost:8080/swagger/views/swagger-ui

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

However, if you wish to deploy the payment service manually you can do so.

First you need to [Login to Oracle Cloud Container Registry](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionslogintoocir.htm) then you can deploy the container image with:

```bash
./gradlew dockerPush
```

Or the native version with:

```bash
./gradlew dockerPushNative
```

The Docker image names to push to can be altered by editing the following lines in [build.gradle](https://github.com/oracle-quickstart/oci-micronaut/blob/983c78a8cd55ecc33b1b3aac6a2d68524683a5b3/src/payment/build.gradle#L56-L62):

```groovy
dockerBuild {
    images = ["iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/$project.name-${javaBaseImage}:$project.version"]
}


dockerBuildNative {
    images = ["iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/${project.name}-native:$project.version"]
}
```

