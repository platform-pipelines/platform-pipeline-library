# shellQuote

Single-quotes a string for safe use inside a shell command. The `'\''` dance
is how you embed a single quote inside single quotes.

!!! note "Use this anywhere a config-controlled value reaches `sh`"
    Any step that interpolates a caller- or config-controlled value into a
    shell command should wrap it with `shellQuote` first — see
    [`githubApiRequest`](githubApiRequest.md), [`githubFetchFile`](githubFetchFile.md),
    [`assumeAwsRole`](../cloud/assumeAwsRole.md), and [`cfnChangeSet`](../cloud/cfnChangeSet.md)
    for examples of exactly this pattern closing a shell-injection gap.

## Syntax

```groovy
shellQuote(String s)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `s` | `String` | yes | — | Value to pass to the shell as exactly one argument. |

## Returns

`s` wrapped in single quotes, with each embedded `'` rewritten as `'\''`.

## Examples

| Input | Output | The shell sees |
|---|---|---|
| `orders-api` | `'orders-api'` | `orders-api` |
| `my stack` | `'my stack'` | `my stack` (one argument) |
| `it's` | `'it'\''s'` | `it's` |
| `x; rm -rf /` | `'x; rm -rf /'` | `x; rm -rf /` (literal text, not a command) |
| `$HOME` | `'$HOME'` | `$HOME` (not expanded) |

```groovy
def url = "https://api.github.com/repos/acme/orders-api/contents/${path}?ref=${branch}"
sh "curl -sS ${shellQuote(url)}"

def stack = envCfg.stackName          // from config — never trust it in a shell
sh "aws cloudformation describe-stacks --stack-name ${shellQuote(stack)}"
```

## Source

[`vars/shellQuote.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/shellQuote.groovy)
