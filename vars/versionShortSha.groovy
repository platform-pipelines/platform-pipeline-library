// Short commit sha. Reads GIT_COMMIT when Jenkins provides it to avoid a
// subprocess on every call.
//
// Usage:
//   def sha = versionShortSha()
// Params: len (int) - number of characters to keep (default 7)
// Returns: the first len characters of the commit sha
def call(int len = 7) {
    def full = env.GIT_COMMIT ?: sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
    return full.take(len)
}
