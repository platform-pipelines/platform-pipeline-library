// Builds the project's sdist/wheel via PEP 517's `build`.
//
// Usage:
//   pythonPackage(cfg)
// Params: cfg (Map) - pipeline config (passed through to pythonVenv)
def call(Map cfg) {
    logBanner 'Package: Python'
    def bin = pythonVenv(cfg)
    sh "${bin}/python -m pip install --disable-pip-version-check --no-cache-dir --quiet build"
    sh "${bin}/python -m build"
}
