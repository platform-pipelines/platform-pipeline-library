// Builds the project's sdist/wheel via PEP 517's `build`.
//
// Usage:
//   pythonPackage(cfg)
// Params: cfg (Map) - pipeline config (unused directly; kept for dispatch signature parity)
def call(Map cfg) {
    logBanner 'Package: Python'
    sh 'pip install --no-cache-dir build'
    sh 'python -m build'
}
