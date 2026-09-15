# GitHub & Slack

## GitHub

- [githubApiRequest](githubApiRequest.md): the curl-based REST call that every other GitHub write step builds on.
- [githubApiUrl](githubApiUrl.md), [githubCredentialsId](githubCredentialsId.md), [githubRepoSlug](githubRepoSlug.md): small config lookups.
- [githubFetchFile](githubFetchFile.md), [githubCommitFile](githubCommitFile.md), [githubFileSha](githubFileSha.md): read or write a single file in any repo.
- [githubSetStatus](githubSetStatus.md): commit status checks.
- [githubRelease](githubRelease.md), [githubUploadReleaseAsset](githubUploadReleaseAsset.md): find or create the release for a tag and attach files to it, replacing same-named assets. [publishArtifact](../deploy/publishArtifact.md) uses both.
- [githubComment](githubComment.md), [githubUpsertComment](githubUpsertComment.md), [githubFindComment](githubFindComment.md): PR comments. `githubUpsertComment` edits its own previous comment instead of adding duplicates.

```groovy
githubSetStatus('ci/contract-tests', 'success', 'All contracts pass')
githubUpsertComment('<!-- my-team:perf -->', '### p95 latency: 212 ms')
def text = githubFetchFile(repo: 'acme/gitops-manifests', path: 'apps/orders-api/prod/kustomization.yaml')
def release = githubRelease(repo: 'acme/orders-api', tag: 'v1.4.0')
githubUploadReleaseAsset(release: release, path: 'dist/orders_api-1.4.0.tar.gz')
```

| Setting | Default | Override with |
|---|---|---|
| API base URL | `https://api.github.com` | `GITHUB_API_URL` env var |
| Token credential | `github-token` | `GITHUB_CREDENTIALS_ID` env var |

## Slack

- [notifySlack](notifySlack.md): the entry point `standardPipeline` calls.
- [slackShouldNotify](slackShouldNotify.md): decides from `cfg.notify.on` whether this build result is worth a message.
- [slackPayload](slackPayload.md): builds the message body.

```yaml
notify:
  slackChannel: "#orders-ci"
  on: change           # always | failure | change
```
