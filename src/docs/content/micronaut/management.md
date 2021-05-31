---
title: "Health Checks & Observability"
draft: false
disableBreadcrumb: true
weight: 3
---

The original MuShop application features extensive code to define application health observability endpoints.

For example the original Go lang code features manual definition of the `/health` endpoint in [endpoints.go](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/catalogue/endpoints.go#L76-L82):

```golang
func MakeHealthEndpoint(s Service) endpoint.Endpoint {
	return func(ctx context.Context, request interface{}) (response interface{}, err error) {
		health := s.Health()
		return healthResponse{Health: health}, nil
	}
}
```

As well as a manually implemented health check routine in [services.go](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/catalogue/service.go#L171-L187):

```golang
func (s *catalogueService) Health() []Health {
	var health []Health
	dbstatus := "OK"


	err := s.db.Ping()
	if err != nil {
		dbstatus = "err"
	}


	app := Health{"catalogue", "OK", time.Now().String()}
	db := Health{"atp:catalogue-data", dbstatus, time.Now().String()}


	health = append(health, app)
	health = append(health, db)


	return health
}
```

The existing Spring application code also features [manually implemented application health logic](https://github.com/oracle-quickstart/oci-cloudnative/blob/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/orders/src/main/java/mushop/orders/controllers/HealthCheckController.java):

```java
@RestController
public class HealthCheckController {


    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, path = "/health")
    public
    @ResponseBody
    Map<String, List<HealthCheck>> getHealth() {
      Map<String, List<HealthCheck>> map = new HashMap<String, List<HealthCheck>>();
      List<HealthCheck> healthChecks = new ArrayList<HealthCheck>();
      Date dateNow = Calendar.getInstance().getTime();

      HealthCheck app = new HealthCheck("orders", "OK", dateNow);
      HealthCheck database = new HealthCheck("orders-db", "OK", dateNow);

      

      healthChecks.add(app);
      healthChecks.add(database);

      map.put("health", healthChecks);
      return map;
    }
}
```


All of these manually crafted implementations of application health could be eliminated in the Micronaut application by adding a dependency on the `micronaut-management` module and adding the following definition to `application.yml` to expose the health information:

```yaml
endpoints:
  health:
    enabled: true
    sensitive: false
    details-visible: ANONYMOUS
```

Micronaut then [exposes health readiness and liveness checks](https://docs.micronaut.io/latest/guide/#healthEndpoint) via `/health/readiness` and `/health/liveness` automatically with many built in health indicators to check that the database and messaging resources remain healthy. 

The developer can tweak the configuration to expose the health endpoint on a different port, require authentication to access the details and more.