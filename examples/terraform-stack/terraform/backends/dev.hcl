# terraform init -backend-config=backends/dev.hcl
# State bucket per AWS account; S3-native locking (Terraform >= 1.10), no DynamoDB table.
bucket       = "acme-tfstate-dev-111122223333"
key          = "platform-network/terraform.tfstate"
region       = "eu-west-1"
encrypt      = true
use_lockfile = true
