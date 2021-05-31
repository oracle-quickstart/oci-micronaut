---
title: "Gateway Service"
draft: false
disableBreadcrumb: true
weight: 8
---

The original MuShop `api` service is written in Node/Express and services as a Gateway service to access other services.

Apart from the already mentioned manual service discovery implementation the Node Gateway service implements all routing logic manually:

```js
  app.get("/catalogue*", function (req, res, next) {
    req.svcClient()
      .get(endpoints.catalogueUrl + req.url.toString())
      .then(({ data }) => res.json(data))
      .catch(next);
  });
```

In Micronaut this is simplified through the use of service discovery combined with the declarative client to allow requests to be easily proxied:

```java
@MuService
@Client(id = "mushop-catalogue") // the service ID to target
@Secured(SecurityRule.IS_ANONYMOUS) // allow unsecured access
public interface CatalogueService {
    @Get("/catalogue/{id}") // route the request and return the response in a well defined shape
    Single<HttpResponse<CatalogueItem>> getItem(String id);
}
```

The Micronaut application is also locked by down by default, meaning that any endpoint exposed should declare `@Secured(SecurityRule.IS_ANONYMOUS)` to explicitly allow anonymous access and avoid serious security vulnerabilities emerging.