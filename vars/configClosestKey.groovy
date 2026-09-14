// The candidate a mistyped key was most likely meant to be, or null.
//
// Plain edit distance, case-insensitive, capped so that an unrelated key is
// not offered as a "suggestion" — a wrong hint is worse than none.
//
// Usage:
//   def hint = configClosestKey('minCoverge', ['minCoverage', 'sonar'])  // 'minCoverage'
// Params: key (String) - the unknown key as written
//         candidates (List) - the keys that are valid at that position
// Returns: the closest candidate String, or null if nothing is close enough
//
// @NonCPS: pure string arithmetic with nested loops; no pipeline steps.
import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def call(String key, List candidates) {
    def a = key.toLowerCase()
    def best = null
    def bestDistance = Integer.MAX_VALUE

    candidates.each { candidate ->
        def b = candidate.toString().toLowerCase()
        int[] prev = (0..b.length()) as int[]
        for (int i = 1; i <= a.length(); i++) {
            int[] cur = new int[b.length() + 1]
            cur[0] = i
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost)
            }
            prev = cur
        }
        if (prev[b.length()] < bestDistance) {
            bestDistance = prev[b.length()]
            best = candidate.toString()
        }
    }

    def allowed = Math.max(2, (int) (key.length() / 3))
    return bestDistance <= allowed ? best : null
}
