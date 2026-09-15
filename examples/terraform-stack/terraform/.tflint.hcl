# The bundled terraform ruleset needs no plugin download, so `tflint --init`
# works on agents without GitHub access. Add the aws ruleset
# (github.com/terraform-linters/tflint-ruleset-aws) when agents can fetch it.
plugin "terraform" {
  enabled = true
  preset  = "recommended"
}
