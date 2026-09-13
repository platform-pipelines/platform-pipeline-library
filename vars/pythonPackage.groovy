def call(Map cfg) {
    logBanner 'Package: Python'
    sh 'pip install --no-cache-dir build'
    sh 'python -m build'
}
