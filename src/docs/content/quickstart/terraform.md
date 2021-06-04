---
title: "[Oracle Cloud] Terraform + Helm"
date: 2020-06-02T12:28:12-07:00
draft: false
weight: 10
tags:
  - Terraform
  - OKE
  - Helm
  - Setup
---

The terraform configuration scripts can be used to completely install MuShop to Oracle Cloud along with necessary Oracle Cloud Infrastructure services.

## Prerequisites

You need to [install `terraform`](https://www.terraform.io/downloads.html) locally.

## Configuration

Create copy of terraform variables file `terraform.tfvars.example`.

```bash
cp terraform.tfvars.example mushop.tfvars
```

Then edit the `mushop.tfvars`:


1. Configure the OCI authentication and deployment variables. [Follow the guidelines](https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/terraformproviderconfiguration.htm) on how to obtain authentication details and configure the provider.:
    ```yaml
    # OCI authentication
    tenancy_ocid     = "ocid1.tenancy....."
    fingerprint      = "" # e.g.: "5f:53:..." or leave blank if using CloudShell
    user_ocid        = "" # e.g.: "ocid1.user..." or leave blank if using CloudShell
    private_key_path = "" # e.g.: "/users/user/.oci/oci_api_key.pem" or leave blank if using CloudShell

    # Deployment compartment
    compartment_ocid = "ocid1.compartment...."

    # region
    region = "us-ashburn-1"
    ```

   {{% alert icon="info" %}}
   {{% /alert %}}


1. Enable streaming service (*optional*):
    ```yaml
    # Streaming
    create_oracle_streaming_service_stream = true
    ```

   {{% alert icon="info" %}}
   Note that by configuring this option the terraform will configure Oracle Cloud Streaming service.
   The default quota allows the account to have 1 stream.
   In case you're out of the quota keep this option disabled, the MuShop application won't be affected.
   {{% /alert %}}

1. Enable newsletter service (*optional*):
    ```yaml
    # Newsletter
    create_oracle_function_newsletter = true
    newsletter_function_approved_email_address = "micronaut-newsletter@mushop.com"
    ```

   {{% alert icon="info" %}}
   Note that by configuring this option the terraform will configure Oracle Functions, Oracle API Gateway and Oracle Email Delivery Service.
   The default quota allows the account to have 1 API Gateway configured.
   In case you're out of the quota keep this option disabled, the MuShop application won't be affected.
   {{% /alert %}}
   
1. Enable Application Performance Monitoring:
  The Oracle Cloud Application Performance Monitoring service doesn't provide the Terraform provider so it needs to be created manually before running the terraform:
   
    1. Navigate to Observability & Management -> Application Performance Monitoring -> Administration.
    1. Hit the button Create APM domain and enter the domain name.
    1. Once created make a note of `Data Upload Endpoint` and `auto_generated_public_key`.
    1. Edit the `mushop.tfvars`:

     ```yaml
     # Tracing
     apm_zipkin_enabled = false
     apm_zipkin_url = "" # Copy here the APM domain Data Upload Endpoint.
     apm_zipkin_path = "" # The format is: /20200101/observations/public-span?dataFormat=zipkin&dataFormatVersion=2&dataKey=<auto_generated_public_key>
     ```

## Installation
From the directory `deploy/complete/terraform`:
1. Init the providers:
    ```shell
    terraform init
    ```

1. Plan the execution (*Optional*):
    ```shell
    terraform plan --var-file mushop.tfvars
    ```

1. Execute the plan:
    ```shell
    terraform apply --var-file mushop.tfvars
    ```
   {{% alert icon="info" %}}
   Note the deployment of all resources may take a while (~20 minutes).
   {{% /alert %}}
   
    After successful deployment you should see output variables like below:
    ```
    Outputs:

    autonomous_database_password = "...."
    comments = "The application URL will be unavailable for a few minutes after provisioning while the application is configured and deployed to Kubernetes"
    deploy_id = "R8Mm"
    deployed_oke_kubernetes_version = "v1.19.7"
    deployed_to_region = "us-ashburn-1"
    dev = "Made with ❤ by Oracle Developers"
    external_ip = "129.159.91.46"
    generated_private_key_pem = <<EOT
    ...
    -----END RSA PRIVATE KEY-----

    EOT
    grafana_admin_password = "....."
    grafana_url = "http://129.159.91.46/grafana"
    kubeconfig_for_kubectl = "export KUBECONFIG=./generated/kubeconfig"
    mushop_source_code = "https://github.com/pgressa/oraclecloud-cloudnative"
    mushop_url = "http://129.159.91.46"
    mushop_url_button = "http://129.159.91.46"
    mushop_url_https = "https://129.159.91.46"
    ```

   To access MuShop use the `mushop_url_https` output variable.

## Cleanup

From the directory `deploy/complete/terraform` destroy the stack:

```shell
# From this directory
terraform destroy
```

