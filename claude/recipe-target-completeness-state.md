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

## Open decision: the quadratic shape

Whether to accept N² on wander-like shapes (one recipe whose region is the whole,
cyclic state space) or redesign. Alternatives considered, none implemented:

1. **Forward launch propagation**: keep per region state the set of launches whose
   runs pass through it, emit a recipe transition when a launch set meets a target,
   deliver already-known targets to a newly arriving launch by a pruned forward walk.
   Linear for `wander;` (one launch), N² for `wander-alap` (N² is then the output
   size). Mirrored worst case: many launches converging on a small region.
2. **SCC sharing**: all states of a strongly connected region component have the same
   reachable targets and the same launches; one shared set per SCC makes wander linear
   in both directions. Needs incremental SCC detection under arbitrary exploration
   orders (GUI, linear), which gh #924 deliberately avoided.
3. **Compact sets**: a global index for `RecipeTarget`s and a `BitSet` per state cuts
   memory about 50× (chain-200-2 would fit) but keeps the quadratic time.

## Next

1. Review; merge.
2. On branch `exploration-performance`, after merging master: re-pin the `hub-wander-*`
   rows (their pinned counts include the shortfall; `hub-wander-100` becomes 4951 /
   24 355, about 9 s).
3. Decide on the open decision above; if a redesign is wanted, file it as a separate
   issue rather than extending this branch.

## Key files

`lts/StateCache.java` (`isDone`, `registerOutPartial`, `registerTransienceChange`,
`searchFull`, `addTarget`), `lts/Status.java` (FULL javadoc),
`lts/StateMatches.java` (`advanceFrame`: the verdict exit before closure),
`test/lts/RecipeCompletenessTest.java`, `junit/samples/wander.gps`.
