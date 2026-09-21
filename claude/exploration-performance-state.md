# Exploration performance: handoff state

Transient handoff note for continuing the work of `claude/exploration-performance.md`
in a fresh session. Delete when the branch is merged or the work is abandoned.

## Goal

Measurable performance improvement of state-space exploration, working down the
findings of `claude/exploration-performance.md` (the review note; read its "Suggested
order of attack" and "Building a throughput harness" sections first).

## State as of 2026-09-21 (evening)

Branch `exploration-performance`, worktree `.claude/worktrees/exploration-performance`,
based on master `c5406f917`, detached for review. Five commits: the review note, the
benchmark harness, the baseline, the unstored depth-first configuration, the reflection
route for the management beans plus fresh grammar per run, and the retention
investigation write-up.

Done:

- Review note with rated findings, file:line references, gates.
- `src/test/java/nl/utwente/groove/test/performance/ExplorationBenchmark.java`,
  `junit/performance/generate-starts.py`. ten pinned
  configurations, `main` + `smoke` + `benchmark` entry points, Eclipse launch
  `launch/GROOVE - exploration benchmark.launch` (`-da -Xmx4g -XX:+UseParallelGC
  --add-modules=java.management,jdk.management`). Product `module-info` and `pom.xml`
  untouched. Smoke test and null check pass.
- Baseline table in the note (JDK 25, 2 warm-ups, 3 runs, one JVM, table order).
- Finding 3.11, the `util.Factory` user leak, found through the harness; fixed as
  gh #919, merged to master 2026-09-21 (`f9db84fda`). Not yet merged into this branch.
- 2026-09-21: master (with the gh #919 fix) merged in. `junit/performance/` is the
  benchmark grammar set: the eight harness grammars pruned to default + largest +
  generated start graphs, `generate-starts.py` for the generated ones, the harness
  `INPUT_DIR` switched and five rows added for the generated graphs (calibration table in
  the note, section "The performance grammar set"). Arend edited `pacman` (rules, both
  start graphs, properties) by hand; the four-ghost graph is his.

## Measured on the desktop

2026-09-21: full baseline taken on the desktop (UT187312, JDK 25.0.4.1, launch flags,
2 warm-ups and 3 runs, all 16 rows in table order, 39 minutes) and recorded in the note,
replacing the laptop table. Maxima settled, `retMB` down a third to two thirds on the heavy
rows, `iso` dominates the large rows. The run used a second, detached worktree
`.claude/worktrees/exploration-performance-baseline` at `4f9f63bc5` so that the branch
worktree stayed editable; it can be removed. Recipe: `mvn -q -DskipTests test-compile`,
`dependency:build-classpath` for the class path, then `java -cp
"target/classes;target/test-classes;<cp>"` with the launch flags and
`-Dgroove.bench.warmups=2 -Dgroove.bench.runs=3 -Dgroove.bench.timeout=1200`, from the
worktree root; no module path needed.

## Next, in order

1. Done: desktop baseline recorded (above).
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
5. Long-tier candidates still missing for As-and-Bs (no size between 12 s and 6+ min in
   the bipartite family) and Mark-Unmark (`tree-22`/`tree-23` unmeasured); then new
   grammars for the uncovered cases (attribute-heavy, symmetric ring, recipe
   transience), then section 3.

## Grammar set extension (started 2026-09-21)

Arend wants `junit/performance` to cover more of the performance-sensitive functionality
than the eight copied samples do. Agreed order, by coverage gained per hour:

1. Done: `leader-election` ring (symmetry, finding 5.6): `ring-8/14/16/18` generated,
   hand-drawn graphs dropped (the `-init` ones were dead: `type:`/`flag:` prefixes the
   rules do not use), four harness rows, calibration in the note.
2. Done except the `ErrorValue` rule: Arend's copies of `attribute-count-to-n` and
   `fibonacci` take the size from a `let:` attribute of the start graph; generated
   `bound-10000/100000/300000` and `fib-12/15`, five harness rows, calibration in the
   note. Not done: a guarded-division rule so `ErrorValue` is on the path, and the
   `algebra=big` row, because the exploration key `algebra=big` is broken (explores to
   one state; the grammar property works). Bug not yet filed; ask Arend. Surprises,
   both in the note: fibonacci's transient states cost hundreds of times a plain state
   and `fib-17` exhausts 8 GB (ground of 4.3.1, investigate before a long-tier size);
   the counter's allocation grows superlinearly (0.4 to 1.2 MB per state from 100k to
   300k).
3. Hub grammar, new: star of N spokes around one hub, k tokens moving through the hub,
   states C(N,k); the only row for 3.1, 3.7, 4.3.2, 5.1, 5.2. Design first.
4. Control with transience: `recipes` (scale the start graph) and `transactions`
   (4.3.1, 1.5).
5. Quantifiers over a large graph: `petrinet` with a generated larger net.
6. Multigraph semantics and merging: `parallel-pump`, `mergers`, scaled.
7. Key-variant rows on existing grammars as needed while fixing.

Then one full re-baseline (all rows, table order) before the first fix. Per step: copy or
write the grammar, extend `generate-starts.py`, calibrate with the headless `Generator`
(quick tier 5 to 60 s, one long-tier candidate), pin counts, one commit per grammar.
Only the harness reads `junit/performance`; new rows stay out of the smoke set unless
small.

## Key files

- `claude/exploration-performance.md`: the findings and the harness documentation.
- `src/test/java/nl/utwente/groove/test/performance/ExplorationBenchmark.java`,
  `junit/performance/generate-starts.py`.
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
