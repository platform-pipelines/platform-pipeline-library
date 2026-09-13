// Puts the plan summary on the PR so reviewers see the blast radius without
// digging through build logs.
def call(Map cfg, Map envCfg, String summary) {
    if (!env.CHANGE_ID) { return }

    def destructive = summary?.contains('DESTRUCTIVE') || summary?.contains('REPLACEMENT')
    def heading = destructive ? '### Infrastructure plan — DESTRUCTIVE CHANGES' : '### Infrastructure plan'

    def body = """${heading}

**${envCfg.name}**: ${summary}

${destructive ? 'Resources will be destroyed or recreated. Check that state loss is expected before approving.\n' : ''}
Version `${env.APP_VERSION}` · commit `${env.GIT_SHORT_SHA}` · [full plan](${env.BUILD_URL}artifact/)"""

    githubUpsertComment("<!-- company-pipeline:plan:${envCfg.name} -->", body)
}
