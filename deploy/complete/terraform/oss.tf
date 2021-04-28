
data "oci_identity_tenancy" "mushop_compartment" {
  tenancy_id = var.tenancy_ocid
}

resource "oci_streaming_stream_pool" "stream_pool" {
  #Required
  compartment_id = var.compartment_ocid
  name = "${local.app_name_normalized}-${random_string.deploy_id.result}-stream-pool"

  kafka_settings {
    log_retention_hours = 24
    num_partitions = 1
  }
}

resource "oci_streaming_stream" "events_stream" {
  #Required
  name = "events"
  partitions = 1

  #Optional
  compartment_id = var.compartment_ocid
}

resource "oci_identity_user" "events_streaming_user" {
  name = "events-stream-${local.app_name_normalized}-user-${random_string.deploy_id.result}"
  description = "${var.app_name} user created for publishing events into the stream"
  compartment_id = var.tenancy_ocid
}

resource "oci_identity_auth_token" "events_streaming_user_auth_token" {
  #Required
  description = "${var.app_name} user auth token for publishing events into the stream"
  user_id = oci_identity_user.events_streaming_user.id
}

resource "oci_identity_group" "events_streaming_user_group" {
  #Required
  compartment_id = var.tenancy_ocid
  description = "${var.app_name} user group created for publishing events into the stream"
  name = "events-stream-${local.app_name_normalized}${random_string.deploy_id.result}-user-group"
}

resource "oci_identity_user_group_membership" "fn_email_user_group_membership" {
  #Required
  group_id = oci_identity_group.events_streaming_user_group.id
  user_id = oci_identity_user.events_streaming_user.id
}

resource "oci_identity_policy" "fn_email_user_group_policy" {
  name = "events-streaming-${local.app_name_normalized}-${random_string.deploy_id.result}-group-policy"
  description = "policy created for ${var.app_name} events streaming"
  compartment_id = var.compartment_ocid

  statements = [
    "Allow group ${oci_identity_group.events_streaming_user_group.name} to manage streams in compartment id ${var.compartment_ocid}",
    "Allow group ${oci_identity_group.events_streaming_user_group.name} to manage stream-pull in compartment id ${var.compartment_ocid}",
    "Allow group ${oci_identity_group.events_streaming_user_group.name} to manage stream-push in compartment id ${var.compartment_ocid}"
  ]
}
