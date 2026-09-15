output "vpc_id" {
  description = "VPC ID."
  value       = aws_vpc.this.id
}

output "ecs_cluster" {
  description = "ecsCluster for .ci/config.yaml."
  value       = aws_ecs_cluster.this.name
}

output "ecs_service" {
  description = "ecsService for .ci/config.yaml."
  value       = aws_ecs_service.service.name
}

output "ecr_repository_url" {
  description = "imageRepo for .ci/config.yaml (only in the environment with manage_ecr = true)."
  value       = var.manage_ecr ? aws_ecr_repository.this[var.service_name].repository_url : null
}

output "load_balancer_dns_name" {
  description = "Point the service's DNS record here."
  value       = aws_lb.this.dns_name
}
