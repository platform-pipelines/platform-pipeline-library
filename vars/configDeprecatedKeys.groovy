// Config keys that moved, mapped old path -> new path.
//
// configLoad copies an old key's value to its new home (when the new key is
// not set explicitly) and logs a deprecation warning, so a repo keeps working
// while it migrates. Renaming a key later means adding one line here.
//
// Usage:
//   configDeprecatedKeys().each { oldPath, newPath -> ... }
// Returns: Map of deprecated dotted key path -> replacement dotted key path
def call() {
    [
        'extra.nexusRepo'          : 'publish.nexusRepo',
        'extra.sonarSources'       : 'quality.sonarSources',
        'extra.sonarExclusions'    : 'quality.sonarExclusions',
        'extra.dependencyCheckCvss': 'quality.dependencyCheckCvss',
        'extra.allowSelfApproval'  : 'approval.allowSelfApproval',
    ]
}
