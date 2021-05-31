---
title: "Deployment"
draft: false
disableBreadcrumb: true
weight: 12
---

[Each](https://github.com/oracle-quickstart/oci-cloudnative/blob/master/src/api/Dockerfile) [example](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/carts/Dockerfile) [application](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/catalogue/Dockerfile) in the original MuShop features a [hand-crafted](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/events/Dockerfile) `Dockerfile` that the developer has to [write](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/fulfillment/Dockerfile) and [maintain](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/orders/Dockerfile) including ensuring all images used in the `Dockerfile` are kept up-to-date to avoid security vulnerabilities.

None of the Micronaut applications feature a `Dockerfile` which is provided automatically and maintained by the framework across releases, making deploying an image a simple matter of executing:

```bash
./gradlew dockerPush
```

for Gradle, or alternatively for Maven:

```bash
./mvnw deploy -Dpackaging=docker
```

Deploying a native version of the application with GraalVM is equally simple:

```bash
./gradlew dockerPushNative
```

for Gradle, or alternatively for Maven:

```bash
./mvnw deploy -Dpackaging=docker-native
```

