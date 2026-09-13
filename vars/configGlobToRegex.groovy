// Converts a branchPattern glob into a regex.
//
// Only * is supported. Full regex in config invites patterns nobody can
// reason about at 2am, and a wrong one silently deploys the wrong branch.
//
// Note that * spans slashes: 'feature/*' matches 'feature/a/b' as well as
// 'feature/a'. That is intentional — nested feature branches should reach the
// same environments as flat ones.
def call(String glob) {
    glob.replace('.', '\\.').replace('*', '.*')
}
