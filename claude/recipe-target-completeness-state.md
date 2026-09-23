# Recipe target completeness (gh #925): handoff state

Transient handoff note. Delete when the branch is merged.

## Goal

Fix gh #925: a recipe launch on a cyclic transient region in which every state is a
recipe end gets only some of its end states as recipe targets under the closing
strategies (bfs, dfs); the linear strategy finds them all. The other half of gh #924,
whose fix (local propagation in `lts.StateCache`) under-approximates here where the code
before it over-approximated.

## State as of 2026-09-23

Branch `recipe-target-completeness` off master `f8ab8a880`, worktree
`.claude/worktrees/recipe-target-completeness`. Done: the fixture
`junit/samples/wander.gps` (rules `moveNext`/`movePrev`, programs `wander` = `wander;` and
`wander-alap` = `alap wander;`, start graphs `chain-4-2`, `chain-6-2`, `chain-20-2`) and
this file. No code touched. Reproduction:

```
Generator -s bfs    junit/samples/wander.gps chain-4-2   # 7 states, 5 recipe transitions
Generator -s linear junit/samples/wander.gps chain-4-2   # 7 states, 6
```

(`-o <file>.aut` exports the LTS; the missing target is the placement discovered last.)
`RecipeCompletenessTest` with the cases `("junit/samples/wander.gps", "wander",
"chain-20-2", true)` and `(..., "wander-alap", "chain-20-2", true)` added to its `CASES`
fails all four variants: "public state s38 has no incoming public transition".

## Next

1. Add the two cases to `RecipeCompletenessTest` (they are the gate; commit them first,
   failing).
2. Diagnose in `lts.StateCache`. Facts so far: `ClosingStrategy.addExplorable` and
   `doNext` push transient states on a `transientStack`, so the region is a nested
   depth-first sub-exploration under every closing strategy, not breadth-first; the
   linear strategy pushes them on its own stack and switches the record's collapse flag
   off in `prepare`, yet still explores 7 states on `chain-4-2`, so what "collapse off"
   changes for the region is the first thing to establish. Unverified reading: a region
   state leaves the recipe at its closure (transience 0), its predecessors become full
   and drop their launch bookkeeping, and targets found deeper afterwards are no longer
   passed to full predecessors (`addTarget` skipping full states). The pre-gh #924 code
   (`b94a0b20a`) over-approximated on the same grammar (343 targets at n = 20).
3. Fix, keeping gh #924's linear cost on acyclic regions (`fib-22` of the benchmark,
   2.5 s) and its open point on repeated fallback searches in mind.
4. Gates: the two wander cases and the rest of `RecipeCompletenessTest`, `RecipeTest`,
   `DeterminismTest`, the `control` and `transactions` tests, `grammar-smoke`; then on
   branch `exploration-performance` re-pin the `hub-wander-*` rows (their counts include
   the shortfall) after merging master.

## Key files

`lts/StateCache.java` (the gh #924 propagation: `notifyDone`, `computeForwOuter`,
`addTarget` and the launch/target bookkeeping), `explore/engine/ClosingStrategy.java`
(`transientStack`), `explore/engine/LinearStrategy.java`,
`test/lts/RecipeCompletenessTest.java`, `claude/statecache-transient-closures-state.md`
in the history of master (the gh #924 session's handoff, deleted at its merge).
