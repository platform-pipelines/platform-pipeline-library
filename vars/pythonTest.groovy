def call(Map cfg) {
    logBanner 'Test: Python'
    sh 'pytest --junitxml=test-results.xml --cov=. --cov-report=xml --cov-report=term'
}
