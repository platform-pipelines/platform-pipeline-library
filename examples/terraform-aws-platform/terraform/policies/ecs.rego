# Rules that keep deployEcs safe: a service without the circuit breaker can
# sit on a crash-looping revision, and tasks with public IPs bypass the ALB.
package main

import rego.v1

changing(rc) if {
	some action in rc.change.actions
	action in {"create", "update"}
}

deny contains msg if {
	rc := input.resource_changes[_]
	rc.type == "aws_ecs_service"
	changing(rc)
	not circuit_breaker_rolls_back(rc.change.after)
	msg := sprintf("%s must enable deployment_circuit_breaker with rollback = true", [rc.address])
}

deny contains msg if {
	rc := input.resource_changes[_]
	rc.type == "aws_ecs_service"
	changing(rc)
	some net in rc.change.after.network_configuration
	net.assign_public_ip == true
	msg := sprintf("%s must not assign public IPs to tasks", [rc.address])
}

deny contains msg if {
	rc := input.resource_changes[_]
	rc.type == "aws_lb_listener"
	changing(rc)
	rc.change.after.protocol == "HTTP"
	some action in rc.change.after.default_action
	action.type != "redirect"
	msg := sprintf("%s serves plain HTTP; it may only redirect to HTTPS", [rc.address])
}

circuit_breaker_rolls_back(after) if {
	some cb in after.deployment_circuit_breaker
	cb.enable == true
	cb.rollback == true
}
