# Dependency analysis and cleanup plan

*Analysis of 2026-08-17 (Claude session, decisions by Arend). Method: jdeps
(package- and class-level) over a fresh `target/classes` (generated ANTLR
included, `module-info` excluded), aggregated to the 16 top-level packages;
SCC computation; code-level inspection of every wrong-direction edge. 5508
cross-package class-level references in total. Raw data lived in the session
scratchpad; the analysis is reproducible from jdeps alone.*

## Headline findings

1. **The gui boundary is clean** (verified at bytecode level): the only
   inbound references to `gui.**` are the six root wrapper shims. This
   confirms the 2026-08-17 import scan in `module-split-plan.md`.
2. **Everything else is one cycle.** The remaining 14 top-level packages form
   a single strongly connected component; at subpackage granularity, 53 of 66
   packages are one SCC. No layering exists anywhere in core.
3. **The rot is thin.** Cutting 25 specific wrong-direction edges — 211
   references, 3.8% of all cross-package traffic — turns the codebase into a
   DAG with the layering below. Code inspection showed almost none of these
   edges are intrinsic: they are misplaced classes, main-method dev tools in
   low layers, and half-finished registry inversions.
4. **gui internally is fully entangled** (all 13 subpackages one SCC). Since
   gui is now a dependency leaf this costs nothing structurally; noted, not
   acted on.

## Target layering

```
util → annotation → graph → algebra
     → {grammar + control + automaton + match + transform}   (the rule-system cluster)
     → io, lts → verify → explore → prolog → gui → root shims
```

The braced cluster is the one honest mutual dependency (a `Rule` knows its
`Matcher`, a `Grammar` contains its control automaton, a `RuleLabel` *is* a
`RegExpr`); it is treated as a single layer. Likewise accepted as intrinsic:
`GrammarModel ↔ io.store.SystemStore` (editable model vs. backing store — a
`GrammarSource` interface could break it but buys little).

## Edge inventory

Classification: (A) misplaced class, (B) invertible coupling, (C) intrinsic.
Status: P1 = on the `dependency-cleanup` branch, P2/P3 = later, acc = accepted.

### util → domain (util must become a leaf)

| Culprit | Actual dependency | Class | Remedy | Status |
|---|---|---|---|---|
| `util.Fonts`/`Resources`/`Version` → grammar | only `grammar.QualName`, used as a dotted-name value | A | move `QualName`+`ModuleName` to `util` (their own imports are util-only) | P1 |
| `util.parse.AExprTree`(+`Parser`) → algebra, grammar | only subclassed by `algebra.syntax.ExprTree`(+`Parser`) | A | move both to `algebra.syntax` | P1 |
| `util.cli.GrooveCmdLineTool`/`VerbosityHandler` → explore | `explore.Verbosity`, a generic zero-import CLI enum | A | move `Verbosity` to `util.cli` | P1 |
| `util.cli.DirectoryHandler`/`GrammarHandler` → io | `io.FileType` extension check | A/B | `util.io` split (below); `DirectoryHandler` could also take a predicate | P1 |
| `util.Checker`/`CycleChecker` → graph | type-parameter bound `G extends Graph`; subclasses all in `grammar.*` | A | move both to `graph` | P1 |
| `util.antlr.AntlrGrapher` → grammar, graph, algebra | builds type/host graphs from ANTLR ASTs; **zero callers, no main** | A | relocate to `control.parse` (Arend: keep, don't delete) | P1 |
| `util.GraphReporter` → graph, io | main-method tool using `Groove.loadGraph` | A | relocate to `io` | P1 |
| `util.OperatorLister` → algebra, io | main-method doc generator over `Sort.values()` | A | relocate to `algebra` | P1 |
| `util.Properties$Entry`/`$ValueType` → grammar, algebra, explore, transform.oracle | `ValueType` enum hard-codes 8 domain classes for `isInstance` checks + named downcast accessors | B | genericize to `Class<V>`/typed `Key<V>`; accessors become `entry.value()` | P2 |
| `util.parse.ATermTreeParser` → algebra | `Sort`/`Constant` baked into the tokenizer; `verify.FormulaParser` pays for machinery it never uses | B | extract an atom-lexer interface, algebra-backed impl in `algebra.syntax` | P2 |
| `util.parse.FormatError`(+`Set`) → grammar, graph, lts | 15-branch `instanceof` chain over domain types for error-navigation context; `Element`/`GraphMap` remapping | A/B | invert the dispatch (context types contribute to the error) or split generic vs domain error types; **largest single item**, needs its own design note | P3 |
| `util.parse.SearchResult`/`SelectableListEntry` → grammar, graph | GUI list contracts (consumers: `gui.list`, `FindReplaceAction`) | A | `SelectableListEntry` → `gui.list`, `SearchResult` → `grammar.model`; blocked on `FormatError implements SelectableListEntry` | P3 |

### Low layers reaching up

| Culprit | Actual dependency | Class | Remedy | Status |
|---|---|---|---|---|
| `annotation.Help` → algebra | `Sort.USER` + `Sort.toSort` in the user-operation help branch | B | move that branch to `algebra` (`Algebras` already post-processes) | P2 |
| `graph.GraphInfo` → grammar | javadoc-only `Rule` import + `Action.Role` convenience accessors | A | delete import; Role accessors ride on `GraphProperties` move | P1 (javadoc) / P2 |
| `graph.GraphProperties` → grammar, grammar.aspect, grammar.rule | an enum of *rule* properties (`PRIORITY`, `INJECTIVE`, `FILTER`, …) with an `AspectGraph`-typed checker | A | move next to `GrammarProperties` in `grammar`; `GraphInfo` keeps base `util.Properties`. Rides on the `Properties.ValueType` fix | P2 |
| `graph.iso` certificate strategies → grammar.host, grammar.type | `instanceof HostNode/ValueNode` seeding in all four strategies (once per node per cert build, not per iteration) | B | `Node.certificateSeed()` default method; `DefaultHostNode` returns type label, `ValueNode` its value. No SPI/registry — would cost more than the instanceof. **Gate with determinism-check + IsoTest + grammar-smoke**: value-node certs change subtly | P2 |
| `graph.iso.IsoChecker` → io | `Groove.loadGraph/saveGraph` only in `main` + compile-time-false debug dumps | A | relocate harness to src/test | P1 |
| `graph.iso.IsoChecker` → control | `CallStack.areEqual` — generic nested-`Object[]` comparison, sole prod caller `GTS` state collapse | B | extract array helpers to `util.collect`; `CallStack` delegates | P2 |
| `graph.EdgeRole` → grammar.aspect | `AspectParser.SEPARATOR` used as a char constant | A | move the constant down | P1 |
| `graph.GDeltaTarget` → grammar.host | javadoc-only `ValueNode` | A | delete import | P1 |
| `algebra.syntax` relabel chain → grammar.type | `relabel(TypeLabel,…)` uses only `graph.Label` members | B | widen parameters to `graph.Label` (same shape in `RegExpr.relabel`) | P2 |
| `algebra.syntax`/`UserSignature` → grammar | `QualName` as dotted identifier | A | covered by the `QualName` move | P1 |

### Mid-layer cycles

| Culprit | Actual dependency | Class | Remedy | Status |
|---|---|---|---|---|
| `grammar.model.SettingsSchemas` → explore.config, io.external.format.ecore | static block hard-codes `register(EcoreMappingSchema)`, `register(ExploreConfigSchema)` in an otherwise clean registry | B | ServiceLoader (decision 2026-08-17); ordering-safe headless | P1 |
| `grammar.Grammar`/`GrammarModel` → prolog | opaque `GrooveEnvironment` carrier on `Grammar`; `GrammarModel` loads `.pro` resources for validation | B | per-`ResourceKind` validator/environment-builder service | P2 |
| `grammar.GrammarKey`/`GrammarProperties` → explore, transform.oracle | typed accessors for property values whose types live upstream | B | keep table generic; typed accessors move to the type-owning packages (`ExploreType.from(props)` etc.) | P2 |
| `grammar.OracleParser` → transform.oracle | hard-coded `switch` over `ValueOracleKind` (the `dialogOracle` static is the one inverted case) | A/B | move to `transform.oracle`, convert switch to registry | P2 |
| `grammar.Rule`/`Prover` → match, transform | `Prover` near-duplicates `Rule`'s matcher-cache block; a stalled extraction | A | finish it: move `Prover` + matcher cache into `match` | P2 |
| `grammar.host` → transform | delta vocabulary (`DeltaTarget`, `DeltaStore`, `DeltaApplier`, …) is a graph-mutation primitive | A | move delta types down to `grammar.host` (or `graph.delta`); `transform` keeps rule application only | P2 |
| `grammar.model.ResourceKind`/`control.CtrlDoc`/`CtrlLoader` → io | `FileType`/`FileUtils` leaf utilities stranded in a high-level package | A | **`util.io` split** (decision 2026-08-17): new `util.io` package for `FileType`, `FileUtils`, `ExtensionFilter`; requires decoupling `FileType` from `graph.GraphRole` first | P1 |
| `transform.Transformer` → lts, explore, io | top-of-stack facade (grammar file → exploration → `ExploreResult`); only prod caller `explore.Generator` | A | move to `explore` | P1 |
| `transform.Phase`/`Record` → lts | `Phase` is `GraphState`'s super-interface but mentions `lts.MatchResult`; `Record` pools `RuleTransitionLabel` | B | move `Phase`+`MatchResult` up to `lts` (cheaper than inverting the label pool) | P2 |
| `match.TreeMatch` ↔ transform | entire output vocabulary is `transform.Proof`, which is a *match* witness | A | move `Proof` to `match`; handle its `RuleEvent.Reuse` import | P1 |
| `lts.GTSFragment` → explore | `ExploreResult` imports only lts; `LTSLabels` is LTS serialization flags | A | move both to `lts` (also fixes `verify`→explore and `AutIO`) | P1 |
| `io.external` LTS exporters → lts | `LTS2ControlExporter`, `ListenerExporter` GTS branch, registered eagerly inside `Exporters` | B | move to lts side, contribute via ServiceLoader (decision 2026-08-17) | P1 |
| `io.graph.AutIO` → explore.util | `ExplorationReporter.time()` stopwatch calls | A | fold into the gh #891 diagnostic-logging work | P2 |
| `verify.CycleAcceptor` ↔ `explore.verify.LTLStrategy` | acceptor/strategy callback pair split across packages | A | move `CycleAcceptor` to `explore.verify` | P1 |
| `verify.CTLModelChecker` → explore | `Generator.LTSLabelsHandler` CLI reuse (+ `ExploreResult`, fixed by the lts move) | B | extract shared picocli handler to `util.cli` | P2 |
| `automaton` ↔ grammar | two tiers sharing a package: `RegExpr` syntax (belongs beside `grammar.rule`) vs host-graph evaluation (belongs beside `match`) | B/C | split, e.g. `match.regexpr` for the evaluation tier; residual `RuleLabel`→`RegAut` needs a factory hook | P3 |
| `automaton.RelationCalculator` → lts | `implements GTSListener` for incremental label indexing; sole caller `gui.menu.ShowHideMenu` | B | strip the listener, GUI caller registers an adapter | P2 |
| `RuleDependencies.main`, `CtrlLoader.main` → io | debug drivers using `Groove.loadGrammar` | A | relocate mains to src/test tool classes | P1 |

## Decisions (Arend, 2026-08-17)

- **Dev tools: relocate, do not delete** — including `AntlrGrapher` despite
  zero callers.
- **io split shape: new `util.io` package**, i.e. the cleaner end state that
  requires decoupling `FileType` from `graph.GraphRole`, not the
  minimal-churn "io root becomes leaf" variant.
- **Registration inversions use `ServiceLoader`** for both `SettingsSchemas`
  and the LTS exporters (new pattern for this codebase; works on classpath
  and under JPMS).
- **Layering regression test: yes**, jdeps-based, on this branch, seeded with
  the remaining violations as a whitelist that shrinks as P2/P3 land.
- Made without asking (recorded here): `QualName`/`ModuleName` land in `util`
  proper (not a subpackage); `Transformer` goes to `explore`; one branch with
  one commit per move, the `QualName` import churn isolated in its own
  mechanical commit.

## Relation to the module split (gh #887)

None of this changes phase 5 — core is one Maven module regardless of its
internal shape. But the enforcement argument differs: javac will police
core→gui after the split, while nothing will ever police the internal
layering above — hence the regression test. If a further split ever becomes
interesting (headless graph/grammar library, Swing-free kernel vs CLI tools),
the target layering is the module boundary map and the P1 moves are its
prerequisites.

## Status

- P1 items in progress on branch `dependency-cleanup` (this branch).
- P2/P3 not started; `FormatError` and the `automaton` split deserve design
  notes before anyone touches them.
