// Routes to the right lint step for this repo's buildTool.
def call(Map cfg) {
    if (!cfg.lint.enabled) {
        logInfo 'Lint disabled in config'
        return
    }

    switch (cfg.buildTool) {
        case 'go':          goLint(cfg);         break
        case 'python':      pythonLint(cfg);     break
        case 'maven':       mavenLint(cfg);      break
        case 'gradle':      gradleLint(cfg);     break
        case 'npm':         nodeLint(cfg);       break
        case 'terraform':      terraformLint(cfg);  break
        case 'cloudformation': cfnLint(cfg);        break
        case 'docker-only': dockerOnlyLint(cfg); break
        default: error "No lint step for buildTool '${cfg.buildTool}'"
    }
}
