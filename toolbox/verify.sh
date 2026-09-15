#!/usr/bin/env bash
# Smoke test for the toolbox image. Run this in CI after every image build —
# a missing tool must fail here, not three stages into somebody's pipeline.
#
#   docker run --rm ghcr.io/acme/ci-toolbox:1.0.0 bash < toolbox/verify.sh

set -uo pipefail
fail=0

check() {
  local name="$1"; shift
  if out=$("$@" 2>&1); then
    printf '  ok    %-16s %s\n' "$name" "$(echo "$out" | head -1 | cut -c1-58)"
  else
    printf '  FAIL  %-16s %s\n' "$name" "$(echo "$out" | head -1 | cut -c1-58)"
    fail=$((fail + 1))
  fi
}

echo "toolchains"
check java      java -version
check javac     javac -version
check mvn       mvn -v
check gradle    gradle --version
check node      node --version
check npm       npm --version
check go        go version
check python    python3 --version
check pip       pip3 --version

echo
echo "linters"
check golangci  golangci-lint --version
check ruff      ruff --version
check mypy      mypy --version
check shellcheck shellcheck --version
check hadolint  hadolint --version

echo
echo "infrastructure"
check terraform terraform version
check tflint    tflint --version
check conftest  conftest --version
check checkov   checkov --version
check cfn-lint  cfn-lint --version
check aws       aws --version

echo
echo "scanners and deploy"
check trivy     trivy --version
check gitleaks  gitleaks version
check sonar     sonar-scanner --version
check argocd    argocd version --client
check cosign    cosign version
check oras      oras version
check buildah   buildah --version
check yq        yq --version
check jq        jq --version
check git       git --version

echo
echo "pipeline expectations"

# golangci-lint v2 renamed the report flag. If this ever fails, goLint.groovy
# needs updating in the same PR as the image.
if golangci-lint run --help 2>&1 | grep -q 'output.checkstyle.path'; then
  echo "  ok    golangci-lint v2 --output.checkstyle.path flag present"
else
  echo "  FAIL  golangci-lint report flag missing — goLint.groovy will break"
  fail=$((fail + 1))
fi

# ruff must support junit output, which pythonLint depends on.
if ruff check --help 2>&1 | grep -q 'output-format'; then
  echo "  ok    ruff --output-format present"
else
  echo "  FAIL  ruff --output-format missing"
  fail=$((fail + 1))
fi

# The library's resource scripts run under this python.
if python3 -c 'import json, re, xml.etree.ElementTree' 2>/dev/null; then
  echo "  ok    python stdlib modules used by resource scripts"
else
  echo "  FAIL  python stdlib incomplete"
  fail=$((fail + 1))
fi

# Builds must not run as root, or the mounted workspace ends up root-owned.
if [ "$(id -u)" -ne 0 ]; then
  echo "  ok    running as uid $(id -u) ($(id -un)), not root"
else
  echo "  FAIL  running as root"
  fail=$((fail + 1))
fi

echo
if [ "$fail" -eq 0 ]; then
  echo "all checks passed"
else
  echo "$fail check(s) failed"
fi
exit "$fail"
