account_id      = "111122223333"
environment     = "dev"
vpc_cidr        = "10.20.0.0/16"
certificate_arn = "arn:aws:acm:eu-west-1:111122223333:certificate/00000000-0000-0000-0000-000000000001"
desired_count   = 1

# dev owns the ECR repositories; prod's account may pull from them.
manage_ecr           = true
ecr_pull_account_ids = ["444455556666"]
