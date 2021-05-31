---
title: Introduction
draft: false
weight: -100
---

## Goals of the Micronaut MuShop

- Explore the integration of [Micronaut](https://micronaut.io) with [cloud native](https://www.oracle.com/cloud/cloud-native/) services offered by Oracle Cloud Infrastructure
- Build and deploy microservices with [Container Engine for Kubernetes](https://www.oracle.com/cloud/compute/container-engine-kubernetes.html) (OKE)
- Experience Oracle Cloud services integrated within a single microservices project
- Provide reference implementations and sample code for _real-world_ application development with Micronaut and Oracle Cloud

## Cloud Services

The MuShop application highlights several topics related to cloud native application
development with Oracle Cloud Infrastructure.

| Cloud Service | Description |
|--|--|
| [API Gateway](https://www.oracle.com/cloud/integration/api-platform-cloud/) | Fully managed gateway for governed HTTP/S interfaces |
| [Container Engine for Kubernetes](https://www.oracle.com/cloud/compute/container-engine-kubernetes.html) | Enterprise-grade Kubernetes on Oracle Cloud |
| [Container Registry](https://www.oracle.com/cloud/compute/container-registry.html) | Highly available service to distribute container images |
| [Email Delivery](https://www.oracle.com/cloud/networking/email-delivery.html) | Enables sending emails | 
| [Functions](https://www.oracle.com/cloud-native/functions/) | Scalable, multitenant serverless functions |
| [Monitoring](https://www.oracle.com/devops/monitoring/) | Integrated metrics from all resources and services |
| [Application Performance Monitoring](https://www.oracle.com/manageability/application-performance-monitoring/) | Integrated distributed tracing and performance analysis |
| [Resource Manager](https://www.oracle.com/cloud/systems-management/resource-manager/) | Infrastructure as code with Terraform |
| [Streaming](https://www.oracle.com/big-data/streaming/) | Large scale data collection and processing |
| _Others coming soon_ | - |
| [Events](https://www.oracle.com/cloud-native/events-service/) | Trigger actions in response to infrastructure changes |
| [Notifications](https://www.oracle.com/cloud/systems-management/notifications/) | Broadcast messages to distributed systems |
| [Logging](https://go.oracle.com/LP=78019?elqCampaignId=179851) | Single pane of glass for resources and applications |

In addition to these Cloud Native topics, MuShop demonstrates the use of several
**backing services**  available on Oracle Cloud Infrastructure.

- [Autonomous Transaction Processing Database](https://www.oracle.com/database/autonomous-transaction-processing.html)
- [Object Storage](https://www.oracle.com/cloud/storage/object-storage.html)
- [Web Application Firewall](https://www.oracle.com/cloud/security/cloud-services/web-application-firewall.html)


### MuShop Services

![services](images/mushop.services.png "MuShop Services")

{{% overflow %}}
| Service | Technology  | Cloud Services | Description |
| --- | --- | --- | --- |
| `src/api` | Micronaut   | | Storefront API |
| `src/assets` | Node.js   | Object Storage | Product images |
| `src/carts` | Micronaut | Autonomous DB (ATP) | Shopping cart |
| `src/catalogue` | Micronaut | Autonomous DB (ATP) | Product catalogue |
| `src/dbtools` | Linux | Autonomous DB (ATP) | Database schema initializations |
| `src/edge-router` | traefik  |  | Request routing |
| `src/events` | Micronaut | Streaming | Application event data collection |
| `src/fulfillment` | Micronaut |  | Order processing |
| `src/functions/newsletter-subscription` | Micronaut | Functions | Newsletter subscription |
| `src/orders` | Micronaut | Autonomous DB (ATP)   | Customer orders |
| `src/payments` | Micronaut | | Payment processing |
| `src/storefront` | JavaScript  |  | Store UI |
| `src/user` | Micronaut | Autonomous DB (ATP)  | Customer accounts, AuthN |
{{% /overflow %}}