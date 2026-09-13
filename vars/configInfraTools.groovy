// buildTool values that describe infrastructure rather than an application.
// These skip container build, SBOM, signing and artifact publishing, and
// deploy by applying a plan instead of bumping a manifest.
def call() {
    ['terraform', 'cloudformation']
}
