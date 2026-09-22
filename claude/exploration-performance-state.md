# Exploration performance: handoff state

Transient handoff note for continuing the work of `claude/exploration-performance.md`
in a fresh session. Delete when the branch is merged or the work is abandoned.

## Goal

Measurable performance improvement of state-space exploration, working down the
findings of `claude/exploration-performance.md` (the review note; read its "Suggested
order of attack" and "Building a throughput harness" sections first).

## State as of 2026-09-22 (quick-tier re-baseline done)

Branch `exploration-performance`, worktree `.claude/worktrees/exploration-performance`,
master merged in up to `4eb233649` (the gh #924 merge), re-attached 2026-09-22 evening.

**gh #924 landed in between.** A parallel session took finding 3.12 (the quadratic
transient closures of `StateCache`, the fibonacci recipe anomaly of this note) on its own
branch `statecache-transient-closures`; merged to master and into this branch at
`da54faa44`. Consequences here: every recipe figure before the merge is void (`fib-15` as
a recipe: 19 s to 0.29 s cold, 63 ms warm, level with the function program), the recipe
rows were recalibrated and re-pinned (`fib-15` smoke, `fib-22` quick, `fib-12` dropped;
no long-tier size for either family, the ordinary per-state cost), and the A/B in the
note shows no cost on the non-recipe path. The gh #924 handoff file
`claude/statecache-transient-closures-state.md` that the merge brought in is that
session's; master has since deleted it, and the next master merge takes it away here.

Done, in addition to the 2026-09-22 morning state (grammar set complete through item 6,
long tier with baseline):

- Quick-tier re-baseline: all 46 quick rows, one JVM, table order, launch flags, JDK 25,
  35 minutes; table and observations in the note ("Quick-tier re-baseline"). Every
  count asserted. Findings: run-order effects of up to 20 % (bigger than thought; fixes
  under 20 % need the one-JVM-per-row A/B shape), `binary-tree-dfs-unstored-9` is
  collector-bound at 4 GB (29.7 s against 14 s at 8 GB) and should move to the long tier
  or a larger heap, all other rows within 15 % of their 8 GB calibration.
- Coverage reassessment (note, last section): nine gaps ranked. Close before measuring
  fixes: (1) the Simulator's random-access copy mode (`SimulatorModel.resetGTS` sets
  `Record.randomAccess`; the harness runs swing mode only), a harness switch; (2) cyclic
  and wide transient regions for gh #924's forward-search fallback, a `hub` control
  program with a backward step in a star-ended recipe on `chain-200-2`; (4) a
  `matchInjective=true` variant row. Then (3) a many-rules grammar, (5) a cache-clearing
  harness option; 6 to 9 on demand.

## Next, in order

1. Close coverage gaps 1, 2 and 4 of the reassessment (harness copy-mode switch with a
   second baseline column for the rows where the modes differ; the cyclic-transient hub
   recipe row, calibrated and pinned; the injective variant row). One commit each.
2. Move `binary-tree-dfs-unstored-9` out of the quick tier (long tier, or drop: the long
   tier has depth 10).
3. Section 1 of the note (always-on `Reporter`, `CHECK_IMAGES`, `Factory.get()` lock,
   the `synchronized` accessors, `java.util.Stack`): one commit per item, each with
   before/after numbers measured one JVM per row (the A/B shape in the note), not from
   the tier table. `Reporter` first: it is on the innermost loop and also the harness's
   own breakdown source, so gate it on a system property and run the harness once with it
   on (for the breakdown) and once off (for the headline).
4. Section 2 (dead optimisations): 2.1 stored `MatchResult` keys, confirm "Confluent:"
   goes non-zero on `inheritance`; 2.3 soft certifier reference; 2.4 refinement loop
   (gate with `grammar-smoke`); 2.5 to 2.7 freezing and chain replay.
5. A long-tier size for As-and-Bs is still missing (`start-4-3` under equality collapse
   does not fit 8 GB; intermediate edge densities untried); then section 3.

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
4. Dropped 2026-09-22 as covered by the fibonacci rows: `recipes` and `transactions`
   (4.3.1, 1.5). Coverage gap 2 of the reassessment (cyclic and wide transient regions)
   revives the shape on the `hub` chain instead.
5. Done (2026-09-22 night): Arend copied `petrinet` over; four of the five hand-drawn
   nets deleted (1 to 38 states), `start2` kept as default. Generated `pipe-k-n`
   (C(n+k, k) markings: `pipe-8-8` and `pipe-9-9` quick, `pipe-11-11` long at 4 min and
   5.2 GB) and `join-f` (2f sub-matches per step on an unstored path: `join-100` and
   `join-1000` quick, 450 µs and 1.1 MB per step at f = 100, linear in f). Calibration
   table in the note.
6. Done (2026-09-22 late): `parallel-pump` (Arend's DPO copy) with generated `pump-k-m`
   (`pump-8-4` smoke, `pump-12-6` quick under DPO and SPO-multi, `pump-16-8` long at
   169 s and 6 GB) and `mergers` (hand copy of the sample, `system.properties` rewritten
   to 3.12 with `semantics=SPO-simple`) with generated `ring-n` (`ring-6` smoke,
   `ring-9` quick under simple and multi, `ring-10-multi` upper quick, `ring-11-dpo`
   quick; no long size, `ring-11` under multi is 7.6 min and 5.6 GB). The variants use a
   new per-row grammar-property override, `Config.properties` (`key=value` pairs like
   the `Generator`'s `-D`). Calibration table and observations in the note: DPO and
   SPO-multi cost the same on the pump (no node erasure, so 4.1.6 never runs; the
   mergers DPO row is the one for it), multi costs 5 to 10 % over simple on mergers,
   DPO cuts mergers' states tenfold through the identification condition.
7. Key-variant rows on existing grammars as needed while fixing.

Per step: copy or
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
- The harness `main` takes row names as arguments; `-Dgroove.bench.run=<name>` is the
  JUnit route's selector and is ignored by `main` (a loop passing it ran the whole quick
  tier per iteration, 2026-09-22).
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
