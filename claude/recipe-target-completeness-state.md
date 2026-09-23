# Recipe target completeness (gh #925): handoff state

Transient handoff note. Delete when the branch is merged.

## Goal

Fix gh #925: a recipe launch on a cyclic transient region in which every state is a
recipe end got only some of its end states as recipe targets under the closing
strategies (bfs, dfs); the linear strategy found them all. The other half of gh #924,
whose fix (local propagation in `lts.StateCache`) under-approximated here where the code
before it over-approximated.

## State as of 2026-09-23

Branch `recipe-target-completeness` off master `f8ab8a880`, worktree
`.claude/worktrees/recipe-target-completeness`. Fix done, awaiting review.

**Mechanism.** A state that leaves its recipe through a verdict becomes steady before
it is closed: the star `(moveNext | movePrev)*` exits with the same verdict either way,
so `StateMatches.advanceFrame` moves the actual frame to the outer location as soon as
the matches are computed, and `registerTransienceChange` runs with the state still
open. Its rule transitions are nonetheless inner steps of the run (computed from the
prime frame). The gh #924 bookkeeping treated a steady state as done for its
predecessors (`notifyDone` at transience 0), so a predecessor whose successors were all
steady became full at its own closure and dropped `innerPreds` and `launches`; targets
found deeper in the region afterwards could not pass through it (`addTarget` skips full
predecessors). On `chain-4-2`: s6 was found via s5 while s3, the only route back to the
launch target s1, was already full.

**Fix** (commit "Resolved gh #925"): `StateCache.isDone(state)` = full, or steady with
an outer prime frame. Used by the pending registration in `registerOutPartial`, by the
fallback `searchFull`, and (as the prime-frame condition) by `registerTransienceChange`,
so a steady inner-prime state is waited for like a transient one and full-ness implies
target completeness. `Status.Flag.FULL` javadoc updated accordingly.

**Gate**: the two wander cases in `RecipeCompletenessTest` (commit "Added the wander
cases"), failing before, passing after in all four variants. All three strategies now
give 191 states / 190 recipe transitions on `chain-20-2`.

**Cost, measured** (warm JVM, `bfs`, this machine):

| grammar / graph | master | fixed |
|---|---|---|
| fibonacci `fib-22` (acyclic region) | 1.0 s | 1.0 s |
| hub `chain-100-2` wander (4951 states) | 4.4 s, 19 602 trans | 8.9 s, 24 355 trans |
| hub `chain-60-2` wander-alap (1771 states) | 3.6 s, 6 068 150 trans | 3.9 s, 6 068 977 trans |
| hub `chain-200-2` wander (19 901 states) | 90 s | out of heap at 6 GB |

The extra cost is the backward target propagation itself: with the correct bookkeeping
every state of a cyclic region accumulates every reachable target in its `forwTarget`
set, N²/2 set entries for wander. A blocked-by marker on `searchFull` (a search that
aborts on an open state marks the visited states, later searches stop there) was tried
and changed nothing, so the fallback search is not the bottleneck; it was dropped.
Master was cheap on this shape only because it was wrong.

## Decision (Arend, 2026-09-23): implement option 1, forward launch propagation

The correctness fix stands; the quadratic cost of the backward target propagation on
cyclic same-verdict regions is to be removed by propagating launches forward instead.
Rejected: option 3 (indexed `BitSet` target sets: memory only, time stays quadratic)
and option 2 (SCC sharing: incremental SCC detection under arbitrary exploration
orders). Continue on this branch: the fix alone slows `hub-wander-100` two-fold, and
the redesign replaces the very code the fix touched.

### Design

Per inner-prime, non-full state cache S:

- `launches`: insertion-ordered set of the launch transitions whose runs pass through
  S. Replaces the current `launches` list (direct launches into S only), `innerPreds`
  and the `forwTarget` set of non-full states.
- Invariant: for every L in `launches(S)` and every recipe target t reachable from S
  over the inner non-launch steps registered so far, the recipe transition (L, t)
  exists.

Operations (all in `registerOutPartial` / `registerTransienceChange`; the absence and
full-ness bookkeeping — `preds`, `pendingCount`, `openCount`, `isDone`, `searchFull`,
`notifyDone` — stays as it is):

1. `propagate(L)` on S, an explicit-stack forward walk: if S is full, emit (L, t) for
   every t in `getForwTarget()` (the existing on-demand `computeForwOuter` for full
   states) and stop; if L is already in `launches(S)`, stop (S has delivered everything
   reachable to L and will deliver what comes later); otherwise add L, emit
   (L, `RecipeTarget(S)`) if S is currently not inner (it left the recipe by verdict),
   and for every registered inner non-launch step S→X: X inner-prime → continue the walk
   at X; else emit (L, `RecipeTarget(step)`).
2. Launch L→S registered: S not inner-prime → single-step recipe transition as now;
   else `S.propagate(L)`.
3. Inner non-launch step P→T registered: for each L in `launches(P)`: T inner-prime →
   `T.propagate(L)`; else emit (L, `RecipeTarget(step)`).
4. S turns from inner to outer (`registerTransienceChange`, `knownInner` flips): emit
   (L, `RecipeTarget(S)`) for each L in `launches(S)`.
5. `setFull`: drop `launches`; later arrivals go through case 1's full branch.
   `forwTarget` survives only as the lazily computed cache of full states.

Cost: each (launch, region state) pair is visited once, Σ_L |region(L)|; for `wander;`
linear, for `wander-alap` the output size. Emission dedup is not needed (a launch enters
a state once, a state turns outer once; `GTS.addTransition` ignores duplicates anyway).
A cleared cache of a closed-but-not-full state loses `launches` as it loses `preds`
today; document, do not defend. Emission order changes (by launch, then walk order):
watch `DeterminismTest`, `CrossJvmDeterminismTest` and the `ExplorationTest` counts.

### Gates and measurements

`RecipeCompletenessTest` (its `getTargets` is independent of the bookkeeping),
`RecipeTransitionTest`, `RecipeTest`, `OneStepRecipeTest`, `RecipeNullArgsTest`,
`ParOutLivenessTest`, the transactions cases, `DeterminismTest`,
`CrossJvmDeterminismTest`, `ExplorationTest`, `null-check`. Then time, warm JVM, against
the table above: fib-22 (must stay 1.0 s), chain-100-2 wander (target: back near
master's 4.4 s), chain-60-2 wander-alap (3.9 s), chain-200-2 wander (must fit in 6 GB,
target near master's 90 s). Harness: a scratch `main` that loads the grammar with
`SystemStore.newGrammar`, sets the host and control resources active, and plays
`LegacySyntaxParser.parse("bfs final 0").newExploration(gts, null)` three times, run
against `target/classes` plus the output of `mvn dependency:build-classpath`; the hub
grammar with the `chain-*` graphs is `junit/performance/hub.gps` in the
`exploration-performance` worktree.

## Next

1. Implement the design above on this branch (fresh session).
2. Review; merge.
3. On branch `exploration-performance`, after merging master: re-pin the `hub-wander-*`
   rows.

## Key files

`lts/StateCache.java` (`isDone`, `registerOutPartial`, `registerTransienceChange`,
`searchFull`, `addTarget`), `lts/Status.java` (FULL javadoc),
`lts/StateMatches.java` (`advanceFrame`: the verdict exit before closure),
`test/lts/RecipeCompletenessTest.java`, `junit/samples/wander.gps`.
