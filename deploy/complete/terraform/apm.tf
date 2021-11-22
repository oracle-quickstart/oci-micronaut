# Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
#
## This terraform file was created by Manuel Martin (manuel.martin@oracle.com)
## 2021-11-22 18:30

# Creates a domain to demo APM with Micronaut
resource "oci_apm_apm_domain" "APM-Domain-Test-01" {
  # Required
  compartment_id = var.compartment_ocid
  display_name = var.apm_display_name

  # Optional
  description  = var.apm_description
}
