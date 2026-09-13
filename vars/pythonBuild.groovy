def call(Map cfg) {
    logBanner 'Build: Python'
    sh 'pip install --no-cache-dir -r requirements.txt'
    sh 'python -m compileall -q .'
}
