# Module exports: what the library promises

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
| Control | `control`, `control.term/template/instance/graph` |
| Utilities | `util`, `util.parse/line/cache/collect/cli` |
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
  `RuleTransition.getStep/getSwitch`) and `Grammar.getControl`. `control.term` is
  exposed by `Procedure.getTerm` and by `Program`/`Fragment` in `control.template`
  (see the open item on that package). `control.graph` is `Automaton.toGraph`.
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
- `util.cli`: the CLI tools `explore.Generator`, `explore.CTLModelChecker`,
  `prolog.PrologChecker`, `algebra.OperatorLister` extend `GrooveCmdLineTool`.
  picocli was already `requires transitive`, so the package adds no new
  third-party surface. See the open item.
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
  warnings stay visible until the controller seam is cleaned (open item 3).
  And on the module path the yFiles add-on itself would hit `IllegalAccessError`
  the moment it touches `ActionStore` or `Options`; it does not today, because
  the installed application and the add-on's own tests run from the class path,
  but it is one more reason to do item 3 before anyone embeds the Simulator
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

Twenty-six `-Xlint:exports` warnings remain on the branch, all from item 1,
and are left visible on purpose: they are the measured debt.

1. **`gui.view` as a backend SPI tier.** The controller classes are the
   Simulator's glue but the backends compile against them. See the session
   proposal below for the design. Until it is done the SPI tier is exported
   with its 26 warnings, and the packages it leaks (`gui`, `gui.action`,
   `gui.tree`, `gui.menu`, `gui.display`) are not.
2. **CLI tools into one `cli` package** together with `util.cli`: `Generator`,
   `CTLModelChecker`, `PrologChecker`, `OperatorLister`, `Imager`. `explore`,
   `prolog` and `algebra` then stop mentioning picocli, `util.cli` goes
   unexported, and the exported CLI surface is one package. This is the `cli`
   seam of the module-split plan.
3. **`control.template` mixes compile time and run time**: `Program`,
   `Fragment`, `TemplateBuilder` (terms in, template out) sit next to `Template`,
   `Switch`, `Location` (what the LTS refers to). Separating them, and moving
   `Procedure`'s term accessors (used by `control.parse`, `control.template`,
   `control.term`) to the compiler side, would let `control.term` go unexported.
4. **`util.collect`**: narrow the four leaking signatures listed above, then
   unexport.

Not verified: whether `requires transitive java.desktop` is still needed by an
exported signature once `gui` is out (exported core types do use `java.awt`
geometry in `graph.layout`/`io.graph`, so presumably yes).

## Session proposal: the backend-facing controller interface (item 1)

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

**Session B, `yfiles-lib` repository**, after A is on a branch. Prompt:

> The `code` branch `<name>` makes the `gui.view` controllers interfaces
> without Simulator types (see `claude/module-exports.md` there). Adapt the
> yFiles backend: replace the uses of `ActionStore`,
> `Options.SHOW_BIDIRECTIONAL_EDGES_OPTION`, `getSimulatorModel().getGrammar()`,
> `getAddPointAction`/`getRemovePointAction`, `getLabelTree().setEnabled` and
> the `getActions() != null` checks in `YFilesCanvas`, `YFilesAspectCanvas`,
> `YFilesAspectEditorCanvas` and siblings by the interface members. The backend
> must import only `gui.view`, `gui.view.cell`, `gui.look`, `gui.layout`,
> `graph.layout`, `util.line`, `control.graph` and packages exported by the
> `code` descriptor. Verify with the unit's tests against the `code` branch
> (65 tests, 4 Robot skips).

## Verification

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
