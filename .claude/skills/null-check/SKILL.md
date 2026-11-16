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

   The script runs `mvn -q test-compile` first, fetches ecj 3.44.0 via Maven if not yet in the
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

`run-ecj.ps1 -All` checks the whole project, in two ecj passes. The main source tree is
compiled as the named module `nl.utwente.groove`: `module-info.java` is included, the
dependencies go on the module path, and the generated ANTLR sources are compiled into the
module alongside the checked-in ones. ecj (up to at least 3.44.0) cannot read a module
descriptor that exists only under `META-INF/versions/` (multi-release jars, e.g. picocli),
so the script substitutes such jars on the module path with copies whose descriptor is
duplicated at the jar root, cached in `target\ecj-mp`. The test tree (`src/test/java`) is
then compiled in the unnamed module, like the per-file mode: the test dependencies (JUnit)
are not required by `module-info`, and the test sources contain no cross-package sealed
hierarchies, so the modular/unnamed distinction does not affect the analysis there.
`-All` does not combine with explicit file arguments, and unrecognized switches are
rejected rather than passed to ecj as file names.

Expected baseline: **13 problems, exit code 0**, all of them warnings for TODO task tags
(9 main, 4 test). The info in `gui/tree/TypeTree.java` that used to be part of the baseline
(exported API mentioning a non-exported type) went when `gui.tree` stopped being exported.
The bar for a change is that it adds nothing to that set.

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
- The script pins ecj 3.44.0. The pin has to keep up with the JDT of the Eclipse in use
  (Help > About > Installation Details lists "Eclipse JDT Core"): a newer JDT adds checks
  that an older ecj passes silently, and then this gate says nothing where Eclipse
  reports errors. That happened with 3.42.0, the previous pin: from 3.44 on, the implicit
  accessor of a record component is checked as an override of an annotated interface
  method, and the component needs an explicit `@NonNull` since `@NonNullByDefault` does
  not reach it; 3.42.0 accepts the unannotated component. When raising the pin, rerun
  `-All` and update the baseline above; 3.42.0 had two more warnings, spurious
  missing-`@Override` reports on the explicit component accessors of
  `control/Binding.java`.
- Do not fall back to 3.37.0: that version dies with an internal
  NPE on switch expressions over sealed types when given many files at once, and reports
  spurious "import is never used" warnings for nested subtypes of sealed types that are
  imported only for their `permits` clauses (`grammar/aspect/AspectContent.java`,
  `verify/Proposition.java`); later versions show neither problem. The multi-release-jar
  descriptor bug is still present in 3.44.0 (checked 2026-08-18), so a version bump alone
  does not obsolete the jar patching.
- Remember the null-annotation idioms in claude/CLAUDE.md (annotate consistently across an
  inheritance web; `Map<K,@Nullable V>` for null-checked lookups; `@Nullable` late-init fields
  with asserting accessors).

## The yFiles backend

The unit in `yfiles/` is not part of the main build, so neither mode above sees it. It has
a script of its own, which needs the licensed yFiles library in the local Maven repository:

```
powershell -NoProfile -ExecutionPolicy Bypass -File .claude/skills/null-check/run-ecj-yfiles.ps1
```

It checks the unit's whole source tree every time (15 main and 8 test sources, well under a
minute) with the same ecj version, settings (`yfiles/.settings`, kept identical to the main
project's) and external annotations, straight from source: the unit runs on the class path
without `module-info` and has no generated sources, so none of the module handling above
applies. The core comes from this checkout's `target\classes` when that is built, as in
Eclipse's workspace resolution, and from the installed `nl.utwente.groove:groove` jar
otherwise or with `-UseInstalledJar`; stale core classes show up as API errors, and ecj
skips null analysis in any unit with an error. The dependency class path is cached in
`yfiles\target\ecj-classpath.txt` (`-Refresh` rebuilds it). The script prints `No problems.`
and exits with 0, or lists the problems (also in `yfiles\target\ecj-problems.txt`) and exits
with 1, warnings included: **zero problems is the bar**, there is no baseline. A clean run is
no proof; see `claude/yfiles-backend.md`, *Null analysis*.
