# Exploration performance: handoff state

Transient handoff note for continuing the work of `claude/exploration-performance.md`
in a fresh session. Delete when the branch is merged or the work is abandoned.

## Goal

Measurable performance improvement of state-space exploration, working down the
findings of `claude/exploration-performance.md` (the review note; read its "Suggested
order of attack" and "Building a throughput harness" sections first).

## State as of 2026-09-20

Branch `exploration-performance`, worktree `.claude/worktrees/exploration-performance`,
based on master `c5406f917`, detached for review. Five commits: the review note, the
benchmark harness, the baseline, the unstored depth-first configuration, the reflection
route for the management beans plus fresh grammar per run, and the retention
investigation write-up.

Done:

- Review note with rated findings, file:line references, gates.
- `src/test/java/nl/utwente/groove/test/performance/ExplorationBenchmark.java`: ten pinned
  configurations, `main` + `smoke` + `benchmark` entry points, Eclipse launch
  `launch/GROOVE - exploration benchmark.launch` (`-da -Xmx4g -XX:+UseParallelGC
  --add-modules=java.management,jdk.management`). Product `module-info` and `pom.xml`
  untouched. Smoke test and null check pass.
- Baseline table in the note (JDK 25, 2 warm-ups, 3 runs, one JVM, table order).
- Finding 3.11, the `util.Factory` user leak, found through the harness; fixed as
  gh #919, merged to master 2026-09-21 (`f9db84fda`). Not yet merged into this branch.
- 2026-09-21: `junit/performance/` holds verbatim copies of the eight harness grammars
  (commit `74c239d30`). Larger start graphs exist only for `sierpinsky` (up to `start13`)
  and `car-platooning` (up to `start-18`); `generate-binary-tree` is bound-driven.
  `inheritance`, `As-and-Bs`, `Mark-Unmark`, `append` and `pacman` need generated larger
  start graphs (`pacman`'s `start_four_ghosts` lacks the turn node and explores to one
  state). The harness `INPUT_DIR` still points at `junit/samples`.

## Blocked on the 3.11 fix

- The maxima of the three heavy rows (`append-4-list-8-equality`, `car-platooning-05`,
  `binary-tree-dfs-unstored`) are GC thrash from a single run's leaked applications;
  only `min ms` is stable there.
- The long-run tier (2 to 5 minute configurations: `car-platooning start-06`,
  `sierpinsky start12`, unstored binary tree depth 9, As-and-Bs under
  `collapse=equality`) cannot run without leaking several GB.

## Next, in order

1. Merge master (with the 3.11 fix) into the branch, or rebase; re-run the full baseline
   with the same JVM flags and replace the table in the note. Expect `retMB` to drop
   sharply on every row and the heavy rows' maxima to settle.
2. Add the long-run tier: a `tier` field on `Config` (QUICK/LONG), calibrate the four
   candidates above at `-Xmx8g` for 2 to 5 minutes each, 1 warm-up and 2 runs, run
   one configuration per JVM if run-order effects persist. Record a long-tier baseline.
3. Section 1 of the note (always-on `Reporter`, `CHECK_IMAGES`, `Factory.get()` lock,
   the `synchronized` accessors, `java.util.Stack`): one commit per item, each with
   before/after harness numbers in the commit body. `Reporter` first: it is on the
   innermost loop and also the harness's own breakdown source, so gate it on a system
   property and run the harness once with it on (for the breakdown) and once off (for
   the headline).
4. Section 2 (dead optimisations): 2.1 stored `MatchResult` keys, confirm "Confluent:"
   goes non-zero on `inheritance`; 2.3 soft certifier reference; 2.4 refinement loop
   (gate with `grammar-smoke`); 2.5 to 2.7 freezing and chain replay.
5. Enlarge the `junit/performance/` set: generated larger start graphs for the five
   grammars named above, switch the harness `INPUT_DIR`, then new grammars for the
   uncovered cases (attribute-heavy, symmetric ring, recipe transience), then section 3.

## Key files

- `claude/exploration-performance.md`: the findings and the harness documentation.
- `src/test/java/nl/utwente/groove/test/performance/ExplorationBenchmark.java`.
- `util/Reporter.java`, `match/plan/PlanSearchStrategy.java`, `util/Factory.java`,
  `lts/AbstractGraphState.java`, `lts/MatchApplier.java`, `graph/iso/CertificateStrategy.java`,
  `graph/iso/PartitionRefiner.java`, `lts/StateCache.java`: the section 1 to 3 targets.

## Facts that cost time to find

- `GrammarModel.toGrammar()` and the `RuleModel`s cache; a fresh `GrammarModel` per run
  is the only unit that isolates runs. `SystemStore.toGrammarModel()` also caches and
  registers an observer; construct `new GrammarModel(store)` instead.
- `persistence=none` only flips the GTS storing switch; the record's collapse flag stays
  on. The linear strategies switch it off. There is no `collapse=none` by design.
- `bound=cost:N` terminates unstored depth-first runs; depth-first rejects `bound=nodes`.
- The `GTSListener` for discovered counts must be registered before
  `ExploreType.newExploration`, which materialises the start state.
- The harness's `retMB` includes softly reachable state caches (about 60 % of the
  unstored row) and whatever the run leaked into the `Grammar`.
- Run order in one JVM changes timings (megamorphic call sites); compare like orders.
- Surefire honours `-DenableAssertions=false`; the harness header prints the status.

## Open decisions

- Whether the section 1 to 4 fixes go on this branch or one branch per section (the
  note is here, the fixes are independent; one branch per section is the house style).
- `STATE_SET_RESOLUTION` (4.4.9) and the plan-ordering heuristics (5.3, 5.8) need
  measurement before any decision.
