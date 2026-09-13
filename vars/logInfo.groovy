// Informational line.
//
// Usage:
//   logInfo "app=${cfg.appName} version=${env.APP_VERSION}"
def call(String msg) { echo "[INFO]  ${msg}" }
