// Runs a body inside a container image, with a named volume at /cache so
// dependency downloads survive between builds.
//
// Usage:
//   inContainer('golang:1.23', '.gocache') { sh 'go build ./...' }
// Params: image (String) - container image to run in
//         cacheDir (String) - cache directory name, mounted at /cache when non-empty
//         body (Closure) - steps to run inside the container
// Returns: nothing; runs body() inside docker.image(image).inside(...)
def call(String image, String cacheDir, Closure body) {
    def mount = ''
    if (cacheDir) {
        def volume = 'ci-cache-' + image.replaceAll(/[^a-zA-Z0-9]/, '-')
        mount = "-v ${volume}:/cache -e CACHE_DIR=/cache"
    }
    docker.image(image).inside(mount) {
        body()
    }
}
