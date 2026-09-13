// Which environments the given branch is permitted to reach.
def call(Map cfg, String branch) {
    if (!branch) { return [] }
    cfg.environments.findAll { e -> branch ==~ configGlobToRegex(e.branchPattern) }
}
