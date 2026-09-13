// Visual stage separator. Makes long console logs scannable.
//
// Usage:
//   logBanner 'Initialise'
// Params: title (String) - text to print, boxed between two rule lines
def call(String title) {
    echo "\n${'=' * 68}\n  ${title}\n${'=' * 68}"
}
