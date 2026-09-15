# conftest runs this against every environment's plan (terraformPlan), before
# the plan can be approved.
package main

import rego.v1

required_tags := {"owner", "environment", "managed-by"}

# Only resources that support tags carry tags_all in the plan.
deny contains msg if {
	rc := input.resource_changes[_]
	some action in rc.change.actions
	action in {"create", "update"}
	tags := object.get(rc.change.after, "tags_all", null)
	tags != null
	missing := required_tags - {k | tags[k]}
	count(missing) > 0
	msg := sprintf("%s is missing required tags: %v", [rc.address, sort(missing)])
}

# Nobody opens SSH to the internet through this repo.
deny contains msg if {
	rc := input.resource_changes[_]
	rc.type == "aws_vpc_security_group_ingress_rule"
	rc.change.after.cidr_ipv4 == "0.0.0.0/0"
	rc.change.after.from_port <= 22
	rc.change.after.to_port >= 22
	msg := sprintf("%s opens port 22 to 0.0.0.0/0", [rc.address])
}
