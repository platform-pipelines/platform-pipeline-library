// Short commit sha. Reads GIT_COMMIT when Jenkins provides it to avoid a
// subprocess on every call.
def call(int len = 7) {
    def full = env.GIT_COMMIT ?: sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
    return full.take(len)
}
