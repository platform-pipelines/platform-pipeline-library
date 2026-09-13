// Runs a scanner body in the right place for this agent.
//
// Scanners (trivy, gitleaks, sonar-scanner, argocd) are all in the toolbox,
// so on a toolbox agent they run directly. Elsewhere each has its own image.
def call(String image, Closure body) {
    if (usingToolbox()) {
        body()
    } else {
        docker.image(image).inside("--entrypoint=''") {
            body()
        }
    }
}
