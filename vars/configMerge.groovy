// Recursive map merge, right side wins.
//
// Lists replace rather than concatenate: when someone writes
// trivyFailOn: [CRITICAL] they mean "only critical", not "critical as well as
// the defaults".
//
// Usage:
//   def merged = configMerge(configDefaults(), rawYamlConfig)
// Params: left (Map) - base map (loses on conflicts)
//         right (Map) - overriding map (wins on conflicts); nested Maps merge recursively, other values replace
// Returns: a new Map; left and right are not mutated
//
// @NonCPS: pure data transformation, no pipeline steps — recursion under the
// CPS transform is far more expensive and more prone to stack issues than
// plain recursive calls.
import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def call(Map left, Map right) {
    def out = [:] + left
    right.each { k, v ->
        if (v instanceof Map && out[k] instanceof Map) {
            // Bare call(...) here would resolve to Closure.call() (invoking
            // this .each closure itself, with out[k]/v silently rebound to
            // its own k/v params) rather than recursing into this method —
            // this.call(...) is required to actually merge sub-maps.
            out[k] = this.call(out[k], v)
        } else if (v != null) {
            out[k] = v
        }
    }
    return out
}
