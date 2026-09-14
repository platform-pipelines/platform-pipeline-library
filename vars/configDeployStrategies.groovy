// Single source of truth for legal deployStrategy values. Adding a strategy
// means adding it here and a matching case in deployToEnvironment.
//
// Usage:
//   def strategies = configDeployStrategies()
// Returns: List of every deployStrategy value configValidate() accepts
def call() {
    ['gitops', 'terraform', 'cloudformation']
}
