// Produces a CycloneDX SBOM for the built image and attaches it to the build.
//
// Trivy generates it, so no extra tool is needed. An SBOM is what lets you
// answer "are we affected by this CVE" in minutes across every deployed
// service, instead of rebuilding each one to find out.
//
// Usage:
//   generateSbom(cfg)
// Params: cfg (Map) - pipeline config; cfg.quality.sbom, cfg.imageRepo are read
// Returns: nothing; archives sbom.cdx.json and logs a component summary
def call(Map cfg) {
    if (!cfg.quality.sbom) {
        logDebug 'SBOM generation disabled'
        return
    }

    logBanner 'SBOM'

    def image = "${cfg.imageRepo}:${env.IMAGE_TAG}"

    inToolContainer('aquasec/trivy:latest') {
        sh "trivy image --format cyclonedx --output sbom.cdx.json --no-progress ${image}"
    }

    archiveArtifacts artifacts: 'sbom.cdx.json', allowEmptyArchive: false, fingerprint: true

    def components = sh(
        script: "python3 ${useScript('sbom_summary.py')} sbom.cdx.json",
        returnStdout: true
    ).trim()

    logInfo "SBOM: ${components}"
    logAudit('sbom.generated', [image: image, components: components])
}
