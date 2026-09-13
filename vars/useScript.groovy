// Writes a bundled resource script to the workspace and returns its path.
// Keeps parsing logic in real .py files that can be tested on their own,
// instead of heredocs buried inside Groovy strings.
def call(String name) {
    def target = ".ci-scripts/${name}"
    if (!fileExists(target)) {
        writeFile file: target, text: libraryResource("com/company/scripts/${name}")
    }
    return target
}
