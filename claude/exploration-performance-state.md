# Exploration performance: handoff state

Transient handoff note for continuing the work of `claude/exploration-performance.md`
in a fresh session. Delete when the branch is merged or the work is abandoned.

## Goal

Measurable performance improvement of state-space exploration, working down the
findings of `claude/exploration-performance.md` (the review note; read its "Suggested
order of attack", then the finding the next item names).

## State as of 2026-09-26

Branch `exploration-performance`, worktree `.claude/worktrees/exploration-performance`,
master merged in up to `1726ed599` (2026-09-26; a fresh quick-tier baseline at that merge
is in the note, stitched from two JVMs after another session disturbed the first run). Everything the branch set out to build is
done and recorded in the note: the harness with its three tiers and the random-access
switch; the grammar set (15 grammars, 58 rows); the quick-tier baseline (2026-09-23, re-run
after gh #925), the long-tier baseline (2026-09-22) and the quick tier in the Simulator's
random-access mode; the coverage assessment, judged sufficient to start fixing. Three
defects found on the way were fixed on their own branches and merged: gh #919 (finding
3.11, the `Factory` user leak), gh #924 (finding 3.12, the transient closures of
`StateCache`, found by the fibonacci rows) and gh #925 (recipe targets missed on cyclic
regions under the closing strategies, found by the wander rows). All recorded figures
are post-fix: the recipe rows mirror the function rows, the wander family is
recalibrated (`hub-wander-200` added; no size of it lands in the long tier), the quick
tier was re-run on the repaired code with every non-recipe row within noise, and the
random-access recipe rows re-measured (time level with swing mode, the single-launch
wander rows retaining 3.7 times as much). `binary-tree-dfs-unstored-9` was dropped as
collector-bound at 4 GB. Findings implemented since, each on its own branch: 3.1/3.13,
4.3.2, 3.2 (Arend, master) and 2.1 (2026-09-25); see "Next".

Measurement discipline, learnt the hard way: the tier table serves counts, breakdown and
order of magnitude; a change is judged one JVM per row, alternating the two builds, on a
quiet machine, in both materialisation modes where the row is mode-sensitive (see "Runs
and outcomes" in the note).

## Next, in order

1. **Finding 3.13: located 2026-09-24, it is 3.1** (the certifier's `NodeCertificate`
   array sized by the factory's node high-water mark; the counter mints a value node per
   state, so the cost was quadratic). Done: fixed on branch `certifier-node-table`
   (probe table, null annotations, probe table replaced by a per-thread scratch array
   after it lost the A/B on `leader-election-14`), gates passed, merged into this branch
   and master 2026-09-24; branch and worktree cleaned up. Figures under 3.13 in the note.
   Finding 3.2: fixed by Arend on master (19dbd4194), A/B 2026-09-25 under 3.2 in the
   note: allocation down, time level, `hub-wander-200` 6 % slower and unexplained.
   Worktrees `ab32-a`/`ab32-b` are kept for that follow-up; do not remove them in a
   cleanup unless Arend says so explicitly.
2. Finding 4.3.2 (per-node edge sets): the whole of the Simulator-mode cost on large
   graphs, 11 to 33 times on the hub rows; a design discussion first (copy-on-write
   sets or sharing), since copying is what the mode asks for.
3. Section 1 of the note, profiled 2026-09-26 (JFR shares and unit costs under 1.1 to
   1.3; section 1 is 11 to 16 % of the samples on the profiled rows). Status: 1.2
   (`CHECK_IMAGES`) fixed on master `1726ed599`; 1.1 retired, not gated, on branch
   `retire-reporter` off master (tip 61e000e7f, detached for review in its own worktree),
   not merged into this branch (the fixes go on their own branches off master, this
   branch keeps the note and the harness); the harness columns `match`, `iso`, `cert`,
   `gen` and `rep`, which read the class, were dropped here in advance (58139ff9c). The profile recipe under "Facts" replaces the columns. 1.3: done on branch
   `factory-fast-path` off master (tip 3e6e7f7f7, 2026-09-26, detached for review in its
   own worktree), lock-preserving fast path with a volatile holder; A/B
   under 1.3 in the note: every row 2 to 11 % faster, direction uniform, single rows
   inside the noise. 1.4 and 1.6 done on branch `section1-locks` off master (tip
   da7559bc6, 2026-09-26, detached for review; profile figures under 1.4 and 1.6 in the
   note, 2 to 4 % on `as-and-bs-4-3` and `mark-unmark`, zero elsewhere, no A/B since
   below the resolvable floor); 1.5 moot (immutable `NestedSwitch`); 1.7 no performance
   case. New 1.8 (Factory dependency registration per `Step`, 10 % of `fib-function-22`)
   awaits a design decision: eager `Step` fields or untracked factories. A `@DebugFlag` marker plus test,
   so a `CHECK_IMAGES` cannot stay on unnoticed again, was handed to a separate session.
4. Section 2 (dead optimisations): 2.1 done 2026-09-25 on branch `confluent-diamond`
   (shortcut revived with content-based event conflicts, up to 54 % off; figures under
   2.1 in the note); 2.2 dropped (footprint, see there). 2.4 and 2.3 done 2026-09-25 on
   branch `iso-refinement` (handoff note `claude/iso-refinement-state.md` there): 2.4
   measured time-neutral and dropped at review (the old final pass hashes adjacency under
   the discrete labelling into the graph certificate), 2.3's soft reference measured and
   rejected (state caches evicted; leader-election-16 2.9 times slower), only the
   `strong` argument honoured. Next: 2.5 to 2.7 freezing and chain replay.
5. A long-tier size for As-and-Bs is still missing (`start-4-3` under equality collapse
   does not fit 8 GB; intermediate edge densities untried); then section 3.

## Grammar set (complete)

Built 2026-09-21 to 2026-09-23, per grammar in the note: the eight copied samples with
generated larger start graphs; `leader-election` rings (symmetry); `attribute-count-to-n`
with `probe-odd` and the BigInteger rows, and `fibonacci` as recipe and as function;
Arend's `hub` (star, chain, field and ring shapes, then the wander programs); `petrinet`
pipelines and joins; `parallel-pump` and `mergers` under three semantics through the
per-row property override; the injective variants. `recipes` and `transactions` were
dropped as covered by the fibonacci and wander rows. Adding a grammar: copy or write it,
extend `generate-starts.py`, calibrate through the harness (rows with `-1` counts, one
JVM per row), pin counts, one commit per grammar; only the harness reads
`junit/performance`, and new rows stay out of the smoke set unless small.

## Key files

- `claude/exploration-performance.md`: the findings, the grammar set and the harness
  documentation.
- `src/test/java/nl/utwente/groove/test/performance/ExplorationBenchmark.java`,
  `junit/performance/generate-starts.py`.
- `lts/StateCache.java`, `grammar/host/DeltaHostGraph.java`, `grammar/host/HostFactory.java`,
  `algebra/` (3.13 and 4.3.2); `util/Reporter.java`, `match/plan/PlanSearchStrategy.java`,
  `util/Factory.java`, `lts/AbstractGraphState.java`, `lts/MatchApplier.java`,
  `graph/iso/CertificateStrategy.java`, `graph/iso/PartitionRefiner.java` (sections 1 to 3).

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
- Across JVMs of one A/B the first JVM of a row is the fastest and later ones 10 to
  25 % slower (1.3 A/B, 2026-09-26), so alternate strictly (A, B, A, B, ...) rather than
  mirrored (A, B, B, A), which hands the favoured first slot to one build.
- Run order in one JVM changes timings little (a few per cent; `fib-15` is the one
  large, unexplained case), machine load changes them a lot: a tier run during an
  Eclipse rebuild was 20 % off on three rows. Two clean tier runs differ by up to 10 %
  per row, so a change is judged one JVM per row, alternating A and B, on a quiet
  machine.
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
- JFR's execution samples misattribute the zero-fill of a large inlined array
  allocation to a neighbouring frame even with `-XX:+DebugNonSafepoints` (3.13:
  `CacheReference.incFrequency` got 70 to 81 % of samples); when `hot-methods` and
  `allocation-by-site` disagree, believe the allocation view.
- Harness launch outside Maven: `mvn -q test-compile dependency:build-classpath
  "-Dmdep.outputFile=target/cp.txt"`, then `java -da -Xmx8g -XX:+UseParallelGC
  "-Dgroove.bench.tier=all" -cp "target/test-classes;target/classes;$(cat target/cp.txt)"
  nl.utwente.groove.test.performance.ExplorationBenchmark <rows>` (used 2026-09-24 for the
  three counter rows incl. the long `count-600000`; whether the tier property is needed
  for named long rows was not checked).
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

- The pump rows are 5 to 15 % slower in the 2026-09-26 baseline with the diamond
  shortcut active but closing few diamonds per transition; an A/B of 2.1 on the pump rows
  decides whether the conflict check needs a cheaper pre-test.

- Decided 2026-09-26: fixes go on their own branches off master (`retire-reporter`,
  `factory-fast-path`); this branch carries only the note, the state file, the harness
  and the A/B records.
- `STATE_SET_RESOLUTION` (4.4.9) and the plan-ordering heuristics (5.3, 5.8) need
  measurement before any decision.
