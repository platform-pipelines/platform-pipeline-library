# logBanner

Visual stage separator. Makes long console logs scannable.

## Signature

```groovy
def call(String title)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `title` | `String` | Text to print, boxed between two rule lines. |

## Returns

Nothing — prints the banner to the build log.

## Usage

```groovy
logBanner 'Initialise'
```

## Source

[`vars/logBanner.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logBanner.groovy)
