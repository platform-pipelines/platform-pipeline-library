// Race detector on by default — Go concurrency bugs are cheap to catch here
// and expensive to catch in production.
def call(Map cfg) {
    logBanner 'Test: Go'
    sh 'go test ./... -race -coverprofile=coverage.out -covermode=atomic > go-test.out 2>&1 || true'
    sh 'cat go-test.out'
    sh 'go run github.com/jstemmer/go-junit-report/v2@latest < go-test.out > test-results.xml || true'

    // Re-run for the exit code so a failure is unambiguous.
    sh 'go test ./... -race'
}
