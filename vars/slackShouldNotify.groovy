// Notification policy.
//
// 'change' is the default because a channel that goes green every twenty
// minutes trains people to ignore it, including when it goes red.
def call(String policy, String status) {
    switch (policy) {
        case 'always':  return true
        case 'failure': return status in ['FAILURE', 'UNSTABLE']
        case 'change':  return currentBuild.previousBuild?.result != status
        default:        return true
    }
}
