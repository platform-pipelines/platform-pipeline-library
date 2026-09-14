# Other

Configuration loading, GitHub API integration, Slack notifications, logging,
versioning, and a couple of small shared utilities. These are the steps
everything else in [CI/CD](../ci-cd/index.md) and [Cloud](../cloud/index.md)
is built on top of.

## Configuration

- [configLoad](configLoad.md) — reads `.ci/config.yaml`, merges over [configDefaults](configDefaults.md), applies overrides, validates, returns a plain `Map`.
- [configDefaults](configDefaults.md), [configEnvDefaults](configEnvDefaults.md) — the baseline every repo inherits.
- [configMerge](configMerge.md) — recursive map merge (right side wins).
- [configValidate](configValidate.md) — collects every config problem in one pass instead of failing on the first.
- [configSupportedTools](configSupportedTools.md) — the `buildTool` allowlist; see [configInfraTools](../cloud/configInfraTools.md) (in Cloud) for the infra-vs-app subset.
- [configEnvironmentsFor](configEnvironmentsFor.md), [configGlobToRegex](configGlobToRegex.md) — which declared environments a given branch reaches.

## GitHub integration

- [githubApiRequest](githubApiRequest.md) — the one curl-based REST call every other GitHub step builds on.
- [githubApiUrl](githubApiUrl.md), [githubCredentialsId](githubCredentialsId.md), [githubRepoSlug](githubRepoSlug.md) — small config lookups.
- [githubFetchFile](githubFetchFile.md), [githubCommitFile](githubCommitFile.md), [githubFileSha](githubFileSha.md) — read/write a single file in any repo.
- [githubSetStatus](githubSetStatus.md) — commit status checks.
- [githubComment](githubComment.md), [githubUpsertComment](githubUpsertComment.md), [githubFindComment](githubFindComment.md) — PR comments; `githubUpsertComment` edits its own previous comment instead of piling up duplicates.

## Notifications

- [notifySlack](notifySlack.md) — the entry point `standardPipeline` calls.
- [slackShouldNotify](slackShouldNotify.md) — decides whether this build state is worth a message per `cfg.notify.on`.
- [slackPayload](slackPayload.md) — builds the Block Kit message body.

## Logging

[logInfo](logInfo.md), [logDebug](logDebug.md), [logWarn](logWarn.md),
[logError](logError.md), [logBanner](logBanner.md), [logAudit](logAudit.md),
[logActor](logActor.md) — every step's output goes through one of these
instead of `println`/`echo` directly, so log shape stays consistent.

## Versioning

- [versionResolve](versionResolve.md) — the main entry point; derives a version string from the branch and git.
- [versionBase](versionBase.md), [versionShortSha](versionShortSha.md) — the pieces `versionResolve` combines.
- [versionSlug](versionSlug.md) — makes an arbitrary branch name safe for an image tag.
- [versionImageTag](versionImageTag.md) — validates a string is a legal OCI tag.

## Utilities

- [shellQuote](shellQuote.md) — single-quotes a string for safe use inside a shell command; used anywhere a config-controlled value reaches `sh`.
- [useScript](useScript.md) — writes a bundled `resources/` script to the workspace and returns its path.
