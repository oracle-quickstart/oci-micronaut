---
title: "Security"
draft: false
disableBreadcrumb: true
weight: 9
---

The original MuShop `api` service includes a lot of error prone logic to assert security rules at the API gateway layer, for example:

```js
  router.get('/orders', (req, res) => {
    if (!helpers.isLoggedIn(req)) {
      return next(helpers.createError("User not logged in.", 401));
    }
    const custId = helpers.getCustomerId(req);
    res.json(this.orders.find(row => custId === row.customer.id));
  })
```

It is very easy to forget an `isLoggedIn` call causing a security vulnerability in your application. 

The Micronaut version of the Gateway includes the [Micronaut Security](https://micronaut-projects.github.io/micronaut-security/latest/guide/) module which is locked down by default with every request to the API service by default returning a 401 unless the user is logged in.

Services that need to allow anonymous access are declared as such through the presence of `@Secured(SecurityRule.IS_ANONYMOUS)`:

```java
@MuService
@Client(id = ServiceLocator.CATALOGUE)
@Secured(SecurityRule.IS_ANONYMOUS)
public interface CatalogueService {
  ...
}
```


