# Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
# 

# Create namespace mushop for the mushop microservices
resource "kubernetes_namespace" "mushop_namespace" {
  metadata {
    name = "mushop"
  }
  depends_on = [oci_containerengine_node_pool.oke_node_pool, local_file.kubeconfig]
}

# Deploy mushop chart
resource "helm_release" "mushop" {
  name          = "mushop"
  chart         = "../helm-chart/mushop"
  namespace     = kubernetes_namespace.mushop_namespace.id
  wait          = true
  wait_for_jobs = true

  set {
    name  = "global.mock.service"
    value = var.mushop_mock_mode_all ? "all" : "none"
  }
  set {
    name  = "global.oadbAdminSecret"
    value = var.db_admin_name
  }
  set {
    name  = "global.oadbConnectionSecret"
    value = var.db_connection_name
  }
  set {
    name  = "global.oadbWalletSecret"
    value = var.db_wallet_name
  }
  set {
    name  = "global.ossConnectionSecret"
    value = var.oss_conection
  }
  set {
    name  = "global.oapmConnectionSecret"
    value = var.apm_connection_name
  }
  set {
    name  = "global.ociDeploymentConfigMap"
    value = var.oci_deployment
  }
  set {
    name  = "global.test"
    value = var.oci_deployment
  }

  set {
    name  = "global.imageSuffix"
    value = var.mushop_micronaut_service_version
  }
  set {
    name  = "api.env.trackingEnabled"
    value = var.create_oracle_streaming_service_stream ? true : false
  }

  set {
    name  = "global.cloud"
    value = "oci"
  }

  set {
    name  = "global.oosBucketSecret"
    value = var.oos_bucket_name
  }

  set {
    name  = "tags.atp"
    value = var.mushop_mock_mode_all ? false : true
  }
  set {
    name  = "tags.streaming"
    value = var.create_oracle_streaming_service_stream ? true : false
  }


  set {
    name  = "ingress.enabled"
    value = var.ingress_nginx_enabled
  }
  set {
    name  = "ingress.hosts"
    value = "{${var.ingress_hosts}}"
  }
  set {
    name  = "ingress.clusterIssuer"
    value = var.cert_manager_enabled ? var.ingress_cluster_issuer : ""
  }
  set {
    name  = "ingress.email"
    value = var.ingress_email_issuer
  }
  set {
    name  = "ingress.tls"
    value = var.ingress_tls
  }

  depends_on = [oci_identity_policy.oke_compartment_policies, oci_objectstorage_bucket.mushop_assets_bucket, helm_release.ingress_nginx, helm_release.cert_manager] # Ugly workaround because of the oci pvc provisioner not be able to wait for the node be active and retry.

  timeout = 500
}
