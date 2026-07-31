# State of the exploration feature model programme (2026-07-27)

Note to a future Claude session. Companion to
[exploration-feature-model-plan.md](exploration-feature-model-plan.md), which holds the
feature model, the phase plan and the decision log; this note records the *as-built*
state, the invariants discovered along the way, and where to pick up.

## Status: phases 1–4 done; 5a done; 5b ongoing (slices 1–2: randomness, beam)

Branch topology (2026-07-26): **phases 1–4 are merged to master and pushed**
(master fast-forwarded onto `explore-feature-model`@64e5813ed at Arend's request;
the `explore-feature-model` branch is deleted — dialog work now happens on
branches off master). The engine branch continues:

    master (64e5813ed, incl. phases 1–4 + eight dialog-review rounds)
      ⊂ explore-parametric-engine
          phase 5a (engine skeleton) + 5b slices 1–2 (seeded randomness,
          beam search)

Merging the engine branch to master fast-forwards (unless master moves on).
Suite at the 5a freeze, measured at the engine tip: 368 fast, **399 including slow
tests** (`mvn test -Dexcluded.test.groups=`), all green; fast suite re-verified
green after every later round. Phases 1–4: the feature model is the only
user-facing way to express exploration (dialog, `-x`, grammar property). Phase 5a:
configuration-based exploration instantiates the `explore.engine` classes
directly, without the encode/Template machinery; the deprecated keyword path
(`-s/-a`, legacy property) still runs the enumerator-instantiated legacy classes
as the parity reference. Phase 5b slice 1 (2026-07-26): seeded randomness — see
the `util.Randomness` entry below and `claude/randomness-seeding.md`. Slice 2
(2026-07-27): beam search (`BeamPool`); **the heuristic dimension (and with it
cost-based ordering) was deferred by Arend at slice start** — he wants to
design that dimension carefully rather than start from `nen` (see the open
threads below). Slice 3 (2026-07-31): persistence=none — see the storing-seam
entry and invariant below. Remaining 5b slices (overrides + trace) and
phase 6 (demolition) are future work — do not start unprompted.

The second dialog-review round (2026-07-22, commits 6718ad5db + 571c958e3) settled:
drop-down defaults are marked with a trailing `*` only ("(default)" stays in the
tooltip), keyed to the *key-inherent* defaults (`ExploreKey.getDefaultKind`), not
the grammar's stored configuration; and the grammar-dependent contents of a stored
configuration are validated by the property checker (see `ExploreConfigChecker`
below), so condition/rule/label errors surface on the system properties.
Arend's fe1a4a74b then fixed the tooltip "(default)" placement (plain, directly
after the kind name).

The third dialog-review round (2026-07-26, commits 8840e76b2 + 211d204f9, decisions
per AskUserQuestion: error area below the Configuration panel; red label + border
for key marking) reworked error display: the dialog computes the composed
configuration's errors itself (per-key parse + `ExploreConfigChecker`, which
gained a per-key `check(GrammarModel, ExploreKey, Setting)` for attribution), so
actual errors show instead of "The grammar has errors" (that text remains only for
unrelated grammar errors); erroneous keys are marked at their row (red key phrase,
red-bordered content editor, tooltips lead with the error); errors live in a
borderless top-aligned area between the Configuration panel and the buttons
(invisible when empty, full text, dialog grows via pack-on-overflow); the
content-syntax tooltip moved off the key/kind tooltip onto the content editor
only; and "Set Default" refreshes the status afterwards (it is now disabled while
grammar-dependent content is invalid — previously a broken config could be stored,
immediately breaking the grammar). Follow-up fixes (716c5dc67, e1540b7b3): the
dialog grows in height only when errors appear (pack() caused a spurious
horizontal resize); the Configuration panel's preview + status are anchored to
the panel top; the error wrap width is the laid-out error-area width (falling
back to the widest sibling's preferred width during construction), so error text
never widens the dialog; the generic "The grammar has errors" message is gone
from the error area (tooltip of the disabled run buttons only) — but an
unparseable stored exploration value (feature-model or legacy key), which cannot
be loaded into the widgets, is reported in the error area via
`Properties.parseProperty` until a new default is stored over it. Further
(d7057d384, delivered on temp branch `explore-feature-model-fixes` while Arend's
checkout held the branch): the error panel reports preferred width 0 (it wraps
anyway, so it must not influence the dialog width), and the equal-thirds
GridLayout rows became BorderLayout rows with the key and kind columns sized to
their widest values (aligned across rows); the content editors keep the
remaining width, deliberately untouched — **Arend plans a sub-dialog for the
more involved contents (such as formulas) as a later refinement**.
Note: the "Runs as" status line shows the legacy `ExploreType` identifier and
disappears in phase 6; the preview field (the config's own text form) stays.

## As-built map

- `explore.config` — the model. `ExploreKey` (13 keys; the `matcher` key was dropped
  2026-07-22 when the RETE retirement on master left a single engine), `Setting`
  (record: `Kind` + content), one kind enum per key (`NextState`, `Successor`,
  `Frontier`, `Heuristic`, `Cost`, `Goal`, `Outcome`, `Shape`, `Count`, `Bound`,
  `Persistence`, `Collapse`, `Algebra`),
  `SettingParser`/`SettingKindMap`/`Null`, and `ExploreConfig`
  (EnumMap; text form = space-separated `key=value`, non-default entries only;
  `check()` = cross-feature consistency only — *realisability* is the converter's job).
- `explore.config.ExploreTypeConverter` — bidirectional partial bridge to legacy
  `ExploreType` (`toExploreType` / `toConfig`); single place that knows what the legacy
  engine can realise. Everything inexpressible errors with an explanation. Since 5a,
  `toExploreType` returns a `ConfiguredExploreType` (subclass holding the config)
  whose `getParsedStrategy`/`getParsedAcceptor` instantiate directly — the engine
  classes below, and the acceptor classes — from the converter-computed legacy
  descriptors, reusing the `Encoded*` semantic parsers but bypassing
  Template/enumerator parsing. All config consumers funnel through `toExploreType`,
  so this one return type switched the GUI, CLI and property paths to the engine.
- `explore.engine` (5a) — `Pool` (take/add/readd/clear; may impose a depth bound;
  stateful, not shareable between explorations) with `QueuePool`/`StackPool`
  replicating the legacy BFS/DFS orderings verbatim; `FrontierStrategy extends
  ClosingStrategy` delegating the pool hooks to an injected `Pool` — deliberately
  *inheriting* `doNext()` (transient stack, KNOWN re-traversal, stop modes) instead
  of re-porting it. `test.explore.EngineParityTest` A/B-compares engine vs enumerator
  paths on ferryman (order proved bit-identical) and checks re-run determinism.
  Since 5b slice 1 also `RandomPool` (uniform swap-remove take, seeded), realising
  `next=random` under the engine-only converter keyword `random-frontier`.
  Since 5b slice 2 also `BeamPool` (engine-only keyword `beam`, arguments
  `next` + `size`), realising `frontier=beam:n`: a size-capped pool whose
  order within the beam is the next-state selection and which on overflow
  drops the state that would be explored last (`oldest` → the incoming state,
  `newest` → the oldest, `random` → a seeded uniform pick, incoming included).
  An unrestricted beam is bit-identical to the corresponding plain pool for
  all three orders (`BeamSearchTest` asserts this, including the seeded
  random draws — `BeamPool.readd` appends under the random order to
  replicate `RandomPool` exactly); `readd` also trims, for robustness (a
  state whose transience resolves between discovery and exploration reaches
  it without a preceding take).
- Persistence feature (5b slice 3, 2026-07-31) — `GTS.setStoring(boolean)` is
  the seam: without storing, `addState` skips the state-set put (every
  discovered state is fresh — **no revisit detection, collapse inoperative**,
  per Arend's decisions) and `AbstractGraphState.setClosed` skips persisting
  the cached transition stubs into the state's hard array, so a closed state
  does not pin its successors; listeners fire as always, so acceptors see the
  full exploration and pin their result states in `ExploreResult`. Memory
  release is pure garbage collection (never-enter, nothing to evict): live =
  frontier + ancestor delta chains + results. Fresh-state numbering comes
  from the explicit counter `GTS.getNextStateNr` (also the discovery count;
  under storing identical to the old `nodeCount()` numbering). Applied per
  run by `ExploreType.prepareGTS(GTS)` (no-op default, called from the
  `Exploration` constructor; `ConfiguredExploreType` overrides). The GTS
  counts and flagged state lists reflect only the retained part (Arend:
  "GUI shows what is retained"). `toConfig` short-circuits for
  `ConfiguredExploreType` (returns a copy of the authoritative config;
  persistence leaves no trace in the legacy descriptors — the copy uses the
  new `ExploreConfig` copy constructor). `PersistenceTest` covers it.
- `util.Randomness` (5b slice 1, 2026-07-26) — the master-seed registry of
  `claude/randomness-seeding.md` (decisions resolved, see there): per-purpose
  streams (EXPLORATION, ORACLE) derived per obtainment, so a fixed master seed
  (explicit `-seed` / `groove.randomSeed` property / generated-and-logged) makes
  every exploration identical. Seeded consumers: `RandomLinearStrategy`,
  `RandomChooserInSequence`, `RandomOracle`. `test.explore.RandomnessTest` covers
  derivation and seeded-run reproducibility.
- `explore.config.ExploreConfigChecker` — validates the grammar-dependent contents
  of a configuration (condition formula syntax, rule existence/enabledness, edge
  labels) against the **GrammarModel**, invoked from the `exploration` property
  checker in `GrammarKey`. It must NOT instantiate the grammar: the property checker
  runs inside `GrammarModel.toGrammar()`, so calling `toGrammar()` there recurses.
  Model-level stand-ins: `getActiveNames(RULE)` for rule enabledness,
  `getTypeGraph()` for labels, and `EncodedRuleFormula`'s `RuleResolver` hook (added
  for this purpose; the `Grammar`-based parse is now one resolver instance) for
  formula checking with a placeholder predicate. `EncodedEdgeMap` gained a
  type-graph `parse` overload likewise. `test.explore.ExploreConfigCheckerTest`
  covers broken and valid contents. Since the third review round the aggregate
  check delegates to a per-key `check(GrammarModel, ExploreKey, Setting)`, which
  the dialog uses to attribute errors to key rows.
- `gui.dialog.ExploreConfigDialog` — replaces `ExplorationDialog`; rows per key,
  dependency-aware enabling, preview + "Runs as", buttons enabled via conversion +
  `ExploreType.test(grammar)`. Stores defaults as config
  (`SimulatorModel.doSetDefaultExploreConfig`). Drop-downs mark the key-inherent
  default kind with a trailing `*` (the "(default)" wording lives in the tooltip);
  the grammar's stored choice is visible as the initial selection, not marked.
  Error display (third round): per-key marking + a dedicated error area below the
  Configuration panel; the status label is informational only; content-syntax
  tooltips live on the content editors only.
- Persistence — `GrammarKey.EXPLORE_CONFIG` ("exploration",
  `ValueType.EXPLORE_CONFIG`); precedence over legacy `EXPLORATION`
  ("explorationStrategy") in `GrammarProperties.getExploreType/getExploreConfig`; lazy
  conversion on read, legacy key deleted on `setExploreConfig`.
- CLI — `Generator -x "<config>"`; `-s/-a/-r` deprecated, mutually exclusive with
  `-x`, warning suggests the equivalent `-x`. `-D exploration=...` works through the
  generic property mechanism. `EXPLORE_USAGE` in `Generator` is hand-written — **keep
  in sync with the kind enums** (picocli annotations need compile-time constants).

## Invariants and gotchas (hard-won; do not rediscover)

- A well-formed `bfs`/`dfs` `Serialized` must always carry the `bound` argument
  (`"0"` = unbounded): the legacy unparser renders an absent optional argument as the
  unparsable `bfs:null`. The converter sets it always; canonical test fixtures too.
- `uptorule` argument encodings: `search` = `bfs|dfs`, `stop` = `->` (up-to) / `=>`
  (include), `polarity` = `Positive|Negative`, and the numeric `bound` argument must
  always be set (TemplateN parses every declared argument; missing ⇒ parse error).
- `StringHandler.splitExpr` treats `<`/`>` as brackets — that is why `ExploreConfig`
  has its own quote-aware tokeniser. Don't "simplify" back.
- `ExploreConfig` values with whitespace are quoted with `"` and escaped with `\`;
  `SettingParser` splits kind from content at the *first* `:`.
- Tests: use `org.junit.Test` (JUnit 4 annotation, public class/methods) + jupiter
  assertions; a jupiter-annotated `@Test` class is *silently skipped* by surefire.
  Single-class runs: `mvn test -Dtest=Name` (jupiter-annotated would show 0 tests).
- **Pools never see transient states** (since the 5b transient-nesting fix,
  2026-07-27): transient states belong to a nested sub-exploration ending in
  an atomic (transactional) transition, run to completion on the strategy's
  internal stack. Historically the trial re-add in `ClosingStrategy.doNext`
  leaked verdict-pending transient states into the pool (the commented-out
  `assert !state.isTransient()` there marked the broken assumption; trigger:
  a try/else around a *recipe* call inside a recipe, i.e. recursion, as in
  `fibonacci.gps` — a plain rule-call try inside a recipe resolves at match
  time and never leaked). Now such states are pushed back on the transient
  stack; verdict resolution is push-driven (closure/transience propagation
  through `StateCache`), so a terminating sub-exploration always resolves
  before the stack drains back to the state — only a non-terminating
  transient descent stays pending, and that diverges under any scheme (rule
  system error, per Arend). The contract is documented on `Pool` and
  asserted in `FrontierStrategy`; `TransientNestingTest` guards it (it also
  fails pre-fix). Pool implementations may therefore drop/reorder freely
  without cutting into transactions.
- `storeValue` removes a key when the value is the key default — hence
  `setExploreConfig` must (and does) delete the legacy key explicitly, or a leftover
  legacy value would win after storing an explicit default config.
- **`GTS.addTransition` → `StateCache.addTransition` is protocol, not just
  storage** (learnt the hard way in slice 3): it removes the explored match
  from the state's match set and closes the state when the set is finished.
  Bypassing it (the first attempt at persistence=none) left every state
  open. The cache is the *working set* of an open state — protected from
  collapse while open (`AbstractGraphState.clearCache` refuses) — and
  persistence lives one step later, in `setClosed`'s
  `setStoredTransitionStubs` copy; that copy is the only place the storing
  flag needs to intervene on the transition side.
- **Recipes do not compose usefully with persistence=none**: a transient
  sub-exploration is exhaustive by design (it runs on the internal stack,
  bypassing any frontier restriction incl. beam), so without collapse a
  diamond-rich recipe body is explored as its full tree unfolding —
  fibonacci OOMs. Inherent, not a bug: under `none` the terminating-tree
  requirement extends to every recipe body. Termination in general needs a
  finite tree unfolding: cyclic grammars need a depth bound, which is
  currently expressible only for bfs/dfs orders (not random/beam).

## Deliberately unsupported (converter errors, awaiting phase 5)

heuristic≠none (the whole dimension deferred by Arend pending a careful
design, along with cost-based ordering), cost=rule, successor=all-random,
single-successor on a multi-state frontier, shape=trace,
collapse/algebra overrides (kinds `grammar` = inherit), goal=graph, goal=ltl/ctl
(stay with the CheckLTL/CTL actions), iterative deepening (`+inc`), bound=size,
`fires`+violate (legacy ruleapp has no polarity), condition bound + depth bound
together (BOUND is a single key). Goal vocabulary since the 2026-07-20 review:
`condition` (merged rule+formula; bare `[!]name` → inv, compound → formula,
violate normalised into the condition) and `fires` (source-state semantics —
PredicateAcceptor records transition.source()). Legacy without feature equivalent: `state`,
`minimax`, `remote` (CLI-only per Arend), LTL strategies, `cycle` acceptor.

## Open threads for later phases

Dialog/Simulator threads (2026-07-26, from Arend's review):

- Three exploration settings are in play at any moment: factory default (the `*`
  in the dialog), **saved** (the grammar's `exploration` property — "saved" is
  the agreed term, "default" is reserved for the factory defaults), and
  **current** (the transient per-run override held by `SimulatorModel`). The
  dialog marks deviation from the current one (bold keys, tooltip, Revert /
  Reset to Saved buttons, commit a2eb94d16). Arend is **unsure about the
  usefulness of the transient override** (its rationale: continue exploration
  *from* a given state under a deviating strategy without touching the saved
  one) — reconsider; if it goes, "current" collapses into "saved" and the
  dialog simplifies.
- Arend wants a **history of past settings**, as the LTL/CTL formula dialogs
  already offer — future dialog round.
- A **sub-dialog for the more involved content editors** (formulas etc.) is
  planned as a later refinement; content editors stay as they are until then.

- Phase 5b+ (random/beam orders and persistence are done):
  trace results (note: under persistence=none the ancestor chain of a
  pinned result state survives by construction — the trace is nearly free);
  collapse/algebra overrides (baked into the GTS at construction — what
  Continue over an existing GTS means under a different override needs an
  Arend decision; the same question exists in miniature for a
  persistence-flipping Continue, currently just documented on
  `setStoring`); then the unsupported list above becomes implementable
  feature by feature; revisit LTL/CTL goals; possibly a target-state
  counterpart to `fires` ("reached by the action"), and `fires(r)` as an
  atom of the condition language. If persistence=none sees real use, a
  depth bound for the random/beam orders is the natural termination aid.
- **Heuristic dimension: deferred by Arend (2026-07-27), design-first.** When
  asked to pick the nen referent for the ordered-pools slice he chose to omit
  heuristics from the slice altogether: "heuristics open the door to a wealth
  of possibilities, nen just scratches the surface; I want to design this
  carefully". Two sub-decisions from the same exchange survive for that
  future design round: cost-based ordering engages only in combination with
  a heuristic (`cost=uniform` alone remains a bound enabler, so legacy
  bfs/dfs + depth-bound configs keep their meaning), and a full beam drops
  the take-order-last state (implemented in `BeamPool`; a quality ordering
  would slot in as the primary sort key above the tie-breaking next policy).
  Do not implement `nen` or priority pools unprompted.
  Arend does not (currently) want conditions as a separate resource kind; they
  remain rules, distinguished at most by role/display.
- Phase 6: delete `explore.encode`, `explore.prettyparse`, `Serialized`,
  `ExploreType`, `StrategyValue`/`AcceptorValue`, legacy property key, `-s/-a/-r`.
  Note: `EncodedTypeEditor` now hosts the colour constants of the deleted
  `ExplorationDialog`; the `encode` editors are unreachable from the GUI already.
  Note: `ConfiguredExploreType` still realises the converter's legacy `Serialized`
  descriptors and reuses the `Encoded*` semantic parsers (`EncodedEnabledRule`,
  `EncodedRuleFormula`, `EncodedEdgeMap`); demolition must first move those parsers
  out of `encode` and re-key the direct instantiation on the config itself.
- Randomness features (`next=random`, `successor=*-random`) must respect the pending
  deterministic-seeding design (see memory: randomness-seeding-design; design note
  committed as claude/randomness-seeding.md).

## Working agreements in force

Worktrees `.claude/worktrees/explore-feature-model` (phases 1–4) and
`.claude/worktrees/explore-parametric-engine` (5a, branched off the former); detach
HEAD when handing over for Eclipse review, re-attach on "continue". No pom or
generated-code changes on these branches ⇒ Eclipse refresh suffices after merge
(module-info gained the `explore.engine` export, which a refresh picks up).
Commits: house style, no trailers.
