// Same value as versionResolve, constrained to characters legal in an OCI tag.
//
// Usage:
//   def tag = versionImageTag()
// Params: branch (String) - branch to resolve (default env.BRANCH_NAME)
// Returns: versionResolve's value with any illegal OCI-tag character replaced by '-'
def call(String branch = null) {
    versionResolve(branch ?: env.BRANCH_NAME).replaceAll(/[^A-Za-z0-9._-]/, '-')
}
