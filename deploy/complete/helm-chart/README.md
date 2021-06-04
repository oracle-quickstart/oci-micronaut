# MuShop Helm Chart

The Helm charts here can be used to install all components of MuShop to the Kubernetes cluster.
For practical purposes, multiple charts are used to separate installation into the following steps:

1. [`setup`](#setup) Installs _optional_ chart dependencies on the cluster
1. **Not supported yet**  [`provision`](#provision) Provisions OCI resources integrated with Service Broker _(optional)_
1. [`mushop`](#installation) Deploys the MuShop application runtime


Visit the [documentation](https://pgressa.github.io/oraclecloud-cloudnative/quickstart/helm/) for detailed installation instructions.
