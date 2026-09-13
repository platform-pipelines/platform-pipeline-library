// The gates that can run at the same time. Returns a map for parallel().
//
// failFast is off deliberately: seeing every problem in one run beats fixing
// them one build at a time.
def call(Map cfg) {
    def checks = [:]

    if (cfg.quality.sonar) {
        checks['sonarqube'] = { scanSonar(cfg) }
    }
    if (cfg.quality.trivy) {
        // Infra repos have no dependency tree to scan; what matters is
        // whether the resources they declare are misconfigured.
        if (isInfraRepo(cfg)) {
            checks['iac-scan'] = { scanIac(cfg) }
        } else {
            checks['trivy-fs'] = { scanTrivy(cfg: cfg, target: '.', type: 'fs') }
        }
    }
    if (cfg.quality.secretScan) {
        checks['secrets'] = { scanSecrets(cfg) }
    }
    if (cfg.quality.dependencyCheck) {
        checks['dependency-check'] = { scanDependencies(cfg) }
    }

    checks.failFast = false
    return checks
}
