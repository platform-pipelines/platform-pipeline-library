// Template files this repo owns.
//
// Explicit config wins; otherwise glob the working directory. Nested stacks
// referenced from a parent are linted through the parent, so a flat glob is
// the right default.
def call(Map cfg) {
    if (cfg.infra.templates) {
        return cfg.infra.templates
    }

    def found = sh(
        script: "find ${cfg.infra.workingDir} -maxdepth 2 \\( -name '*.yaml' -o -name '*.yml' -o -name '*.json' \\) -not -path '*/.*' | sort",
        returnStdout: true
    ).trim()

    return found ? found.split('\n') as List : []
}
