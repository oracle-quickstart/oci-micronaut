Micronaut MuShop Sources
=======

Introduction
----

This directory contains the sources of the Micronaut MuShop implementation.

For introductory information and getting started documentation on deployment see the [reference documentation](https://oracle-quickstart.github.io/oci-micronaut/index.html) for Micronaut MuShop.

Structure
---

   The sources are split into a number of subprojects each of which implement an application in the Micronaut MuShop Microservice architecture.

   A summary of each directory of importance (some directories are simply utilities and can be ignored) and the purpose of the application is described below:

### The `api` project

   Typically with a Microservice architecture the majority of services are not directly exposed to the outside world and a single gateway Microservice acts to secure and route requests to other services.

   The `api` application is an HTTP gateway application that is responsibile for security and routing to the different Microservices that occupy the backend and are not directly exposed over the internet. 

### The `assets` project

   In order to serve static assets (images, large files etc.) the `assets` Microservice is responsible for optimizing and deliverying assets served up from Object Storage. This application is currently written as a Node application.

### The `carts` project

   The `carts` subproject's purpose is to manage shopping carts in the Micronaut MuShop application.

   These shopping carts are stored in the user session and persisted using document database storage.

### The `catalogue` project

   The `catalogue` project is responsible for managing the information about the range of products available on the Micronaut MuShop store front.

   A relational database is used to store and retrieve product informatino.

### The `events` project

   The `events` subproject is used to consume application events such as when a user logins in, or adds a product to their cart etc.

   These `events` are sent to a backend streaming for processing (Kafka).

### The `fullfillment` project

   A small Microservice that simulates processing the final order for product sold on the Micronaut MuShop.

### The `functions` project

   Contains Serverless functions. Currently only a single function which is responsible for handling subscriptions to to a hypothetical mailing list for Micronaut MuShop users.

### The `orders` project

   The `orders` Microservice is responsible for managing information for a particular product order including card and shipment information.

### The `payment` project

   The `payment` Microservice simulates a Microservice that would handle payment processing.   

### The `storefront` project

   The `storefront` application provides the HTML/JS UI of the application.  

### The `user` project

   The `user` Microservice is responsible for managing user accounts, registration and login.