# Orders

Orders is a microservice implemented as a Micronaut application.

The API is exposed over HTTP, and when deployed over Kubernetes, can be scaled horizontally while other services discover and interact with this microservice over its Service abstraction.

The `app` subproject contains the application code with no Cloud specific dependencies or configuration.

The `aws` subproject depends on the `app` project and introduces configuration (defined in `aws/src/main/resources/application-ec2.yml`) and dependencies (defined in `aws/build.gradle`) that integrate the application with services of AWS:

* AWS RDS MySQL
* AWS CloudWatch Metrics
* AWS CloudWatch Tracing
* AWS Secrets Manager

The `oci` subproject depends on the `app` project and introduces configuration (defined in `oci/src/main/resources/application-oraclecloud.yml`) and dependencies (defined in `oci/build.gradle`) that integrate the application with services of Oracle Cloud:

* Oracle Cloud Autonomous Transaction Processing (ATP)
* Oracle Cloud Application Monitoring (Metrics)
* Oracle Cloud Application Performance Monitoring (Tracing)

# Micronaut Features

* `app`
    * [Micronaut Data JPA](https://micronaut-projects.github.io/micronaut-data/latest/guide/)
    * [NATS Messaging](https://micronaut-projects.github.io/micronaut-nats/latest/guide/)
    * [Kubernetes Service Discovery](https://micronaut-projects.github.io/micronaut-kubernetes/latest/guide/)
    * Monitoring with [Micrometer](https://micrometer.io/) and [Prometheus](https://prometheus.io/)
    * Tracing with [Zipkin](https://zipkin.io/)
    * [Swagger API documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/)
* `aws`
    * [AWS Secrets Manager](https://micronaut-projects.github.io/micronaut-aws/latest/guide/#distributedconfigurationsecretsmanager)
    * [AWS Parameter Store](https://micronaut-projects.github.io/micronaut-aws/latest/guide/#parametersStore)
* `oci`
    * [Micronaut Oracle Cloud](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/)

# Usage

The MuShop application deploys this service using Helm, Kubernetes, and Docker. (See
[/deploy/complete/helm-chart/](https://github.com/oracle-quickstart/oci-micronaut/tree/master/deploy/complete/helm-chart)).

# Running Locally

This application uses NATs for messaging.

You can start NATs locally:

```bash
docker run -p 4222:4222 -p 6222:6222 -p 8222:8222 nats
```

Then run the application with:

```bash
./gradlew :app:run
```

The available endpoints can be browsed at http://localhost:8082/swagger/views/swagger-ui/

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

However, if you wish to deploy the orders service manually you can do so.

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
| `ORACLECLOUD_ATP_OCID` | The Oracle Autonomous Database OCID. See the [documentation for more info](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#autonomousDatabase).  |
| `ORACLECLOUD_ATP_WALLET_PASSWORD` | password to encrypt the keys inside the wallet, that must be at least 8 characters long and must include at least 1 letter and either 1 numeric character or 1 special character |
| `ORACLECLOUD_ATP_USERNAME` | The database username |
| `ORACLECLOUD_ATP_PASSWORD` | The database password |

In addition [instance principal needs to be configured](https://docs.oracle.com/en-us/iaas/Content/Identity/Tasks/callingservicesfrominstances.htm) to ensure the VM or container has access to the necessary Oracle Cloud resources.
