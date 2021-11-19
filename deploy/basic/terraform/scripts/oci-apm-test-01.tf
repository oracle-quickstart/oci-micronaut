## This configuration was created by Manuel Martin (manuel.martin@oracle.com)
## 2021-11-19 19:35
## UPLOAD - PLAN - APPLY

variable "compartment_ocid" {
  # manuelmartinoracle (root)
  default = ""
}

resource oci_apm_apm_domain APM-Domain-Test-01 {
  # Required
  compartment_id = var.compartment_ocid
  display_name = "APM-Domain-Test-01"
  
  # Optional
  description  = "APM Test 01 - A domain to demo APM with Micronaut"
  # (false) by default
  is_free_tier = "true" 
}
