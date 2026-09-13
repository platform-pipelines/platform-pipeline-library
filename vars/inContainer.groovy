// Runs a body inside a container image, with a named volume at /cache so
// dependency downloads survive between builds.
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
