// Template files this repo owns.
//
// Explicit config wins; otherwise glob the working directory. Nested stacks
// referenced from a parent are linted through the parent, so a flat glob is
// the right default.
//
// Usage:
//   def templates = cfnTemplates(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.infra.templates/workingDir
// Returns: List of template file paths (explicit cfg.infra.templates, or a glob of workingDir)
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
