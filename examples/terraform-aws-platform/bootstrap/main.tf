# One-off, per AWS account, run by a human with admin credentials:
#
#   cd bootstrap
#   terraform init
#   terraform apply -var account_id=111122223333 -var 'jenkins_principal_arns=["arn:aws:iam::111122223333:user/jenkins"]'
#
# Creates what the pipeline needs before it can run at all: the state bucket
# (the pipeline cannot create its own backend) and the JenkinsDeploy role it
# assumes. State stays local on purpose; keep terraform.tfstate somewhere safe
# or migrate it into the bucket afterwards.

terraform {
  required_version = ">= 1.10"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

variable "region" {
  type    = string
  default = "eu-west-1"
}

variable "account_id" {
  description = "Account being bootstrapped."
  type        = string
}

variable "jenkins_principal_arns" {
  description = "IAM principals (the identity behind the aws-credentials Jenkins credential) allowed to assume JenkinsDeploy."
  type        = list(string)
}

variable "deploy_policy_arns" {
  description = "Managed policies for JenkinsDeploy. PowerUserAccess plus IAM for the roles the stacks create; scope down for production use."
  type        = list(string)
  default = [
    "arn:aws:iam::aws:policy/PowerUserAccess",
    "arn:aws:iam::aws:policy/IAMFullAccess",
  ]
}

provider "aws" {
  region              = var.region
  allowed_account_ids = [var.account_id]

  default_tags {
    tags = { owner = "platform", environment = "shared", managed-by = "terraform" }
  }
}

resource "aws_kms_key" "state" {
  description         = "Terraform state"
  enable_key_rotation = true
}

resource "aws_s3_bucket" "state" {
  bucket = "acme-tfstate-${var.account_id}-${var.region}"

  lifecycle {
    prevent_destroy = true
  }
}

resource "aws_s3_bucket_versioning" "state" {
  bucket = aws_s3_bucket.state.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "state" {
  bucket = aws_s3_bucket.state.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = aws_kms_key.state.arn
    }
    bucket_key_enabled = true
  }
}

resource "aws_s3_bucket_public_access_block" "state" {
  bucket                  = aws_s3_bucket.state.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_lifecycle_configuration" "state" {
  bucket = aws_s3_bucket.state.id
  rule {
    id     = "expire-old-state-versions"
    status = "Enabled"
    filter {}
    noncurrent_version_expiration {
      noncurrent_days = 90
    }
  }
}

data "aws_iam_policy_document" "state_tls_only" {
  statement {
    sid     = "DenyInsecureTransport"
    effect  = "Deny"
    actions = ["s3:*"]
    principals {
      type        = "*"
      identifiers = ["*"]
    }
    resources = [aws_s3_bucket.state.arn, "${aws_s3_bucket.state.arn}/*"]
    condition {
      test     = "Bool"
      variable = "aws:SecureTransport"
      values   = ["false"]
    }
  }
}

resource "aws_s3_bucket_policy" "state" {
  bucket = aws_s3_bucket.state.id
  policy = data.aws_iam_policy_document.state_tls_only.json
}

data "aws_iam_policy_document" "jenkins_trust" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "AWS"
      identifiers = var.jenkins_principal_arns
    }
  }
}

resource "aws_iam_role" "jenkins_deploy" {
  name                 = "JenkinsDeploy"
  assume_role_policy   = data.aws_iam_policy_document.jenkins_trust.json
  max_session_duration = 3600
}

resource "aws_iam_role_policy_attachment" "jenkins_deploy" {
  for_each   = toset(var.deploy_policy_arns)
  role       = aws_iam_role.jenkins_deploy.name
  policy_arn = each.value
}

output "state_bucket" {
  value = aws_s3_bucket.state.bucket
}

output "jenkins_deploy_role_arn" {
  value = aws_iam_role.jenkins_deploy.arn
}
