# Oracle Functions Variables
variable "create_oracle_function_newsletter" {
  description = "Create Oracle Function Newsletter"
  default = false
}

variable "newsletter_function_approved_email_address" {
  description = "Email sender for Newsletter Subscription Function"
}
variable "newsletter_function_display_name" {
  default = "newsletter-subscription"
  description = "Newsletter subscription function name."
}
variable "newsletter_function_docker_image_repository" {
  default = "phx.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/newsletter-subscription"
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

