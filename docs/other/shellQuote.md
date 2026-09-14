# shellQuote

Single-quotes a string for safe use inside a shell command. The `'\''` dance
is how you embed a single quote inside single quotes.

!!! note "Use this anywhere a config-controlled value reaches `sh`"
    Any step that interpolates a caller- or config-controlled value into a
    shell command should wrap it with `shellQuote` first — see
    [`githubApiRequest`](githubApiRequest.md), [`githubFetchFile`](githubFetchFile.md),
    [`assumeAwsRole`](../cloud/assumeAwsRole.md), and [`cfnChangeSet`](../cloud/cfnChangeSet.md)
    for examples of exactly this pattern closing a shell-injection gap.

## Signature

```groovy
def call(String s)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `s` | `String` | Value to make safe as a single shell argument. |

## Returns

`s` wrapped in single quotes, with any embedded quote escaped.

## Usage

```groovy
sh "curl ${shellQuote(url)}"
```

## Source

[`vars/shellQuote.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/shellQuote.groovy)
