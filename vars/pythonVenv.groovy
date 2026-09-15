// Creates (or reuses) .venv and installs the project's requirements into it.
//
// Installing into the system interpreter fails in both places builds run:
// python:*-slim as the agent uid cannot write site-packages, and the toolbox's
// Ubuntu Python refuses with PEP 668 "externally-managed-environment". A venv
// in the workspace works in both, and every stage calls this because a stage
// starts from the source stash, not from the previous stage's files.
//
// requirements-dev.txt is where the test and lint tools belong (pytest,
// pytest-cov, ruff, mypy), so the per-language python image needs nothing
// extra. Keep .venv in .dockerignore.
//
// Usage:
//   def bin = pythonVenv(cfg)
//   sh "${bin}/python -m pytest"
// Params: cfg (Map) - pipeline config; unused, kept for signature parity with the other python steps
// Returns: path of the venv's bin directory ('.venv/bin')
def call(Map cfg) {
    if (!fileExists('.venv/bin/python')) {
        sh 'python3 -m venv .venv'
    }

    def pip = '.venv/bin/python -m pip install --disable-pip-version-check --no-cache-dir --quiet'
    ['requirements.txt', 'requirements-dev.txt'].findAll { fileExists(it) }.each { file ->
        sh "${pip} -r ${file}"
    }

    return '.venv/bin'
}
