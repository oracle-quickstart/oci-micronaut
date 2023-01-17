---
title: "[AWS] Cloudformation"
date: 2020-03-05T15:34:14-07:00
draft: false
weight: 20
tags:
  - AWS
  - EKS
  - Helm
  - Cloudformation
---

Deploys the complete MuShop to [Amazon Elastic Kubernetes Service (EKS)](https://aws.amazon.com/eks/) in combination with [Amazon DocumentDb (with MongoDB compatibility)](https://aws.amazon.com/documentdb/), [Amazon Relational Database Service (RDS)](https://aws.amazon.com/rds/) and [Amazon Managed Streaming for Apache Kafka (MSK)](https://aws.amazon.com/msk/).

## Prerequisites

AWS account with configured billing. 

{{% alert icon="warning" %}}
Note that the charges will be applied since used services are not part of the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all). 
{{% /alert %}}

## Create S3 bucket
Create a new S3 bucket and upload the latest [CloudFormation templates](https://github.com/oracle-quickstart/oci-micronaut/releases/latest/download/mushop-aws-latest.zip) to the new bucket.
The CloudFormation templates should be uploaded together with its parent directory whose name defines CloudFormation templates version, for example: `3.1.0`

## Create stack
To start with the [AWS Cloudformation](https://aws.amazon.com/cloudformation/), create a new stack using S3 URL of the uploaded mushop-entrypoint.yaml template file. 
</br>

{{% width 1-2 %}}
![Mushop stack create](../images/aws/aws-stack-create.png)
{{% /width %}}

Proceed next to the specify stack details.

## Specify stack details

The stack contains lot of configuration options for AWS services. Majority of the options are already preconfigured however there are few options that needs to be configured manually:

- `Availability Zones` - List of Availability Zones in the region the stack is going to be deployed into. It is recommended to select at least 3 AZs.
- `VPC network configuration` - Insert the number of selected availability zones.
- `SSH key name` - select existing or [create new ec2 keypair](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html#having-ec2-create-your-key-pair). This keypair is used to access the bastion host.

{{% width 1-2 %}}
![Mushop stack configure](../images/aws/aws-stack-mandatory.png)
{{% /width %}}

Once you have provided the values, proceed next to the stack options.

## Stack options

Configure here the Cloudformation stack options. Generally you can skip this page and proceed to the Stack Review page.

{{% width 1-2 %}}
![Mushop stack options](../images/aws/aws-stack-configure.png)
{{% /width %}}

## Review

On this page review the configuration options and check all requested capabilities. Then hit `Create stack` button.

{{% width 1-2 %}}
![Mushop stack review](../images/aws/aws-stack-review.png)
{{% /width %}}

{{% alert icon="info" %}}
Note that if you do not check all required capabilities the stack won't be completely created.
{{% /alert %}}

## Deployment

The stack itself consist from several sub-stacks:

- `EKSStack` - stack deploys EKS and VNC, deployment time ~40 minutes
- `DocumentDB` - stack deploys AWS DocumentDb, deployment time ~10 minutes
- `MSKStack` - stack deploys the AWS MSK, deployment time ~40-60minutes
- `RDSStack` - stack deploys the AWS RDS with MySQL engine, deployment time ~10minutes
- `MushopUtilitiesStack` - stack deploys helper Lambda functions, deployment time ~5 minutes
- `MuShop` - stack creates configuration options in the AWS Secret Manager, AWS Parameter Store and uses MuShop Helm chart to deploy the MuShop application to the EKS.

The deployment of the complete stack takes around ~60-90 minutes. Once finished the `Outputs` tab will contain the url of the application as well as the IP of bastion host (if requested to create).

