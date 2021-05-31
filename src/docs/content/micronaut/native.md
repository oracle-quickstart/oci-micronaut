---
title: "GraalVM Native Image"
draft: false
disableBreadcrumb: true
weight: 13
---

Every single application in the Micronaut MuShop can be built into a GraalVM native image and deployed natively as well as in JIT mode allowing the developer to flexibly choose their preferred deployment model and benefit from massive reductions in memory usage and startup time when choosing native.

To build any of the services into a native image simply navigate to the to the project directory and if using Gradle run:

```bash
./gradlew nativeImage
```

Or if the project is using Maven run:

```bash
./mvnw package -Dpackaging=native-image
```
