// Routes to the right test step for this repo's buildTool.
def call(Map cfg) {
    switch (cfg.buildTool) {
        case 'go':          goTest(cfg);         break
        case 'python':      pythonTest(cfg);     break
        case 'maven':       mavenTest(cfg);      break
        case 'gradle':      gradleTest(cfg);     break
        case 'npm':         nodeTest(cfg);       break
        case 'terraform':      terraformTest(cfg);  break
        case 'cloudformation': cfnTest(cfg);        break
        case 'docker-only': dockerOnlyTest(cfg); break
        default: error "No test step for buildTool '${cfg.buildTool}'"
    }
}
