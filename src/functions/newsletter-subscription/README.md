# newsletter-subscription function

The purpose of the `newsletter-subscription` function is simple: it takes an email address as an input and sends an email to the recipient, informing them that they are subscribed to a newsletter. Note that the subscription is not tracked nor stored anywhere, the idea is just to showcase how to invoke a function through an API gateway and how to send emails using Oracle Email Delivery feature.

The function is written as a Micronaut Function (Serverless) application in Java. It uses the [JavaMail](https://javaee.github.io/javamail/) API to send emails.

# Micronaut Features

* [Micronaut Oracle Cloud Function](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#functions)

# Usage

The MuShop application deploys this service using Helm, Kubernetes, and Docker. (See
[/deploy/complete/helm-chart/](https://github.com/oracle-quickstart/oci-micronaut/tree/master/deploy/complete/helm-chart)).

# Running Locally

The functions tests can be executed with:

```bash
./gradlew test
```

This will run the `src/test/java/newsletter/subscription/FunctionTest.java` file with a mocked mail configuration.
