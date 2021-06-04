# Oracle Functions Variables
variable "create_oracle_function_newsletter" {
  description = "Create Oracle Function Newsletter"
  default = false
}

variable "newsletter_function_approved_email_address" {
  description = "Email sender for Newsletter Subscription Function"
  default = "newsletter@mushop.org"
}
variable "newsletter_function_display_name" {
  default = "newsletter-subscription"
  description = "Newsletter subscription function name."
}

variable "newsletter_function_docker_image_repo_mapping" {
  type = map(string)
  default = {
    us-phoenix-1 = "phx.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/newsletter-subscription"
    us-ashburn-1 = "iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/newsletter-subscription"
    eu-frankfurt-1 = "fra.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/newsletter-subscription"
    uk-london-1 = "lhr.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/newsletter-subscription"
  }
}

variable "newsletter_function_docker_image_repository" {
  description = "Newsletter subscription function docker image repository."
  default = ""
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

