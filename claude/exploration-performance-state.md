# Exploration performance: handoff state

Transient handoff note for continuing the work of `claude/exploration-performance.md`
in a fresh session. Delete when the branch is merged or the work is abandoned.

## Goal

Measurable performance improvement of state-space exploration, working down the
findings of `claude/exploration-performance.md` (the review note; read its "Suggested
order of attack" and "Building a throughput harness" sections first).

## State as of 2026-09-22 (fibonacci investigation)

Branch `exploration-performance`, worktree `.claude/worktrees/exploration-performance`,
master merged in up to `ed8b740cf` (the explore-search-order-rules merge), re-attached
2026-09-22. Commits since
the 2026-09-21 state: the fibonacci-function control rows (via a scratch branch, merged
by Arend), the long-run tier with its calibration, and the long-tier baseline.

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

0. Decided 2026-09-22: the fibonacci recipe-path anomaly is finding 3.12, the quadratic
   transient closures in `StateCache` (JFR profile of `fib-15`: 97 % of samples in
   `HashMap` operations under `registerOutPartial` and `testSetFull`). Its fix is a
   redesign of the transient bookkeeping (local propagation over direct predecessor
   edges plus a cycle fallback, see the finding) and belongs on its own branch, like
   gh #919; the recipe family gets no long-tier size before it. Grammar-set item 4
   (`recipes`, `transactions`) is dropped as covered by the fibonacci rows.
1. Done: desktop baseline; long-run tier (`Tier` SMOKE/QUICK/LONG, seven long rows,
   `-Dgroove.bench.tier=long`, one JVM per row at `-Xmx8g`) with calibration and
   baseline, note section "The long-run tier (2026-09-22)"; BigInteger counter rows.
2. Re-baseline the quick tier (all rows, table order, launch flags): its table predates
   the tier split and the five new quick rows, and the long-tier baseline showed that
   `append-4-list-10` was collector-bound at `-Xmx4g` (206 s there, 76 s alone at 8 GB),
   so read `retMB` next to the times.
3. Section 1 of the note (always-on `Reporter`, `CHECK_IMAGES`, `Factory.get()` lock,
   the `synchronized` accessors, `java.util.Stack`): one commit per item, each with
   before/after harness numbers in the commit body. `Reporter` first: it is on the
   innermost loop and also the harness's own breakdown source, so gate it on a system
   property and run the harness once with it on (for the breakdown) and once off (for
   the headline).
4. Section 2 (dead optimisations): 2.1 stored `MatchResult` keys, confirm "Confluent:"
   goes non-zero on `inheritance`; 2.3 soft certifier reference; 2.4 refinement loop
   (gate with `grammar-smoke`); 2.5 to 2.7 freezing and chain replay.
5. A long-tier size for As-and-Bs is still missing (`start-4-3` under equality collapse
   does not fit 8 GB; intermediate edge densities untried); then new grammars for the
   uncovered cases (attribute-heavy, symmetric ring, recipe
   transience), then section 3.

## Grammar set extension (started 2026-09-21)

Arend wants `junit/performance` to cover more of the performance-sensitive functionality
than the eight copied samples do. Agreed order, by coverage gained per hour:

1. Done: `leader-election` ring (symmetry, finding 5.6): `ring-8/14/16/18` generated,
   hand-drawn graphs dropped (the `-init` ones were dead: `type:`/`flag:` prefixes the
   rules do not use), four harness rows, calibration in the note.
2. Done: Arend's copies of `attribute-count-to-n` and
   `fibonacci` take the size from a `let:` attribute of the start graph; generated
   `bound-10000/100000/300000` and `fib-12/15`, five harness rows, calibration in the
   note. The guarded-division rule `probe-odd` (2026-09-22) puts `ErrorValue`
   construction on the path of every counter row (a self-loop per odd state, an error
   per even one; states unchanged, transitions re-pinned). The
   `algebra=big` row waited for gh #923; the fix landed and the rows
   `count-100000-big` and `count-300000-big` are in since 2026-09-22 (the harness builds
   its GTS through `ExploreType.newGTS` now). Surprises, both in the
   note: fibonacci's transient states cost hundreds of times a plain state and `fib-17`
   exhausts 8 GB; the exponential state count is by design (Arend), the per-state cost
   is not, and the `fibonacci-function` control rows added 2026-09-21 (same states as
   plain states, 60 times faster at `fib-15`, time in the `gen` column) pin it on the
   recipe path (ground of 4.3.1, investigate before a long-tier size); the counter's
   allocation grows superlinearly (0.4 to 1.2 MB per state from 100k to 300k).
3. Done: Arend's `hub` grammar (2026-09-22) with the star row (5.6, 4.3.2) and
   the chain rows (many states of large graphs: 3.1, the certifier's diameter
   dependence); generated by the script. Under isomorphism collapse the star has one
   state, so "states C(N,k)" needed the symmetry-breaking chain. The unstored rows
   (2026-09-22 evening): `field-2-100-2500` with `hop` (5.1: target-bound edge item at
   the hub enumerates 2 703 incident edges for one match, 6.1 s) and `jump` (5.2: bare
   typed node over 5 400 nodes, 5.5 s), and `ring-1000-1` with `chain` against `counted`
   (3.7). All four are depth-first unstored with a depth bound on deterministic
   systems, since the linear traversal admits no bound. Outcome in the note: 3.7 is
   demoted to Low for exploration (swing mode carries the in-edge store along the
   chain); a new lead is the 1.3 KB allocated per attribute-test candidate in `hop`.
4. Control with transience: `recipes` (scale the start graph) and `transactions`
   (4.3.1, 1.5).
5. Done (2026-09-22 night): Arend copied `petrinet` over; four of the five hand-drawn
   nets deleted (1 to 38 states), `start2` kept as default. Generated `pipe-k-n`
   (C(n+k, k) markings: `pipe-8-8` and `pipe-9-9` quick, `pipe-11-11` long at 4 min and
   5.2 GB) and `join-f` (2f sub-matches per step on an unstored path: `join-100` and
   `join-1000` quick, 450 µs and 1.1 MB per step at f = 100, linear in f). Calibration
   table in the note.
6. Multigraph semantics and merging: `parallel-pump`, `mergers`, scaled.
7. Key-variant rows on existing grammars as needed while fixing.

Then the quick-tier re-baseline (item 2 above) before the first fix. Per step: copy or
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
- Calibrate through the harness itself: a row with `-1` counts, no warm-up, one run, one
  JVM per row; the loop runs fine as a Bash background task well past ten minutes.
- `retMB` in the long tier varies by a factor of five between runs of the same row (soft
  caches surviving or not); compare `med ms` only.
- `fNodes`/`fEdges` in the harness table are the host factory's element counts, not the
  final graph's; a `let:` per step grows them by one value node and its edges.
- The linear traversal (`successor=single frontier=single`) rejects a depth bound; an
  unstored path of N steps is `next=newest cost=uniform bound=cost:N persistence=none`
  on a system with one successor per state.
- Profiling a row: the JDK 25 binary is `C:/Program Files/Java/jdk-25.0.4.1/bin/java`
  (plain `java` is 26 here); add `-XX:StartFlightRecording=filename=<f>.jfr,settings=profile`
  to the harness command with `warmups=0 runs=1`, then `jfr view hot-methods`,
  `jfr view allocation-by-site` and `jfr print --events jdk.ExecutionSample` with an awk
  count of the first `StateCache` frame per sample give the breakdown in a minute.
- `PlanSearchEngine.PRINT` (a compile-time constant) prints every search plan on
  construction; flip it, run one row, flip it back. The planner is greedy and prefers
  the item that binds the fewest unbound nodes, so an edge to an already bound node
  wins over an attribute edge (which also binds its value node).

## Open decisions

- Whether the section 1 to 4 fixes go on this branch or one branch per section (the
  note is here, the fixes are independent; one branch per section is the house style).
- `STATE_SET_RESOLUTION` (4.4.9) and the plan-ordering heuristics (5.3, 5.8) need
  measurement before any decision.
