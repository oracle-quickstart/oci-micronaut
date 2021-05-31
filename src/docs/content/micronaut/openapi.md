---
title: "OpenAPI / Swagger"
draft: false
disableBreadcrumb: true
weight: 7
---

In the original MuShop sample application most of the applications defined [manually crafted OpenAPI definitions](https://github.com/oracle-quickstart/oci-cloudnative/blob/master/src/catalogue/api-spec/catalogue.json) that in fact were in some cases out-of-date with the actual state of the code.

Keeping the OpenAPI documentation for a service aligned with the actual code can be a real challenge.

With the Micronaut MuShop every single applications defines an accessible OpenAPI definition available via Swagger UI.


For example you can `cd` into the `api` service and run the application with:

```bash
cd src/api
./gradlew run
```

The available endpoints can be browsed at [localhost:8080/swagger/views/swagger-ui](http://localhost:8080/swagger/views/swagger-ui)

Micronaut will automatically at compilation time generate the API definition by parsing the source code and javadoc comments of the application ensuring that any javadoc and API documention exposed to the user is correctly aligned and doesn't become stale over time.
