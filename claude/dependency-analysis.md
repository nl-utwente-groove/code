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
| `util.Properties$Entry`/`$ValueType` → grammar, algebra, explore, transform.oracle | `ValueType` enum hard-codes 8 domain classes for `isInstance` checks + named downcast accessors | B | genericize to `Class<V>`/typed `Key<V>`; accessors become `entry.value()` | done (P2 branch: `ValueType<V>` identity tokens, domain tokens as `VALUE_TYPE` constants on their types, single generic `Entry.value(token)`; full `Key<V>` remains open — key sets are enums) |
| `util.parse.ATermTreeParser` → algebra | `Sort`/`Constant` baked into the tokenizer; `verify.FormulaParser` pays for machinery it never uses | B | extract an atom-lexer interface, algebra-backed impl in `algebra.syntax` | P2 |
| `util.parse.FormatError`(+`Set`) → grammar, graph, lts | 15-branch `instanceof` chain over domain types for error-navigation context; `Element`/`GraphMap` remapping | A/B | invert the dispatch (context types contribute to the error) or split generic vs domain error types; **largest single item**, needs its own design note | P3 |
| `util.parse.SearchResult`/`SelectableListEntry` → grammar, graph | GUI list contracts (consumers: `gui.list`, `FindReplaceAction`) | A | `SelectableListEntry` → `gui.list`, `SearchResult` → `grammar.model`; blocked on `FormatError implements SelectableListEntry` | P3 |

### Low layers reaching up

| Culprit | Actual dependency | Class | Remedy | Status |
|---|---|---|---|---|
| `annotation.Help` → algebra | `Sort.USER` + `Sort.toSort` in the user-operation help branch | B | move that branch to `algebra` (`Algebras` already post-processes) | done (P2 branch) |
| `graph.GraphInfo` → grammar | javadoc-only `Rule` import + `Action.Role` convenience accessors | A | delete import; Role accessors ride on `GraphProperties` move | done (P2 branch: typed accessors moved to `ResourceProperties`; `GraphInfo` stores a nullable base `util.Properties`, created lazily by the grammar side) |
| `graph.GraphProperties` → grammar, grammar.aspect, grammar.rule | an enum of *rule* properties (`PRIORITY`, `INJECTIVE`, `FILTER`, …) with an `AspectGraph`-typed checker | A | move next to `GrammarProperties` in `grammar`; `GraphInfo` keeps base `util.Properties`. Rides on the `Properties.ValueType` fix | done (P2 branch: moved and renamed to `grammar.ResourceProperties` — the keys are resource metadata, not graph metadata; `Graph.getProperties()` removed; base `Properties` gained abstract `clone()`) |
| `graph.iso` certificate strategies → grammar.host, grammar.type | `instanceof HostNode/ValueNode` seeding in all four strategies (once per node per cert build, not per iteration) | B | `Node.certificateSeed()` default method; `DefaultHostNode` returns type label, `ValueNode` its value. No SPI/registry — would cost more than the instanceof. **Gate with determinism-check + IsoTest + grammar-smoke**: value-node certs change subtly | done (P2 branch: `Node.certificateSeed()` + `Node.hasIdentityCertificate()`; `HostNode` seeds with its type label, `ValueNode` with its value; per-strategy `MyValueNodeCert` renamed to `MyIdentityNodeCert`, comparing by seed) |
| `graph.iso.IsoChecker` → io | `Groove.loadGraph/saveGraph` only in `main` + compile-time-false debug dumps | A | relocate harness to src/test | P1 |
| `graph.iso.IsoChecker` → control | `CallStack.areEqual` — generic nested-`Object[]` comparison, sole prod caller `GTS` state collapse | B | extract array helpers to `util.collect`; `CallStack` delegates | done (P2 branch: `util.collect.NestedArrays`) |
| `graph.EdgeRole` → grammar.aspect | `AspectParser.SEPARATOR` used as a char constant | A | move the constant down | P1 |
| `graph.GDeltaTarget` → grammar.host | javadoc-only `ValueNode` | A | delete import | P1 |
| `algebra.syntax` relabel chain → grammar.type | `relabel(TypeLabel,…)` uses only `graph.Label` members | B | widen parameters to `graph.Label` (same shape in `RegExpr.relabel`) | done (P2 branch) |
| `algebra.syntax`/`UserSignature` → grammar | `QualName` as dotted identifier | A | covered by the `QualName` move | P1 |

### Mid-layer cycles

| Culprit | Actual dependency | Class | Remedy | Status |
|---|---|---|---|---|
| `grammar.model.SettingsSchemas` → explore.config, io.external.format.ecore | static block hard-codes `register(EcoreMappingSchema)`, `register(ExploreConfigSchema)` in an otherwise clean registry | B | ServiceLoader (decision 2026-08-17); ordering-safe headless | P1 |
| `grammar.Grammar`/`GrammarModel` → prolog | opaque `GrooveEnvironment` carrier on `Grammar`; `GrammarModel` loads `.pro` resources for validation | B | per-`ResourceKind` validator/environment-builder service | P2 |
| `grammar.GrammarKey`/`GrammarProperties` → explore, transform.oracle | typed accessors for property values whose types live upstream | B | keep table generic; typed accessors move to the type-owning packages (`ExploreType.from(props)` etc.) | done (P2 branch: `EXPLORATION` retyped as an uninterpreted string key; `getLegacyExploreType` and `GrammarModel.getDefaultExploreType/Config` replaced by `ExploreType.ofGrammar/ofLegacy/parseLegacy` and `ExploreConfig.ofGrammar/ofResource`; `getExploreSettings` prefixes the folder via a grammar-level `EXPLORE_SCHEMA_NAME` constant that `ExploreConfigSchema.NAME` aliases (compile-time folded). Known loss: the properties table no longer validates the deprecated legacy key's syntax — the compass icon and explore dialog still do, via `parseLegacy`. The transform.oracle part is intra-cluster and rides on the `OracleParser` item) |
| `grammar.OracleParser` → transform.oracle | hard-coded `switch` over `ValueOracleKind` (the `dialogOracle` static is the one inverted case) | A/B | move to `transform.oracle`, convert switch to registry | P2 |
| `grammar.Rule`/`Prover` → match, transform | `Prover` near-duplicates `Rule`'s matcher-cache block; a stalled extraction | A | finish it: move `Prover` + matcher cache into `match` | P2 |
| `grammar.host` → transform | delta vocabulary (`DeltaTarget`, `DeltaStore`, `DeltaApplier`, …) is a graph-mutation primitive | A | move delta types down to `grammar.host` (or `graph.delta`); `transform` keeps rule application only | P2 |
| `grammar.model.ResourceKind`/`control.CtrlDoc`/`CtrlLoader` → io | `FileType`/`FileUtils` leaf utilities stranded in a high-level package | A | **`util.io` split** (decision 2026-08-17): new `util.io` package for `FileType`, `FileUtils`, `ExtensionFilter`; requires decoupling `FileType` from `graph.GraphRole` first | P1 |
| `transform.Transformer` → lts, explore, io | top-of-stack facade (grammar file → exploration → `ExploreResult`); only prod caller `explore.Generator` | A | move to `explore` | P1 |
| `transform.Phase`/`Record` → lts | `Phase` is `GraphState`'s super-interface but mentions `lts.MatchResult`; `Record` pools `RuleTransitionLabel` | B | move `Phase`+`MatchResult` up to `lts` (cheaper than inverting the label pool) | done (P2 branch: `Phase` → lts, label pool → `GTS.normaliseLabel`) |
| `match.TreeMatch` ↔ transform | entire output vocabulary is `transform.Proof`, which is a *match* witness | A | move `Proof` to `match`; handle its `RuleEvent.Reuse` import | P1 |
| `lts.GTSFragment` → explore | `ExploreResult` imports only lts; `LTSLabels` is LTS serialization flags | A | move both to `lts` (also fixes `verify`→explore and `AutIO`) | P1 |
| `io.external` LTS exporters → lts | `LTS2ControlExporter`, `ListenerExporter` GTS branch, registered eagerly inside `Exporters` | B | resolved by ranking `io` *above* `lts` (revised 2026-08-17): the exporters stay in `io.external.format` and their GTS references become downward; `lts` itself has no `io` dependency | P1 |
| `io.graph.AutIO` → explore.util | `ExplorationReporter.time()` stopwatch calls | A | fold into the gh #891 diagnostic-logging work | P2 |
| `verify.CycleAcceptor` ↔ `explore.verify.LTLStrategy` | acceptor/strategy callback pair split across packages | A | move `CycleAcceptor` to `explore.verify` | P1 |
| `verify.CTLModelChecker` → explore | `Generator.LTSLabelsHandler` CLI reuse (+ `ExploreResult`, fixed by the lts move) | B | extract shared picocli handler to `util.cli` | P2 |
| `automaton` ↔ grammar | two tiers sharing a package: `RegExpr` syntax (belongs beside `grammar.rule`) vs host-graph evaluation (belongs beside `match`) | B/C | split, e.g. `match.regexpr` for the evaluation tier; residual `RuleLabel`→`RegAut` needs a factory hook | P3 |
| `automaton.RelationCalculator` → lts | `implements GTSListener` for incremental label indexing; sole caller `gui.menu.ShowHideMenu` | B | strip the listener, GUI caller registers an adapter | done (P2 branch) |
| `RuleDependencies.main`, `CtrlLoader.main` → io | debug drivers using `Groove.loadGrammar` | A | relocate mains to src/test tool classes | P1 |

## Decisions (Arend, 2026-08-17)

- **Dev tools: relocate, do not delete** — including `AntlrGrapher` despite
  zero callers.
- **io split shape: new `util.io` package**, i.e. the cleaner end state that
  requires decoupling `FileType` from `graph.GraphRole`, not the
  minimal-churn "io root becomes leaf" variant.
- **Registration inversions use `ServiceLoader`** for both `SettingsSchemas`
  and the LTS exporters (new pattern for this codebase; works on classpath
  and under JPMS). *Revised during review (2026-08-17): the exporter
  inversion was undone again — `LTS2ControlExporter` was the only `lts → io`
  edge, so moving it back to `io.external.format` and ranking `io` above
  `lts` is strictly cleaner (one whitelist entry fewer, no single-class
  `lts.export` package, no service machinery). The `SettingsSchemas`
  inversion stands; its contributors genuinely live in different layers.*
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

- **P1 complete** on branch `dependency-cleanup` (this branch), 2026-08-17:
  all moves and inversions listed as P1 above, the change-log entries for the
  public-API relocations, and `LayeringTest` (see below). Verified by
  compile, ecj null analysis, targeted schema/exporter tests, a class-path
  service-loading run, and the fast suite.
- **`LayeringTest`** (`src/test/.../test/LayeringTest.java`) now guards the
  layering: jdeps over `target/classes` via `ToolProvider`, rule-system
  cluster as one layer, `io` *above* `lts` — `io` is a domain-serialization
  layer that consumes the objects it serializes (including the GTS), and
  nothing in `lts` refers to `io`. Every remaining upward edge is
  whitelisted with a pointer into this document, and stale whitelist entries
  fail the test, so the list can only shrink. Remove entries here and there
  together as P2/P3 items land.
- P2/P3 not started; `FormatError` and the `automaton` split deserve design
  notes before anyone touches them.

### Findings from the implementation (2026-08-17)

- `FileType.getFilter(GraphRole)` had zero callers and was deleted outright —
  the leaf/graph decoupling needed no redesign.
- `Groove`'s tracing helpers moved to new `util.Trace`; its default resource
  names were inlined into `ResourceKind` (users go through
  `getDefaultName()`); only the loader facade remains in `io.Groove`.
- `Proof`'s only transform coupling was event construction; it is now
  `static RuleEvent.createEvent(Proof, Record)`. Note `RuleEvent` is not
  `@NonNullByDefault`, so the extracted code lost its non-null default.
- Moving `CycleAcceptor` exposed a **suspect listener filter**:
  `verify.ProductStateSet.fireCloseState` dispatched `closeUpdate` only to
  listeners that are `instanceof CycleAcceptor`, silently dropping every
  other `ProductListener`. Resolved by Arend during review (widened to
  arbitrary `ProductListener`s).
- `RuleDependenciesTool` initially sat in the test tree under package
  `nl.utwente.groove.grammar` (split package across source roots) because it
  used package-private members. Resolved during review: the four map getters
  of `RuleDependencies` were made public and the tool moved to
  `test.grammar`; its explicit `collectCharacteristics()` call was dropped
  as redundant (every getter lazily triggers collection).
- `IsoChecker`'s `SAVE_FALSE_NEGATIVES` branch keeps its `io.Groove` import
  in source, but javac eliminates the `if (false)` branch, so the edge is
  invisible to the bytecode-based `LayeringTest` — no whitelist entry. If
  the flag is ever switched on, the test will flag it, correctly.
- The `ServiceLoader` inversion (now only `SettingsSchemas`) uses a
  `Provider` indirection because the contributed schemas are
  identity-sensitive singletons (`GrammarModel` asserts on `INSTANCE`
  identity) and class-path service loading cannot call static factories.
  Providers are declared twice: `META-INF/services` (class path, installed
  app) and `module-info` (module path, Eclipse). A Simulator settings smoke
  test in Eclipse is part of the review. The parallel `Exporter.Provider`
  mechanism existed briefly but was reverted with the `lts.export` undo; it
  is cheap to reinstate if a higher layer ever needs to contribute an
  exporter.
