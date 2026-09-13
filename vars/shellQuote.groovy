// Single-quotes a string for safe use inside a shell command.
// The '\'' dance is how you embed a single quote inside single quotes.
//
// Usage:
//   sh "curl ${shellQuote(url)}"
// Params: s (String) - value to make safe as a single shell argument
// Returns: s wrapped in single quotes, with any embedded quote escaped
def call(String s) {
    "'" + s.replace("'", "'\\''") + "'"
}
