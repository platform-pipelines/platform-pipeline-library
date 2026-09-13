// Same value as versionResolve, constrained to characters legal in an OCI tag.
def call(String branch = null) {
    versionResolve(branch ?: env.BRANCH_NAME).replaceAll(/[^A-Za-z0-9._-]/, '-')
}
