// Runs a scanner body in the right place for this agent.
//
// Scanners (trivy, gitleaks, sonar-scanner, argocd, cosign, aws) are all in the
// toolbox, so on a toolbox agent they run directly, and when CI_TOOLBOX_IMAGE
// is set they run inside it. Elsewhere each has its own image.
//
// Usage:
//   inToolContainer('aquasec/trivy:latest') { sh 'trivy image ...' }
// Params: image (String) - scanner image to fall back to off the toolbox
//         body (Closure) - scanner steps to run
// Returns: whatever body() returns
def call(String image, Closure body) {
    if (usingToolbox()) {
        return body()
    }
    return inContainer(env.CI_TOOLBOX_IMAGE ?: image, null, body)
}
