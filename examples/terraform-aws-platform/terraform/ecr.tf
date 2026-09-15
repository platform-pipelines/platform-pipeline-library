# The application repository, plus the kaniko layer cache kanikoArgs pushes to
# <imageRepo>/cache. ECR does not create repositories on push, so a missing
# cache repository fails the image build.
locals {
  ecr_repositories = var.manage_ecr ? toset([var.service_name, "${var.service_name}/cache"]) : toset([])
}

resource "aws_kms_key" "ecr" {
  count = var.manage_ecr ? 1 : 0

  description         = "${var.service_name} ECR images"
  enable_key_rotation = true
}

# trivy:ignore:AVD-AWS-0031 `latest` and the short-SHA tag move on every main build; deploys pin version tags and the audit log records digests.
resource "aws_ecr_repository" "this" {
  for_each = local.ecr_repositories

  name                 = each.value
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "KMS"
    kms_key         = aws_kms_key.ecr[0].arn
  }
}

resource "aws_ecr_lifecycle_policy" "this" {
  for_each   = aws_ecr_repository.this
  repository = each.value.name

  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Keep the last 100 images"
      selection    = { tagStatus = "any", countType = "imageCountMoreThan", countNumber = 100 }
      action       = { type = "expire" }
    }]
  })
}

data "aws_iam_policy_document" "ecr_cross_account_pull" {
  count = var.manage_ecr && length(var.ecr_pull_account_ids) > 0 ? 1 : 0

  statement {
    sid     = "CrossAccountPull"
    actions = ["ecr:BatchGetImage", "ecr:GetDownloadUrlForLayer", "ecr:BatchCheckLayerAvailability"]
    principals {
      type        = "AWS"
      identifiers = [for id in var.ecr_pull_account_ids : "arn:aws:iam::${id}:root"]
    }
  }
}

resource "aws_ecr_repository_policy" "cross_account_pull" {
  count = var.manage_ecr && length(var.ecr_pull_account_ids) > 0 ? 1 : 0

  repository = aws_ecr_repository.this[var.service_name].name
  policy     = data.aws_iam_policy_document.ecr_cross_account_pull[0].json
}
