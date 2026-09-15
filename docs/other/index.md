# Other

Configuration loading, GitHub API integration, Slack notifications, logging,
versioning, and a couple of small shared utilities. These are the steps
everything else in [CI/CD](../ci-cd/index.md) and [Cloud](../cloud/index.md)
is built on top of.

## Configuration

- [configLoad](configLoad.md) — reads `.ci/config.yaml`, merges over [configDefaults](configDefaults.md), applies overrides, validates, returns a plain `Map`.
- [configDefaults](configDefaults.md), [configEnvDefaults](configEnvDefaults.md) — the baseline every repo inherits (the full default config is on the `configDefaults` page).
- [configMerge](configMerge.md) — recursive map merge (right side wins).
- [configValidate](configValidate.md) — collects every config problem in one pass instead of failing on the first.
- [configUnknownKeys](configUnknownKeys.md), [configClosestKey](configClosestKey.md) — warn about typos in config, with a "did you mean" hint.
- [configDeprecatedKeys](configDeprecatedKeys.md) — keys that moved (e.g. `extra.nexusRepo` → `publish.nexusRepo`) and still work with a warning.
- [configSupportedTools](configSupportedTools.md), [configImageBuilders](configImageBuilders.md), [configDeployStrategies](configDeployStrategies.md) — the allowlists for `buildTool`, `imageBuilder` and `deployStrategy`; see [configInfraTools](../cloud/configInfraTools.md) (in Cloud) for the infra-vs-app subset.
- [configEnvironmentsFor](configEnvironmentsFor.md), [configGlobToRegex](configGlobToRegex.md) — which declared environments a given branch reaches.

```groovy
def cfg  = configLoad()                                  // merged + validated Map
def envs = configEnvironmentsFor(cfg, env.BRANCH_NAME)   // e.g. [[name: 'dev', ...], [name: 'prod', ...]]
```

## GitHub integration

- [githubApiRequest](githubApiRequest.md) — the one curl-based REST call every other GitHub write step builds on.
- [githubApiUrl](githubApiUrl.md), [githubCredentialsId](githubCredentialsId.md), [githubRepoSlug](githubRepoSlug.md), [githubPackagesRepo](githubPackagesRepo.md) — small config lookups.
- [githubFetchFile](githubFetchFile.md), [githubCommitFile](githubCommitFile.md), [githubFileSha](githubFileSha.md) — read/write a single file in any repo.
- [githubSetStatus](githubSetStatus.md) — commit status checks.
- [githubComment](githubComment.md), [githubUpsertComment](githubUpsertComment.md), [githubFindComment](githubFindComment.md) — PR comments; `githubUpsertComment` edits its own previous comment instead of piling up duplicates.

```groovy
githubSetStatus('ci/contract-tests', 'success', 'All contracts pass')
githubUpsertComment('<!-- my-team:perf -->', '### p95 latency: 212 ms')
def text = githubFetchFile(repo: 'acme/gitops-manifests', path: 'apps/orders-api/prod/kustomization.yaml')
```

| Setting | Default | Override with |
|---|---|---|
| API base URL | `https://api.github.com` | `GITHUB_API_URL` env var |
| Token credential | `github-token` | `GITHUB_CREDENTIALS_ID` env var |

## Notifications

- [notifySlack](notifySlack.md) — the entry point `standardPipeline` calls.
- [slackShouldNotify](slackShouldNotify.md) — decides whether this build result is worth a message per `cfg.notify.on`.
- [slackPayload](slackPayload.md) — builds the message body.

```yaml
notify:
  slackChannel: "#orders-ci"
  on: change           # always | failure | change
```

## Logging

[logInfo](logInfo.md), [logDebug](logDebug.md), [logWarn](logWarn.md),
[logError](logError.md), [logBanner](logBanner.md), [logAudit](logAudit.md),
[logActor](logActor.md) — every step's output goes through one of these
instead of `println`/`echo` directly, so log shape stays consistent.

```groovy
logBanner 'Smoke test'
logInfo  "version=${env.APP_VERSION}"           // [INFO]  version=1.4.0
logWarn  'retrying once'                        // [WARN]  retrying once
logDebug 'only with PIPELINE_DEBUG=true'        // [DEBUG] ...
logAudit('smoke.passed', [environment: 'dev'])  // [AUDIT] + a line in .ci-audit.jsonl
```

## Versioning

- [versionResolve](versionResolve.md) — the main entry point; derives a version string from the branch and git.
- [versionBase](versionBase.md), [versionShortSha](versionShortSha.md) — the pieces `versionResolve` combines.
- [versionSlug](versionSlug.md) — makes an arbitrary branch name safe for an image tag.
- [versionImageTag](versionImageTag.md) — the OCI-tag-safe form of the version.

| Branch | `versionResolve()` (tag `v1.4.0`, build 42, sha `ab12cd3`) |
|---|---|
| `main` | `1.4.0` |
| `release/1.4` | `1.4.0-rc.42` |
| `hotfix/login` | `1.4.0-hotfix.42.gab12cd3` |
| `feature/login` | `1.4.0-feature-login.42.gab12cd3` |

## Utilities

- [shellQuote](shellQuote.md) — single-quotes a string for safe use inside a shell command; used anywhere a config-controlled value reaches `sh`.
- [useScript](useScript.md) — writes a bundled `resources/` script to the workspace and returns its path.

```groovy
sh "aws cloudformation describe-stacks --stack-name ${shellQuote(envCfg.stackName)}"
sh "python3 ${useScript('trivy_summary.py')} trivy-fs.json"
```
