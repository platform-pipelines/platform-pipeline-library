// Branch name to a token safe for versions and image tags.
//
// Bounded to 30 characters so a long branch name does not produce an
// unreadable tag, then re-trimmed because truncation can land on a separator
// and a trailing dash is illegal in an OCI tag.
//
// Usage:
//   def slug = versionSlug('feature/Add_Login-Page')
// Params: s (String) - raw branch name
// Returns: lowercase, dash-separated, <=30 char slug; 'branch' if the input is all separators
def call(String s) {
    def out = s.toLowerCase()
               .replaceAll(/[^a-z0-9]+/, '-')
               .replaceAll(/^-+|-+$/, '')
               .take(30)
               .replaceAll(/-+$/, '')

    // A name made entirely of separators would otherwise yield an empty
    // segment and produce a version like "1.4.0-.42.gabc123".
    return out ?: 'branch'
}
