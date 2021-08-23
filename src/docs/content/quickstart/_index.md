---
title: "Getting Started"
date: 2020-03-05T15:34:14-07:00
weight: 1
draft: false
tags:
  - Quickstart
  - Source code
---

This project supports deployment modes for the purposes of demonstrating
different functionality of Micronaut on Oracle Cloud Infrastructure and Amazon Web Services.

| [Oracle Cloud Infrastructure `deploy/complete/terraform`](oci-terraform) | [Amazon Web Services `deploy/complete/aws-cloudformation`](aws-cloudformation) |
|--|--|
| Full-featured [Kubernetes](https://kubernetes.io/) microservices deployment showcasing Oracle [Cloud Native](https://www.oracle.com/cloud/cloud-native/) technologies and backing services <br/> <br/>  [![Deploy to Oracle Cloud](https://oci-resourcemanager-plugin.plugins.oci.oraclecloud.com/latest/deploy-to-oracle-cloud.svg)](https://cloud.oracle.com/resourcemanager/stacks/create?zipUrl=https://github.com/oracle-quickstart/oci-micronaut/releases/latest/download/mushop-stack-latest.zip) | Full-featured [Kubernetes](https://kubernetes.io/) microservices deployment showcasing Oracle [Cloud Native](https://www.oracle.com/cloud/cloud-native/) technologies and backing services <br/> <br/>  [![Launch Stack](https://s3.amazonaws.com/cloudformation-examples/cloudformation-launch-stack.png)](https://console.aws.amazon.com/cloudformation/home?#/stacks/new?stackName=MicronautMuShop&templateURL=https://micronaut-mushop-aws.s3.us-west-2.amazonaws.com/3.1.0/mushop-entrypoint.yaml) |


```text
mushop
└── deploy
    ├── basic
    └── complete
        └── aws-cloudformation
        └── docker-compose
        └── helm-chart
        └── terraform
```

## Clone Repository

Each topic in this material references the source code, which should be
cloned to a personal workspace.

```shell--macos-linux
git clone https://github.com/oracle-quickstart/oci-cloudnative.git mushop
cd mushop
```

```shell--win
git clone https://github.com/oracle-quickstart/oci-cloudnative.git
dir mushop
```

## Structure

The source code will look something like the following:

```text
#> mushop
├── deploy
│   ├── basic
│   └── complete
        └── aws-cloudformation
│       ├── docker-compose
│       ├── helm-chart
│       └── terraform
└── src
    ├── api
    ├── assets
    ├── carts
    ├── catalogue
    ├── edge-router
    ├── events
    ├── fulfillment
    ├── dbtools
    ├── load
    ├── orders
    ├── payment
    ├── storefront
    └── user
```

- `deploy`: Collection of application deployment resources.
- `src`: Individual service source code, Dockerfiles, etc.
