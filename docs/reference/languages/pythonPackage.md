# pythonPackage

Builds the project's sdist and wheel with PEP 517's `build`.

## Syntax

```groovy
pythonPackage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Package` step has the same signature. |

## Returns

Nothing. Writes `dist/*.whl` and `dist/*.tar.gz`, which
[appArtifacts](../pipelines/appArtifacts.md) (`dist/*`) archives and
[publishArtifact](../deploy/publishArtifact.md) uploads.

## Examples

```groovy
pythonPackage(cfg)
```

Runs:

```bash
pip install --no-cache-dir build
python -m build
```

With this `pyproject.toml`:

```toml
[build-system]
requires = ["setuptools>=75"]
build-backend = "setuptools.build_meta"

[project]
name = "orders-api"
version = "1.4.0"
```

the output is:

```
dist/orders_api-1.4.0-py3-none-any.whl
dist/orders_api-1.4.0.tar.gz
```

!!! note "Version"
    Unlike the JVM and Node package steps, this one doesn't pass
    `APP_VERSION` to the build — the version comes from `pyproject.toml` (or
    a dynamic-version plugin such as `setuptools-scm`).

## How it fits

Called by [packageApp](../pipelines/packageApp.md) when `cfg.buildTool == 'python'`.

## Source

[`vars/pythonPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/pythonPackage.groovy)
