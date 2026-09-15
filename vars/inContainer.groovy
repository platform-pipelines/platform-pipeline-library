// Runs a body inside a container image as the agent's user.
//
// Two things break stock images under docker.inside, and both are handled here:
//   - Images whose ENTRYPOINT is the tool itself (hashicorp/terraform,
//     amazon/aws-cli, aquasec/trivy) turn Jenkins' keep-alive `cat` into
//     `terraform cat` and the container exits at once. --entrypoint='' fixes it.
//   - The agent uid has no home directory in the image, so HOME is `/` and
//     go, npm, pip, gradle, trivy and sonar-scanner all fail creating their
//     caches. HOME points into the workspace's @tmp sibling instead, which the
//     build owns, is mounted by docker.inside, and is never part of a stash or
//     a kaniko build context.
//
// Usage:
//   inContainer('golang:1.27', '.gocache') { sh 'go build ./...' }
// Params: image (String) - container image to run in
//         cacheDir (String) - cache directory name under HOME; informational, the tools pick their own paths under HOME
//         body (Closure) - steps to run inside the container
// Returns: whatever body() returns
def call(String image, String cacheDir, Closure body) {
    def home = "${env.WORKSPACE}@tmp/home"
    logDebug "Container ${image} (HOME=${home}${cacheDir ? ", cache ${cacheDir}" : ''})"

    return docker.image(image).inside("--entrypoint=''") {
        withEnv([
            "HOME=${home}",
            "XDG_CACHE_HOME=${home}/.cache",
            "GRADLE_USER_HOME=${home}/.gradle",
            "npm_config_cache=${home}/.npm",
        ]) {
            return body()
        }
    }
}
