// Shared Maven flags. Batch mode and no transfer progress keep logs readable;
// a workspace-local repo makes the cache mountable.
//
// Usage:
//   sh "mvn ${mavenOpts()} clean compile"
// Returns: shared Maven CLI flags as a String
def call() { '-B -ntp -Dmaven.repo.local=.m2' }
