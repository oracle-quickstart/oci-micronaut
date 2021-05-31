---
title: "Distributed Tracing"
draft: false
disableBreadcrumb: true
weight: 3
---

The original MuShop application featured hand crafted logic to wrap each endpoint in distributed tracing logic that can be exported to Zipkin, for example in the Go lang code which manually wraps logic in calls to `opentracing.TraceServer(..)`:

```golang
func MakeEndpoints(s Service, tracer stdopentracing.Tracer) Endpoints {
	return Endpoints{
		ListEndpoint:       opentracing.TraceServer(tracer, "GET /catalogue")(MakeListEndpoint(s)),
		CountEndpoint:      opentracing.TraceServer(tracer, "GET /catalogue/size")(MakeCountEndpoint(s)),
		GetEndpoint:        opentracing.TraceServer(tracer, "GET /catalogue/{id}")(MakeGetEndpoint(s)),
		CategoriesEndpoint: opentracing.TraceServer(tracer, "GET /categories")(MakeCategoriesEndpoint(s)),
		HealthEndpoint:     opentracing.TraceServer(tracer, "GET /health")(MakeHealthEndpoint(s)),
	}
}
```

Micronaut has built-in support for distributed tracing that only requires the addition of the `micronaut-tracing` module and then configuring the target Zipkin or Jaeger service to send traces to, for example in the `micronaut-oraclecloud.yml` configuration:

```yaml
# Configures Micronaut to Export application level trace information to 
# Oracle Cloud Application Performance Monitoring.
# See https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#tracing
tracing:
  zipkin:
    enabled: true
    sampler:
      probability: 1
    http:
      url: ${ORACLECLOUD_TRACING_ZIPKIN_HTTP_URL}
      path: ${ORACLECLOUD_TRACING_ZIPKIN_HTTP_PATH}
    supportsJoin: false

```

Once enabled Micronaut will automatically publish traces received from the service and also automatically instrument any outgoing HTTP client or Kafka producers to include trace information such that trace information can be propagated from one service to another.

This is universally enabled across all applications without the developer having to do anything additional, whilst the original MuShop requires explicit declarations of all incoming and outgoing traced endpoints.