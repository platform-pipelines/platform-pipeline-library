// Ruff covers what flake8, isort and pyupgrade used to, in one fast pass.
def call(Map cfg) {
    logBanner 'Lint: Python'

    if (cfg.lint.autoFormat) {
        sh 'ruff format .'
        sh 'ruff check --fix .'
    }

    sh 'ruff check --output-format junit --output-file ruff-report.xml . || true'

    def lintStatus   = sh(script: 'ruff check .',         returnStatus: true)
    def formatStatus = sh(script: 'ruff format --check .', returnStatus: true)

    if (fileExists('mypy.ini') || fileExists('pyproject.toml')) {
        sh 'mypy . || true'
    }

    if ((lintStatus != 0 || formatStatus != 0) && cfg.lint.failOnError) {
        error 'ruff reported problems. Run: ruff check --fix . && ruff format .'
    }
}
