// Shared Maven flags. Batch mode and no transfer progress keep logs readable;
// a workspace-local repo makes the cache mountable.
def call() { '-B -ntp -Dmaven.repo.local=.m2' }
