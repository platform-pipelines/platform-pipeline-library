// Routes to the right package step for this repo's buildTool.
def call(Map cfg) {
    switch (cfg.buildTool) {
        case 'go':          goPackage(cfg);         break
        case 'python':      pythonPackage(cfg);     break
        case 'maven':       mavenPackage(cfg);      break
        case 'gradle':      gradlePackage(cfg);     break
        case 'npm':         nodePackage(cfg);       break
        case 'terraform':      terraformPackage(cfg);  break
        case 'cloudformation': cfnPackage(cfg);        break
        case 'docker-only': dockerOnlyPackage(cfg); break
        default: error "No package step for buildTool '${cfg.buildTool}'"
    }
}
