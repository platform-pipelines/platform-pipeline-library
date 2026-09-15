// Uploads one file as an asset on a GitHub release. Asset names are unique per
// release, so an asset already there under the same name is deleted first —
// otherwise rebuilding a version would fail on the duplicate.
//
// Usage:
//   def release = githubRelease(repo: 'acme/api', tag: 'v1.4.0')
//   githubUploadReleaseAsset(release: release, path: 'dist/api')
// Params: args.release (Map) - release from githubRelease; upload_url and assets are read
//         args.path (String) - file to upload
//         args.name (String) - asset name (default: the last segment of path)
// Returns: nothing; errors if deleting the old asset or uploading fails
def call(Map args) {
    def release = args.release
    def path    = args.path
    def name    = args.name ?: path.tokenize('/').last()

    if (!release?.upload_url) { error 'githubUploadReleaseAsset: release has no upload_url (pass the Map githubRelease returns)' }

    // upload_url is a URI template: .../releases/7/assets{?name,label}
    def uploadUrl = release.upload_url.replaceAll(/\{.*\}$/, '')
    def existing  = (release.assets ?: []).find { it.name == name }

    withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        if (existing) {
            sh """curl -sS --fail -o /dev/null -X DELETE -H "Authorization: Bearer \$GH_TOKEN" ${shellQuote(existing.url)}"""
            logInfo "Replacing ${name}"
        }
        sh """
            curl -sS --fail -o /dev/null -X POST \\
              -H "Authorization: Bearer \$GH_TOKEN" \\
              -H "Content-Type: application/octet-stream" \\
              --data-binary @${shellQuote(path)} \\
              ${shellQuote("${uploadUrl}?name=${URLEncoder.encode(name, 'UTF-8')}")}
        """
    }
}
