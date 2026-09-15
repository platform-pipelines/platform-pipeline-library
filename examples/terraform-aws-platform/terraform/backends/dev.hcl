# Created by bootstrap/ in account 111122223333. S3-native locking, no DynamoDB.
bucket       = "acme-tfstate-111122223333-eu-west-1"
key          = "platform-ecs/dev/terraform.tfstate"
region       = "eu-west-1"
encrypt      = true
use_lockfile = true
