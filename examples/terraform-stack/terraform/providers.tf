provider "aws" {
  region = var.region

  # Every taggable resource gets these, which is what policies/tags.rego checks.
  default_tags {
    tags = {
      owner       = var.owner
      environment = var.environment
      managed-by  = "terraform"
      repository  = "acme/platform-network"
    }
  }
}
