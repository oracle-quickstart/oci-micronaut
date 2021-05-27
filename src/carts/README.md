# Shopping Cart

A microservice demo that stores the MuShop shopping carts. The service is a Micronaut application written in Java and makes use of the following components:

  * **Autonomous Database JSON** - Each shopping cart is stored in the autonomous database as a JSON document using [SODA (Simple Oracle Document Access)](https://docs.oracle.com/en/database/oracle/simple-oracle-document-access/).  The microservice stores cart data using simple create, read, update, and delete operations over a collection of JSON documents.  For example:
    
    ```java
    OracleCollection col = db.openCollection("carts");
    // insert a cart
    OracleDocument doc = db.createDocumentFromString("{\"customerId\" : 123, \"items\" : [...] }")
    col.save(doc);

    // get a cart
    doc = col.find().filter("{\"customerId\" : 123}").getOne()
    ```
    
    See [src/main/java/mushop/carts/repositories/CartRepositoryDatabaseImpl.java](src/main/java/mushop/carts/repositories/CartRepositoryDatabaseImpl.java)

    Why JSON in the Autonomous Database?

    * **Flexibility** - the shopping cart service can evolve to store new attributes without modifying a database schema or existing SQL queries and DML. [Jackson](https://github.com/FasterXML/jackson) is used to automatically map basic [Cart](src/main/java/mushop/carts/entities/Cart.java) objects to and from JSON. Storing a new Cart attribute only requires modifying the Cart class (not the database, queries, or other parts of the application code).

    * **Performance** - JSON documents can be read and written with consistent **single-digit millisecond latency at scale**. The autonomous database can manually or automatically scale out to increase throughput based on demand.  A cart is read from the database without requiring any joins between underlying tables.  An ORM solution, such as [JPA](https://en.wikipedia.org/wiki/Java_Persistence_API), would likely use joins between an underlying _cart_ and _item_ table each time the [Cart](src/main/java/mushop/carts/entities/Cart.java) object is retrieved.  Such joins can be prohibitively expensive for highly concurrent workloads. Also, low-latency, high-throughput JSON performance is maintained **without sacrificing consistency, durability, or isolation** (sacrifices typically made in NoSQL databases).

    * **Analytics** - Oracle Database is the world's leading [translytic database](https://blogs.oracle.com/database/oracle-1-in-forresters-translytical-data-platforms-wave-v2).  Even though the cart microservice is written without using SQL, SQL can still be used to access JSON collections.  The data can be exposed, in-place, to existing analytics tools that don't necessarily support JSON and might use older database drivers.  There are no special restrictions on the types of queries that can be used over JSON collections.  In contrast, NoSQL databases typically have significant restrictions on the types of joins and subqueries that can be expressed and don't support standard SQL drivers such as JDBC.

      ```SQL
      SELECT c.json_document.customerId
      FROM carts c
      ```
      See [sql/examples.sql](sql/examples.sql) for more examples of how SQL can be used over the _carts_ collection used by this service.

    * **Multimodel** - Data stored in JSON collections can be queried along side other types of data in Oracle Database such as relational, geospatial, graph, and so on.  See [sql/examples.sql](sql/examples.sql) for an example that joins the _cart_ collection with other relational tables.

    * **Autonomous** - JSON collections benefit from all the general features of the [autonomous database](https://www.oracle.com/database/what-is-autonomous-database.html) such as advanced security, automated patching, automated backups, and so on.

    * **Cost** - JSON collections are in the [always-free tier](https://www.oracle.com/cloud/free/) of the autonomous database.  You can run this shopping cart service for free, forever, in the Oracle Cloud.

  * **REST API** - The REST calls are handled by Micronaut's non-blocking HTTP server built on [Netty](https://netty.io/).

    ```java
    @Get("/{cartId}/items")
    List<Item> getCartItems(String cartId) {
        Cart cart = cartRepository.getById(cartId);
        if (cart == null) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND,
                "Cart with id " + cartId + " not found");
        }
        return cart.getItems();
    }
    ```
     See [src/main/java/mushop/carts/controllers/CartsController.java](src/main/java/mushop/carts/controllers/CartsController.java)

     Micronaut is designed to support cloud-native applications. It comes with built-in support for things like health checks, metrics, tracing, and fault tolerance.  These features make it work well for deployment in Docker and Kubernetes.

# Micronaut Features

* [Micronaut Oracle Cloud](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/)
* Monitoring with [Micrometer](https://micrometer.io/) and [Prometheus](https://prometheus.io/)
* Tracing with [Zipkin](https://zipkin.io/)

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
./mvnw mn:run
```