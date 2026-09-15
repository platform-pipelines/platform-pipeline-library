# Mocked provider: no AWS credentials, no state. Runs in the pipeline's Test
# stage on every branch.

mock_provider "aws" {
  mock_data "aws_availability_zones" {
    defaults = {
      names = ["eu-west-1a", "eu-west-1b", "eu-west-1c"]
    }
  }
  mock_data "aws_iam_policy_document" {
    defaults = {
      json = "{\"Version\":\"2012-10-17\",\"Statement\":[]}"
    }
  }
}

variables {
  region          = "eu-west-1"
  account_id      = "111122223333"
  environment     = "dev"
  owner           = "platform"
  vpc_cidr        = "10.20.0.0/16"
  service_name    = "payments-api"
  certificate_arn = "arn:aws:acm:eu-west-1:111122223333:certificate/test"
}

run "service_is_private_and_self_healing" {
  command = plan

  assert {
    condition     = aws_ecs_service.service.network_configuration[0].assign_public_ip == false
    error_message = "tasks must not get public IPs"
  }

  assert {
    condition     = aws_ecs_service.service.deployment_circuit_breaker[0].rollback == true
    error_message = "the circuit breaker must roll back failed deployments"
  }

  assert {
    condition     = jsondecode(aws_ecs_task_definition.service.container_definitions)[0].readonlyRootFilesystem == true
    error_message = "the container must run with a read-only root filesystem"
  }
}

run "https_only_edge" {
  command = plan

  assert {
    condition     = aws_lb_listener.http_redirect.default_action[0].type == "redirect"
    error_message = "port 80 must only redirect"
  }

  assert {
    condition     = aws_lb.this.drop_invalid_header_fields == true
    error_message = "the ALB must drop invalid headers"
  }
}

run "ecr_only_where_managed" {
  command = plan

  variables {
    manage_ecr           = false
    ecr_pull_account_ids = ["444455556666"]
  }

  assert {
    condition     = length(aws_ecr_repository.this) == 0 && length(aws_ecr_repository_policy.cross_account_pull) == 0
    error_message = "ECR must not be created when manage_ecr is false"
  }
}

run "ecr_with_cache_repo_and_cross_account_pull" {
  command = plan

  variables {
    manage_ecr           = true
    ecr_pull_account_ids = ["444455556666"]
  }

  assert {
    condition     = contains(keys(aws_ecr_repository.this), "payments-api/cache")
    error_message = "kaniko's layer cache repository must exist"
  }

  assert {
    condition     = length(aws_ecr_repository_policy.cross_account_pull) == 1
    error_message = "prod must be allowed to pull"
  }
}

run "prod_protects_the_load_balancer" {
  command = plan

  variables {
    environment = "prod"
    account_id  = "444455556666"
  }

  assert {
    condition     = aws_lb.this.enable_deletion_protection == true
    error_message = "prod ALB must have deletion protection"
  }
}

run "rejects_a_malformed_account_id" {
  command = plan

  variables {
    account_id = "12345"
  }

  expect_failures = [var.account_id]
}
