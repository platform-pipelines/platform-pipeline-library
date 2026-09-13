// Error line. Does not fail the build — call error() for that.
//
// Usage:
//   logError "Trivy scan found critical vulnerabilities"
def call(String msg) { echo "[ERROR] ${msg}" }
