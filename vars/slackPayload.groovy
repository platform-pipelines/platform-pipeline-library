// Builds the Slack attachment body.
def call(Map cfg, String status) {
    def colours = [SUCCESS: '#2eb886', FAILURE: '#cc0000', UNSTABLE: '#e8a317', ABORTED: '#808080']
    def icons   = [SUCCESS: ':white_check_mark:', FAILURE: ':x:', UNSTABLE: ':warning:', ABORTED: ':black_square_for_stop:']

    def fields = [
        [title: 'Version',     value: env.APP_VERSION ?: 'n/a', short: true],
        [title: 'Branch',      value: env.BRANCH_NAME ?: 'n/a', short: true],
        [title: 'Duration',    value: currentBuild.durationString?.replace(' and counting', '') ?: 'n/a', short: true],
        [title: 'Triggered by', value: logActor(), short: true],
    ]

    if (env.DEPLOY_APPROVER) {
        fields << [title: 'Approved by', value: env.DEPLOY_APPROVER, short: true]
    }

    return [
        channel: cfg.notify.slackChannel,
        attachments: [[
            color     : colours[status] ?: '#808080',
            fallback  : "${cfg.appName}: ${status}",
            title     : "${icons[status] ?: ''} ${cfg.appName} — ${status}",
            title_link: env.BUILD_URL,
            fields    : fields,
            footer    : "Jenkins · build #${env.BUILD_NUMBER}",
        ]]
    ]
}
