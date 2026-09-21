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
2. `view-controller-context` (tip `40305603e`, 9 commits over 1): the
   composition rework. `GraphViewContext<G>` in `gui.view` is what a graph view
   needs of its host; `SimulatorViewContext` + `AspectViewContext` +
   `LTSViewContext` in `gui.display` implement it; controllers stay classes.
   Warnings 24 → 0; fast suite, GUI tests, null analysis clean per its note.
   **Awaiting Fable review** (see below).
3. `view-controller-interfaces`: the first, interface-based attempt, superseded.
   Delete after 2 is merged. Its note records a yFiles compile result that the
   rework branch shows to have been wrong.

**Next.**

- Fable review of `view-controller-context` (chip / prompt in the session
  hand-over message). Points to weigh: the four deviations listed under "Item 1
  as built"; the wide-interface smell (`getPopupItems`/`getExportItems`/
  `getExploreItems`/`getGotoItems`, empty by default, one kind each); null-context
  degradation for `Imager`, `Viewer`, `GraphPreviewDialog`; attach/detach
  symmetry of listener registration; behaviour at the four ex-`controllerOf`
  sites; whether `gui.view` still references any Simulator type in bodies or
  imports; the yFiles finding (main sources fail at six sites, all session B's).
- Session B in `yfiles-lib`, branch `view-controller-context`, prompt in
  `module-exports.md` under "Session B"; add the six sites the rework's
  verification section names, and a test fixture instead of constructing
  `gui.display` classes from backend tests.
- Remaining open items (CLI package, `control.template` split, `util.collect`)
  are listed in `module-exports.md` and are not release-critical.

**Key files.** `src/main/java/module-info.java`; `gui/view/GraphViewContext.java`,
`gui/view/GraphViewController.java` and kinds; `gui/display/SimulatorViewContext.java`,
`AspectViewContext.java`, `LTSViewContext.java`; `claude/module-exports.md`.

**Open decisions.** None blocking. Naming (`GraphViewContext` / `*ViewContext`)
was the session's choice and is open to the reviewer.
