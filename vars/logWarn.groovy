// Warning line. Does not affect build result on its own.
//
// Usage:
//   logWarn "GitHub call failed: ${what}"
def call(String msg) { echo "[WARN]  ${msg}" }
