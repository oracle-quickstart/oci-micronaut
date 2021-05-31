---
title: "Connecting Autonomous Database"
draft: false
disableBreadcrumb: true
weight: 5
---

The original MuShop reference applications contained significant logic in order to download and configure the Oracle Wallet definition to connect to autonomous database.

The details of this requirement are described in [Application Configuration](https://github.com/oracle-quickstart/oci-cloudnative/tree/39d80e5e91a2d5b1e48b0d8cf3516a2acd8c258e/src/orders#application-configuration) section of the original MuShop.

Developers have to enhance their CI pipeline to ensure the client credentials contained with the Oracle Wallet are packaged within the file system of the Docker container before uploading. The downside of this approach is that every time the Oracle Wallet is rotated a new container image needs to be built.

With Micronaut connecting to Autonomous Database is a simple matter of supplying the appropriate configuration in `application-oraclecloud.yml`:

```yaml
# Configures Micronaut to automatically generate a wallet definition and connect
# to Oracle Autonomous Database using the specified OCID
# See https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#autonomousDatabase
datasources:
  default:
    ocid: ${ORACLECLOUD_ATP_OCID}
    walletPassword: ${ORACLECLOUD_ATP_WALLET_PASSWORD}
    username: ${ORACLECLOUD_ATP_USERNAME}
    password: ${ORACLECLOUD_ATP_PASSWORD}
```

With this configuration in place Micronaut will use the OCI SDK to download the wallet and store the credentials in-memory, automatically configuring the underlying datasource.