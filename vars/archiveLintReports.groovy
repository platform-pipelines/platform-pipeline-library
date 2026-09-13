// Archives whatever lint report this toolchain produced.
//
// Centralised so each lint step only has to know how to run its linter, not
// how Jenkins stores artifacts.
def call(Map cfg) {
    def report = appLintReport(cfg)
    if (report) {
        archiveArtifacts artifacts: report, allowEmptyArchive: true
    }
}
