# Oracle Functions Variables
variable "newsletter_function_approved_email_address" {
  description = "Email sender for Newsletter Subscription Function"
}
variable "newsletter_function_smtp_endpoint" {
  default = "smtp.email.us-ashburn-1.oci.oraclecloud.com"
  description = "Oracle Email Delivery SMTP endpoint to use for Newsletter Subscription Function"
}
variable "newsletter_function_display_name" {
  default = "newsletter-subscription"
  description = "Newsletter subscription function name."
}
variable "newsletter_function_docker_image_repository" {
  default = "iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/newsletter-subscription"
  description = "Newsletter subscription function docker image repository."
}
variable "newsletter_function_docker_image_version" {
  default = "1.0.0"
  description = "Newsletter subscription function docker image version."
}
variable "newsletter_function_timeout" {
  default = 120
  description = "Newsletter subscription function timeout."
}
variable "newsletter_function_memory" {
  default = 512
  description = "Newsletter subscription function memory requirement."
}

