// Only emitted when PIPELINE_DEBUG=true, so normal logs stay readable.
//
// Usage:
//   logDebug "cache dir resolved to ${dir}"
def call(String msg) {
    if (env.PIPELINE_DEBUG == 'true') { echo "[DEBUG] ${msg}" }
}
