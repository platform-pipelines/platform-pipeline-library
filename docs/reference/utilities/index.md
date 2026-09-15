# Utilities

## Logging

[logInfo](logInfo.md), [logDebug](logDebug.md), [logWarn](logWarn.md),
[logError](logError.md), [logBanner](logBanner.md), [logAudit](logAudit.md),
[logActor](logActor.md). Every step logs through one of these instead of
calling `println`/`echo` directly, so log output looks the same everywhere.

```groovy
logBanner 'Smoke test'
logInfo  "version=${env.APP_VERSION}"           // [INFO]  version=1.4.0
logWarn  'retrying once'                        // [WARN]  retrying once
logDebug 'only with PIPELINE_DEBUG=true'        // [DEBUG] ...
logAudit('smoke.passed', [environment: 'dev'])  // [AUDIT] + a line in .ci-audit.jsonl
```

## Versioning

- [versionResolve](versionResolve.md): the main entry point; derives a version string from the branch and git.
- [versionBase](versionBase.md), [versionShortSha](versionShortSha.md): the pieces `versionResolve` combines.
- [versionSlug](versionSlug.md): makes an arbitrary branch name safe for an image tag.
- [versionImageTag](versionImageTag.md): the OCI-tag-safe form of the version.

| Branch | `versionResolve()` (tag `v1.4.0`, build 42, sha `ab12cd3`) |
|---|---|
| `main` | `1.4.0` |
| `release/1.4` | `1.4.0-rc.42` |
| `hotfix/login` | `1.4.0-hotfix.42.gab12cd3` |
| `feature/login` | `1.4.0-feature-login.42.gab12cd3` |

## Shell and scripts

- [shellQuote](shellQuote.md): single-quotes a string for safe use inside a shell command. Use it anywhere a config-controlled value reaches `sh`.
- [useScript](useScript.md): writes a bundled `resources/` script to the workspace and returns its path.

```groovy
sh "aws cloudformation describe-stacks --stack-name ${shellQuote(envCfg.stackName)}"
sh "python3 ${useScript('trivy_summary.py')} trivy-fs.json"
```
