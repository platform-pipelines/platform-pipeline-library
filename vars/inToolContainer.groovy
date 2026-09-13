// Runs a scanner body in the right place for this agent.
//
// Scanners (trivy, gitleaks, sonar-scanner, argocd) are all in the toolbox,
// so on a toolbox agent they run directly. Elsewhere each has its own image.
//
// Usage:
//   inToolContainer('aquasec/trivy:latest') { sh 'trivy image ...' }
// Params: image (String) - scanner image to fall back to off the toolbox
//         body (Closure) - scanner steps to run
// Returns: nothing; runs body() in place or inside the scanner's own image
def call(String image, Closure body) {
    if (usingToolbox()) {
        body()
    } else {
        docker.image(image).inside("--entrypoint=''") {
            body()
        }
    }
}
