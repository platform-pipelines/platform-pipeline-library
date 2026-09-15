# terraform init -backend-config=backends/prod.hcl
bucket       = "acme-tfstate-prod-111122223333"
key          = "platform-network/terraform.tfstate"
region       = "eu-west-1"
encrypt      = true
use_lockfile = true
