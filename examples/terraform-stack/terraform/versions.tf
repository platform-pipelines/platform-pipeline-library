terraform {
  required_version = ">= 1.10"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }

  # Partial configuration: bucket, key and region come from
  # backends/<env>.hcl via environments[].backendConfig, so one codebase
  # keeps separate state per environment.
  backend "s3" {}
}
