// No daemon in CI: it survives between builds and leaks state across jobs.
def call() { '--no-daemon --console=plain' }
