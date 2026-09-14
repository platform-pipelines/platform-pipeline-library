// Single source of truth for legal imageBuilder values. Adding a builder means
// adding it here and a matching case (plus buildImage* step) in buildImage.
//
// Usage:
//   def builders = configImageBuilders()
// Returns: List of every imageBuilder value configValidate() accepts
def call() {
    ['kaniko-docker', 'kaniko-k8s', 'buildah']
}
