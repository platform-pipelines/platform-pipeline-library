# Runs in the Test stage with no AWS credentials and no state: the provider
# is mocked, so these check the module's own logic (CIDR maths, counts,
# validation), not AWS.

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
  environment = "dev"
  owner       = "platform-networking"
  vpc_cidr    = "10.10.0.0/16"
}

run "subnets_per_az" {
  command = plan

  assert {
    condition     = length(aws_subnet.private) == 2 && length(aws_subnet.public) == 2
    error_message = "expected one private and one public subnet per AZ"
  }

  assert {
    condition     = aws_subnet.private[0].cidr_block == "10.10.0.0/20" && aws_subnet.public[0].cidr_block == "10.10.128.0/20"
    error_message = "subnet CIDRs are not carved from the VPC range as expected"
  }

  assert {
    condition     = alltrue([for s in aws_subnet.public : s.map_public_ip_on_launch == false])
    error_message = "public subnets must not hand out public IPs by default"
  }
}

run "three_azs_in_prod" {
  command = plan

  variables {
    environment = "prod"
    az_count    = 3
  }

  assert {
    condition     = length(aws_subnet.private) == 3
    error_message = "az_count = 3 should create three private subnets"
  }
}

run "rejects_unknown_environment" {
  command = plan

  variables {
    environment = "qa"
  }

  expect_failures = [var.environment]
}
