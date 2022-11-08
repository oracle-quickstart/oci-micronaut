---
title: "[Oracle Cloud] Resource manager"
date: 2020-03-05T15:34:14-07:00
draft: false
weight: 10
tags:
  - Terraform
  - OKE
  - Helm
  - Resource manager
---

## Prerequisites

You need to have an Oracle Cloud Infrastructure account.

{{% alert icon="warning" %}}
Note that the charges will be applied since used services are not part of the [Oracle Cloud Free Tier](https://www.oracle.com/cloud/free/).
{{% /alert %}}

## Create stack
To start with the [OCI Resource Manager](https://docs.oracle.com/en-us/iaas/Content/ResourceManager/Concepts/resourcemanager.htm) click on the following button:
</br>
[![Deploy to Oracle Cloud](https://oci-resourcemanager-plugin.plugins.oci.oraclecloud.com/latest/deploy-to-oracle-cloud.svg)](https://cloud.oracle.com/resourcemanager/stacks/create?zipUrl=https://github.com/oracle-quickstart/oci-micronaut/releases/latest/download/mushop-stack-latest.zip)

The link will redirect you to the Resource manager create stack page. Before the Resource manager will load the Terraform templates, you have to comply with Terms of Use.
{{% width 1-2 %}}
![Mushop stack create](../images/oci/oci-stack-welcome.png)
{{% /width %}}


Once you agree with the Terms of Use, the Resource manager loads the stack configuration written in Terraform:
{{% width 1-2 %}}
![Mushop stack create](../images/oci/oci-stack-welcome-reviewed.png)
{{% /width %}}

## Configure stack variables

The stack contains lot of configuration options where all of them are already preconfigured. Optionally you can enable additionally services that will be deployed along the full Mushop application.

{{% width 1-2 %}}
![Mushop stack configure](../images/oci/oci-stack-configure.png)
{{% /width %}}

These are:
- Newsletter Oracle Cloud Function that configures SMTP credentials and OCI Function that sends the newsletter email
- Events streaming service that sends audit events to the Oracle Streaming Service
- Application Performance Monitoring

## Review

Review the stack info and select the `Run apply` to immediately run the Terraform scripts right after confirmation.  

{{% width 1-2 %}}
![Mushop stack configure](../images/oci/oci-stack-review.png)
{{% /width %}}

## Deployment

The deployment of the complete stack takes around ~40 minutes. Once the Apply job is finished the outputs resource contains the stack details:

{{% width 1-2 %}}
![Mushop stack outputs](../images/oci/oci-stack-outputs.png)
{{% /width %}}


