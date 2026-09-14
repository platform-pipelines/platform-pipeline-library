// Writes a bundled resource script to the workspace and returns its path.
// Keeps parsing logic in real .py files that can be tested on their own,
// instead of heredocs buried inside Groovy strings.
//
// Usage:
//   sh "python3 ${useScript('coverage_percent.py')} coverage.xml"
// Params: name (String) - filename under resources/com/platformpipelines/scripts/
// Returns: workspace-relative path the script was written to (cached across calls)
def call(String name) {
    def target = ".ci-scripts/${name}"
    if (!fileExists(target)) {
        writeFile file: target, text: libraryResource("com/platformpipelines/scripts/${name}")
    }
    return target
}
