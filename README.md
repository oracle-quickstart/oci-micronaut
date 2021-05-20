# ![MuShop Logo](./images/logo.png)

MuShop is a showcase of several [Oracle Cloud Infrastructure](https://cloud.oracle.com/en_US/cloud-infrastructure) services in a unified reference application. The sample application implements an e-commerce platform built as a set of microservices. The accompanying content can be used to get started with cloud native application development on Oracle Cloud Infrastructure.

| ![home](./images/screenshot/mushop.home.png) | ![browse](./images/screenshot/mushop.browse.png) | ![cart](./images/screenshot/mushop.cart.png) | ![about](./images/screenshot/mushop.about.png) |
|---|---|---|---|

MuShop can be deployed to Oracle Cloud Infrastructure on [Kubernetes](https://kubernetes.io/) with [Helm](https://helm.sh) and [Terraform](https://www.terraform.io), or run locally in [Docker](https://www.docker.com/).

### Docker

To deploy locally with Docker, follow [these instructions](https://github.com/oracle-quickstart/oci-cloudnative/blob/master/deploy/complete/docker-compose). Note that you will need to have installed the [Oracle Cloud CLI](https://docs.cloud.oracle.com/en-us/iaas/Content/API/SDKDocs/cliinstall.htm), with local access to Oracle Cloud configured by running `oci setup config`.

### Helm + Terraform

MuShop Complete is a polyglot microservices application built to showcase a cloud-native approach to application development on Oracle Cloud Infrastructure using Oracle's [cloud native](https://www.oracle.com/cloud/cloud-native/) services. MuShop Complete uses a Kubernetes cluster, and can be deployed using the provided `helm` charts. It is recommended to use an Oracle Container Engine for Kubernetes cluster, however other Kubernetes distributions will also work.

The [helm chart documentation](https://github.com/pgressa/oraclecloud-cloudnative/tree/master/deploy/complete/helm-chart#setup) walks through the deployment process and various options for customizing the deployment.

If you do not have an Oracle Cloud account yet, you can create a [free trial account](https://signup.oraclecloud.com).

#### Topology

The following diagram shows the topology created by this stack.

![MuShop Complete Infra](./images/complete/00-Topology.png)

#### [![Deploy to Oracle Cloud](https://oci-resourcemanager-plugin.plugins.oci.oraclecloud.com/latest/deploy-to-oracle-cloud.svg)](https://cloud.oracle.com/resourcemanager/stacks/create?zipUrl=https://github.com/pgressa/oraclecloud-cloudnative/releases/latest/download/mushop-stack-latest.zip)

## Questions

If you have an issue or a question, please take a look at our [FAQs](./deploy/basic/FAQs.md) or [open an issue](https://github.com/pgressa/oraclecloud-cloudnative/issues/new).
