# configDefaults

Baseline every consuming repo inherits. Anything not set in `.ci/config.yaml`
comes from here, so adding a new capability with a safe default does not
require touching every repo.

This map is also the schema: [`configUnknownKeys`](configUnknownKeys.md) warns
about any key a repo writes that does not appear here. A new key therefore has
to be added here, even when its default is `null`. `extra` is the one
free-form section — the pipeline never reads or checks it.

## Syntax

```groovy
configDefaults()
```

## Parameters

None.

## Returns

A `Map` of every recognized top-level config key with its default value.
`null` means "no default — set it yourself if you need it".

## Full default config

The same map, written as the YAML a repo would write. Anything you leave out
of `.ci/config.yaml` takes the value shown here.

```yaml
appName: null               # required
buildTool: null             # required — see configSupportedTools
runtimeVersion: null        # picks the fallback tool image tag, e.g. "3.12"
imageRepo: null             # required when containerize: true
containerize: true          # forced to false for terraform / cloudformation
dockerfile: Dockerfile
imageBuilder: kaniko-docker # kaniko-docker | kaniko-k8s | buildah
gitopsRepo: null            # required for gitops environments, e.g. acme/gitops-manifests
gitopsBranch: main
deployStrategy: null        # derived: gitops for apps, buildTool for infra

infra:
  workingDir: .
  varFiles: []
  backendConfig: null
  policyDir: null
  region: us-east-1
  assumeRole: null
  awsCredentialsId: aws-credentials
  template: template.yaml
  templates: null
  artifactBucket: null
  capabilities: [CAPABILITY_IAM, CAPABILITY_NAMED_IAM]

environments: []            # each entry defaults from configEnvDefaults

lint:
  enabled: true
  failOnError: true
  autoFormat: false

quality:
  sonar: true
  sonarProjectKey: null     # defaults to appName
  sonarSources: .
  sonarExclusions: "**/node_modules/**,**/target/**,**/build/**,**/dist/**"
  failOnQualityGate: true
  trivy: true
  trivyFailOn: [HIGH, CRITICAL]
  trivyIgnoreUnfixed: true
  secretScan: true
  dependencyCheck: false
  dependencyCheckCvss: 7
  minCoverage: null         # coverage gate off
  sbom: true
  signImage: false

publish:
  nexusRepo: null           # publishing skipped when unset

approval:
  allowSelfApproval: false

notify:
  slackChannel: null        # Slack off when unset
  on: change                # always | failure | change
  githubChecks: true
  emails: []

extra: {}                   # free-form, never read or checked
```

## Examples

```groovy
def defaults = configDefaults()

defaults.imageBuilder             // → 'kaniko-docker'
defaults.quality.trivyFailOn      // → ['HIGH', 'CRITICAL']
defaults.infra.region             // → 'us-east-1'
defaults.containsKey('extra')     // → true
```

Layering a repo's YAML on top (what [`configLoad`](configLoad.md) does):

```groovy
def raw = readYaml(file: '.ci/config.yaml')        // [appName: 'orders-api', quality: [minCoverage: 75]]
def cfg = configMerge(configDefaults(), raw)

cfg.quality.minCoverage   // → 75      (from the repo)
cfg.quality.sonar         // → true    (from the defaults)
```

## How it fits

Called by [`configLoad`](configLoad.md), which merges it under whatever a
repo's `.ci/config.yaml` declares via [`configMerge`](configMerge.md). See
[`configEnvDefaults`](configEnvDefaults.md) for the per-environment
equivalent.

## Source

[`vars/configDefaults.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configDefaults.groovy)
