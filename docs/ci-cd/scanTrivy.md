# scanTrivy

Trivy scan of a filesystem or a container image.

## Signature

```groovy
def call(Map args)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `args.cfg` | `Map` | Pipeline config; reads `cfg.quality.trivyFailOn` / `trivyIgnoreUnfixed`. |
| `args.target` | `String` | Path or image ref to scan (default `.`). |
| `args.type` | `String` | `fs` or `image` (default `fs`). |

## Returns

Nothing. Archives `trivy-<type>.*` and throws on a vulnerability at
`trivyFailOn` severity.

## Usage

```groovy
scanTrivy(cfg: cfg, target: '.', type: 'fs')
scanTrivy(cfg: cfg, target: 'ghcr.io/acme/api:1.4.0', type: 'image')
```

!!! note
    Reports are written before the pass/fail scan runs, so a failing scan
    still leaves something readable behind rather than only an exit code.
    The one-line summary comes from [trivySummary](trivySummary.md).

Called from [qualityChecks](qualityChecks.md) (filesystem scan) when
`cfg.quality.trivy` is `true` and the repo isn't infra (see
[scanIac](scanIac.md)); the image variant is called separately after a build.

## Source

[`vars/scanTrivy.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanTrivy.groovy)
