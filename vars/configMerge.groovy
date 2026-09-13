// Recursive map merge, right side wins.
//
// Lists replace rather than concatenate: when someone writes
// trivyFailOn: [CRITICAL] they mean "only critical", not "critical as well as
// the defaults".
def call(Map left, Map right) {
    def out = [:] + left
    right.each { k, v ->
        if (v instanceof Map && out[k] instanceof Map) {
            out[k] = call(out[k], v)
        } else if (v != null) {
            out[k] = v
        }
    }
    return out
}
