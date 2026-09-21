# Module exports: hand-over state (2026-09-21)

Short state for the next session; the full account is `module-exports.md`.

**Goal.** Make `module-info` a deliberate API contract for 8.0.0 (first release
that keeps the descriptor), with `-Xlint:exports` as the guard, and close the
one leak that guard still showed: Simulator types in the exported backend SPI
tier `gui.view`.

**Done, three branches, to be merged in this order.**

1. `module-exports` (tip `92a6669a8` + note commit): lint in the pom, exports
   cut from 74 to ~50 (no `gui.*` but the SPI tier; `prolog.builtin.*` only to
   `gnuprologjava`; `explore.engine` exported as strategy extension point),
   leaks closed in `FormatException`, `verify`, `CtrlLoader`
   (`control.Invisibility`, `addControl` void, `getTermPrototype`), `FileType`
   moved to `util` (not `io`: `LayeringTest`). 26 warnings left, all `gui.view`.
   Reviewed by Arend.
2. `view-controller-context` (9 commits over `2678fe43c`, plus the review's 5): the
   composition rework. `GraphViewContext<G>` in `gui.view` is what a graph view
   needs of its host; `SimulatorViewContext` + `AspectViewContext` +
   `LTSViewContext` in `gui.display` implement it; controllers stay classes.
   Warnings 24 → 0; fast suite, GUI tests and yFiles gate re-run by the Fable
   review of 2026-09-21, which committed four fixes on the branch (unused
   imports, `getProperties` back to the context's grammar and private,
   `@AIGenerated` on the new types, the editor assertion on `setLevelTree`)
   and rewrote the session B prompt. **Two decisions open**, see below.
3. `view-controller-interfaces`: the first, interface-based attempt, superseded.
   Delete after 2 is merged. Its note records a yFiles compile result that the
   rework branch shows to have been wrong.

**Next.**

- Decide the two open points below; the first changes the constructor that
  session B's tests call, so decide before B starts.
- Session B in `yfiles-lib`, branch `view-controller-context`, prompt in
  `module-exports.md` under "Session B", made concrete by the review: the six
  sites, the listener registration to delete, the test fixture.
- Remaining open items (CLI package, `control.template` split, `util.collect`)
  are listed in `module-exports.md` and are not release-critical.

**Key files.** `src/main/java/module-info.java`; `gui/view/GraphViewContext.java`,
`gui/view/GraphViewController.java` and kinds; `gui/display/SimulatorViewContext.java`,
`AspectViewContext.java`, `LTSViewContext.java`; `claude/module-exports.md`.

**Open decisions**, from the Fable review (2026-09-21), both Arend's:

- The aspect controller's `GraphRole` + `forState` constructor (review
  finding 3). Proposal: drop `forState` from the controller. Its only remaining
  user is `ExportAction.getActionName`, and the context creates that action
  and can tell it; the controller then takes a `GraphRole` alone, which is the
  abstraction the look values and the cells already consume. `DisplayKind`
  stays in `gui.display` by design, not by necessity: its only unexported
  dependency is `gui.Icons`, in the body, so it could move without a warning,
  but it enumerates the Simulator's tabs, which is not view API.
- Naming. "Host" is rejected (clashes with host graph). Proposal: keep
  `GraphViewContext`, and rename the implementations with a uniform prefix,
  `SimulatorViewContext<G>`, `SimulatorAspectContext`, `SimulatorLTSContext`,
  so that `AspectViewContext` no longer reads as part of the `AspectView*`
  cell family of `gui.view`. Alternative word, if one is wanted:
  `GraphViewSite`, after Eclipse's `IViewSite`, the workbench's handle given
  to a view part. The javadoc says "host" throughout and follows the decision.
