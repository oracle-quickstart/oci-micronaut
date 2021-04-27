#
# Identity
#
data "oci_identity_compartment" "mushop_compartment" {
  #Required
  id = var.compartment_ocid
}

resource "oci_identity_user" "fn_email_user" {
  name = "fn-${local.app_name_normalized}-newsletter-user-${random_string.deploy_id.result}"
  description = "${var.app_name} user created for email delivery"
  compartment_id = var.tenancy_ocid
}

resource "oci_identity_group" "fn_email_user_group" {
  #Required
  compartment_id = var.tenancy_ocid
  description = "${var.app_name} email user group"
  name = "fn-${local.app_name_normalized}${random_string.deploy_id.result}-user-group"
}

resource "oci_identity_user_group_membership" "fn_email_user_group_membership" {
  #Required
  group_id = oci_identity_group.fn_email_user_group.id
  user_id = oci_identity_user.fn_email_user.id
}

resource "oci_identity_policy" "fn_email_user_group_policy" {
  name = "fn-${local.app_name_normalized}-${random_string.deploy_id.result}-email-group-policy"
  description = "policy created for ${var.app_name} email group"
  compartment_id = var.compartment_ocid

  statements = [
    "Allow group ${oci_identity_group.fn_email_user_group.name} to use email-family in compartment id ${var.compartment_ocid}"
  ]
}

resource "oci_identity_user_capabilities_management" "fn_email_user_capabilities_management" {
  user_id = oci_identity_user.fn_email_user.id
  can_use_api_keys = "false"
  can_use_auth_tokens = "false"
  can_use_console_password = "false"
  can_use_customer_secret_keys = "false"
  can_use_smtp_credentials = "true"
}

resource "oci_email_sender" "fn_email_sender" {
  compartment_id = var.compartment_ocid
  email_address = var.newsletter_function_approved_email_address
}

resource "oci_identity_dynamic_group" "fn_dynamic_group" {
  compartment_id = var.tenancy_ocid
  name = "fn-newsletter-${local.app_name_normalized}-${random_string.deploy_id.result}-dynamic-group"
  description = " dynamic group created for ${var.app_name} newletter function"
  matching_rule = "ALL {resource.type = 'fnfunc', resource.compartment.id = '${var.compartment_ocid}'}"
}

resource "oci_identity_dynamic_group" "api_gw_dynamic_group" {
  compartment_id = var.tenancy_ocid
  name = "api-gw-${local.app_name_normalized}-${random_string.deploy_id.result}-dynamic-group"
  description = " dynamic group created for ${var.app_name} API Gateway function invocation"
  matching_rule = "ALL {resource.type = 'ApiGateway', resource.compartment.id = '${var.compartment_ocid}'}"
}

resource "oci_identity_policy" "api_gw_dg_policy" {
  name = "api-gw-${local.app_name_normalized}-${random_string.deploy_id.result}-policy"
  description = "policy created for ${var.app_name} api gateway"
  compartment_id = var.compartment_ocid

  statements = [
    "ALLOW any-user to use functions-family in compartment ${data.oci_identity_compartment.mushop_compartment.name} where ALL {request.principal.type= 'ApiGateway', request.resource.compartment.id = '${var.compartment_ocid}'}"
  ]
}

resource "oci_identity_policy" "fn_dg_policy" {
  name = "fn-${local.app_name_normalized}-${random_string.deploy_id.result}-policy"
  description = "policy created for ${var.app_name} function"
  compartment_id = var.compartment_ocid

  statements = [
    "Allow dynamic-group ${oci_identity_dynamic_group.fn_dynamic_group.name} to use virtual-network-family in compartment id ${var.compartment_ocid}",
    "Allow dynamic-group ${oci_identity_dynamic_group.fn_dynamic_group.name} to manage public-ips in compartment id ${var.compartment_ocid}",
    "Allow dynamic-group ${oci_identity_dynamic_group.fn_dynamic_group.name} to use functions-family in compartment id ${var.compartment_ocid}"
  ]
}

resource "oci_identity_smtp_credential" "fn_email_user_smtp_credential" {
  description = "fn-newsletter-${local.app_name_normalized}-${random_string.deploy_id.result}-smtp-credential"
  user_id = oci_identity_user.fn_email_user.id
}

data "oci_identity_smtp_credentials" "fn_email_user_smtp_credential" {
  user_id = oci_identity_smtp_credential.fn_email_user_smtp_credential.user_id
}

#
# Networking
#

resource "oci_core_virtual_network" "fn_vcn" {
  cidr_block = "10.20.0.0/16"
  compartment_id = var.compartment_ocid
  display_name = "Fn ${var.app_name} VCN - ${random_string.deploy_id.result}"
  dns_label = "fn${random_string.deploy_id.result}"

}

resource "oci_core_nat_gateway" "fn_vcn_nat_gw" {
  block_traffic = "false"
  compartment_id = var.compartment_ocid
  display_name = "oke-nat-gateway-${local.app_name_normalized}-${random_string.deploy_id.result}"
  vcn_id = oci_core_virtual_network.fn_vcn.id
}

resource "oci_core_internet_gateway" "fn_vcn_ig" {
  compartment_id = var.compartment_ocid
  vcn_id = oci_core_virtual_network.fn_vcn.id
  display_name = "fn-${local.app_name_normalized}-internet-gateway-${random_string.deploy_id.result}"
  enabled = true
}

data "oci_core_services" "all_services" {
  filter {
    name = "name"
    values = [
      "All .* Services In Oracle Services Network"]
    regex = true
  }
}

resource "oci_core_service_gateway" "fn_vcn_service_gw" {
  compartment_id = var.compartment_ocid
  vcn_id = oci_core_virtual_network.fn_vcn.id
  display_name = "fn-${local.app_name_normalized}-service-gateway-${random_string.deploy_id.result}"
  services {
    service_id = lookup(data.oci_core_services.all_services.services[0], "id")
  }
}


resource "oci_core_route_table" "fn_public_route_table" {
  compartment_id = var.compartment_ocid
  vcn_id = oci_core_virtual_network.fn_vcn.id
  display_name = "fn-public-route-table-${local.app_name_normalized}-${random_string.deploy_id.result}"

  # not needed to have access from internet
  route_rules {
    description = "Traffic to/from internet"
    destination = "0.0.0.0/0"
    destination_type = "CIDR_BLOCK"
    network_entity_id = oci_core_internet_gateway.fn_vcn_ig.id
  }
}

resource "oci_core_route_table" "fn_private_route_table" {
  compartment_id = var.compartment_ocid
  vcn_id = oci_core_virtual_network.fn_vcn.id
  display_name = "fn-private-route-table-${local.app_name_normalized}-${random_string.deploy_id.result}"

  route_rules {
    description = "Traffic to the internet"
    destination = "0.0.0.0/0"
    destination_type = "CIDR_BLOCK"
    network_entity_id = oci_core_nat_gateway.fn_vcn_nat_gw.id
  }
  route_rules {
    description = "Traffic to OCI services"
    destination = lookup(data.oci_core_services.all_services.services[0], "cidr_block")
    destination_type = "SERVICE_CIDR_BLOCK"
    network_entity_id = oci_core_service_gateway.fn_vcn_service_gw.id
  }
}

resource "oci_core_subnet" "fn_public_subnet" {
  cidr_block = "10.20.10.0/24"
  compartment_id = var.compartment_ocid
  dhcp_options_id = oci_core_virtual_network.fn_vcn.default_dhcp_options_id
  display_name = "fn-public-subnet-${local.app_name_normalized}-${random_string.deploy_id.result}"
  dns_label = "fnpub${random_string.deploy_id.result}"
  prohibit_public_ip_on_vnic = false
  route_table_id = oci_core_route_table.fn_public_route_table.id
  security_list_ids = [
    oci_core_virtual_network.fn_vcn.default_security_list_id,
    oci_core_security_list.api_gw_seclist.id
  ]
  vcn_id = oci_core_virtual_network.fn_vcn.id
}

resource "oci_core_subnet" "fn_private_subnet" {
  cidr_block = "10.20.20.0/24"
  compartment_id = var.compartment_ocid
  display_name = "fn-private-subnet-${local.app_name_normalized}-${random_string.deploy_id.result}"
  dns_label = "fnpriv${random_string.deploy_id.result}"
  vcn_id = oci_core_virtual_network.fn_vcn.id
  prohibit_public_ip_on_vnic = true
  route_table_id = oci_core_route_table.fn_private_route_table.id
  dhcp_options_id = oci_core_virtual_network.fn_vcn.default_dhcp_options_id
  security_list_ids = [
    oci_core_virtual_network.fn_vcn.default_security_list_id,
    oci_core_security_list.api_gw_seclist.id
  ]
}

resource "oci_core_security_list" "api_gw_seclist" {
  compartment_id = var.compartment_ocid
  display_name   = "api-wg-seclist-${local.app_name_normalized}-${random_string.deploy_id.result}"
  vcn_id         = oci_core_virtual_network.fn_vcn.id

  # Ingresses
  ingress_security_rules {
    description = "Allow Api GW to forward requests to functions"
    source      = "0.0.0.0/0"
    source_type = "CIDR_BLOCK"
    protocol = 6
    stateless   = false
    tcp_options {
      min = 443
      max = 443
    }
  }
}

#
# Functions
#
resource "oci_functions_application" "fn_mushop_application" {
  #Required
  compartment_id = var.compartment_ocid
  display_name = "${var.app_name} Application ${random_string.deploy_id.result}"
  subnet_ids = [
    oci_core_subnet.fn_public_subnet.id,
  ]
}

data "oci_functions_application" "fn_mushop_application" {
  #Required
  application_id = oci_functions_application.fn_mushop_application.id

  #Optional
  id = oci_functions_application.fn_mushop_application.id
  state = "AVAILABLE"
}

resource "oci_functions_function" "fn_newsletter_function" {
  #Required
  application_id = data.oci_functions_application.fn_mushop_application.id
  display_name = var.newsletter_function_display_name
  image = "${var.newsletter_function_docker_image_repository}:${var.newsletter_function_docker_image_version}"
  memory_in_mbs = var.newsletter_function_memory

  #Optional
  timeout_in_seconds = var.newsletter_function_timeout
  config = {
    "SMTP_USER": oci_identity_smtp_credential.fn_email_user_smtp_credential.username,
    "SMTP_PASSWORD": oci_identity_smtp_credential.fn_email_user_smtp_credential.password,
    "SMTP_HOST": var.newsletter_function_smtp_endpoint,
    "APPROVED_SENDER_EMAIL": var.newsletter_function_approved_email_address
  }
}

data "oci_functions_function" "fn_newsletter_function" {
  #Required
  function_id = oci_functions_function.fn_newsletter_function.id
}


#
# API Gateway
#
resource "oci_apigateway_gateway" "mushop_apigateway_gateway" {
  #Required
  compartment_id = var.compartment_ocid
  endpoint_type = "PUBLIC"
  subnet_id = oci_core_subnet.fn_public_subnet.id

  #Optional
  display_name = "${var.app_name}-${random_string.deploy_id.result} Api GW"
}

data "oci_apigateway_gateway" "mushop_apigateway_gateway" {
  #Required
  gateway_id = oci_apigateway_gateway.mushop_apigateway_gateway.id
}

resource "oci_apigateway_deployment" "fn_newsletter_deployment" {
  #Required
  compartment_id = var.compartment_ocid
  gateway_id = data.oci_apigateway_gateway.mushop_apigateway_gateway.id
  path_prefix = "/subscribe"

  specification {
    #Optional
    logging_policies {
      #Optional
      access_log {
        #Optional
        is_enabled = true
      }

      execution_log {
        #Optional
        is_enabled = true
        log_level = "INFO"
      }
    }

    routes {
      #Required
      backend {
        #Required
        type = "ORACLE_FUNCTIONS_BACKEND"
        function_id = data.oci_functions_function.fn_newsletter_function.id
      }

      logging_policies {
        #Optional
        access_log {
          #Optional
          is_enabled = true
        }

        execution_log {
          #Optional
          is_enabled = true
          log_level = "INFO"
        }
      }

      path = "/{path*}"
      methods = [
        "POST"]
    }
  }

  #Optional
  display_name = var.newsletter_function_display_name
}
