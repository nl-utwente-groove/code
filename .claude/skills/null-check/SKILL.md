---
name: null-check
description: Verify touched Java files with Eclipse JDT null analysis (ecj batch compiler). Run before handing over any branch that adds or changes Java code; the Maven build cannot catch these violations.
---

# Null-analysis check (ecj harness)

The Maven build does not run JDT's null analysis (`org.eclipse.jdt.annotation`), so violations
surface only in Eclipse. Verify all touched Java files before handover.
**Zero errors and zero new warnings is the bar.**

## Procedure

From the root of the checkout or worktree being verified:

1. Run the helper script with all touched `.java` files (repo-relative paths):

   ```
   powershell -NoProfile -ExecutionPolicy Bypass -File .claude/skills/null-check/run-ecj.ps1 src/main/java/nl/utwente/groove/foo/Bar.java ...
   ```

   The script runs `mvn -q test-compile` first, fetches ecj 3.42.0 via Maven if not yet in the
   local repository, caches the dependency classpath in `target/ecj-classpath.txt`, and runs ecj
   with the project's own `.settings/org.eclipse.jdt.core.prefs` at `--release 21`, with the
   curated external null annotations from `lib/eea` attached (`-annotationpath`) — the same
   set Eclipse applies via the `.classpath` JRE container attribute.

2. Fix every error and any warning introduced by the change, then rerun until clean.

Compiling only the touched files against `target/classes` and `target/test-classes` works
because the annotations have class-file retention; test files referencing other test classes
(e.g. `SlowTest`) resolve against the latter. Per-file runs compile in the unnamed module —
see the masking caveat below.

## Whole-project mode

`run-ecj.ps1 -All` checks the entire main source tree, compiled as the named module
`nl.utwente.groove`: `module-info.java` is included, the dependencies go on the module path,
and the generated ANTLR sources are compiled into the module alongside the checked-in ones.
ecj (up to at least 3.44.0) cannot read a module descriptor that exists only under
`META-INF/versions/` (multi-release jars, e.g. picocli), so the script substitutes such jars
on the module path with copies whose descriptor is duplicated at the jar root, cached in
`target\ecj-mp`.

Expected baseline: **13 problems, exit code 0** — 12 warnings (2 missing-`@Override` in
`control/Binding.java`, 10 TODO task tags) plus 1 info in `gui/tree/TypeTree.java` (exported
API mentioning a non-exported type; surfaces only in modular compilation). The bar for a
change is that it adds nothing to that set.

## Error masking: why `-All` is the authoritative gate

ecj skips null/flow analysis for any compilation unit with a compile error, so an error in a
file silently suppresses **all** null-analysis findings in that file. This bit once: per-file
runs compile in the unnamed module, where the cross-package sealed hierarchy rooted at
`algebra/syntax/Expression.java` is illegal, and the resulting spurious error ("Permitted
type Constant in an unnamed module should be declared in the same package") masked a real
null bug in that file. `-All` compiles modularly and has no such error. Consequences:

- A per-file run of `Expression.java` still reports the spurious sealed-permits error and
  does **not** null-check that file; use `-All` for it. Its permitted subtypes
  (`Constant`, `Variable`, `FieldExpr`, `CallExpr`) check cleanly per-file.
- Generally, if a per-file run reports any error in a unit, that unit has not been
  null-checked — resolve the error (or run `-All`) before trusting the result.

## Notes

- This check plus a quiet test run is a good task for an Opus subagent: have it run both and
  report only failures and warnings.
- If the classpath cache is stale after a pom change, delete `target/ecj-classpath.txt` and rerun.
- The script pins ecj 3.42.0. Do not fall back to 3.37.0: that version dies with an internal
  NPE on switch expressions over sealed types when given many files at once. The
  multi-release-jar descriptor bug is still present in 3.44.0 (checked 2026-08-18), so a
  version bump alone does not obsolete the jar patching.
- Remember the null-annotation idioms in claude/CLAUDE.md (annotate consistently across an
  inheritance web; `Map<K,@Nullable V>` for null-checked lookups; `@Nullable` late-init fields
  with asserting accessors).
