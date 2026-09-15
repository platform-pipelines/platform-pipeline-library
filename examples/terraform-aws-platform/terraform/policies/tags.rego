package main

import rego.v1

required_tags := {"owner", "environment", "managed-by"}

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
