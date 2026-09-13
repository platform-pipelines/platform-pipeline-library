// Which environments the given branch is permitted to reach.
//
// Usage:
//   def envs = configEnvironmentsFor(cfg, env.BRANCH_NAME)
// Params: cfg (Map) - pipeline config; reads cfg.environments
//         branch (String) - branch name to match against each environment's branchPattern
// Returns: List of environment config Maps the branch is permitted to reach
def call(Map cfg, String branch) {
    if (!branch) { return [] }
    cfg.environments.findAll { e -> branch ==~ configGlobToRegex(e.branchPattern) }
}
