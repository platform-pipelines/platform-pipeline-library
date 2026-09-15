# Quickstart

Three steps take a repo from nothing to a full CI/CD pipeline.

## 1. Add a Jenkinsfile

```groovy
@Library('platform-pipeline@main') _
standardPipeline()
```

Create a **Multibranch Pipeline** job for the repo. Other entry points exist for
promotion-only and drift-check jobs; see [Pipelines](../guides/pipelines.md).

## 2. Add `.ci/config.yaml`

The smallest valid config is:

```yaml
appName: orders-api
buildTool: python          # maven | gradle | npm | python | go | docker-only | terraform | cloudformation
imageRepo: ghcr.io/acme/orders-api
```

Only `appName`, `buildTool` and (for container builds) `imageRepo` are
required. Everything else has a default. The
[Configuration guide](../guides/configuration.md) lists every key.

## 3. Run the build and read the Init stage

[`configLoad`](../reference/config/configLoad.md) reads the file, layers it over
[`configDefaults`](../reference/config/configDefaults.md), and validates it with
[`configValidate`](../reference/config/configValidate.md).

- **A misspelled key** is logged with a "did you mean" hint by
  [`configUnknownKeys`](../reference/config/configUnknownKeys.md).
- **An invalid value** (an unsupported `buildTool`, a missing `imageRepo`, ...)
  fails the build, and every problem is listed at once.
- **The first lines of Init** summarise what the build will do: deploy strategy,
  image builder, lint mode, enabled gates, coverage floor, and the environments
  this branch reaches.

## Next

- Copy a complete, working repo from the [Examples](examples.md).
- Learn the conventions used on every step page in
  [Reading the step reference](reading-the-reference.md).
