# Module exports: what the library promises

*Branch `worktree-module-exports`, 2026-09-20. Context: 8.0.0 is the first
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
| Pipeline | root, `io.store`, `io.graph`, `io.external`, `grammar`, `grammar.model/aspect/host/type/rule`, `graph`, `graph.plain/iso/layout`, `match`, `transform`, `transform.oracle`, `lts`, `explore`, `explore.config/feature/result`, `verify`, `prolog`, `prolog.builtin` |
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
- `control.parse`: ANTLR 3 generated classes plus the tree; see open item.
- `explore.engine`, `explore.util`: rewritten in 2026-08, least settled code.
  `explore.verify` (the LTL strategies and cycle acceptor) turned out to be
  engine-side too and is not exported either.
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

## Open items, in recommended order

Thirty `-Xlint:exports` warnings remain on the branch, all in the first three
items, and are left visible on purpose: they are the measured debt.

1. **`control.parse` leaks through `CtrlLoader`** (3 warnings): `addControl`
   returns `CtrlTree` (used by `ControlModel` and `CompositeControlModel`, which
   keeps a tree map), `getNamespace()` returns `Namespace`, and
   `addInvisibleControl` takes `Namespace.InvisibleDecl.Reason` (used by both
   models). `Namespace` is the declaration table, a semantic object; move it to
   `control`. The tree must stop crossing the loader boundary: let the loader
   own its trees and offer the two models what they do with them (`check()`,
   `toFragment()`) by name.
2. **`explore.engine` leaks through `ExploreType.Realisation`** (1 warning):
   the public record has a `Strategy` component, consumed by `Exploration`
   (same package) and by tests (`.strategy()` for `assertInstanceOf`). Decide
   whether custom strategies are a supported extension point. If yes, export
   `explore.engine` and accept the instability. If no, make `Realisation` a class
   with package-private accessors and let the tests assert differently.
3. **`gui.view` as a backend SPI tier.** Needs a backend-facing controller
   interface that mentions no Simulator type: actions as `javax.swing.Action`,
   options as a small `gui.view`-level interface implemented by `Options`, the
   label tree behind a filter interface, `createPopupMenu`/`getModeButton`
   returning plain Swing types. The yFiles side has to move at the same time,
   since it is compiled against the controller. See the session proposal below.
   Until then the SPI tier is exported with its 30 warnings, and the packages
   it leaks (`gui`, `gui.action`, `gui.tree`, `gui.menu`, `gui.display`) are not.
4. **CLI tools into one `cli` package** together with `util.cli`: `Generator`,
   `CTLModelChecker`, `PrologChecker`, `OperatorLister`, `Imager`. `explore`,
   `prolog` and `algebra` then stop mentioning picocli, `util.cli` goes
   unexported, and the exported CLI surface is one package. This is the `cli`
   seam of the module-split plan.
5. **`control.template` mixes compile time and run time**: `Program`,
   `Fragment`, `TemplateBuilder` (terms in, template out) sit next to `Template`,
   `Switch`, `Location` (what the LTS refers to). Separating them, and moving
   `Procedure`'s term accessors (used by `control.parse`, `control.template`,
   `control.term`) to the compiler side, would let `control.term` go unexported.
6. **`util.collect`**: narrow the four leaking signatures listed above, then
   unexport.

Not verified: whether `requires transitive java.desktop` is still needed by an
exported signature once `gui` is out (exported core types do use `java.awt`
geometry in `graph.layout`/`io.graph`, so presumably yes).

## Session proposal for `yfiles-lib`

To be run in the private repository (`../yfiles-lib`), paired with a `code`
branch of the same name for item 3 above. Prompt:

> The `code` branch `<name>` makes `gui.view` exportable as the backend SPI
> tier: `GraphViewController` gets a backend-facing interface without Simulator
> types (actions typed `javax.swing.Action`, options behind a `gui.view`
> interface, label-tree filtering behind an interface, popup menu and mode
> buttons as plain Swing). Adapt the yFiles backend to that interface: replace
> the direct uses of `ActionStore`, `Options.SHOW_BIDIRECTIONAL_EDGES_OPTION`,
> `getLayoutAction`, `getAddPointAction`, `getRemovePointAction`,
> `getLabelTree`, `getSimulatorModel`, `getModeButton` and `createPopupMenu`
> in `YFilesCanvas`, `YFilesAspectCanvas` and siblings. The backend must import
> only `gui.view`, `gui.view.cell`, `gui.look`, `gui.layout`, `graph.layout`,
> `util.line`, `control.graph` and the core packages listed in
> `claude/module-exports.md` of the `code` repository. Verify with the unit's
> tests (`mvn -q -f pom.xml "-Dgroove.dir=<code checkout>" test`, 65 tests,
> 4 Robot skips) and by checking that `mvn clean compile` in `code` reports no
> `exports` warnings from `gui.view` any more (30 on the branch this note
> belongs to).

The `code` side of that pair is the larger half and belongs to gh #909; the
backend session only makes sense once the controller interface exists on a
branch.

## Verification

- `mvn clean compile`: 30 `exports` warnings (`gui.view` 27, `CtrlLoader` 3
  lines, `ExploreType` 1), nothing else.
- Fast test suite: 876 tests, all pass after the SPI-tier and Prolog exports
  were added (before them: `ExtensionsTest` 1 error, `PredicateTests` 11
  failures, as described under Principle).
- Null analysis (`null-check` skill, ecj `-All`): 0 errors; the two Javadoc
  warnings caused by widening `TokenFamily` and `CompositeEventCache` were
  fixed by adding the comments; ecj's own "type not exported" infos on
  `CtrlLoader` mirror the javac warnings.
- GUI and yFiles gates: not run; no source change under `gui/`, and the
  yFiles unit compiles against GROOVE on the class path, where the descriptor
  is ignored.
