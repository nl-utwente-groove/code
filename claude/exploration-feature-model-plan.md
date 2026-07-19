# Migration plan: feature-model-based exploration configuration

Branch: `worktree-explore-feature-model`. Source: Arend's document *A Feature Model for
Exploratory Search* (July 2026), transcribed below so the repo is self-contained.

## Goal

Replace the rigid strategy/acceptor enumeration and its bespoke (de)serialisation
machinery (`explore.encode`, `explore.prettyparse`, `Serialized`, the `Template`s built in
`StrategyValue`/`AcceptorValue` — ~4,400 lines across ~37 classes) by a configuration
model with one orthogonal *feature* per variation dimension. Extension then means adding
a value to a feature, not writing a new `Strategy` subclass plus a hand-built template.

## The feature model (from the source document)

Exploration always starts with a fresh (empty) state space; exploring onwards from a
state is done by saving that state and starting a new exploration.

1. **Next-state selection** — choice of next state from the frontier (only among
   equal-quality candidates when a heuristic is used): *Oldest* (BFS), *Newest* (DFS),
   *Random*. Transient states never enter the frontier; they are explored nested, as part
   of successor generation.
2. **Successor selection** — which successors of the selected state to generate and put
   into the frontier: *Single-InOrder*, *Single-Random*, *All-InOrder*, *All-Random*.
   With Single-*, the state re-enters the frontier while it has unexplored transitions
   (unless frontier size is Single). Constraints: Oldest → All-* only; Random →
   All-InOrder only; Newest → all four are sensible (All-* has the lower memory
   footprint).
3. **Frontier size**: *Single* (linear search; next-state selection irrelevant), *Beam*
   (bound > 1), *Complete*. Single and Beam intentionally explore incompletely.
4. **Heuristic** — state quality function, typically estimated distance/cost to goal
   (smaller = better; strongly connected to goal type): *None*, *NEN* (node/edge/node
   tuple count; goal must be a complete graph or unnested NAC-free rule),
   *abstraction-based distances*, … an open area.
5. **Cost** — cost of a single transition; total path cost then matters (find the least
   costly solution): *None*, *Uniform* (cost = length), *Rule* (per-rule fixed cost or
   exposed rule parameter, selectable per rule). Cost + non-monotonic heuristic
   complicates the algorithm: closed states can be re-reached along a cheaper path and
   must be re-opened.
6. **Goal** — a condition plus desired outcome (satisfy/violate): *None* (satisfy only;
   explore until frontier empty), *Final* (no outgoing transitions; satisfy only),
   *Graph* (state graph isomorphic to a given graph), *Rule* (state satisfies a rule
   condition), *Formula* (propositional formula over the above), *LTL*, *CTL*.
   (Side note in the document: rule conditions should be kept visually more separate
   from rules — a separate action, out of scope here.)
7. **Result type**: *State*, *Trace*. (Trace needs additional support — storing/playing
   back, and a characterisation that accounts for recipe transitions; open question.)
8. **Result count** — when exploration may stop (it always stops on empty frontier):
   *First*, *Count* (n > 1), *All*.
9. **Bound** — limits depth or imposes ordering; a combination of a *function* on states
   (*None*, *Cost*, *Graph size*, *node/edge counts of selected element types*), a
   *maximum* beyond which states are not explored (or delayed), and an *increment*
   (> 0 enables iterative deepening).
10. **Persistence** — degree to which discovered states are stored: *All*, *None*.
    Requires lifting the GTS's built-in assumption that states are added on discovery
    and never removed; instead, fresh states live only in the frontier and enter the
    GTS when closed.
11. **State collapse** — when a fresh state is considered equal to a known one (other
    state properties, e.g. control frame, must coincide regardless): *Equality*,
    *Isomorphism*, *Hash* (iso hash only; may collapse non-isomorphic states; weaker
    hashes conceivable at the price of losing the morphism).
12. **Matcher**: *Plan* (with common/control edge types to tune the search plan),
    *Rete* (currently incomplete).
13. **Algebra**: *Default*, *Big*, *Point*, *Term*.

## Decisions taken (with Arend, 2026-07-19)

- **Scope: config first, engine later.** This branch delivers the new configuration
  model, GUI, CLI, and persistence, mapped onto the *existing* Strategy/Acceptor classes
  via a bridge. Engine unification (one parametric search algorithm) and demolition of
  the old machinery follow on later branches.
- **Prototype: mine, then delete.** The abandoned `explore.config` / `ExploreConfig` /
  `gui.dialog.config` / `ExploreConfigDialog` prototype (~1,400 lines, unreferenced) is
  harvested for ideas (notably the editor framework) and removed in this branch, so no
  third architecture lingers. The new model is built fresh to match the document.
- **Compatibility: convert on load, deprecate.** Stored `explorationStrategy` grammar
  properties are converted to the new form on load; `Generator -s/-a` keep working as
  deprecated aliases (translated through the legacy mapping) for at least one release.
- **Matcher/algebra/collapse: explore config overrides grammar default.** `algebraFamily`
  and `checkIsomorphism` remain grammar properties; the exploration config may override
  them per run (absent = inherit the grammar property). The matcher feature likewise
  replaces choosing Rete via dedicated strategies.

## Current architecture (what we are migrating away from)

- `Serialized` (`keyword(arg=value,…)`) is the currency between three consumers: the GUI
  (`gui.dialog.ExplorationDialog`, editors generated by `TemplateList.createEditor`),
  the CLI (`explore.Generator -s/-a` → `StrategyEnumerator.parseCommandLineStrategy`),
  and persistence (`GrammarKey.EXPLORATION` = `explorationStrategy`, value =
  `ExploreType.unparse()` = `"<strategy> <acceptor> <bound>"`).
- Every strategy is a closed case in `StrategyValue` (17 strategies) with a hand-built
  `Template` combining `prettyparse` combinators and per-argument `Encoded*` types that
  double as Swing editor factories. `AcceptorValue` does the same for acceptors.
- `ExploreType` (strategy `Serialized` + acceptor `Serialized` + bound) is the stored
  unit; `Exploration` binds it to a GTS and plays it.

## Phases

Each phase is a reviewable, independently mergeable unit; phases 1–4 are this branch's
programme (possibly as successive PRs off the same line).

### Phase 0 — this plan

### Phase 1 — configuration model (no behaviour change)

- Delete the `explore.config` prototype, `explore.ExploreConfig`,
  `gui.dialog.ExploreConfigDialog` and `gui.dialog.config` in a first commit (mining
  anything useful as we go), freeing the `explore.config` package name.
- New `explore.config` package:
  - `ExploreKey` — enum with one constant per feature above (as built: NEXT, SUCCESSOR,
    FRONTIER, HEURISTIC, COST, GOAL, OUTCOME, RESULT, COUNT, BOUND, PERSISTENCE,
    COLLAPSE, MATCHER, ALGEBRA; the goal's desired outcome became its own key).
  - Per-key value types: an interface `Setting` (kind + optional content), with kinds as
    enums and contents as small records (e.g. `Beam(int bound)`, `Count(int n)`,
    `Bound(Function, int max, int increment)`, `Goal(kind, ref, Polarity)`).
  - `ExploreConfig` — `EnumMap<ExploreKey, Setting>`; absent key = default. Defaults
    reproduce today's `ExploreType.DEFAULT` (bfs/final/0): Oldest, All-InOrder,
    Complete, no heuristic, no cost, goal Final/satisfy, result State/All, no bound,
    persistence All; MATCHER/ALGEBRA/COLLAPSE absent = inherit grammar property.
  - Text form: single line of `key value` pairs (only non-default entries), parsed with
    the standard `util.Parser`/`util.parse` infrastructure — **not** the `prettyparse`
    combinators. Round-trip unit tests for every key and content shape.
  - Cross-feature constraint checking (`ExploreConfig.check()`): the document's
    combination rules (frontier Single ⇒ next-state irrelevant, Oldest ⇒ All-*, etc.)
    reported as errors/silent normalisations, shared by GUI and CLI.

### Phase 2 — semantic bridge to the existing engine

- `ExploreConfigAssembler` (name t.b.d.): maps every *expressible* `ExploreConfig` to
  existing `Strategy` + `Acceptor` + bound instances. Initial mapping table:

  | config | legacy realisation |
  |---|---|
  | Oldest, All-InOrder, Complete | `BFSStrategy` |
  | Newest, All-InOrder, Complete | `DFSStrategy` |
  | Frontier Single, Single-InOrder | `LinearStrategy` |
  | Frontier Single, Single-Random | `RandomLinearStrategy` |
  | Matcher Rete + the above | `ReteStrategy` / `ReteLinearStrategy` / `ReteRandomLinearStrategy` |
  | Bound (graph size / rule set) | bounded BFS/DFS variants, `Boundary` |
  | Goal Final | `FinalStateAcceptor` |
  | Goal None | `NoStateAcceptor` |
  | Goal Rule (+ polarity) | `PredicateAcceptor` (ruleapp / inv) |
  | Goal Formula | `PredicateAcceptor` (formula) |
  | Result count First/Count/All | `Exploration` bound 1/n/0 |

- Legacy converter: `StrategyValue`/`AcceptorValue` + `Serialized` → `ExploreConfig`,
  for every dialog-exposed combination; used by property conversion and `-s/-a`
  aliases. Inexpressible legacy strategies (LTL family, `MinimaxStrategy`,
  `RemoteStrategy`, `ExploreStateStrategy`) stay on the old path untouched — they are
  not dialog strategies (LTL runs via the CheckLTL/CTL actions; the rest are
  development/internal) and are reconsidered in phase 5.
- Regression: run `ExplorationTest` scenarios through both paths and assert identical
  GTS shape; respect determinism practices for any Random feature (deterministic
  seeding — coordinate with the pending randomness-seeding design).

### Phase 3 — new GUI dialog

- New feature-model dialog replacing `ExplorationDialog`: one control per `ExploreKey`
  (combo + content sub-editors), constraint-aware enabling per `ExploreConfig.check()`,
  a live preview of the textual form, Run / Explore / Set-as-default buttons preserved.
  Editor widgets generically derived from the `Setting` structure (harvest the
  `gui/dialog/config` `SettingsPanel`/`EditorFactory` ideas, rewritten against the new
  model). MATCHER/ALGEBRA/COLLAPSE rows show the inherited grammar-property value when
  absent, with an explicit "override" affordance.
- Parity target: everything `StrategyValue.DIALOG_STRATEGIES` × acceptors could
  express. Old dialog is deleted in the same phase once parity is demonstrated.

### Phase 4 — CLI and persistence switchover

- `Generator`: new repeatable option taking `key=value` settings (exact letter/name
  t.b.d.), plus full-config string form; help text generated from `ExploreKey`
  metadata. `-s/-a` remain as deprecated aliases: parsed by the old machinery, then
  converted via the phase-2 legacy converter (warning printed); inexpressible values
  still run legacily.
- `GrammarKey`: new property (working name `exploration`, ValueType EXPLORE_CONFIG)
  storing the textual `ExploreConfig`. `GrammarProperties.getExploreConfig()` falls
  back to converting a legacy `explorationStrategy` value; saving from the dialog
  writes the new key and drops the old one.

### Phase 5 (later branch) — engine unification

One parametric frontier-based search algorithm (frontier + next-state policy +
successor policy + goal + bound + persistence hooks) replacing the `ClosingStrategy`
family, so feature values compose instead of multiplying strategy classes. Only then do
heuristic, cost, beam, hash-collapse, persistence None, trace results, iterative
deepening become implementable as ordinary feature values. LTL/CTL as goal types are
revisited here.

### Phase 6 (later branch) — demolition

Delete `explore.encode`, `explore.prettyparse`, `Serialized`, `ExploreType`,
`StrategyValue`/`AcceptorValue` templates and the enumerators; retire the deprecated
CLI aliases and the legacy property key.

## Open points (to be settled during the phases, with Arend where marked)

- **AnyStateAcceptor mapping** (Arend): `any` collects *every* state as a result, which
  the feature model has no goal value for (Goal None yields no results). Add a goal
  value *Any*, or drop the acceptor?
- **Exact textual syntax** of the config (separators, content notation) — proposed
  during phase 1 review.
- **CLI option naming** for the new settings — phase 4 review.
- **Trace result type / beam / heuristic / cost placeholders**: these are modelled in
  the phase-1 vocabulary so it is stable. *Resolved during phase 1:* `check()` verifies
  only the feature model's internal consistency rules; what is *implementable* is
  reported by the phase-2 assembler, which is the component that actually knows.
