// Keys in a repo's config that the library does not recognise.
//
// Without this a typo like `minCoverge: 80` is silently ignored and the
// default applies, so the gate the team thinks it turned on never runs. The
// result is a list of warnings (not errors) with a "did you mean" hint.
//
// Checked: top-level keys, one level inside each section (lint, quality,
// infra, ...), and the keys of each environments[] entry. Not checked:
// `extra` (free-form by design) and values of free-form maps such as
// environments[].parameters.
//
// Usage:
//   configUnknownKeys(readYaml(file: '.ci/config.yaml')).each { logWarn it }
// Params: raw (Map) - config as written by the repo, before defaults are merged in
// Returns: List of human-readable warning strings; empty when every key is recognised
def call(Map raw) {
    def warnings = []
    def defaults = configDefaults()
    def envDefaults = configEnvDefaults()
    def deprecated = configDeprecatedKeys().keySet()

    def report = { String path, String key, Collection valid ->
        def hint = configClosestKey(key, valid as List)
        warnings << ("unknown config key '${path}'" + (hint ? " — did you mean '${hint}'?" : ' — it is ignored')).toString()
    }

    (raw ?: [:]).each { key, value ->
        def k = key.toString()

        if (!defaults.containsKey(k)) {
            report(k, k, defaults.keySet())
        } else if (k == 'environments' && value instanceof List) {
            value.eachWithIndex { entry, i ->
                if (entry instanceof Map) {
                    entry.keySet().each { ek ->
                        if (!envDefaults.containsKey(ek.toString())) {
                            report("environments[${i}].${ek}", ek.toString(), envDefaults.keySet())
                        }
                    }
                }
            }
        } else if (k != 'extra' && value instanceof Map && defaults[k] instanceof Map) {
            value.keySet().each { sk ->
                def path = "${k}.${sk}"
                if (!defaults[k].containsKey(sk.toString()) && !(path in deprecated)) {
                    report(path, sk.toString(), defaults[k].keySet())
                }
            }
        }
    }

    return warnings
}
