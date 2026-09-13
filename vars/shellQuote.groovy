// Single-quotes a string for safe use inside a shell command.
// The '\'' dance is how you embed a single quote inside single quotes.
def call(String s) {
    "'" + s.replace("'", "'\\''") + "'"
}
