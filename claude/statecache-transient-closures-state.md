# StateCache transient closures (gh #924): handoff state

Transient handoff note for a fresh session. Delete when the branch is merged.

## Goal

Make recipe and atomic-block exploration linear in the body length: replace the
transitive closures that `lts.StateCache` keeps per transient/inner state by local
propagation over direct transient predecessor edges. Issue: gh #924 (the full
diagnosis, the proposed design and the gates). Finding 3.12 of
`claude/exploration-performance.md` on branch `exploration-performance` has the
file:line references and the profile breakdown; the fix lives here, on its own branch
off master, because it is independent of the performance note.

## State as of 2026-09-22

Branch `statecache-transient-closures` off master `ed8b740cf`, worktree
`.claude/worktrees/statecache-transient-closures`. Nothing implemented yet.

## Design constraints, from the diagnosis

- Consumers of the bookkeeping: `getAbsence()` on the fly in
  `lts/StateMatches.advanceFrame` (target absence against the frame transience);
  `isFull` in `GTSCounter`, `RecipeTransition.getSteps` and the GUI trees; the
  launch-to-target pairing that calls `addRecipeTransition`.
- Absence of a state = lowest transience over its known reachable states (javadoc of
  `GraphState.getAbsence`); it only decreases. Full = closed and every reachable
  transient state closed; then the absence is final and stored in the status.
- Property to preserve (Arend, from an earlier bug in this area): a recipe transition
  must be created once the recipe run is fully explored, for every launch and every
  target, including targets discovered after the launch state closed.
- Cycles inside transient regions (`alap`/`while` in a recipe or atomic block) defeat
  the local "all direct successors full" rule; see the fallback in the issue.
- The closures came with the September 2024 transient/inner rework (`6d4fd0814`,
  `68daacf6c`, `49fe72f87`); `9495647a2` (2025-03) only added commented-out assertions.
  Read `49fe72f87` before redesigning: its message says "bugs removed".

## Next

1. Read `lts/StateCache.java` (init at 455, `registerOutPartial` at 564,
   `registerClosure`/`testSetFull`/`registerTransienceChange` at 640-708) and
   `lts/StateMatches.java`, and write down the invariants the sets encode.
2. Design the local propagation (direct predecessor lists per transient state; full
   notification; absence decrease propagation; target propagation to launches; the
   cycle fallback), then implement in one or a few reviewable commits.
3. Gates: `grammar-smoke`, `mvn test` (control and transactions tests), `DeterminismTest`
   via the `determinism-check` skill, GUI tests (the trees read `isFull`), `null-check`.
4. Measure before/after with the exploration benchmark on branch
   `exploration-performance`: rows `fib-12`, `fib-15`, `fib-function-15`,
   `recipes`-based rows if any; the harness recipe is in that branch's state file
   (`claude/exploration-performance-state.md`, "Measured on the desktop" and the
   profiling fact). Expect `fib-15` to drop from 23.5 s to about the function
   variant's time.

## Key files

- `src/main/java/nl/utwente/groove/lts/StateCache.java`, `StateMatches.java`,
  `AbstractGraphState.java` (`setFull`, status flags), `RecipeTransition.java`,
  `GTSCounter.java`.
- `src/main/java/nl/utwente/groove/control/instance/Frame.java` (transience, inner).
- Tests: `src/test/java/nl/utwente/groove/test/control/`, `junit/control/`,
  `junit/samples/recipes.gps`, `junit/samples/transactions.gps`.
