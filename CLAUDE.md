# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

GROOVE is a tool for graph transformation and verification (state-space exploration and model checking), developed at the University of Twente. It is a single Maven project (Java 21, module `nl.utwente.groove`) producing both a Swing GUI and command-line tools. Website: <https://nl-utwente-groove.github.io>. The authoritative version number lives in `src/main/resources/nl/utwente/groove/resource/version/GROOVE_VERSION` (the pom `revision` property may lag behind it).

## Building and testing

### Dependencies

All dependencies resolve automatically; a fresh clone builds without setup. Four libraries not available on Maven Central (`gnuprologjava`, `ltl2buchi`, `osxadapter`, `groove-gxl`) are served from the project-local Maven repository checked in at `lib/repo` (declared as a `<repository>` in the pom). To add or upgrade such a library, place jar + pom (+ optional sources jar) in the standard Maven layout under `lib/repo` and commit.

### Commands

```
mvn clean package                              # build (shade plugin bundles the local libs into the jar)
mvn compile                                    # compile only
mvn test                                       # fast test suite (slow tests excluded)
mvn test -Dexcluded.test.groups=               # full suite including slow tests
mvn test -Dtest=AlgebraTest                    # run a single test class
```

**Slow tests are excluded by default**: `ExplorationTest` (full state-space exploration), `ImagerTest` and `IOTest` (image rendering / I/O round-trips), and `test/performance` carry the `nl.utwente.groove.test.SlowTest` category, which Surefire excludes via the `excluded.test.groups` pom property. Override with `-Dexcluded.test.groups=` to run everything.

### Eclipse

Development also happens in Eclipse; ready-made launch configurations live in `launch/` (Eclipse picks them up automatically): `GROOVE - all JUnit tests`, `GROOVE - maven test`, `GROOVE - build local maven artefact`, `GROOVE - generate javadoc`, `GROOVE - build local release (all)`.

## Entry points

Thin wrappers in `src/main/java/nl/utwente/groove/` delegate to the real implementations:

| Class | Kind | Purpose |
|---|---|---|
| `Simulator` | GUI | Interactive grammar editor/simulator (delegates to `gui.Simulator`) |
| `Viewer` | GUI | Read-only graph/grammar viewer |
| `Generator` | CLI | Headless state-space exploration (`explore.Generator`) |
| `ModelChecker` | CLI | CTL model checking (`verify.CTLModelChecker`) |
| `PrologChecker` | CLI | Prolog queries over a grammar/GTS (`prolog.PrologChecker`) |
| `Imager` | GUI/CLI | Renders graphs/grammars to image files (`io.Imager`) |

CLI argument parsing uses args4j via `util.cli`.

## Architecture

### Core pipeline: from disk to state space

```
.gps grammar directory
  → io.store.SystemStore            loads files from the .gps bundle
  → grammar.aspect.AspectGraph      each graph file parsed via AspectParser
  → grammar.model.GrammarModel      editable resource models (RuleModel, HostModel, TypeModel, ControlModel, …)
  → GrammarModel.toGrammar()
  → grammar.Grammar                 compiled: Rules + type graph + control automaton + start graph
  → explore.Exploration             Strategy + Acceptor drive the run
      match.Matcher                 finds rule matches (plan-based or RETE engine)
      transform.RuleApplication     applies them as deltas
      control.instance.Automaton    sequences rule calls
  → lts.GTS                         the resulting transition system (states = graphs)
  → verify / prolog / acceptors     consume the GTS
```

### Domain concepts

- **Graphs** (`graph`): directed, edge-labelled multigraphs behind the `Graph`/`Node`/`Edge`/`Label` interfaces. `graph/plain` is the raw string-labelled representation; `graph/iso` does isomorphism checking (used to collapse LTS states).
- **Aspect graphs** (`grammar/aspect`): the universal *editable* representation. An `AspectGraph` is a plain graph whose labels carry parsed aspect prefixes (`del:`, `new:`, `not:`, `forall:`, `int:`, `type:`, …, see `AspectKind`). All typed graphs are derived from aspect graphs.
- **Resources** (`grammar/model`): a grammar is a set of named resources enumerated by `ResourceKind` — RULE (`.gpr`), HOST (`.gst`), TYPE (`.gty`), CONTROL (`.gcp`), PROLOG (`.pro`), GROOVY, PROPERTIES, CONFIG — each with a `ResourceModel` subclass. Graph-based kinds wrap `AspectGraph`; text-based kinds wrap source text.
- **Rules** (`grammar`, `grammar/rule`): `Rule` is built from nested `Condition`s (quantified subconditions, embargoes/NACs). `grammar/host` holds typed host-graph elements (incl. `ValueNode` for data values); `grammar/type` holds type graphs with multiplicity/containment checking.
- **Matching** (`match`): two selectable engines — `match/plan` (`PlanSearchEngine`, ordered search plan with backtracking) and `match/rete` (incremental RETE network).
- **Transformation** (`transform`): `RuleEvent` (rule + anchor image) → `RuleApplication` producing **deltas** (`DeltaStore`, `MergeMap`). Deltas are central to scalability: LTS states share structure and are reconstructed on demand (`lts.StateCache`, `grammar/host.DeltaHostGraph`).
- **LTS** (`lts`): `GTS` extends `AGraph`; nodes are `GraphState`s, edges are `RuleTransition`/`RecipeTransition`.
- **Exploration** (`explore`): `Exploration` combines a `Strategy` (BFS, DFS, linear, LTL-guided, RETE variants, symbolic, …) with an `Acceptor` (final states, cycles, predicates, …). Strategies/acceptors are registered in `StrategyEnumerator`/`AcceptorEnumerator` and are string-parseable for CLI/GUI use.
- **Control language** (`control`): steers which rules fire when (sequencing, choice, loops, recipes/functions with parameters). Compiled via `control/template` into an executable automaton (`control/instance.Automaton`) that exploration walks in lock-step with matching.
- **Verification** (`verify`): CTL checking via `CTLModelChecker`/`CTLMarker`; LTL via Büchi automata (external `ltl2buchi` lib) and a product construction.
- **Algebras** (`algebra`): data attribute semantics. `AlgebraFamily` selects the interpretation: DEFAULT/BIG (concrete Java/BigInteger), POINT (collapsed, for abstraction), TERM (symbolic).
- **I/O** (`io`): `io/store.SystemStore` for `.gps` bundles; `io/graph` for native formats (GXL is the native graph serialization); `io/external` for the `Importer`/`Exporter` framework; `io/conceptual` is a metamodel-neutral IR used to round-trip with EMF/Ecore.
- **GUI** (`gui`): Swing `Simulator` around a central `SimulatorModel`; graphs rendered with JGraph (`gui/jgraph`, `AspectJGraph` etc.).

### Naming conventions

- `A*` prefix (`AGraph`, `ANode`) = abstract base class; `Default*` = standard concrete implementation.
- `*Model` = editable grammar-resource form vs. the compiled runtime object: `GrammarModel` → `Grammar`, `RuleModel` → `Rule`, `HostModel` → `HostGraph`.
- Many classes use the freeze-after-build pattern `util.Fixable`/`DefaultFixable`.

## Working practices for Claude sessions

**Isolation.** Do all non-trivial work in a git worktree on a fresh branch off `master` (EnterWorktree); never commit to `master` directly, and never touch the main checkout at `C:\Groove\code` or its uncommitted changes. The user reviews and merges; they pull into their checkout when they choose.

**Reviewability.** One concern per branch/PR, small independently mergeable commits in the house commit style. No drive-by changes: no reformatting, renaming, or "improving" code adjacent to the task — the diff contains only what the task requires. Write PR descriptions and session summaries as explanations (why the change, what was rejected, what was surprising), not changelogs.

**Confirmation.** Never `git push` or open a PR without explicit confirmation in the current session.

**Sub-agent model policy.** Default sub-agents to Opus (`model: "opus"`) for well-scoped, verifiable work: code search, mechanical refactors, dependency bumps, compile/test-fix loops. Reserve Fable-tier reasoning for orchestration, architecture and design decisions, adversarial verification, and changes with subtle correctness risk. If a specific task would likely suffer from Opus-level sub-agents, say so and ask the user before proceeding.

## Commit messages

Match the existing style: a single short subject line in sentence case, usually past tense ("Resolved regression bug gh #873", "Added user aspect to type graph aspects"). Reference GitHub issues as `gh #N` and SourceForge issues as `SF issue #N`. No body unless a brief parenthetical note is needed, and **no trailers** — do not add `Co-Authored-By` or other AI-attribution lines.

## Coding conventions

- **Null annotations**: the codebase uses `org.eclipse.jdt.annotation` (`@NonNullByDefault` on classes, `@Nullable`/`@NonNull` on members). Follow this in new and modified code.
- **Generated code**: `CtrlLexer`, `CtrlParser`, and `CtrlChecker` (package `nl.utwente.groove.control.parse`) are generated at build time by the `antlr3-maven-plugin` from `Ctrl.g` and `CtrlChecker.g` in `src/main/antlr3/nl/utwente/groove/control/parse/` into `target/generated-sources/antlr3`; they are not checked in. To change them, edit the `.g` files and rebuild. Other files in `control/parse` (`CtrlTree`, `CtrlHelper`, …) are hand-written.
- **Determinism**: successive explorations of the same rule system must behave identically (guarded by `DeterminismTest`). Therefore: hash codes of objects on the exploration path must be deterministic across runs (number- or content-based, never `System.identityHashCode` — see `ANode.computeHashCode` for the house pattern); collections that are *iterated* must be insertion-ordered (`LinkedHashSet`/`LinkedHashMap`/`ArrayList`) or sorted, never plain `HashSet`/`HashMap` unless their keys' hashes are deterministic; and `TreeHashSet.iterator()` iterates in insertion order — code relies on this, so it must stay that way.

## Test fixtures

Tests live under `src/test/java/nl/utwente/groove/test/` (JUnit 5; `TestSuite.java` auto-discovers the whole package tree). The top-level `junit/` directory holds on-disk fixtures — real grammars and graphs the tests load: `junit/rules/` (single-feature grammars, one per rule mechanism), `junit/control/` (control-language grammars), `junit/samples/` (larger examples), `junit/graphs/` (standalone graphs incl. isomorphism fixtures), plus `abstraction/`, `criticalpair/`, `types/`, `pattern/`. Tests point `SystemStore` at these `.gps` directories and assert on matches, GTS shape, or model-checking outcomes.
