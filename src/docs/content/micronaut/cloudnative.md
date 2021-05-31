---
title: "Cloud Native Configuration"
draft: false
disableBreadcrumb: true
weight: 1
---

A significant advantage of Micronaut is the ability to use Cloud Native configuration.

Each Micronaut application is able to define an `application-oraclecloud.yml` file that is automatically activated when deployed into the Oracle Cloud environment.

This allows the Micronaut applications to adapt to run on any Cloud and simplifies deployment.

Decoupling distributed configuration and service discovery responsibilities leads to significant simplifications in the code.

Each Micronaut application has no direct references to Oracle Cloud and can easily be migrated from one Cloud to another simplify by providing the appropriate configuration for the Cloud platform being used.

An example `application-oraclecloud.yml` file from the `users` service can be seen below (additional commentary about each configuration entry can be seen in the comments):

```yaml
# Enables export for Application metrics to Oracle Cloud Monitoring.
# Note that Micronaut uses Micrometer and can support multiple metric systems simultaneously 
# See https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#micrometer
micronaut:
  metrics:
    export:
      oraclecloud:
        enabled: true
        namespace: ${ORACLECLOUD_METRICS_NAMESPACE:micronaut_mushop}
        resourceGroup: ${ORACLECLOUD_METRICS_RESOURCEGROUP:user}
        compartmentId: ${ORACLECLOUD_METRICS_COMPARTMENT_ID}

# Allows Micronaut to automatically authenticate with the OCI SDK through
# the use of instance prinicipals. See https://docs.oracle.com/en-us/iaas/Content/Identity/Tasks/callingservicesfrominstances.htm
oci:
  config:
    instance-principal:
      enabled: true

# Configures Micronaut to automatically generate a wallet definition and connect
# to Oracle Autonomous Database using the specified OCID
# See https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#autonomousDatabase
datasources:
  default:
    ocid: ${ORACLECLOUD_ATP_OCID}
    walletPassword: ${ORACLECLOUD_ATP_WALLET_PASSWORD}
    username: ${ORACLECLOUD_ATP_USERNAME}
    password: ${ORACLECLOUD_ATP_PASSWORD}

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




