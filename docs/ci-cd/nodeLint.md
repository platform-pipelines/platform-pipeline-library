# nodeLint

ESLint, Prettier, and a TypeScript typecheck when `tsconfig.json` is present.

!!! note "Type errors always fail"
    TypeScript type errors fail the build regardless of `cfg.lint.failOnError`
    — shipping a build that doesn't typecheck isn't a style preference.

## Syntax

```groovy
nodeLint(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `lint.autoFormat` | `false` | `true` | Runs `prettier --write` and `eslint --fix` first. |
| `lint.failOnError` | `true` | `false` | Controls ESLint/Prettier failures only. |

## Returns

Nothing. Writes `eslint-report.xml` (JUnit, converted from ESLint's JSON).
Fails the build:

| Check | Fails when | Message |
|---|---|---|
| `tsc --noEmit` (only with `tsconfig.json`) | any type error — always | `TypeScript type errors` |
| `eslint .` / `prettier --check .` | problems and `failOnError: true` | `ESLint or Prettier reported problems. Run: npx eslint . --fix && npx prettier --write .` |

## Examples

```yaml
# .ci/config.yaml — from examples/node-service
buildTool: npm
lint:
  failOnError: true
  autoFormat: true
```

```groovy
nodeLint(cfg)
```

Runs:

```bash
npm ci --prefer-offline --no-audit --fund=false
npx prettier --write . || true           # autoFormat only
npx eslint . --fix || true               # autoFormat only
npx eslint . --format json | python3 .ci-scripts/eslint_to_junit.py > eslint-report.xml || true
npx eslint .
npx prettier --check .
npx tsc --noEmit                         # when tsconfig.json exists
```

ESLint, Prettier and TypeScript come from the project's own
`devDependencies`:

```json
{
  "devDependencies": {
    "eslint": "^9.20.0",
    "prettier": "^3.5.0",
    "typescript": "^5.7.0"
  }
}
```

A repo adopting Prettier gradually:

```yaml
lint:
  failOnError: false     # ESLint/Prettier report only; type errors still fail
```

## How it fits

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'npm'`. Installs
dependencies via [nodeInstall](nodeInstall.md) first.

## Source

[`vars/nodeLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodeLint.groovy)
