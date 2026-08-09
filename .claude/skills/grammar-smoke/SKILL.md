---
name: grammar-smoke
description: Semantic regression check for changes to exploration, matching, transformation, control, or algebra semantics. Runs the full state-space exploration tests (excluded from the default suite) against their expected state/transition counts; includes a helper for ad-hoc headless exploration of a single grammar.
---

# Grammar smoke test

The fast test suite barely explores any state spaces: `ExplorationTest`, which explores ~25
sample grammars and asserts their exact state/transition counts, carries the `SlowTest`
category and is excluded by default. A semantic regression in matching, transformation,
control or algebra evaluation can therefore pass `mvn test` unnoticed. Run this check before
handover whenever a branch touches semantics in `grammar`, `match`, `transform`, `control`,
`explore`, `lts`, or `algebra`.

## Procedure

From the root of the checkout or worktree being verified:

```
mvn -q test "-Dtest=ExplorationTest" "-Dexcluded.test.groups=" > target/smoke.log 2>&1
```

The `-Dexcluded.test.groups=` override is required — without it surefire filters the class out
and reports 0 tests run (which looks like a pass; check the report to be sure the tests
actually ran). Failure details land in `target/smoke.log` and `target/surefire-reports`.

## Interpreting a count mismatch

`ExplorationTest` expectations are exact state/transition counts per (grammar, start graph,
strategy). A mismatch means the transformation semantics changed:

- **Unintended** — a regression; find it before handover. The helper below reproduces single
  cases quickly.
- **Intended** (the change deliberately alters rule/control/exploration semantics) — update the
  expected counts in `ExplorationTest` in the same commit, and justify the new numbers in the
  commit body (why the state space legitimately grew or shrank). Never adjust counts to make
  a test pass without being able to explain the delta.

## Ad-hoc exploration of a single grammar

To see actual counts for one grammar (e.g. to reproduce a failure or derive new expected
numbers), run the headless Generator via the helper script:

```
powershell -NoProfile -ExecutionPolicy Bypass -File .claude/skills/grammar-smoke/run-generator.ps1 junit/samples/ferryman.gps
```

Further Generator arguments (strategy, start graph, …) are passed through — run with `-h` for
the option list. The script compiles first and caches the dependency classpath in
`target/ecj-classpath.txt` (shared with the null-check skill).

## Notes

- Delegate the surefire run to an Opus sub-agent: have it run the command and report only
  failures with their expected/actual counts.
- This check is complementary to the `determinism-check` skill: this one catches *wrong*
  state spaces, that one catches *unstable enumeration* of the right state space. Branches
  touching the exploration path typically need both.
