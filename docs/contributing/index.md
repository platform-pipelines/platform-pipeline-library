# Design rules

These rules shape every step in the library. Read them before adding or
changing one.

**No `src/` classes.** Everything is a `vars/` script. Jenkins serialises
pipeline state between steps. `src/` classes must implement `Serializable` and
avoid non-serialisable fields, or the build dies mid-run with
`NotSerializableException`. Plain scripts avoid that whole category of bug, and
they load directly in tests.

**One function per file.** Every file in `vars/` defines exactly one `call()`,
and the filename is the step name. No logic is hidden in a helper method that
you have to open a file to discover.

**Config is data.** `configLoad` returns a plain `Map`, and every other step
reads that map. You can print, diff, and validate a config without Jenkins.

**Parsing lives in Python, not Groovy strings.** Anything that needs real logic
(coverage maths, JSON walking) is a file in
`resources/com/platformpipelines/scripts/` that runs and tests standalone.
Groovy only runs a command and checks the exit code.

**Tools are containers, not plugins.** Sonar, Trivy, Gitleaks, Kaniko and Argo
are containers or REST calls. `plugins.txt` stays at a dozen entries, and the
same commands run on a laptop.

**Jenkins never touches the cluster.** The last CI step commits an image tag to
the GitOps repo, and Argo converges. Rollback is `git revert`.

**Config values never reach a shell unquoted.** Wrap them in
[shellQuote](../reference/utilities/shellQuote.md) or validate them first. The
`*InjectionTest` classes enforce this.

## Next

- [Extending the library](extending.md)
- [Local development](local-development.md)
