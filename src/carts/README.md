# Shopping Cart

A microservice demo service written as a Micronaut application in Java that stores the MuShop shopping carts.

The `app` subproject contains the application code with no Cloud specific dependencies or configuration.

The `aws` subproject depends on the `app` project and introduces configuration (defined in `aws/src/main/resources/application-ec2.yml`) and dependencies (defined in `aws/pom.xml`) that integrate the application with services of AWS:

* AWS DocumentDB
* AWS CloudWatch Metrics
* AWS CloudWatch Tracing
* AWS Secrets Manager

The `oci` subproject depends on the `app` project and introduces configuration (defined in `oci/src/main/resources/application-oraclecloud.yml`) and dependencies (defined in `oci/pom.xml`) that integrate the application with services of Oracle Cloud:

* Oracle Cloud Autonomous Transaction Processing (ATP)
* Oracle Cloud Application Monitoring (Metrics)
* Oracle Cloud Application Performance Monitoring (Tracing)

The `oci` subproject makes use of the following components:

  * **Autonomous Database JSON** - Each shopping cart is stored in the autonomous database as a JSON document using [Oracle Database API for MongoDB](https://docs.oracle.com/en/database/oracle/mongodb-api/mgapi/overview-oracle-database-api-mongodb.html).  The microservice uses [Micronaut Data MongoDB](https://micronaut-projects.github.io/micronaut-data/latest/guide/#mongo) to execute create, read, update and delete operations over a collection of JSON documents.
    
    See [lib/src/main/java/mushop/carts/repositories/CartRepository.java](lib/src/main/java/mushop/carts/repositories/CartRepository.java)

    Why JSON in the Autonomous Database?

    * **Flexibility** - the shopping cart service can evolve to store new attributes without modifying a database schema or existing SQL queries and DML. [Micronaut Serialization](https://micronaut-projects.github.io/micronaut-serialization/snapshot/guide/#introduction) is used to automatically map basic [Cart](src/main/java/mushop/carts/entities/Cart.java) objects to and from JSON. Storing a new Cart attribute only requires modifying the Cart class (not the database, queries, or other parts of the application code).

    * **Performance** - JSON documents can be read and written with consistent **single-digit millisecond latency at scale**. The autonomous database can manually or automatically scale out to increase throughput based on demand.  A cart is read from the database without requiring any joins between underlying tables.  An ORM solution, such as [JPA](https://en.wikipedia.org/wiki/Java_Persistence_API), would likely use joins between an underlying _cart_ and _item_ table each time the [Cart](lib/src/main/java/mushop/carts/entities/Cart.java) object is retrieved.  Such joins can be prohibitively expensive for highly concurrent workloads. Also, low-latency, high-throughput JSON performance is maintained **without sacrificing consistency, durability, or isolation** (sacrifices typically made in NoSQL databases).

    * **Analytics** - Oracle Database is the world's leading [translytic database](https://blogs.oracle.com/database/oracle-1-in-forresters-translytical-data-platforms-wave-v2).  Even though the cart microservice is written without using SQL, SQL can still be used to access JSON collections.  The data can be exposed, in-place, to existing analytics tools that don't necessarily support JSON and might use older database drivers.  There are no special restrictions on the types of queries that can be used over JSON collections.  In contrast, NoSQL databases typically have significant restrictions on the types of joins and subqueries that can be expressed and don't support standard SQL drivers such as JDBC.

      ```SQL
      SELECT c.data.customerId
      FROM cart c
      ```
      See [sql/examples.sql](sql/examples.sql) for more examples of how SQL can be used over the _carts_ collection used by this service.

    * **Multimodel** - Data stored in JSON collections can be queried along side other types of data in Oracle Database such as relational, geospatial, graph, and so on.  See [sql/examples.sql](sql/examples.sql) for an example that joins the _cart_ collection with other relational tables.

    * **Autonomous** - JSON collections benefit from all the general features of the [autonomous database](https://www.oracle.com/database/what-is-autonomous-database.html) such as advanced security, automated patching, automated backups, and so on.

    * **Cost** - JSON collections are in the [always-free tier](https://www.oracle.com/cloud/free/) of the autonomous database.  You can run this shopping cart service for free, forever, in the Oracle Cloud.

  * **REST API** - The REST calls are handled by Micronaut's non-blocking HTTP server built on [Netty](https://netty.io/).

    ```java
    @Get("/{cartId}/items")
    List<Item> getCartItems(String cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new HttpStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cart with id " + cartId + " not found"
                )
            );
        return cart.getItems();
    }
    ```
     See [lib/src/main/java/mushop/carts/controllers/CartsController.java](lib/src/main/java/mushop/carts/controllers/CartsController.java)

     Micronaut is designed to support cloud-native applications. It comes with built-in support for things like health checks, metrics, tracing, and fault tolerance.  These features make it work well for deployment in Docker and Kubernetes.

# Micronaut Features

* `app`
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

This application uses Oracle Autonomous Database when running in Oracle Cloud. To run the application locally you can use a local MongoDB database and modify the `datasources` configuration found in `app/src/main/resources/application.yml` accordingly.

Alternatively you can run MongoDB in a container with the following command:

```bash
$ docker run -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=mongoadmin -e MONGO_INITDB_ROOT_PASSWORD=mongopass -d mongo:tag
```

Then start the application with:

```bash
./mvnw install -pl -app,-aws,-oci
./mvnw mn:run -pl app -DMONGO_HOST=localhost -DMONGO_PORT=27017 -DMONGO_USER=mongoadmin -DMONGO_PASS=mongopass
```

The available endpoints can be browsed at http://localhost:8080/swagger/views/swagger-ui/

# Building and Running a GraalVM Native Image

To build the application into a GraalVM native image, go to one of the subprojects and execute:

```bash
./mvnw package -Dpackaging=native-image
```

Once the native image is built you can run it with:

```bash
./target/carts
```

# Deployment to Oracle Cloud

The entire MuShop application can be deployed with the [Helm Chart](../../deploy/complete/helm-chart).

However, if you wish to deploy the carts service manually you can do so.

First you need to [Login to Oracle Cloud Container Registry](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionslogintoocir.htm), go to one of the subprojects and deploy the container image with:

```bash
./mvnw deploy -Dpackaging=docker
```

Or the native version with:

```bash
./mvnw deploy -Dpackaging=docker-native
```

The Docker image names to push to can be altered by editing the following lines in [pom.xml](https://github.com/oracle-quickstart/oci-micronaut/blob/983c78a8cd55ecc33b1b3aac6a2d68524683a5b3/src/carts/pom.xml#L224-L228):

```xml
<configuration>
    <to>
        <image>phx.ocir.io/oraclelabs/micronaut-showcase/mushop/${project.artifactId}-${docker.image.suffix}:${project.version}</image>
    </to>
</configuration>
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
| `ORACLECLOUD_ATP_HOST` | The database host |

In addition [instance principal needs to be configured](https://docs.oracle.com/en-us/iaas/Content/Identity/Tasks/callingservicesfrominstances.htm) to ensure the VM or container has access to the necessary Oracle Cloud resources.
