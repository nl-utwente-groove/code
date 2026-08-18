# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

GROOVE is a tool for graph transformation and verification (state-space exploration and model checking), developed at the University of Twente. It is a single Maven project (Java 21, module `nl.utwente.groove`) producing both a Swing GUI and command-line tools. Website: <https://nl-utwente-groove.github.io>. The authoritative version number is the pom's `revision` property; the `GROOVE_VERSION` resource read by `util.Version` contains `${revision}` and is generated from the pom by resource filtering (in Eclipse, a changed `revision` reaches the filtered copy only after a Maven &gt; Update Project or full build).

## Building and testing

### Dependencies

All dependencies resolve from Maven Central; a fresh clone builds without setup. Four third-party libraries never published by their upstreams (`gnuprologjava`, `ltl2buchi`, `osxadapter`, `groove-gxl`) are republished to Central under the `nl.utwente.groove` group id, with a fourth version digit marking the repackaging. The repackaging modules, the upstream jars they are built from, and the publication procedure live in `lib/publish` (see its README); upgrading such a library means a new upstream jar in `lib/publish/upstream`, a new published version, and a redeploy by a maintainer.

### Commands

```
mvn clean package                              # build
mvn compile                                    # compile only
mvn test                                       # fast test suite (slow tests excluded)
mvn test -Dexcluded.test.groups=               # full suite including slow tests
mvn test -Dtest=AlgebraTest                    # run a single test class
```

**Slow tests are excluded by default**: `ExplorationTest` (full state-space exploration), `ImagerTest` and `IOTest` (image rendering / I/O round-trips), and `test/performance` carry the `nl.utwente.groove.test.SlowTest` category, which Surefire excludes via the `excluded.test.groups` pom property. Override with `-Dexcluded.test.groups=` to run everything.

**Keep build output out of the model context**: a `PreToolUse` hook (`.claude/hooks/pretool-guard.ps1`, wired up in `.claude/settings.json`) denies `mvn` test/package/install/verify runs that are neither quiet nor redirected. Run `mvn -q <goals> > <log> 2>&1` and grep the log; test-failure details land in `target/surefire-reports`. The same hook blocks `git commit` on `master` and turns `git push` / `gh pr create` into a user confirmation prompt.

### Eclipse

Development also happens in Eclipse; ready-made launch configurations live in `launch/` (Eclipse picks them up automatically): `GROOVE - all JUnit tests`, `GROOVE - maven test`, `GROOVE - build local maven artefact`, `GROOVE - generate javadoc`, `GROOVE - build local release (all)`.

## Entry points

Thin wrappers in `src/main/java/nl/utwente/groove/` delegate to the real implementations:

| Class | Kind | Purpose |
|---|---|---|
| `Simulator` | GUI | Interactive grammar editor/simulator (delegates to `gui.Simulator`) |
| `Viewer` | GUI | Read-only graph/grammar viewer |
| `Generator` | CLI | Headless state-space exploration (`explore.Generator`) |
| `ModelChecker` | CLI | CTL model checking (`explore.CTLModelChecker`) |
| `PrologChecker` | CLI | Prolog queries over a grammar/GTS (`prolog.PrologChecker`) |
| `Imager` | GUI/CLI | Renders graphs/grammars to image files (`gui.Imager`) |

CLI argument parsing uses picocli via `util.cli`.

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
- **Verification** (`verify`): CTL checking via `CTLMarker` over a `ModelFacade` (the CLI shell is `explore.CTLModelChecker`, since it generates the state space before checking it); LTL via Büchi automata (external `ltl2buchi` lib) and a product construction.
- **Algebras** (`algebra`): data attribute semantics. `AlgebraFamily` selects the interpretation: DEFAULT/BIG (concrete Java/BigInteger), POINT (collapsed, for abstraction), TERM (symbolic).
- **I/O** (`io`): `io/store.SystemStore` for `.gps` bundles; `io/graph` for native formats (GXL is the native graph serialization); `io/external` for the headless `Importer`/`Exporter` framework and registries (`Exporters`/`Importers`). Exporters that work by rendering a graph via JGraph live on the GUI side (`gui/export`) and are contributed to the registry at start-up through `Exporters.register`.
- **GUI** (`gui`): Swing `Simulator` around a central `SimulatorModel`; graphs rendered with JGraph (`gui/jgraph`, `AspectJGraph` etc.).

### Naming conventions

- `A*` prefix (`AGraph`, `ANode`) = abstract base class; `Default*` = standard concrete implementation.
- `*Model` = editable grammar-resource form vs. the compiled runtime object: `GrammarModel` → `Grammar`, `RuleModel` → `Rule`, `HostModel` → `HostGraph`.
- Many classes use the freeze-after-build pattern `util.Fixable`/`DefaultFixable`.

## Working practices for Claude sessions

**Isolation.** Do all non-trivial work in a git worktree on a fresh branch off `master` (EnterWorktree); never commit to `master` directly, and never touch the main checkout at `C:\Groove\code` or its uncommitted changes. The user reviews and merges; they pull into their checkout when they choose.

**Reviewability.** One concern per branch/PR, small independently mergeable commits in the house commit style. No drive-by changes: no reformatting, renaming, or "improving" code adjacent to the task — the diff contains only what the task requires. Write PR descriptions and session summaries as explanations (why the change, what was rejected, what was surprising), not changelogs.

**Confirmation.** Never `git push` or open a PR without explicit confirmation in the current session.

**Review handoff.** The user reviews branches in Eclipse by switching the main checkout onto them, hand-fixing and committing there. To make that a legitimate single checkout: when a branch is ready for review, detach the worktree's HEAD (`git switch --detach` in the worktree) and tell the user the branch is free. When the user says to continue working on a branch, assume the review is finished — re-attach the worktree (`git switch <branch>`) and continue on the current tip without asking. If git refuses the re-attach because the main checkout still holds the branch, report that and wait; never manipulate the main checkout to free it.

**Sub-agent model policy.** Default sub-agents to Opus (`model: "opus"`) for well-scoped, verifiable work: code search, mechanical refactors, dependency bumps, compile/test-fix loops. Full-suite `mvn test` runs and `null-check` verification are standing delegation targets: have an Opus sub-agent run them and report only failures, so the bulk output never enters the main context. Below Opus, use Haiku (`model: "haiku"`) for purely mechanical tasks with no judgment content — bulk greps, file inventories, log digestion, pass/fail test runs; a Haiku agent reports facts, it does not decide what matters. Reserve Fable-tier reasoning for orchestration, architecture and design decisions, adversarial verification, and changes with subtle correctness risk. If a specific task would likely suffer from Opus-level sub-agents, say so and ask the user before proceeding.

**Claude files.** Files created primarily for Claude's own use — these instructions, and any future notes or plans worth committing — live in the `claude/` subdirectory. The root `CLAUDE.md` is only a stub that imports `claude/CLAUDE.md` (Claude Code auto-loads only the root file); keep it that way.

## Communication style

**Register.** Write like a terse senior engineer addressing a colleague: neutral, factual, understated. Enthusiastic adjectives ("great", "perfect", "excellent", "elegant") only when they carry real information, which is rarely. Disagree plainly when the user is wrong; don't hedge for politeness.

**No sycophancy.** Never open with praise or agreement flourishes ("You're absolutely right", "Great question", "Excellent idea"), and don't compliment the user's questions, the codebase, or your own results.

**No filler openers.** Start with content, not throat-clearing. Closing offers of follow-up work are fine.

**Calibrated claims.** State as fact only what was verified ("tests pass"); phrase the rest as expectation ("should work, untested"). Report failures, partial results and skipped steps plainly, without softening.

## Commit messages

Match the existing subject style: a short subject line in sentence case, usually past tense ("Resolved regression bug gh #873", "Added user aspect to type graph aspects"). Reference GitHub issues as `gh #N` and SourceForge issues as `SF issue #N`. Add a body as extensive as the change warrants — explain the why, rejected alternatives, and surprises rather than restating the diff; trivial changes can stay subject-only. **No trailers** — do not add `Co-Authored-By` or other AI-attribution lines.

## Coding conventions

- **Null annotations**: the codebase uses `org.eclipse.jdt.annotation` (`@NonNullByDefault` on classes, `@Nullable`/`@NonNull` on members). Standard practice: add `@NonNullByDefault` wherever possible — always on new classes, and on existing classes being modified. Annotate *consistently across an inheritance web*: an annotated class extending or implementing an unannotated one (or vice versa) is a compile error under Eclipse's null analysis, so widen the annotation to the interfaces, base classes and implementors involved rather than annotating one file in isolation. Idioms: lookup maps whose `get` result is null-checked are declared `Map<K,@Nullable V>` (see `Rule.matcherMap`) — with a non-null value type, JDT flags the null check as impossible; late-initialised fields are `@Nullable` with an asserting accessor.

  **External annotations**: `lib/eea` holds a curated subset of JDK nullness contracts (gh #881; currently the java.util collection framework — `Queue`/`Deque`, `Map`, the collection interfaces and implementations, `Arrays`/`Collections`/`Objects`, `Properties` — plus all of java.lang including reflect, java.io/net/zip/jar except the printf carriers `PrintStream`/`PrintWriter`, and the javax.swing tree with one trim: `UndoableEditSupport`'s listener registrations stay unannotated, since their `@NonNull` parameters clash with jgraph's unannotated `GraphModel` interface across the `JModel` hierarchy), applied both in Eclipse (JRE-container `annotationpath` in `.classpath`) and by the ecj harness. Consequences: `poll()`/`peek()`, `Map.get()` and `Properties.getProperty()` results are `@Nullable` and need a null check or the assert idiom even when the code guards presence another way (`!isEmpty()`, `containsKey`); collection bulk operations and copy constructors (`addAll`, `new HashSet<>(c)`, `EnumSet.copyOf`) require `@NonNull` arguments, so a `@Nullable` lookup result must be hoisted and asserted before being passed on; `List.sort` requires a non-null comparator — write `sort(Comparator.naturalOrder())`, not `sort(null)`. From java.lang: `Object.equals` has a `@Nullable` parameter, so every `equals` implementation must null-guard before dereferencing its argument (fold `obj == null ||` into the existing `getClass()`/`super.equals` test); `clone()` must never return null (an unreachable `CloneNotSupportedException` catch throws `Exceptions.unreachable()`); `Class.getEnumConstants`, `getAnnotation`, `Throwable.getCause`/`getMessage`, `System.getProperty` and `Method.invoke` results are `@Nullable` (a primitive-returning invoked method still yields a non-null box — assert with that reason). From java.io: `File.listFiles` genuinely returns null on I/O errors — write a real null check with an error path, never the assert idiom (in the test tree, where surefire enables assertions, the assert is acceptable as the failure path); `readLine` is `@Nullable` at end of stream. From javax.swing: `JFileChooser.getSelectedFile` and various model/selection getters are `@Nullable`. Extend the set deliberately — every added method turns unchecked call sites into warnings; see `lib/eea/README.md` for the curation policy.

  **The Maven build does not run JDT's null analysis**, so `mvn test` cannot catch violations; they surface only in Eclipse. Before handing over work that adds or changes Java code, run the `null-check` skill (procedure and helper script in `.claude/skills/null-check/`) on the touched files. Zero errors and zero new warnings is the bar.
- **Generated code**: `CtrlLexer`, `CtrlParser`, and `CtrlChecker` (package `nl.utwente.groove.control.parse`) are generated at build time by the `antlr3-maven-plugin` from `Ctrl.g` and `CtrlChecker.g` in `src/main/antlr3/nl/utwente/groove/control/parse/` into `target/generated-sources/antlr3`; they are not checked in. To change them, edit the `.g` files and rebuild. Other files in `control/parse` (`CtrlTree`, `CtrlHelper`, …) are hand-written.
- **Determinism**: successive explorations of the same rule system must behave identically (guarded by `DeterminismTest`). Therefore: hash codes of objects on the exploration path must be deterministic across runs (number- or content-based, never `System.identityHashCode` — see `ANode.computeHashCode` for the house pattern); collections that are *iterated* must be insertion-ordered (`LinkedHashSet`/`LinkedHashMap`/`ArrayList`) or sorted, never plain `HashSet`/`HashMap` unless their keys' hashes are deterministic; and `TreeHashSet.iterator()` iterates in insertion order — code relies on this, so it must stay that way. Beware that insertion order itself is not stable under cache collapse: state caches are softly referenced (`CacheReference`) and may be cleared by the garbage collector mid-exploration, after which graphs and transition data are *reconstructed* in a potentially different insertion order. Order-bearing decisions must therefore not depend on collection iteration order at all, but be made canonical by explicit comparison (as `MatchCollector.canonicalise` does for the match application order); `DeterminismTest` simulates such collapses by GC-sweep-like cache clearing at various points of the exploration (see `claude/determinism-ferryman-flake.md` for the full analysis).
- **Performance**: exploration is the hot path — code touching states, matches, events, control frames or graph elements runs millions of times per run. A content-based `hashCode()` that does more than a few field operations must be cached, in the house pattern of `ANode`: a lazily computed `private int hashCode` field with `0` as the unset sentinel (remapped to 1) and the computation in a separate `computeHashCode()`. Caching is only sound if live computation would return the same value every time: every field contributing to the hash must be immutable, or the object must be fixed (cf. `Fixable`) before the hash is first requested — otherwise do not cache. More generally, prefer the existing scalability mechanisms (canonical/pooled instances, `Factory` lazy caching, delta-based graph reconstruction) over recomputing derived data.

- **AI provenance**: code substantially written by Claude is marked with `nl.utwente.groove.util.AIGenerated`, with a note naming the model that actually generated the code and the date, like `@AIGenerated("Claude Fable 5, 2026-08")`. Apply it at the type level for new classes written wholesale, and at the member level for wholly new members added to existing classes. Do *not* mark small edits to existing code — git history covers that granularity, and sprinkled annotations are noise. When substantially rewriting an annotated element, remove or retain the annotation as part of the change so it never goes stale. The annotation documents origin, not quality: annotated code has been reviewed like any other.

## Test fixtures

Tests live under `src/test/java/nl/utwente/groove/test/` (JUnit 5; `TestSuite.java` auto-discovers the whole package tree). The top-level `junit/` directory holds on-disk fixtures — real grammars and graphs the tests load: `junit/rules/` (single-feature grammars, one per rule mechanism), `junit/control/` (control-language grammars), `junit/samples/` (larger examples), `junit/graphs/` (standalone graphs incl. isomorphism fixtures), plus `abstraction/`, `criticalpair/`, `types/`, `pattern/`. Tests point `SystemStore` at these `.gps` directories and assert on matches, GTS shape, or model-checking outcomes.
