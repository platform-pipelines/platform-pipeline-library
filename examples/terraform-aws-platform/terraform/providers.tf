provider "aws" {
  region = var.region

  # Guard rail: a prod var file with dev credentials (or the reverse) fails at
  # plan time instead of creating resources in the wrong account.
  allowed_account_ids = [var.account_id]

  default_tags {
    tags = {
      owner       = var.owner
      environment = var.environment
      managed-by  = "terraform"
      repository  = "acme/platform-ecs"
    }
  }
}
