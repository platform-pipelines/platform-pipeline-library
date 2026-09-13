// ESLint, Prettier, and a TypeScript typecheck when tsconfig is present.
def call(Map cfg) {
    logBanner 'Lint: Node'
    nodeInstall(cfg)

    if (cfg.lint.autoFormat) {
        sh 'npx prettier --write . || true'
        sh 'npx eslint . --fix || true'
    }

    // ESLint's built-in formatters are html, json, json-with-metadata and
    // stylish — junit lives in a separate package. Converting from json here
    // avoids depending on the project having that package installed.
    def converter = useScript('eslint_to_junit.py')
    sh "npx eslint . --format json | python3 ${converter} > eslint-report.xml || true"

    def eslintStatus   = sh(script: 'npx eslint .',           returnStatus: true)
    def prettierStatus = sh(script: 'npx prettier --check .', returnStatus: true)

    def typeStatus = 0
    if (fileExists('tsconfig.json')) {
        typeStatus = sh(script: 'npx tsc --noEmit', returnStatus: true)
    }

    // Type errors always fail. Shipping a TypeScript build that does not
    // typecheck is not a style preference, so failOnError does not apply.
    if (typeStatus != 0) {
        error 'TypeScript type errors'
    }
    if ((eslintStatus != 0 || prettierStatus != 0) && cfg.lint.failOnError) {
        error 'ESLint or Prettier reported problems. Run: npx eslint . --fix && npx prettier --write .'
    }
}
