// Installs requirements and compiles all modules as a fast syntax check.
//
// Usage:
//   pythonBuild(cfg)
// Params: cfg (Map) - pipeline config (unused directly; kept for dispatch signature parity)
def call(Map cfg) {
    logBanner 'Build: Python'
    sh 'pip install --no-cache-dir -r requirements.txt'
    sh 'python -m compileall -q .'
}
