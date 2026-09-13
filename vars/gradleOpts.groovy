// No daemon in CI: it survives between builds and leaks state across jobs.
//
// Usage:
//   sh "gradle ${gradleOpts()} test"
// Returns: shared gradle CLI flags as a String
def call() { '--no-daemon --console=plain' }
