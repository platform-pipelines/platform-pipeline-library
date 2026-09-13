// Routes to the right build step for this repo's buildTool.
//
// Usage:
//   buildApp(cfg)
// Params: cfg (Map) - pipeline config; cfg.buildTool selects the build step
// Returns: nothing; errors if buildTool is unsupported
def call(Map cfg) {
    switch (cfg.buildTool) {
        case 'go':          goBuild(cfg);         break
        case 'python':      pythonBuild(cfg);     break
        case 'maven':       mavenBuild(cfg);      break
        case 'gradle':      gradleBuild(cfg);     break
        case 'npm':         nodeBuild(cfg);       break
        case 'terraform':      terraformBuild(cfg);  break
        case 'cloudformation': cfnBuild(cfg);        break
        case 'docker-only': dockerOnlyBuild(cfg); break
        default: error "No build step for buildTool '${cfg.buildTool}'"
    }
}
