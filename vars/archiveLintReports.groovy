// Archives whatever lint report this toolchain produced.
//
// Centralised so each lint step only has to know how to run its linter, not
// how Jenkins stores artifacts.
//
// Usage:
//   archiveLintReports(cfg)
// Params: cfg (Map) - pipeline config; passed through to appLintReport(cfg)
// Returns: nothing; archives the report artifact if one exists
def call(Map cfg) {
    def report = appLintReport(cfg)
    if (report) {
        archiveArtifacts artifacts: report, allowEmptyArchive: true
    }
}
