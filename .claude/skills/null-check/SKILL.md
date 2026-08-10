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

   The script runs `mvn -q test-compile` first, fetches ecj 3.37.0 via Maven if not yet in the
   local repository, caches the dependency classpath in `target/ecj-classpath.txt`, and runs ecj
   with the project's own `.settings/org.eclipse.jdt.core.prefs` at `--release 21`.

2. Fix every error and any warning introduced by the change, then rerun until clean.

Compiling only the touched files against `target/classes` and `target/test-classes` works
because the annotations have class-file retention; test files referencing other test classes
(e.g. `SlowTest`) resolve against the latter. Whole-project ecj runs currently fail on
unrelated code — only pass the touched files.

## Notes

- This check plus a quiet test run is a good task for an Opus subagent: have it run both and
  report only failures and warnings.
- If the classpath cache is stale after a pom change, delete `target/ecj-classpath.txt` and rerun.
- ecj must be ≥ 3.35; the script pins 3.37.0.
- Remember the null-annotation idioms in claude/CLAUDE.md (annotate consistently across an
  inheritance web; `Map<K,@Nullable V>` for null-checked lookups; `@Nullable` late-init fields
  with asserting accessors).
