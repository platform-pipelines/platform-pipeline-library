// CodeNarc ruleset for this library.
//
// Tuned to the conventions documented in README.md: vars/*.groovy scripts
// with a single call() entry point, `def` everywhere instead of explicit
// types, no `src/` classes, and Map-based config. Rules that would only flag
// those deliberate choices are turned off; everything else stays on so real
// Java-isms and correctness smells (unused imports/vars, empty blocks,
// duplicate code, unsafe equality) get caught automatically.

ruleset {

    ruleset('rulesets/basic.xml') {
        exclude 'BrokenNullCheck'
        exclude 'EmptyClass'
    }

    ruleset('rulesets/unused.xml') {
        // Every buildTool dispatch target shares the call(Map cfg) signature
        // on purpose (buildApp.groovy / packageApp.groovy / testApp.groovy
        // call every tool's step uniformly), even when a specific tool
        // (docker-only, go, gradle, ...) doesn't need cfg's contents.
        exclude 'UnusedMethodParameter'
    }

    ruleset('rulesets/imports.xml') {
        // Every test file in this repo consistently imports org.junit.Test
        // first and the AssertJ static imports after — a deliberate,
        // uniform convention, not import disorder.
        exclude 'MisorderedStaticImports'
    }

    ruleset('rulesets/braces.xml')

    ruleset('rulesets/exceptions.xml') {
        // error() in these scripts often carries a deliberately generic
        // message built from a Map; that is a style choice, not a bug.
        exclude 'ThrowRuntimeException'
    }

    ruleset('rulesets/naming.xml') {
        // Every step file's entry point is literally named 'call' by
        // Jenkins shared-library convention — this is not a naming smell.
        exclude 'MethodName'
        exclude 'FactoryMethodName'
    }

    ruleset('rulesets/size.xml') {
        // Config dispatchers (appToolImage, lintDispatch, ...) legitimately
        // have long switch statements — one arm per supported build tool —
        // and standardPipeline.groovy / cfnChangeSet.groovy are inherently
        // complex top-level orchestrators, not a sign of needing a refactor.
        exclude 'CyclomaticComplexity'
        exclude 'MethodSize'
        exclude 'AbcMetric'
        // Jenkins declarative-pipeline nesting (pipeline > stages > stage >
        // node > container > if) is inherently deep; that's the DSL's shape,
        // not a code smell in standardPipeline.groovy.
        exclude 'NestedBlockDepth'
    }

    ruleset('rulesets/unnecessary.xml') {
        // Groovy-idiomatic `return` is used intentionally throughout for
        // readability in multi-branch call() methods.
        exclude 'UnnecessaryReturnKeyword'
        exclude 'UnnecessaryGetter'
    }
}
