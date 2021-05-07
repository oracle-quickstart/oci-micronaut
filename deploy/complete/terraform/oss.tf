
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

  count = var.create_oracle_streaming_service_stream ? 1 : 0
}

resource "oci_streaming_stream" "events_stream" {
  #Required
  name = "events"
  partitions = 1

  #Optional
  stream_pool_id = oci_streaming_stream_pool.stream_pool[0].id

  count = var.create_oracle_streaming_service_stream ? 1 : 0
}

resource "oci_identity_user" "events_streaming_user" {
  name = "events-streaming-${local.app_name_normalized}-user-${random_string.deploy_id.result}"
  description = "${var.app_name} user created for publishing events into the stream"
  compartment_id = var.tenancy_ocid
  provider = oci.home_region

  count = var.create_oracle_streaming_service_stream ? 1 : 0
}

resource "oci_identity_auth_token" "events_streaming_user_auth_token" {
  #Required
  description = "${var.app_name} user auth token for publishing events into the stream"
  user_id = oci_identity_user.events_streaming_user[0].id
  provider = oci.home_region

  count = var.create_oracle_streaming_service_stream ? 1 : 0
}

resource "oci_identity_group" "events_streaming_user_group" {
  #Required
  compartment_id = var.tenancy_ocid
  description = "${var.app_name} user group created for publishing events into the stream"
  name = "events-streaming-${local.app_name_normalized}${random_string.deploy_id.result}-user-group"
  provider = oci.home_region

  count = var.create_oracle_streaming_service_stream ? 1 : 0
}

resource "oci_identity_user_group_membership" "events_streaming_user_group_membership" {
  #Required
  group_id = oci_identity_group.events_streaming_user_group[0].id
  user_id = oci_identity_user.events_streaming_user[0].id
  provider = oci.home_region

  count = var.create_oracle_streaming_service_stream ? 1 : 0
}

resource "oci_identity_policy" "events_streaming_user_group_policy" {
  name = "events-streaming-${local.app_name_normalized}-${random_string.deploy_id.result}-group-policy"
  description = "policy created for ${var.app_name} events streaming"
  compartment_id = var.compartment_ocid

  statements = [
    "Allow group ${oci_identity_group.events_streaming_user_group[0].name} to manage streams in compartment id ${var.compartment_ocid}",
    "Allow group ${oci_identity_group.events_streaming_user_group[0].name} to manage stream-pull in compartment id ${var.compartment_ocid}",
    "Allow group ${oci_identity_group.events_streaming_user_group[0].name} to manage stream-push in compartment id ${var.compartment_ocid}"
  ]
  provider = oci.home_region

  count = var.create_oracle_streaming_service_stream ? 1 : 0
}
