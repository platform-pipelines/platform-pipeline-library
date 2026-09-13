// Single source of truth for legal buildTool values. Adding a language or an
// IaC type means adding it here and registering its lint/build/test steps.
//
// Usage:
//   def tools = configSupportedTools()
// Returns: List of every buildTool value configValidate() accepts
def call() {
    [
        // application languages
        'maven', 'gradle', 'npm', 'python', 'go', 'docker-only',
        // infrastructure as code
        'terraform', 'cloudformation',
    ]
}
