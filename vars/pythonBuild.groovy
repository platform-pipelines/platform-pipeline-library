// Installs requirements into .venv and compiles all modules as a fast syntax check.
//
// Usage:
//   pythonBuild(cfg)
// Params: cfg (Map) - pipeline config (passed through to pythonVenv)
def call(Map cfg) {
    logBanner 'Build: Python'
    def bin = pythonVenv(cfg)
    sh "${bin}/python -m compileall -q -x '(^|/)\\.venv/' ."
}
