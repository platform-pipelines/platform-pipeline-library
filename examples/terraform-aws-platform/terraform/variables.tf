variable "region" {
  description = "AWS region."
  type        = string
}

variable "account_id" {
  description = "AWS account this environment must be deployed into."
  type        = string

  validation {
    condition     = can(regex("^[0-9]{12}$", var.account_id))
    error_message = "account_id must be a 12-digit AWS account ID."
  }
}

variable "environment" {
  description = "dev, staging or prod."
  type        = string

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "environment must be dev, staging or prod."
  }
}

variable "owner" {
  description = "Owning team, applied as the owner tag."
  type        = string
}

variable "name" {
  description = "Platform name prefix for shared resources (VPC, cluster)."
  type        = string
  default     = "platform"
}

variable "vpc_cidr" {
  description = "VPC CIDR block."
  type        = string
}

variable "service_name" {
  description = "ECS service, container and ECR repository name."
  type        = string
}

variable "container_port" {
  description = "Port the container listens on."
  type        = number
  default     = 8080
}

variable "health_check_path" {
  description = "HTTP path the load balancer health-checks."
  type        = string
  default     = "/healthz"
}

variable "cpu" {
  description = "Fargate task CPU units."
  type        = number
  default     = 256
}

variable "memory" {
  description = "Fargate task memory (MiB)."
  type        = number
  default     = 512
}

variable "desired_count" {
  description = "Number of tasks. Only used at creation; scaling is not reverted by later applies."
  type        = number
  default     = 1
}

variable "bootstrap_image" {
  description = "Image for the first task definition revision. The pipeline (deployEcs) replaces it on the first deploy and Terraform ignores it afterwards."
  type        = string
  default     = "public.ecr.aws/nginx/nginx-unprivileged:stable"
}

variable "certificate_arn" {
  description = "ACM certificate for the HTTPS listener."
  type        = string
}

variable "manage_ecr" {
  description = "Create the ECR repositories in this environment's account. Exactly one environment should own them."
  type        = bool
  default     = false
}

variable "ecr_pull_account_ids" {
  description = "Other AWS accounts whose ECS tasks may pull from the ECR repository (e.g. prod)."
  type        = list(string)
  default     = []
}

variable "enable_nat_gateway" {
  description = "One NAT gateway so private tasks can reach ECR and the internet. Costs money even when idle."
  type        = bool
  default     = true
}
