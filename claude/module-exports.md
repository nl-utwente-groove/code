# Module exports: what the library promises

*Status (2026-09-22): `module-exports` and its follow-up `view-controller-context`
(item 1) are merged to master, session B is merged into yfiles-lib `main`, and
master compiles with no `exports` warning; the descriptor comment was brought in
line with this note the same day. Items 2 to 4 under "Open items" remain open;
item 2 is in progress on branch `cli-package`.*

*Branch `module-exports`, 2026-09-20. Context: 8.0.0 is the first
release whose jar keeps its `module-info` (earlier releases stripped it because of
the shadowed libraries), so the export list becomes the API contract of the
library for the first time. Retracting an export later is a breaking change,
extending one is not, so the list starts small.*

## Principle

`exports` is the only stability signal the module system offers. The installed
application runs from the class path, where the descriptor is ignored, so the
export list affects only consumers that put GROOVE on the module path. Those can
reach an unexported package with `--add-exports` while waiting for a proper
export. Being strict is therefore cheap and reversible.

One qualification, found by the test suite rather than the compiler: surefire
runs GROOVE *on the module path*, and there the list is a runtime constraint as
well. Two cases exist today. An extension jar (`util.Extensions`, exercised by
`ExtensionsTest`) is loaded through a child `URLClassLoader` into the unnamed
module and implements `gui.view.GraphBackend`, which fails with
`IllegalAccessError` unless `gui.view` is exported; and the Prolog engine
instantiates the predicate classes of `prolog.builtin.*` itself, so those need
an export qualified `to gnuprologjava` or every predicate silently goes missing
(`PredicateTests`, eleven failures). So: after any change to the list, run the
fast suite, not only `mvn compile`.

The set must be closed under signature reachability: a type of an unexported
package that occurs in an exported signature is usable only through its exported
supertypes. `javac -Xlint:exports` (now in the pom) reports each such leak, so a
change to the list is checked by `mvn compile` and reading the warnings. Note
that javac stops after 100 warnings by default; when iterating on a large cut,
add `-Xmaxwarns 1000` temporarily, or the list is silently truncated (it was, on
the first cut here).

## What is exported, and why

| Tier | Packages |
|---|---|
| Pipeline | root, `io.store`, `io.graph`, `io.external`, `grammar`, `grammar.model/aspect/host/type/rule`, `graph`, `graph.plain/iso/layout`, `match`, `transform`, `transform.oracle`, `lts`, `explore`, `explore.config/feature/result/engine`, `verify`, `prolog`, `prolog.builtin` |
| Data values | `algebra`, `algebra.syntax`, `annotation` |
| Control | `control`, `control.template/instance/graph` |
| Utilities | `util`, `util.parse/line/cache/collect` |
| CLI | `cli` (since 2026-09-22; before that `util.cli` and the tools in `explore`, `prolog`, `algebra`) |
| Backend SPI | `gui.view`, `gui.view.cell`, `gui.look`, `gui.layout` |
| Qualified | `prolog.builtin.algebra/graph/lts/rule/trans/type` to `gnuprologjava` only |

Reasons for the less obvious ones, all forced by signature reachability:

- `algebra.syntax` (`Expression`, `Variable`, `SortMap`, …) is the attribute
  expression model, mentioned by `algebra`, `control`, `grammar.aspect/rule/host/type`
  in some 45 signatures. It is core, not parser internals; only `ExprTree`
  (reachable through `AspectContent.ExprContent`) is really syntax.
- `annotation`: `Help`/`HelpMap` are returned by `EdgeRole`, `AspectKind`,
  `RegExpr`; the package also holds `@UserOperation`/`@UserType`, which users of
  the user-signature feature need anyway.
- `control.instance` (`Frame`, `Step`, `Automaton`) and `control.template`
  (`Switch`, `Template`) are exposed by `lts` (`GraphState.getFrame`,
  `RuleTransition.getStep/getSwitch`) and `Grammar.getControl`; `control.template`
  holds only the run-time side of the compiler since 2026-09-22 (see "Item 3 as
  built"). `control.graph` is `Automaton.toGraph`.
- `match`: `Proof` is the anchor of `RuleEvent`/`RuleTransition`.
- `util.cache`: `AGraph` extends `AbstractCacheHolder`. Four classes, the
  scalability mechanism, fine to export.
- `util.collect`: only four types leak — `TreeHashSet` (`GTS.StateSet`,
  `StoreFactory.createEdgeStore`), `DeltaMap` (`GrammarProperties.getRuleEnabling`,
  `Properties.QUAL_NAME_DELTA_MAP`), `SmallCollection` (`PartitionMap.get`),
  `AbstractComparator` (the `Action` comparator constants). Narrowing those four
  signatures would let the whole 31-class package go unexported; not done.
- `util`: besides the obvious (`QualName`, `Property`, `Pair`, …) it now holds
  `FileType`, which occurs in 20 signatures of `io.external`, `io.graph` and
  `grammar.model`, and its `ExtensionFilter`.
- `cli`: the command-line tools and their picocli base `GrooveCmdLineTool`,
  gathered from `util.cli`, `explore`, `prolog`, `algebra` and `io` on
  2026-09-22 (see "Item 2 as built"). picocli was already `requires transitive`,
  so the package adds no new third-party surface.
- `prolog.builtin`: `GrooveEnvironment.addPredicates(Class<? extends GroovePredicates>)`
  is the extension point for predicate sets. The subpackages with the concrete
  predicates are exported to `gnuprologjava` only, which instantiates them.

Not exported, deliberately:

- **`gui.*` except the backend SPI tier.** The Simulator composition
  (`gui`, `gui.action`, `gui.display`, `gui.dialog`, `gui.menu`, `gui.tree`,
  `gui.list`, `gui.prolog`, `gui.jgraph`, `gui.export`, `gui.export.util`) is
  internal wiring under active rewrite (gh #909) and would leave the artifact
  under the module split (gh #887). `gui.export.util` was the only reason
  `batik.all`, `fop.core`, `xmlgraphics.commons` and `jdk.xml.dom` were
  `requires transitive`; they are plain `requires` now. The tier that is
  exported (`gui.view`, `gui.view.cell`, `gui.look`, `gui.layout`) has to be,
  for the runtime reason above, but it is not clean: `GraphViewController` and
  its four subclasses take a `@Nullable Simulator` and expose `SimulatorModel`,
  `ActionStore`, `Options`, the menus, `LabelTree`, `RuleLevelTree`,
  `DisplayKind` and concrete action classes in 30 public signatures (the
  `-Xlint:exports` warnings left on the branch), and the yFiles backend uses a
  good part of that (`getSimulatorModel`, `getActions`, `getLayoutAction`,
  `getAddPointAction`, `getLabelTree`, `getOptions`, `getModeButton`,
  `createPopupMenu`). Two consequences. Exporting the packages those signatures
  mention would drag in the whole Simulator, so they stay unexported and the
  warnings stay visible until the controller seam is cleaned (item 1, since done).
  And on the module path the yFiles add-on itself would hit `IllegalAccessError`
  the moment it touches `ActionStore` or `Options`; it does not today, because
  the installed application and the add-on's own tests run from the class path,
  but it is one more reason item 1 had to be done before anyone embeds the Simulator
  from the module path.
- `match.plan`, `match.automaton`: engines are selected through `match`.
- `control.parse`: ANTLR 3 generated classes plus the tree and the name space.
  `CtrlLoader` used to leak all three; see the fixes below.
- `explore.util` and `explore.verify` (the LTL strategies and cycle acceptor):
  engine-side code rewritten in 2026-08. `explore.engine` itself *is* exported,
  as the strategy extension point: a custom strategy extends `GTSStrategy` or
  `ClosingStrategy` and needs a `Pool`, so the abstract bases and the pools are
  the extension kit; the four concrete strategies alongside them were not worth
  a package split. The stable part is the protected hook set of the bases
  (`prepare`, `computeNextState`, `addExplorable`, `isStop`,
  `createExploreListener`); the concrete classes are reference implementations.
- `io.external.format`, `io.external.format.ecore`, `prolog.builtin.*`,
  `prolog.util`, `prolog.exception`, `transform.criticalpair`, `util.antlr`,
  `util.io`, `io`.

## Fixes made on the branch

Small visibility and signature changes closing leaks that were not worth an
export:

- `FormatException(RecognitionException)` removed; its two callers in
  `CtrlTree` construct the exception from message, line and column themselves.
  ANTLR no longer occurs in any exported signature.
- `BuchiGraph.newBuchiGraph(gov.nasa.ltl.trans.Formula)`, `Formula.toLtlFormula()`,
  `BuchiLabel`'s constructor and `guard()` are package-private; a new
  `BuchiGraph.newBuchiGraph(Formula)` takes a GROOVE formula. `LTLStrategy` and
  `LTL2BuchiGraphTest` use it (negation via `Formula.neg()`, the same `Not`
  node the old code built on the NASA side). ltl2buchi is no longer visible.
- `Generator.getReporter()` is private (no external callers).
- `BasicEvent.BasicEventCache`, `CompositeEvent.CompositeEventCache` public and
  `ATermTreeParser.TokenFamily`/`LineFragment` protected: they occurred in
  protected signatures of public classes.
- `FileType` and `ExtensionFilter` moved from `util.io` to `util`, which
  unexports `util.io` without adding an exported package. The open item had
  proposed `io` as the destination; that is wrong, because `io` ranks above
  `algebra`, the rule-system cluster and `util` in the layering `LayeringTest`
  guards, and `FileType` is used from all three tiers. `ExtensionFilter` had
  to move too: it is `FileType.getFilter()`'s return type.
- `CtrlLoader` no longer mentions `control.parse`: `addControl` returns nothing
  (its tree went into a map `CompositeControlModel` never read, and
  `ControlModel` only checked it, which `buildProgram` does anyway);
  `getNamespace` became `getTermPrototype`, the one thing a test needed from
  it; and the invisibility reason that the two control models pass is the
  top-level enum `control.Invisibility`, while the record carrying it
  (`Namespace.InvisibleDecl`, with the message formatting) stays in the parser
  package. Two tests that used the returned tree now parse through
  `CtrlTree.parse` on a name space they build themselves.

## Open items, in recommended order

Twenty-six `-Xlint:exports` warnings remained on this branch, all from item 1,
and were left visible on purpose: they were the measured debt. Item 1 is done
(branch `view-controller-context`, off this one); `mvn clean compile` now
reports no `exports` warning at all.

1. ~~**`gui.view` as a backend SPI tier.**~~ **Done**, 2026-09-21, on branch
   `view-controller-context` off this one, by composition rather than by the
   interfaces of the session proposal below: see "Item 1 as built".
2. ~~**CLI tools into one `cli` package**~~ **Done**, 2026-09-22, branch
   `cli-package`: see "Item 2 as built".
3. ~~**`control.template` mixes compile time and run time**: `Program`,
   `Fragment`, `TemplateBuilder` (terms in, template out) sit next to `Template`,
   `Switch`, `Location` (what the LTS refers to). Separating them, and moving
   `Procedure`'s term accessors (used by `control.parse`, `control.template`,
   `control.term`) to the compiler side, would let `control.term` go unexported.~~
   **Done**, 2026-09-22, branch `control-term-split`: see "Item 3 as built".
4. **`util.collect`**: narrow the four leaking signatures listed above, then
   unexport.

Verified 2026-09-22: `requires transitive java.desktop` is still needed. With
`transitive` dropped, javac hits its cap of 100 warnings before running out:
`java.awt.Color` in `util.line.Line`/`LineFormat`, `util.HTMLConverter`,
`grammar.Action` and `grammar.aspect.AspectContent`, and
`java.beans.PropertyChangeListener` in `util.Observable`, before any geometry
type. `java.prefs` was tested in the same run and hidden behind the cap; no
`java.util.prefs` type occurs outside `gui`, so it could probably be plain
`requires`, not verified separately.

## Session proposal: the backend-facing controller interface (item 1)

*Superseded, kept for the record: this is the design that was built and then
rejected. It states what the backends demonstrably call, which is still the
authoritative list of the backend-facing surface; what it got wrong is the
mechanism. See "Item 1 as built" below.*

Two sessions, in order, on branches of the same name in both repositories
(the cross-repository workflows pair them by name; the first push runs red
until the second exists).

**Design.** Neither backend constructs a controller; both only call one. So
`GraphViewController`, `AspectGraphViewController`, `LTSGraphViewController`
and `CtrlGraphViewController` become interfaces in `gui.view`, keeping their
names so backend code does not move, and the classes move to the Simulator
side (`gui.display`, where the displays that own them live) with the
Simulator-typed constructors. The interfaces carry what the two backends
demonstrably use, in exported types only:

- lifecycle and model: `attachCanvas`, `getCanvas`, `removeListeners`,
  `getGraphRole`, `getGraph`, `isEditing`, and `getGrammar()` returning
  `GrammarModel` (the only use of `getSimulatorModel` in either backend is
  `.getGrammar()`);
- options: `getOptionValue`, `addOptionListener`, the `isShow*` queries;
  `getOptions()` returns a small `ViewOptions` interface (selected-state query
  plus listener registration) that `Options` implements, and the `SHOW_*` keys
  move to `gui.view` so the yFiles bidirectional-edge listener stops importing
  `gui.Options`;
- actions: `getLayoutAction`/`getExportAction` typed `javax.swing.Action`;
  the point actions, used only as `execute(edge, at)`, become
  `addPoint(ViewEdge, Point2D)`/`removePoint(...)`; the single `ActionStore`
  use (registering the select-colour action as a canvas listener) moves into
  the controller's `attachCanvas`; the `getActions() != null` checks become
  `isInteractive()`;
- label tree: only `setEnabled` is called on it, so `setLabelTreeEnabled(boolean)`;
- Swing surface unchanged: `createPopupMenu`, `getModeButton`,
  `setModeButtonsEnabled`, `setToolTipEnabled`;
- layout and LTS methods are already in exported types (`Layouter`, `Filter`,
  `GraphState`, `GraphTransition`).

Constructors, `getSimulator`, `getActions`, `getSimulatorModel`, the concrete
menu and action types, `DisplayKind` and `RuleLevelTree` stay on the classes.

**Session A, `code` repository.** Prompt:

> Read `claude/module-exports.md` (section "Session proposal") and
> `claude/yfiles-migration.md`. On a fresh branch off `master`, turn the four
> `gui.view` controller classes into interfaces as designed there, with the
> implementations in `gui.display`, so that `mvn clean compile` reports no
> `-Xlint:exports` warnings at all (26 on `module-exports`, all from those
> classes). Adapt the JGraph backend in `gui.jgraph` to the interfaces; it is
> the in-tree reference for what a backend may call. Gates: the fast suite,
> the GUI tests, the `null-check` skill on touched files, and the yFiles unit
> compiled against the branch (`mvn -q -f ../yfiles-lib/pom.xml
> "-Dgroove.dir=<worktree>" test`), which is expected to fail until session B
> and whose failures must be limited to the members listed in the design.

## Item 1 as built: the controller's host context (2026-09-21)

Branch `view-controller-context`, off `module-exports` rather than off `master`,
because the warning count it had to drive to zero only exists on this branch.
It starts at the third commit of the abandoned `view-controller-interfaces`
(`2678fe43c`), whose first three commits are the neutral members the backends
call — `ViewOptions`, `isFiltered`/`isLevelFiltered` on the controller instead
of its trees, `addPoint`/`removePoint`, `isInteractive`, `getGrammar` — and
takes the rest a different way.

**Why not interfaces.** The interface split (branch `view-controller-interfaces`,
now **superseded**; delete it once this one is merged) was the textbook answer to
"public for my module, hidden from clients", and the wrong trade here. The
dependency problem is visibility only: there is no second implementation of any
controller and no scenario that wants one — the headless users (`Imager`,
`Viewer`, the preview dialogs) and the yFiles tests all construct the same
classes. It produced five interfaces with one implementation each, sixty
`@Override` tags, and `controllerOf`, a checked downcast at every place the
Simulator wanted its own controller back. Decision (Arend): reach the same
zero-warning state by composition.

**What it does.** The five controllers stay classes in `gui.view`. The Simulator
types leave them through one new interface `GraphViewContext<G>` in `gui.view`,
which states what a graph view needs of the tool hosting it, in exported types
only: the grammar as `GrammarModel`, the options as `ViewOptions`, whether the
view is interactive, the answers of the label filter (`isFiltered(ViewCell)`,
`isFiltered(Label)`, `setFilteringEnabled`, `getFilterLabels` and the level
filter of a rule view), the actions and menu items the host contributes (the
export action and the layout-dialog action as `javax.swing.Action`; the popup,
export, selection, explore and goto items as `JMenu`), the LTS exploration
result and trace, and a callback on canvas attachment and detachment. A
controller takes a `@Nullable GraphViewContext<G>` where it took a
`@Nullable Simulator`; a null context is a graph view outside a host tool, and
every host-dependent member degrades to nothing.

The implementation is `SimulatorViewContext<G>` in `gui.display`, with a
subclass for each kind that needs more: `SimulatorAspectContext` (the display kind,
the rule level tree, the resource actions, the colour-selection listener) and
`SimulatorLTSContext` (exploration and traversal actions, the model-checking menu,
the explore result and the trace). The displays and tabs that create a
controller create its context and hand it the trees they build on the canvas —
which is why `setLabelTree`/`setLevelTree` live on the context and are called
after construction, exactly as before. The four places the interface branch
served with `controllerOf` are served without a cast: the Simulator's display
menu and the layout dialog ask the controller (see the deviations), the label
tree and the LTS tree are handed the simulator by the display that builds them,
and find/replace asks the graph tab for its label tree.

**Deviations from the prompt's design**, all deliberate:

- **The zoom, show/hide and layout-setting menus stay on the controller**,
  typed `JMenu` and `Action` instead of `ZoomMenu`, `ShowHideMenu` and
  `SetLayoutMenu`. They are graph-view state, not Simulator state: zooming and
  showing/hiding act on the canvas, and choosing a layouter is a property of the
  view. Moving them to the context would have made the host build the view's own
  menus. The layout dialog, which pressed the layout button through the
  set-layout menu it fetched, now asks the controller for
  `selectLayouter(Layouter)`, which returns the action that runs it. The label
  sub-menu of the show/hide menu, the one part that did need the tree, is served
  by `getFilterLabels()`: the tree's own record of a label and its cells moved to
  `gui.view` as `LabelledCells`, and `ShowHideMenu` takes a supplier of it.
- **There is a detachment callback too.** The design named only
  `canvasAttached`; `removeListeners` has to undo what it registered, and the
  export action (owned by the context, since it needs the Simulator) has to be
  unregistered as a refreshable, so `canvasDetached` mirrors it.
- **The aspect controller takes a `GraphRole`**, not a `DisplayKind`. Nothing
  technical keeps `DisplayKind` out of `gui.view` (its one unexported
  dependency, `gui.Icons`, is in the body), but it enumerates the Simulator's
  tabs, which is not view API. The role is the abstraction the look values and
  the cells already consume. Whether the graphs are states is known to the
  context alone (`SimulatorAspectContext` holds the kind), which tells the
  export action it creates; the first version of this branch also passed a
  state flag to the controller, dropped in review as ad hoc. Note that
  `DisplayKind.STATE.getGraphRole()` throws, which is why the state display
  passes `GraphRole.HOST` explicitly, as the old constructor computed.
- **The point actions keep their logic where it was**, in the canvas-based
  cell-edit actions of `gui.action` that the controller creates. They touch no
  Simulator state, so composition has nothing to move; only their accessors
  become private, `addPoint`/`removePoint` and a `javax.swing.Action`-typed
  line-style accessor staying public. Rebuilding them on the view model would
  rewrite working code without changing the exported surface.
- **`SimulatorViewContext` has no `DisplayKind` constructor parameter**, unlike
  the design's one class holding "Simulator, ActionStore, LabelTree,
  RuleLevelTree and DisplayKind": the action store and the model come off the
  Simulator, and the kind is only meaningful for aspect views.

**One member that did not fit** and is worth watching: `getPopupItems`,
`getExportItems`, `getExploreItems` and `getGotoItems` are four menu-shaped
members on one interface, each empty by default and filled by one kind's
subclass. That is the price of one interface for all kinds rather than one per
kind; it reads as a wide interface with narrow implementations, which is the
honest picture of how much of the Simulator the popup menus carry.

**Session B, `yfiles-lib` repository**, after A is on a branch. Prompt:

> The `code` branch `view-controller-context` takes the Simulator out of the
> `gui.view` controllers (see `claude/module-exports.md` there, "Item 1 as
> built"). Create the backend branch under the same name,
> `view-controller-context` (the paired workflows find it by name), and adapt
> the backend at the six sites that fail to compile:
>
> - `YFilesCanvas` 424: `getOptions()` returns `ViewOptions`; the fifteen
>   static `SHOW_*_OPTION` imports of the three canvases move from
>   `gui.Options` to `gui.view.ViewOptions`.
> - `YFilesCanvas` 429: delete the `getActions()` delegation; the two
>   `getActions() != null` tests (497, 1310) become
>   `getController().isInteractive()`.
> - `YFilesCanvas` 1227: `getController().setLabelTreeEnabled(enabled)`.
> - `YFilesAspectCanvas` 113: the grammar is `getController().getGrammar()`,
>   asserted non-null as in `AspectJGraph.newModel`; the fallback through
>   `getSimulatorModel()` goes.
> - `YFilesAspectCanvas` 66-84: **delete** the registration of the
>   colour-selection action in `installListeners`/`removeListeners`, do not
>   replace it: the context registers that listener on attachment and removes
>   it on detachment, so keeping it would register it twice.
> - `YFilesAspectEditorCanvas` 498-500: `addPoint`/`removePoint`.
>
> Tests: the six `new AspectGraphViewController(null, DisplayKind.X, b)` calls
> take `(null, X.getGraphRole(), b)`, except the `DisplayKind.STATE` one in
> `YFilesCanvasTest` 168, which takes `GraphRole.HOST`, since
> `STATE.getGraphRole()` throws. `YFilesEditorTest` 519 drops
> its `setLabelTree` call, as the in-tree `EditorLabelTreeTest` did.
> `YFilesCanvasTest` 172 tests visibility under a filter and needs a test
> fixture implementing `GraphViewContext<AspectGraph>` over a `TypeTree`: the
> filter members delegate to the tree, the rest keep their defaults; it is
> created before the controller and handed the tree once the canvas exists.
> `TypeTree` now takes a leading nullable `Simulator`.
>
> Of `gui`, the backend's main sources import only `gui.view`, `gui.view.cell`,
> `gui.look` and `gui.layout`, plus `gui.Options` and `gui.Icons` for the
> look-and-feel, the gesture predicates, the cancel key and a cursor: known
> residuals outside item 1, to be listed in the note, not extended. Verify
> with the unit's tests against the `code` branch (65 tests, 4 Robot skips).

## Verification

On `view-controller-context`:

- `mvn clean compile`: no `exports` warning at all (24 → 0; the 26 of
  `module-exports` were already 24 after the three carried-over commits).
- Fast test suite: 876 tests, 0 failures, 0 errors, 2 skipped.
- GUI tests: all six `*GuiTest` classes run, none skipped, 18 tests, all pass
  (`EditorCancelGuiTest` 2, `AddOnGuiTest` 1, `DisplaySwitchGuiTest` 5,
  `LabelCountGuiTest` 2, `SimulatorGuiTest` 6, `WarningDisplayGuiTest` 2).
- Null analysis (`null-check` skill, ecj `-All`): 0 errors, 0 infos. The
  Fable review of 2026-09-21 found five never-used imports that the decoupling
  had left in files the branch changed, removed since; the remaining 11 main
  and 8 test warnings are the skill's baseline plus four pre-existing ones in
  `AddOnGuiTest` that the baseline does not list (and the `TypeTree` info of
  the baseline no longer fires under the trimmed descriptor).
- **yFiles unit against the branch: the backend's main sources do not
  compile**, at six sites — `getActions()` (`YFilesCanvas` 430, and the
  `getActions() != null` check at 497 through it), `getOptions()` returning
  `ViewOptions` where `Options` is declared (425), `getLabelTree()` (1227),
  `getSimulatorModel()` (`YFilesAspectCanvas` 113) and
  `getAddPointAction`/`getRemovePointAction` (`YFilesAspectEditorCanvas`
  498-500). All six are session B's work, and none of them is specific to
  composition: five of the members are ones the interface design dropped as
  well, and the sixth (`getOptions`) was already incompatible at the base
  commit `2678fe43c`, which is on `view-controller-interfaces`. **The record
  on that branch, that its main sources compiled clean, is therefore wrong**;
  whatever was measured there cannot have been the unit against that branch.
  The test sources were not reached, so the expectation that their failures
  are limited to constructors and `setLabelTree` is still unverified.
- Session B landed afterwards on the yfiles-lib branch of the same name and
  was reviewed on 2026-09-21: 70 tests, 5 Robot skips against the code tip; the
  paired workflows `backend.yml` (master) and `test.yml` (main) are green since
  the merges.

On `module-exports`:

- `mvn clean compile`: 26 `exports` warnings, all `gui.view`, nothing else.
- Fast test suite: 876 tests, all pass at the branch tip (before the SPI-tier
  and Prolog exports were added: `ExtensionsTest` 1 error, `PredicateTests` 11
  failures, as described under Principle). The `FileType` move to `io` was
  tried and failed `LayeringTest` with six violations, hence `util`.
- Null analysis (`null-check` skill, ecj `-All`): 0 errors, no new warnings in
  touched files; the two Javadoc warnings caused by widening `TokenFamily` and
  `CompositeEventCache` were fixed by adding the comments. ecj's own "type not
  exported" infos mirror the javac warnings on `gui.view`.
- GUI and yFiles gates: not run; no source change under `gui/`, and the
  yFiles unit compiles against GROOVE on the class path, where the descriptor
  is ignored.

## Item 2 as built: the cli package (2026-09-22)

Branch `cli-package`, off `master` (item 1 was already merged), in three moves
plus this note, each move compiling on its own.

**What moved.** `nl.utwente.groove.cli` now holds the nine former `util.cli`
classes (`CmdLineException`, `DirectoryHandler`, `ExistingFileHandler`,
`GrammarHandler`, `GrooveCmdLineParser`, `GrooveCmdLineTool`, `HelpHandler`,
`LogHandler`, `VerbosityHandler`) and the five command-line tools that were
parked in the package of the subsystem they drive: `explore.Generator`,
`explore.CTLModelChecker`, `prolog.PrologChecker`, `algebra.OperatorLister` and
`io.GraphReporter`. `util.cli` is gone; `explore`, `prolog`, `algebra` and `io`
no longer mention picocli. The exported CLI surface is the single package
`cli`, listed in the descriptor under its own heading after the pipeline block.

**`Verbosity` went to `util`, not to `cli`.** It is the one member of the old
`util.cli` that is not a command-line concept: the four exploration reporters in
`explore.util` use it to decide how much to print. Since `cli` ranks *above*
`explore` in the layering, leaving the enum with the CLI classes would have
inverted that edge. Its picocli converter `VerbosityHandler` stayed behind with
the rest. This corrects the layering bullet of `CHANGES-8_0_0.md`, which had
recorded the 2026-08 move as going to `util.cli`.

**`Imager` and `Viewer` stay in `gui`**, against the wording of the open item:
both open windows, so moving them would pull Swing into an exported package that
is otherwise headless, and `gui` already sits above `cli`. They keep using
`GrooveCmdLineTool` across that edge, as does `GuiShutdownHook`.

**The root-package shims stay** (`nl.utwente.groove.Generator`, `ModelChecker`,
`PrologChecker`) — maintainer's decision; they are the documented launcher class
names and the main classes of the runnable jars. Only their delegation and
`@see` javadoc were retargeted.

**Layering.** `LayeringTest` gains `cli` at rank 10, between `prolog` (9) and
`gui` (now 11, root 12). `cli` may use every pipeline package and uses no `gui`;
`gui` and the root shims may use `cli`. The whitelist is untouched.

**`opens` lines dropped**: `nl.utwente.groove.explore` and
`nl.utwente.groove.prolog`, which existed for the picocli-annotated fields of
`Generator`/`CTLModelChecker` and `PrologChecker` respectively; neither package
contains a picocli-annotated class any more. Note that `opens
nl.utwente.groove.verify` and `opens nl.utwente.groove.util` are stale for the
same reason and were *already* stale before this branch — neither package
mentions picocli — but they are outside the scope of this change and were left
alone.

**Gates.** `mvn clean compile`: no `exports` warning (the only warnings are the
pre-existing automodule notice). Fast suite: 881 tests, 0 failures, 0 errors, 2
skipped, with `LayeringTest` 1, `ExploreCliTest` 7, `CTLModelCheckerTest` 5,
`ExtensionsTest` 4 and `PredicateTests` 11 all green. GUI tests: all seven
`*GuiTest` classes run, 19 tests, none skipped (`EditorCancelGuiTest` 2,
`AddOnGuiTest` 1, `DisplaySwitchGuiTest` 5, `LabelCountGuiTest` 2,
`SaveGrammarAsGuiTest` 1, `SimulatorGuiTest` 6, `WarningDisplayGuiTest` 2).
Null analysis (`null-check` skill, ecj `-All`): 0 errors, 11 main and 8 test
warnings — the same set as on `view-controller-context`, so nothing new. The
yFiles backend needed no run: its sources mention none of the moved names,
only `gui.Imager`, which did not move.

## Item 3 as built: the term level unexported (2026-09-22)

Branch `control-term-split`, off `master`, in three code commits plus the
descriptor and these records.

**What moved.** `Fragment` and `TemplateBuilder` went from `control.template`
to `control.term` (`git mv`). `Program` was split: the compile-time half
(fragment merging with the duplicate-main and duplicate-procedure errors, the
recursion/finality/termination analyses, the body checks) is relocated
unchanged into the new `control.term.ProgramBuilder`, whose `build()` runs the
checks and the `TemplateBuilder` and returns the `Program`. `Program` keeps the
run-time half: a constructor from main name, main template, procedure map and
property list, and `getMainName`, `getTemplate`, `getProcs`, `getProc`,
`hasProperties`, `getProperties`. It is no longer `Fixable` (a `Program` only
exists after a successful build), which removed the `isFixed` asserts in
`Automaton` and `PreviewControlAction` and the redundant `setFixed` calls in
`CtrlLoader.run`; `hasMain` went too, since the builder rejects a program
without main and `CtrlLoader` supplies the default main, so it was always true.
`CtrlLoader.getTermPrototype` is gone; its one user, `ProgramBuildTest`, parses
through `CtrlTree` on its own name space and asserts on the builder.

**Procedure bodies live in the term pool.** `Procedure.getTerm/setTerm` were
the leak through `Recipe` and `Function`. Three packages need the bodies —
`control.parse` sets them, `control.term` reads them for nested derivations,
the compiler analyses and compiles them — so `Procedure` could not keep a
package-private accessor. The pool of the name space is the natural owner:
every body is a term of that pool and every consumer holds such a term.
`Term.prototype()` now creates a package-private `TermPool` with a lookup-only
procedure-to-body map; `Term.setBody` (recipe bodies made atomic, procedure
fixed) and `Term.getBody` replace the accessors. A `Fragment` receives the
name-space prototype at construction, because its main may be absent.

**`Template` visibility.** The constructors `Template(QualName)` and
`Template(Procedure)` and `Template.initVars()` became public for the moved
`TemplateBuilder`; none of them mentions a term type.

**Not annotated.** `ProgramBuilder` is not `@NonNullByDefault`: its analyses
pass arity-dependent nullable argument locals around in some twenty places,
and annotating would have meant rewriting relocated code. `Program` is
annotated; its `getProc` is `@Nullable`, which cost three asserts in tests.

**Gates.** `mvn clean compile`: no `exports` warning. Fast suite: 883 tests,
0 failures, 0 errors, 2 skipped, all `test.control` classes and
`ExtensionsTest` 4 and `LayeringTest` 1 green. `ExplorationTest`: 25 tests,
all pass. GUI tests: eight `*GuiTest` classes, 22 tests, none skipped.
Null analysis (`-All`): 0 errors, 11 main and 8 test warnings, the same set as
before. The yFiles backend mentions none of the changed names except
`getProgram().getTemplate()` in one test, which is unchanged API.
