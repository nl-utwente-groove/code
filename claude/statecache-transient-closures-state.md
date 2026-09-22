# StateCache transient closures (gh #924): handoff state

Transient handoff note. Delete when the branch is merged.

## Goal

Make recipe and atomic-block exploration linear in the body length: replace the
transitive closures that `lts.StateCache` kept per transient/inner state by local
propagation over direct predecessor edges. Issue: gh #924 (diagnosis, design, gates).

## State as of 2026-09-22: ready for review (second round)

Branch `statecache-transient-closures` off master `ed8b740cf`, worktree
`.claude/worktrees/statecache-transient-closures` (detached for review). Commits, in order:

1. `Fixed the recipe target recomputation of recreated full-state caches`: latent master
   bug (`computeForwOuter` tested `known.add` inverted; GC-timing dependent) plus
   `test/lts/RecipeCompletenessTest`, the gate: every launch has a recipe transition to
   every reachable target, every state ends up full, every public state is publicly
   reached, for the recipe and atomic-block samples under bfs/dfs with and without a
   simulated GC sweep of the collectable caches.
2. `Replaced the transient closures of StateCache by local propagation (gh #924)`: the
   redesign; body explains the scheme, the cycle fallback, the deliberate behaviour
   changes and the measurements (fib-15: 33 s to 0.27 s, 4.7 GB to 98 MB allocated,
   identical counts).
3. `Stored the recipe transitions of closed states with the state`: master bug found
   by the collapsing test after the traversal change: recipe transitions reach the
   launch source after its closure and lived only in its cache, so a GC of a full
   source lost them.
4. `Followed recipe runs through states that left the recipe by a verdict`: Arend's
   decision on the review question of round one. `recipe r() { newA; delB*; }` ends by
   verdict in the state after `newA`, which still has the `delB` step to a further
   recipe end; both are results now. Master created a spurious recipe transition out
   of the verdict-ended state instead. New fixture `junit/samples/recipes.gps/star.gcp`
   (`newB; r;` main), in the completeness test.

Gates on the tip (commit 4), all green: ExplorationTest (25), GUI tests (20 in 8
classes), full suite with slow tests (942, only the known GrammarsTest worktree skip),
DeterminismTest + CacheReconstructionTest, PersistenceTest, TraceShapeTest, ecj null
analysis on the touched files.

## Open points

- **Aborted fallback searches.** On cyclic transient regions the forward search can be
  repeated once per successor notification, bounded by the closed non-full region. Not
  observed to matter; a memoisation of the blocking open state would bound it.
- **Null annotations.** `StateCache` is still unannotated (its lazily initialised fields
  would need `@Nullable` throughout); left out as a drive-by. Possible follow-up.
- **Explicit cache clearing of closed non-full transient states** (as `DeterminismTest`
  does for all closed states) loses the predecessor lists, as it lost the old sets; the
  GC cannot do this since non-full caches are strongly referenced. Documented in the
  code comment.

## After merging

- Branch `exploration-performance`: finding 3.12 and the fibonacci rows can be
  re-baselined; the recipe family can get a long-tier size.
- Close gh #924 with the numbers from the commit body of commit 2.
