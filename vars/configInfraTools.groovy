// buildTool values that describe infrastructure rather than an application.
// These skip container build, SBOM, signing and artifact publishing, and
// deploy by applying a plan instead of bumping a manifest.
//
// Usage:
//   def infraTools = configInfraTools()
// Returns: List of buildTool values that are infrastructure-as-code, not an application
def call() {
    ['terraform', 'cloudformation']
}
