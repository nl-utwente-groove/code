# Plan: module split (gui first, cli second)

*Findings and plan as of 2026-08-10 (rev 3, Claude sessions, reviewed by Arend).
Tracked in gh #887. This document is the spec for redoing the split fresh off
current master; do not resurrect the old working branches.*

## Goal

Split GROOVE into Maven modules: a **gui** module first (everything Swing/JGraph),
a **cli** module second. The core module must compile and run headless without any
GUI classes on the classpath.

## History

- `module-split-phase1` (Arend's original attempt) is superseded — master
  independently redid 3 of its 8 commits. The local branch still exists;
  whether to delete it is Arend's call.
- `module-split-phase2` (five mechanical-move commits, tip `91c07cbd4`) was
  abandoned and deleted on 2026-08-10 because master had moved too far under
  it. Its moves are small and trivially redone (listed below); do not attempt
  to rebase or cherry-pick the old commits.
- The exploration rework (merge `251fa2f08`, 2026-08-02) deleted
  `explore.encode`/`prettyparse`. That removed what was previously the biggest
  structural blocker: `explore.**` is now clean of gui/Swing dependencies.

## Blocker inventory

Full re-scan done 2026-08-02 on the merged exploration tree. File/line details
will have drifted; **re-verify each item before acting on it**.

### (a) Trivial deletions

- `GraphPreviewDialog` debug blocks in `grammar.model.HostModelMorphism` and
  `verify.BuchiGraph` (behind compile-time-false flags).
- `util.Groove` `ActionMap`/`InputMap` `toString` overloads (zero callers).
- `util.FontCheck`, `util.ShowFonts` (zero callers).
- `Version.main`: replace `JOptionPane` output with stdout.
- Javadoc-only imports: `prolog.GrooveEnvironment` (org.fife),
  `io.graph.LayoutIO` (org.jgraph).
- DEBUG-only `gui.Viewer` usage in `AutomatonBuildTest`/`RecipeTest`.

### (b) Mechanical moves

The five former phase-2 moves:

1. `SelectableListEntry` out of `gui.list.ListPanel` → `util.parse`
   (de-guis `FormatError`).
2. Ctrl/Prolog RSyntaxTextArea token makers (+ `.flex` sources +
   `prolog-syntax.xml`) → `gui.display`.
3. `DialogOracle` → `gui.dialog`. **Beware:** this move alone re-creates a
   grammar→gui edge in `OracleParser` — it must be paired with the oracle
   registry inversion (c1) below.
4. Visual constants: role colours → `util.Colors` with `Values` redirecting to
   them; keystrokes and JGraph line-style codes out of `util.line.LineStyle`;
   add `Options.getLineStyleKey`.
5. `SearchResult` → `util.parse` (de-guis `AspectGraph`).

Further moves:

- Font loading out of `gui.Options` into core `util` (de-guis `HTMLConverter`
  and `HTMLLineFormat`; the `explore.config.SettingParser` taint turned out to
  be already gone).
- `io.ExtensionFilter` → plain `java.io.FileFilter`, with a Swing adapter
  beside `GrooveFileChooser`.
- `grammar.groovy`: only `GraphManager` moves to the gui (its one consumer is
  `GroovyDisplay`). **Correction (2026-08-11):** the groovy *library* stays a
  core dependency — `grammar.rule.GroovyMatchChecker` compiles groovy match
  filters that run headless on the exploration path (`lts.MatchCollector`).
  The `Util.isGroovyPresent` probe was dead (groovy is a hard dependency) and
  has been deleted in phase 1.
- Root shims `Simulator`/`Viewer`/`Imager` → gui module
  (`Generator`/`ModelChecker`/`PrologChecker` shims are clean and stay in
  core). *Deferred to phase 5 — nothing to do while there is one module.*
- Prolog builtin `Predicate_show_graph` → registered by the GUI.
- `ImagerTest` → gui test set. *Deferred to phase 5 likewise.*

### (c) SPI/registry inversions — the three real design points

1. **Oracle registry**: `OracleParser` resolves `ValueOracleKind.DIALOG` via a
   registry or `ServiceLoader`; the GUI registers `DialogOracle` at startup
   (precedent: `JGraphExporters.register()` in the `Simulator` constructor).
   Note this sits on the hot path of every grammar load.
2. **Label render options**: `lts.RuleTransitionLabel` reads
   `gui.Options.SHOW_CALL_NESTING_OPTION` live → introduce a core
   label-render-options interface; the GUI registers the live implementation.
3. **Shutdown hook**: `util.cli.GrooveCmdLineTool.waitForWindows()` polls
   `java.awt.Window` → a pluggable shutdown hook installed only by GUI tools.

### (d) Redesign — largest single item

`io.store.SystemStore` extends `javax.swing.undo.UndoableEditSupport`; the whole
store edit protocol is Swing's undo model (~20 sites). Replace with a core edit
model plus a gui-side `UndoManager` adapter.

**Downgraded after a 2026-08-16 fact sweep.** The coupling proved shallow: the
inherited `UndoableEditSupport` machinery was used only for its listener list
(single registrant: `gui.SimulatorUndoManager`; `beginUpdate`/`endUpdate` never
called), no public mutator signature mentioned a Swing type, and non-gui code
never mutates a store outside four test classes. Since core keeps
`requires java.desktop` for awt data types anyway (which also covers
`javax.swing.undo`), the full redesign was unnecessary. Implemented instead as
a c1–c3-style inversion: `SystemStore` no longer extends
`UndoableEditSupport`; a core-owned `addEditListener(Consumer<Edit>)` replaces
the Swing listener list; `Edit` is Swing-free (the concrete edit classes still
extend `AbstractUndoableEdit`/`CompoundEdit` as an implementation detail);
`SimulatorUndoManager` bridges posted edits into its Swing undo history. The
`reload()`-bypasses-the-undo-channel and `undoSuspended` semantics are
preserved. The `EditType` action-name constants stay in core: they are the
presentation names of the edits, produced by `SystemStore` itself.

## Endgame: the Maven/module-info split

Once (a)–(d) are done, split the build: the gui module takes `jgraph`,
`jgoodies.looks`, `osxadapter`, RSyntaxTextArea, `batik`, `fop`. Core keeps a
non-transitive `requires java.desktop` for the ~20 files using awt
`Color`/`Point`/geom classes as plain data — accepted trade-off; defining our
own geometry types was judged not worth it.

Fold in the **release-reactor version handoff** at this point. The `release/`
poms form a separate reactor that receives the version via `-Drevision`; every
consumer (the `GROOVE - zip up local release` launch prompt, `release/do-all.sh`,
`.github/workflows/release.yml`) has to re-derive or re-enter the main pom's
`revision` property, and a mismatch makes the release reactor fail to resolve
the `nl.utwente.groove:groove` dependency. This bit in practice on 2026-08-12:
the launch prompt answered `7.5.4` against a pom saying `7.5.4-SNAPSHOT`. When
the split introduces a root aggregator pom, make it own `revision` and pull the
release modules into that reactor, eliminating the `-Drevision` handoff, the
launch prompt, and the derivation steps in the script and workflow.

The **checked-in Eclipse metadata** splits along with the build. `.classpath`,
`.project` and `.settings/` are tracked and m2e-generated; after the split the
root holds the aggregator project and core/gui/cli each need their own set.
Two things not to lose in the copy: `.settings/org.eclipse.jdt.core.prefs`
carries the whole null-analysis configuration (`annotation.nullanalysis=enabled`,
`nullSpecViolation=error`, the `org.eclipse.jdt.annotation` type names), so a
module that does not get it silently compiles without null checking; and
`.settings/org.eclipse.jdt.apt.core.prefs` likewise. The `launch/` configs name
the project explicitly (`PROJECT_ATTR` and, in `GROOVE - all JUnit tests`, a
`runtimeClasspathEntry projectName="groove"`) and have to be retargeted —
at which point the launch that runs the whole suite has to span three projects.

## Open decision: keep or drop `module-info` (2026-08-17)

**To be settled before phase 5 starts**, because the deferred root-shim move
depends on the answer. Undecided; Arend's call.

Keeping a modular build means splitting the single `module-info.java` (~70
`exports`, 30 `requires`) into one descriptor per Maven module. Three things
that are currently free stop being free:

- **Split packages become illegal.** A package may live in exactly one named
  module; two named modules containing the same package fail at boot-layer
  creation, exported or not. This hits the deferred shim move in (b) directly:
  moving `Simulator`/`Viewer`/`Imager` to the gui module while
  `Generator`/`ModelChecker`/`PrologChecker` stay in core splits package
  `nl.utwente.groove` across two modules. Under one module the move is a free
  choice; under JPMS it forces either all six shims into one module or two
  differently-named packages.
- **Resource packages must land whole.** `nl.utwente.groove.resource.icon`,
  `.font`, `.version`, `.antlr` (each with its `Stub.java`, which exists only
  so the package is real enough to `opens`) must each sit in one module, and
  that module must be the one opening it. A package straddling the boundary
  gives a null stream at runtime, not a compile error.
- **`requires transitive` becomes load-bearing** across our own modules: every
  core-exported signature mentioning jgraph, picocli, jdt.annotation or awt
  types forces `transitive` or the downstream module will not compile. A pass
  over all exports. Likewise the eight automatic modules (`antlr.complete`,
  `antlrworks`, `jgraph`, `ltl2buchi`, `osxadapter`, `batik.all`,
  `xmlgraphics.commons`, `fop.core`) get requires-d per module, with the derived
  name having to agree between javac, JDT and the runtime in each.

The recurring Eclipse cost: maven-compiler-plugin and JDT compute modulepath
vs. classpath placement independently, m2e mediates via the `module` classpath
attribute, and hand-fixes to `.classpath` do not survive a Maven > Update
Project — so IDE/`mvn` disagreements have to be resolved in the pom. Test
sources are patched into the main module, which is why
`launch/GROOVE - all JUnit tests.launch` carries per-test-package `--add-opens`
(f4908e152); after the split that becomes one such list per module, plus
`--add-exports`/`--add-opens` for any test reaching across our own module
boundaries.

Against all that: **the shipped product does not use JPMS at runtime**.
`release/jpackage/build-installer.sh` packages with `--main-jar` plus a
jdeps-computed `--add-modules`, i.e. GROOVE runs from the classpath in the
unnamed module; `module-info.class` is inert there and the `opens` clauses have
no effect in the installed app. The descriptor buys compile-time dependency
discipline and IDE/`mvn` launch enforcement — and the Maven split delivers the
same direction of enforcement (core cannot see gui) on its own, coarser but
sufficient for the goal stated at the top of this document.

So the alternative is to **drop `module-info` as part of phase 5** and give each
module an `Automatic-Module-Name` manifest entry instead. That makes the shim
split package legal, removes the add-opens maintenance and the whole
modulepath/classpath disagreement class. It costs the enforced `exports`/`opens`
encapsulation, and forecloses ever jlink-ing a modular runtime image (jpackage
as used here does not need one).

## IDE choice: Eclipse stays (2026-08-17)

Considered and rejected: doing the split in IntelliJ instead. IDEA does handle
multi-module Maven better on every axis that bears on phase 5 — its project
model has real modules (Eclipse has a flat workspace of peers that m2e makes
look like a reactor), it has no lifecycle-mapping concept so generated sources
need no connector, it can delegate builds to Maven and therefore cannot
disagree with it about the module path, and it derives JUnit command lines from
the module model rather than needing the hand-written per-test-package
`--add-opens`.

None of that outweighs **JDT's null analysis, which is Eclipse-only** and is
currently the only place the `@NonNullByDefault` discipline is checked at all,
the Maven build not running it. IDEA's nullability inspections are a different
and weaker analysis with no `@NonNullByDefault` equivalent. The lever for the
Eclipse-side friction is the `module-info` decision above, not the IDE.

Cheap use of IDEA anyway: it imports from the pom and needs nothing checked in,
so during phase 5 it can serve as a second opinion — a split that compiles
there but not in Eclipse localises the problem to m2e's classpath computation
rather than to the pom. `.idea/` is already tracked (`modules.xml`,
`compiler.xml`, `jarRepositories.xml`) and, unlike `.classpath`, is regenerated
from the pom, so it needs no hand-migration.

Also rejected: splitting into three Eclipse projects ahead of the Maven split.
m2e regenerates `.classpath` from the pom, so hand-written projects last until
the next Update Project unless they are un-managed (losing dependency
resolution); Eclipse projects cannot share a source tree, so the trial requires
either the physical package move — which is the expensive half of the split,
after which three Maven modules is the small remaining step — or linked folders
that neither `mvn` nor CI checks. A one-shot import scan of `src/main/java`
outside `gui` on 2026-08-17 found no unexpected core→gui edges: only the three
root shims deferred above, plus `SystemStore`'s `javax.swing.undo` and
`util.Fonts`' `UIManager`, both inside core's accepted `requires java.desktop`.
A standing architecture test was judged not worth adding this late: once core
is its own Maven module javac enforces the boundary permanently and catches
more than an import scan does. Re-run the scan once immediately before starting
phase 5 instead.

## Phasing

Each phase is a separate branch/PR, in dependency order:

1. Deletions (a) — no design content, mergeable immediately.
2. Mechanical moves (b), with move 3 bundled with inversion (c1).
3. Remaining inversions (c2), (c3).
4. `SystemStore` redesign (d).
5. Maven/module-info split, including the release-reactor version handoff.
   Settle the keep-or-drop-`module-info` decision above before starting.

## Status (2026-08-16)

- Phase 1 (deletions, incl. the dead `Util.isGroovyPresent` probe) and
  phase 2 (moves plus inversion (c1): oracle registry in `OracleParser`, a
  `GrooveEnvironment.addPredicates` extension point for `show_graph`, a
  `util.Fonts` initializer hook for LAF-before-fonts ordering) are merged
  into master. Behavioural changes, all deliberate: headless runs no longer
  initialise a Swing LAF via HTML formatting, `show_graph` and the dialog
  value oracle are Simulator-only.
- Phase 3 done on branch `label-render-options`: inversion (c2) —
  `RuleTransitionLabel` consults a static `BooleanSupplier` hook instead of
  `gui.Options`, with the Simulator plugging in the live option (a dedicated
  label-render-options interface was rejected: only this one flag is read
  from core) — and inversion (c3) — `GrooveCmdLineTool.tryExecute` runs a
  pluggable no-op shutdown hook, with `gui.GuiShutdownHook` contributing the
  former wait-for-windows loop, registered by the `Viewer` and batch-`Imager`
  mains.
- Phase 4 done on branch `store-edit-inversion`: the `SystemStore` undo
  coupling turned out shallow (see the note under (d)) and was resolved by
  inverting the notification rather than redesigning the edit model. Note:
  the undo/Edit API has no test coverage; the change was gated by the fast
  suite (which mutates stores headless); a manual Simulator undo/redo smoke
  test is part of the review.
- A utility-class rearrangement (2026-08-16, merged) preceded this phase:
  `util.Groove` dissolved into `io.Groove`/`util.Resources`/`Strings`/
  `FileUtils`, `io.Util` split into `util.Unicode` + `io.FileUtils`,
  `gui.look.Values` colour re-exports removed.
- Remaining: phase 5 (build split + deferred shim/test moves + release-reactor
  version handoff), blocked on the `module-info` decision recorded above.
