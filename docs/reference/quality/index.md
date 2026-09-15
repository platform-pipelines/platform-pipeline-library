# Quality & scanning

The Quality & Security stage. [qualityChecks](qualityChecks.md) runs the
enabled checks in parallel, as configured by `cfg.quality`.

- **Sonar:** [scanSonar](scanSonar.md), [sonarProperties](sonarProperties.md), [sonarWaitForGate](sonarWaitForGate.md) (see also [appSonarProps](../pipelines/appSonarProps.md)).
- **Trivy:** [scanTrivy](scanTrivy.md), [trivySummary](trivySummary.md).
- **Other scanners:** [scanSecrets](scanSecrets.md), [scanIac](scanIac.md), [scanDependencies](scanDependencies.md).
- **Coverage:** [checkCoverage](checkCoverage.md), [coveragePercent](coveragePercent.md).
- **Reports:** [archiveLintReports](archiveLintReports.md), [postScanSummary](postScanSummary.md).

```yaml
quality:
  sonar: true
  failOnQualityGate: true
  trivy: true
  trivyFailOn: [HIGH, CRITICAL]
  secretScan: true
  dependencyCheck: false
  minCoverage: 75
```

## Quick reference

| Step | Syntax | Returns |
|---|---|---|
| [qualityChecks](qualityChecks.md) | `parallel qualityChecks(cfg)` | `Map` of closures |
| [scanTrivy](scanTrivy.md) | `scanTrivy(cfg: cfg, target: '.', type: 'fs')` | — |
| [coveragePercent](coveragePercent.md) | `coveragePercent(cfg)` | e.g. `82.4`, or `-1` |
| [postScanSummary](postScanSummary.md) | `postScanSummary(cfg, [Trivy: 'clean'])` | — |
