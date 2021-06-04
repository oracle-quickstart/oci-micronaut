---
title: "[Oracle Cloud] Helm"
date: 2020-03-06T12:27:43-07:00
draft: false
weight: 20
tags:
  - Kubernetes
  - OKE
  - Setup
  - Helm
---

This deployment option utilizes [`helm`](https://github.com/helm/helm) for
configuration and installation onto a [Kubernetes](https://kubernetes.io/)
cluster. It is _recommended_ to use an
[Oracle Container Engine for Kubernetes](https://docs.cloud.oracle.com/iaas/Content/ContEng/Concepts/contengoverview.htm)
cluster, however other **standard** Kubernetes clusters will also work.

```shell--linux-macos
cd deploy/complete/helm-chart
```

```shell--win
dir deploy/complete/helm-chart
```

> Path for Cloud Native deployment configurations using `helm`

Deploying the complete MuShop application with backing services from Oracle Cloud
Infrastructure involves the use of the following helm charts:

1. [`setup`](#setup): Installs umbrella chart dependencies on the cluster _(optional)_
1. **Not supported yet** [`provision`](#provision): Provisions OCI resources integrated with Service Broker _(optional)_
1. [`mushop`](#deploy-mushop): Deploys the MuShop application runtime

To get started, create a namespace for the application and its associative deployments:

```shell
kubectl create ns mushop
```

---

### Setup

{{% content/setup %}}

### Deploy MuShop

Deploying the full application requires cloud backing services from Oracle Cloud Infrastructure.
These services must be provisioned manually and are configured using kubernetes secrets.

#### Configure

1. Provision an Autonomous Transaction Processing (ATP) database. Once **RUNNING** download the DB Connection Wallet and configure secrets as follows:

   - Create `oadb-admin` secret containing the database administrator password. Used once for schema initializations.

       ```shell
       kubectl create secret generic oadb-admin \
         --namespace mushop \
         --from-literal=oadb_admin_pw='<DB_ADMIN_PASSWORD>'
       ```

   - Create `oadb-wallet` secret with the Wallet _contents_ using the downloaded `Wallet_*.zip`. The extracted `Wallet_*` directory is specified as the secret contents.

       ```shell
       kubectl create secret generic oadb-wallet \
         --namespace mushop \
         --from-file=<PATH_TO_EXTRACTED_WALLET_FOLDER>
       ```

   - Create `oadb-connection` secret with the Wallet **password** and the service **TNS name** to use for connections.

       ```shell
       kubectl create secret generic oadb-connection \
         --namespace mushop \
         --from-literal=oadb_wallet_pw='<DB_WALLET_PASSWORD>' \
         --from-literal=oadb_service='<DB_TNS_NAME>' \
         --from-literal=oadb_ocid='<DB_OCID>' \
       ```

     > Each database has 5 unique TNS Names displayed when the Wallet is downloaded an example would be `mushopdb_TP`.

1. **Optional**: Instead of creating a shared database for the entire application, you may establish full separation of services by provisioning _individual_ ATP instances for each service that requires a database. To do so, repeat the previous steps for each database,and give each secret a unique name, for example: `carts-oadb-admin`, `carts-oadb-connection`, `carts-oadb-wallet`.

   - `carts`
   - `catalogue`
   - `orders`
   - `user`

1. Provision a Streaming instance from the [Oracle Cloud Infrastructure Console](https://console.us-phoenix-1.oraclecloud.com/storage/streaming), and make note of the created Stream Pool configuration values bootstrapServers and stream pool ID.

   - Create `oss-connection` secret containing the Stream connection details.

       ```shell
       kubectl create secret generic oss-connection \
         --namespace mushop \
         --from-literal=bootstrapServers='<OSS STREAM BOOTSTRAP SERVERS>' \
         --from-literal=jaasConfig='<JAAS CONFIG>'
       ```
   
   Note that `<OSS STREAM BOOTSTRAP SERVERS>` and `<JAAS CONFIG>` values can can be found in the `Stream Pool -> Kafka Connection Setting`. In case you want to connect under different user then the `<JAAS CONFIG>` format is:
    ```
    jaasConfig="org.apache.kafka.common.security.plain.PlainLoginModule required username=\"<USER_COMPARTMENT_NAME>/<USER_NAME>/<OSS_POOL_ID>\" password=\"<USER_TOKEN>\";"
    ```
   Make sure the user has permission to write to the given stream.

1. Configure a config map with deployment details:

    ```shell
    kubectl create cm oci-deployment \
      --namespace mushop \
      --from-literal=compartment_id='<COMPARTMENT ID>' \
      --from-literal=region='<OCI REGION>'
    ```

1. **Optional**: If you want to configure and use Oracle Application Monitoring service, make sure you create the APM domain and create K8s secret with connection details.

    1. Navigate to Observability & Management -> Application Performance Monitoring -> Administration.
    1. Hit the button Create APM domain and enter the domain name.
    1. Once created make a note of `Data Upload Endpoint` and `auto_generated_public_key`.
    1. Edit the `mushop.tfvars`:

     ```shell
       kubectl create secret generic oss-connection \
         --namespace mushop \
         --from-literal=zipkin_enabled=true \
         --from-literal=zipkin_path='<APM DOMAIN DATA UPLOAD ENDPOINT>' \
         --from-literal=zipkin_url='/20200101/observations/public-span?dataFormat=zipkin&dataFormatVersion=2&dataKey=<AUTO_GENERATED_PUBLIC_KEY>'
     ```

1. **Optional**: If you want to configure and use the functions/API Gateway functionality, make sure you create and deploy the function and the API Gateway by following the instructions in the `src/functions/newsletter-subscription` folder.

   To configure the `api` chart to use the newsletter subscribe function, create K8s external service:

   ```shell
   cat <<EOF | kubectl apply -f -
   piVersion: v1
   kind: Service
   metadata:
     name: mushop-newsletter
     namespace: mushop
   spec:
     externalName: <API_GATEWAY_URL>
     ports:
     - port: 443
       protocol: TCP
       targetPort: 443
     type: ExternalName
   status:
     loadBalancer: {}
   EOF
   ```

   Replace the `API_GATEWAY_URL` with an actual **hostname** URL you got when you deployed your API gateway instance. For example: `jbwyanwkmxqweq.apigateway.us-ashburn-1.oci.customer-oci.com`

1. Make a copy of the [`values-dev.yaml`](./mushop/values-dev.yaml) file in this directory. Then complete the missing values (e.g. secrets) like the following:

    ```yaml
    global:
      ossConnectionSecret: oss-connection     # Name of Stream connection secret
      oadbAdminSecret: oadb-admin             # Name of DB Admin secret
      oadbWalletSecret: oadb-wallet           # Name of Wallet secret
      oadbConnectionSecret: oadb-connection   # Name of DB Connection secret
      ociDeploymentConfigMap: oci-deployment  # Name of Deployment details config map
    tags:
      atp: true                               # General flag to use Oracle Autonomous Database
      streaming: true                         # General flag to use Oracle Streaming Service
    ```

   > **NOTE:** If it's desired to connect a separate databases for a given service, you can specify values specific for each service, such as `carts.oadbAdminSecret`, `carts.oadbWalletSecret`...

#### Deploy

1. From `deploy/complete/helm-chart` directory:
    ```shell
    helm install mushop mushop \
      --namespace mushop \
      -f <edited values-dev.yaml>
    ```

1. Wait for services to be _Ready_:

    ```shell
    kubectl get pod --watch --namespace mushop
    ```

1. Open a browser with the `EXTERNAL-IP` created during setup, **OR** `port-forward`
directly to the `edge` service resource:

    ```shell
    kubectl port-forward \
      --namespace mushop \
      svc/edge 8000:80
    ```

    > Using `port-forward` connecting [localhost:8000](http://localhost:8000) to the `edge` service

    ```shell
    kubectl get svc mushop-utils-ingress-nginx-controller \
      --namespace mushop-utilities
    ```

    > Locating `EXTERNAL-IP` for Ingress Controller. **NOTE** this will be
    [localhost](https://localhost) on local clusters.

<aside class="warning">
  It may take a few moments to download all the application images.
  It is also normal for some pods to show errors in mock mode.
</aside>

#### Cleanup

The following list represents cleanup operations, which may vary
depending on the actions performed for setup and deployment of MuShop.

- List any `helm` releases that may have been installed:

    ```shell
    helm list --all-namespaces
    ```

    ```text
    NAME                    NAMESPACE               REVISION        UPDATED                                 STATUS          CHART                           APP VERSION   
    mushop                  mushop                  1               2020-01-31 21:14:48.511917 -0600 CST    deployed        mushop-0.1.0                    1.0         
    oci-broker              mushop-utilities        1               2020-01-31 20:46:30.565257 -0600 CST    deployed        oci-service-broker-1.3.3                   
    mushop-provision        mushop                  1               2020-01-31 21:01:54.086599 -0600 CST    deployed        mushop-provision-0.1.0          0.1.0      
    mushop-utils            mushop-utilities        1               2020-01-31 20:32:05.864769 -0600 CST    deployed        mushop-setup-0.0.1              1.0  
    ```

- Remove the application from Kubernetes where `--name mushop` was used during install:

    ```shell
    helm delete mushop -n mushop
    ```

- If used OCI Service broker, remove the `provision` dependency installation, including ATP Bindings (Wallet, password) and instances:

    ```shell
    helm delete mushop-provision -n mushop
    ```

  {{% alert icon="info" %}}
  After delete, `kubectl get serviceinstances -A` will show resources that are deprovisioning
  {{% /alert %}}

- If used OCI Service broker, remove the `oci-broker` installation:

    ```shell
    helm delete oci-broker -n mushop-utilities
    ```

- Uninstall Istio service mesh _(if applicable)_:

    ```shell
    istioctl manifest generate --set profile=demo | kubectl delete -f -
    ```

- Remove the `setup` cluster dependency installation:

    ```shell
    helm delete mushop-utils -n mushop-utilities
    ```

