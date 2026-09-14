# scanIac

Security scan over infrastructure code. Trivy's config scanner covers
Terraform and CloudFormation (it absorbed tfsec), so this reuses a tool
already in the toolbox rather than adding one.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.workingDir` and `cfg.quality.trivyFailOn`. |

## Returns

Nothing. Archives `trivy-iac.*` and throws on a misconfiguration at
`trivyFailOn` severity.

## Usage

```groovy
scanIac(cfg)
```

Called from [qualityChecks](qualityChecks.md) instead of
[scanTrivy](scanTrivy.md) when [isInfraRepo](../cloud/isInfraRepo.md) is
true — infra repos have no dependency tree to scan, so what matters is
whether the declared resources are misconfigured.

## Source

[`vars/scanIac.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanIac.groovy)
