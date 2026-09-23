# Recipe target completeness (gh #925): handoff state

Transient handoff note. Delete when the branch is merged.

## Goal

Fix gh #925: a recipe launch on a cyclic transient region in which every state is a
recipe end got only some of its end states as recipe targets under the closing
strategies (bfs, dfs); the linear strategy found them all. The other half of gh #924,
whose fix (local propagation in `lts.StateCache`) under-approximated here where the code
before it over-approximated.

## State as of 2026-09-23 (evening)

Branch `recipe-target-completeness` off master `f8ab8a880`, worktree
`.claude/worktrees/recipe-target-completeness`. Correctness fix and the forward
launch propagation redesign both done, awaiting review.

**Mechanism of the bug.** A state that leaves its recipe through a verdict becomes
steady before it is closed: the star `(moveNext | movePrev)*` exits with the same
verdict either way, so `StateMatches.advanceFrame` moves the actual frame to the
outer location as soon as the matches are computed, and `registerTransienceChange`
runs with the state still open. Its rule transitions are nonetheless inner steps of
the run (computed from the prime frame). The gh #924 bookkeeping treated a steady
state as done for its predecessors, so a predecessor whose successors were all
steady became full at its own closure and dropped its propagation bookkeeping;
targets found deeper in the region afterwards could not pass through it.

**Fix** (commit "Resolved gh #925"): `StateCache.isDone(state)` = full, or steady
with an outer prime frame; a steady inner-prime state is waited for like a
transient one, and full-ness implies target completeness. `Status.Flag.FULL`
javadoc updated accordingly.

**Redesign** (commit "Replaced backward target propagation by forward launch
propagation"): the fix alone made the backward target propagation quadratic on
cyclic same-verdict regions (every state accumulated every reachable target). Now
the launches travel forwards, as designed below; the `innerPreds` lists and the
`forwTarget` sets of non-full states are gone, `forwTarget` survives only as the
on-demand cache of full states.

**Gate**: the two wander cases in `RecipeCompletenessTest` (commit "Added the wander
cases"), failing before the fix, passing after both commits in all four variants.
All three strategies give 191 states / 190 recipe transitions on `chain-20-2`.

## Design as built (option 1, chosen by Arend 2026-09-23)

Rejected: option 3 (indexed `BitSet` target sets: memory only, time stays quadratic)
and option 2 (SCC sharing: incremental SCC detection under arbitrary exploration
orders, which gh #924 deliberately avoided).

Per inner-prime, non-full state cache S (`lts/StateCache`):

- `launches` (list, arrival order) + `launchIndices` (`BitSet` over
  `GTS.getLaunchIndex`, a GTS-wide numbering of the launches on first request):
  the launches whose runs pass through S.
- `innerSteps`: the registered inner non-launch steps out of S.
- Invariant: for every L in `launches(S)` and every recipe target t reachable from S
  over `innerSteps` transitively, the recipe transition (L, t) exists.

Operations (absence and full-ness bookkeeping unchanged):

1. `propagate(L)` on S, explicit-stack walk: S full → emit (L, t) for every t in
   `getForwTarget()` (computed on demand by `computeForwOuter`, complete because
   full-ness implies every reachable inner state is full), stop; L already in S →
   stop; else record L, emit (L, `RecipeTarget(S)`) if `!knownInner`, then for each
   step in `innerSteps`: inner-prime target → continue there, else emit
   (L, `RecipeTarget(step)`).
2. Launch L→S registered (`registerOutPartial`): S not inner-prime → single-step
   recipe transition; else `S.propagate(L)`.
3. Inner non-launch step P→T registered: append to `P.innerSteps`; for each L in
   `launches(P)`: T inner-prime → `T.propagate(L)`, else emit (L, `RecipeTarget(step)`).
4. S flips inner→outer (`registerTransienceChange`): emit (L, `RecipeTarget(S)`) for
   each L in `launches(S)`.
5. `setFull`: drop `launches`, `launchIndices`, `innerSteps`.

Two constant-factor lessons from profiling `chain-60-2` under `alap wander` (six
million (launch, state) visits): the walk must not iterate the transition map (it
holds the state's own recipe transitions, thousands per state: factor 40), and the
membership test must not be an allocating hash set (30% of the run time; hence the
bit set). A cleared cache of a closed-but-not-full state loses `launches` and
`innerSteps` as it loses `preds`; documented in the class comment, not defended.

## Measurements (warm JVM, bfs, this machine)

| grammar / graph | master | fix only | fix + redesign |
|---|---|---|---|
| fibonacci `fib-22` (acyclic region) | 1.0 s | 1.0 s | 0.9 s |
| hub `chain-100-2` wander (4951 states) | 4.4 s, 19 602 trans (incomplete) | 8.9 s, 24 355 trans | 1.5 s, 24 355 trans |
| hub `chain-60-2` wander-alap (1771 states, 6.07 M trans) | 3.6 s | 3.9 s | 4.1 s |
| hub `chain-200-2` wander (19 901 states) | 90 s (incomplete) | out of heap at 6 GB | 21 s, 98 705 trans |

Residual on wander-alap, as measured after merging `nested-call-recipe-lookup` into
this branch (tip 3.7–3.9 s warm, five runs): caching `RecipeTarget(state)` per state
was investigated and refuted. With the recipe lookup cheap, the constructor is fully
inlined and never appears as a frame in a `-XX:+DebugNonSafepoints` profile; all of
`propagate`'s own time is 34 of 1018 samples, so the ceiling of the gain is under 3%
and the realistic figure about 1%, below the run-to-run noise. The cache owner would
have been the visited state's own cache (no parameter passing), and the step targets
could have been cached in the source state's cache just as well, so the asymmetry is
avoidable but pointless. The same profile showed `GTS.getLaunchIndex` at 7% (one hash
lookup per `propagate` call, of which there are about 24 million: one per registered
step and launch of its source); numbering the launch once at registration in a
`Launch` record replaced the GTS map by a counter, but gave no measurable wall-time
gain either (3.8–3.9 s). Kept as a simplification. Remaining top frames are the GTS
transition set (`TreeHashSet.put`) and `CacheReference.incFrequency`, i.e. the output.

Harness: a scratch `main` that loads the grammar with `SystemStore.newGrammar`, sets
the host and control resources active with `setLocalActiveNames`, and plays
`LegacySyntaxParser.parse("bfs final 0").newExploration(gts, null)` three times, run
against `target/classes` plus the output of `mvn dependency:build-classpath`; the hub
grammar with the `chain-*` graphs is `junit/performance/hub.gps` in the
`exploration-performance` worktree. JFR (`-XX:StartFlightRecording=settings=profile`,
`jfr print --events jdk.ExecutionSample`) gave the profile.

## Gates run

Fast: `RecipeCompletenessTest`, `RecipeTransitionTest`, `RecipeTest`,
`OneStepRecipeTest`, `RecipeNullArgsTest`, `ParOutLivenessTest`, `DeterminismTest`
all pass; slow (`ExplorationTest` 25 tests, `CrossJvmDeterminismTest` 1 test) pass;
`null-check` on StateCache and GTS: zero errors, zero warnings.

## Next

1. Review; merge.
2. On branch `exploration-performance`, after merging master: re-pin the `hub-wander-*`
   rows (their pinned counts include the shortfall; `hub-wander-100` becomes 4951 /
   24 355).
3. Close gh #925.

## Key files

`lts/StateCache.java` (`isDone`, `registerOutPartial`, `registerTransienceChange`,
`propagate`, `searchFull`, `setFull`, `computeForwOuter`), `lts/GTS.java`
(`getLaunchIndex`), `lts/Status.java` (FULL javadoc), `lts/StateMatches.java`
(`advanceFrame`: the verdict exit before closure),
`test/lts/RecipeCompletenessTest.java`, `junit/samples/wander.gps`.
