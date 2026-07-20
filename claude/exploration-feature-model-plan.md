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

- ~~**AnyStateAcceptor mapping**~~ *Decided 2026-07-19: add a goal value `any`.* See
  the goal-kind proposal below.
- ~~**RuleApp acceptor mapping**~~ *Decided 2026-07-19: pre- and post-application are
  different and both must be expressible.* See the goal-kind proposal below.
- **Exact textual syntax** of the config (separators, content notation) — proposed
  during phase 1 review.
- **CLI option naming** for the new settings — phase 4 review.
- **Trace result type / beam / heuristic / cost placeholders**: these are modelled in
  the phase-1 vocabulary so it is stable. *Resolved during phase 1:* `check()` verifies
  only the feature model's internal consistency rules; what is *implementable* is
  reported by the phase-2 assembler, which is the component that actually knows.

## Design proposal: goal kinds `any` and `applied` (2026-07-19, approved and implemented)

Following Arend's decisions that (a) the `any` acceptor gets a goal value and (b)
pre- and post-application rule goals are different and must both be expressible.

### The underlying distinction: state goals versus transition goals

The legacy predicate classes already carve the semantics at the right joint:
`Predicate.RuleApplicable` is a *state* predicate (does the rule's condition match
the state graph?), while `Predicate.ActionApplied` is a *transition* predicate over
an `Action` — the supertype of rules *and* recipes (is the incoming transition an
application of this action?). So the two rule goals do not differ merely in timing;
they differ in **referent domain**:

- *pre-application* refers to a **graph condition** (a rule used as a property of the
  state graph, ignoring scheduling and priorities);
- *post-application* refers to an **action** (a rule or recipe whose scheduled
  application produced the state).

This argues for two distinct goal kinds rather than a mode flag inside one kind's
content: the content of the two kinds names different things, and their future
evolution differs (conditions become their own resource kind per the side note in
the feature model document; actions include recipes).

### Proposed goal vocabulary

| kind | content | condition on | meaning |
|---|---|---|---|
| `none` | – | – | no goal; no results |
| `any` | – | state | every state is a result *(new)* |
| `final` | – | state | no outgoing transitions |
| `graph` | graph name | state | state graph isomorphic to the named graph |
| `rule` | rule name | state | the named rule's condition matches the state graph (pre-application; ignores scheduling) |
| `applied` | action name | transition | the state was reached by an application of the named action (post-application; respects scheduling; actions include recipes) *(new)* |
| `formula` | formula | state | propositional formula over rule conditions |
| `ltl` / `ctl` | formula | trace / state space | temporal properties (deferred) |

Naming: `rule` keeps its document name for now; when rule conditions become a
separate resource kind, renaming it to `condition` is the natural follow-up. The
post-application kind is `applied` (reads as `goal=applied:load`).

### Semantic details to pin down

- **Outcome interaction.** `any` requires outcome `satisfy` (a violated any-goal is
  the none-goal); add this to `check()`. `applied` composes with both outcomes:
  violated means "reached by no application of the action" — which makes the start
  state (no incoming transition) a result under `violate` and never a result under
  `satisfy`. This matches the transition-predicate semantics of the legacy classes.
- **Late results.** A state can gain an incoming transition after its discovery, so
  under `applied` a state may *become* a result later in the exploration. With
  result count `first` this stops exploration at the first such transition, which is
  the legacy `ruleapp` behaviour and the intended one.
- **Trace results (future).** Under result type `trace`, an `applied` goal marks the
  transition that ends the trace; a state goal marks its final state. The
  distinction thus carries over cleanly to traces.
- **Formula alignment (future).** When the formula goal gets a structured atom
  language, `rule` and `applied` become the two atom forms (`r` and `applied(r)`),
  keeping the distinction inside formulas without further vocabulary.

### Bridge mapping

- `any` + satisfy → the `any` acceptor; legacy `any` → goal `any`.
- `applied` + satisfy → the `ruleapp` acceptor; legacy `ruleapp` → goal `applied`.
- `applied` + violate → **not realisable via the current acceptor keywords**: the
  predicate (`Not(ActionApplied)`) exists, but the `ruleapp` template has no
  polarity argument. Options: (i) reject until phase 5, or (ii) a small legacy
  extension giving the `ruleapp` template an optional polarity like `inv` has.
  Proposal: (i), unless the combination is wanted in the dialog now — the legacy
  extension touches the machinery this branch is trying to retire.

### Implementation sketch (once approved)

Goal enum: add `ANY` (no content) and `APPLIED` (string content); `check()`: `any`
requires satisfy; converter: the three mappings above plus rejection of
`applied`+violate; tests: extend both round-trip matrices and the rejection lists.

## Design proposal: goal vocabulary revision (2026-07-20, approved and implemented)

*Arend approved both parts, with `condition` as the surviving name for the merged
rule/formula kind. As implemented: goal kinds are now `none`, `any`, `final`,
`graph`, `condition` (propositional condition over rule names), `fires` (action
fires in the state, as scheduled), `ltl`, `ctl`. A bare `[!]name` condition maps to
the legacy `inv` acceptor (polarity absorbing negation and outcome), compound
conditions to the `formula` acceptor (a violated outcome negates the whole
condition); on conversion the outcome is normalised into the condition. Arend also
noted he no longer favours conditions as a separate resource kind; the `condition`
goal kind is agnostic on that question — it references rule names either way.*

Follow-up to Arend's dialog review, which raised two goal-vocabulary issues that are
not mechanical: the semantics/name of `applied`, and the redundancy of `rule` next to
`formula`. (The review's mechanical points — per-kind content memory, inline errors,
content syntax tooltips, default marking, richer feature tooltips, and the
`result` → `shape` rename — are implemented.)

### 1. The `applied` goal is a *source*-state condition

Confirmed in `PredicateAcceptor.addUpdate(GTS, GraphTransition)`: when a transition
matching the action is added, `transition.source()` becomes the result. So the goal
holds for the state *in which* the named action is applied (taken, as scheduled), not
for the state produced by it — the name `applied` (past tense) wrongly suggests the
latter. Options:

- **(a) Rename, keep source semantics** *(recommended)*. Candidate names:
  - `fires` — "a state in which the named action fires"; natural production-system
    terminology, present tense matches the source-state reading. `goal=fires:load`.
  - `taken` / `applies` — same semantics, weaker idiom.
  No engine change; the bridge keeps mapping to `ruleapp`.
- **(b) Switch to target-state semantics, keep the name `applied`.** Requires a new
  transition-predicate variant recording `transition.target()` — a change to (or next
  to) the legacy acceptor machinery this branch avoids touching; better done in
  phase 5.
- **(c) Both.** Rename to `fires` now (option a); add a separate target-state kind
  (`reached`?) in phase 5 if wanted. The two are genuinely different conditions, and
  traces end differently under them.

Proposal: (a) now, leaving (c)'s second kind as a phase-5 candidate.

### 2. `formula` subsumes `rule`

`goal=rule:r` and `goal=formula:r` build the identical predicate
(`Predicate.RuleApplicable`), since a bare rule name is a valid formula. Proposal:
**drop the `rule` kind**:

- The converter maps a formula that is a single `[!]name` to the legacy `inv`
  acceptor (identical semantics, nicer identifier), and anything else to `formula`;
  legacy `inv` converts back to `formula:[!]r`.
- `outcome=violate` becomes uniformly expressible by wrapping the formula in
  `!(...)` at conversion time — today violate works for `rule` but is rejected for
  `formula`; after the merge it works for every formula.
- The dialog keeps the rule-name convenience by using an editable rule-name dropdown
  as the formula editor (free text allowed) — a formula usually starts with a rule
  name.
- Naming: with `rule` gone, the kind could be renamed `condition`, anticipating the
  planned separation of rule conditions into their own resource kind; and if the
  `fires(r)` form of issue 1 later becomes a formula *atom* (as sketched in the
  earlier goal-kind proposal), `condition` becomes the single state-condition
  language. Keeping the name `formula` is the conservative alternative.

Decisions needed from Arend: (i) rename `applied` to `fires` (or another name /
option b/c)? (ii) drop `rule` in favour of the formula kind, and if so, keep the
name `formula` or rename to `condition`?
