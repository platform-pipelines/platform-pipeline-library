// Who triggered this build.
//
// Walks the causes chain rather than reading BUILD_USER, which only exists
// if you install the build-user-vars plugin — deliberately not in plugins.txt.
def call() {
    try {
        def causes = currentBuild.getBuildCauses()
        def user = causes.find { it._class?.contains('UserIdCause') }
        if (user?.userId) { return user.userId }
        if (causes.find { it._class?.contains('SCMTrigger') })   { return 'scm-trigger' }
        if (causes.find { it._class?.contains('TimerTrigger') }) { return 'timer' }
        if (causes.find { it._class?.contains('BranchIndexing') }) { return 'branch-indexing' }
        return causes ? (causes[0].shortDescription ?: 'unknown') : 'unknown'
    } catch (ignored) {
        return 'unknown'
    }
}
