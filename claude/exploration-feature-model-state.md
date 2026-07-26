# State of the exploration feature model programme (2026-07-26)

Note to a future Claude session. Companion to
[exploration-feature-model-plan.md](exploration-feature-model-plan.md), which holds the
feature model, the phase plan and the decision log; this note records the *as-built*
state, the invariants discovered along the way, and where to pick up.

## Status: phases 1–4 done; phase 5a done; both awaiting merge to master

Branch topology (2026-07-26; nothing pushed beyond
`explore-feature-model`@defa76d8f — later commits are local only):

    master (c3a6db7b2, incl. RETE retirement and the gh #855 fix)
      ⊂ explore-feature-model (fbfa74d6a)
          phases 1–4 + three dialog-review rounds (+ layout fixes) + master merges
      ⊂ explore-parametric-engine (dab65ebfc)
          phase 5a (engine skeleton) + merges of the above

Merging to master in that order fast-forwards; the engine branch subsumes the
feature-model branch.
Suite at the 5a freeze, measured at the engine tip: 368 fast, **399 including slow
tests** (`mvn test -Dexcluded.test.groups=`), all green; fast suite re-verified
green after the third review round on both branches. Phases 1–4: the feature model
is the only user-facing way to express exploration (dialog, `-x`, grammar
property). Phase 5a: configuration-based exploration instantiates the
`explore.engine` classes directly, without the encode/Template machinery; the
deprecated keyword path (`-s/-a`, legacy property) still runs the
enumerator-instantiated legacy classes as the parity reference. Phases 5b+
(priority pools, trace results, overrides, persistence) and 6 (demolition) are
future branches — do not start unprompted.

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
- `storeValue` removes a key when the value is the key default — hence
  `setExploreConfig` must (and does) delete the legacy key explicitly, or a leftover
  legacy value would win after storing an explicit default config.

## Deliberately unsupported (converter errors, awaiting phase 5)

heuristic≠none, cost=rule, frontier=beam, next=random, successor=all-random,
single-successor on unrestricted frontier, shape=trace, persistence=none,
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

- Phase 5b+ (5a — the engine skeleton — is done, see the plan): priority/beam/random
  orders as new `Pool` implementations; trace results; collapse/algebra overrides;
  persistence None (the one feature that forces rewriting the inherited `doNext()`
  protocol); then the unsupported list above becomes implementable feature by
  feature; revisit LTL/CTL goals; possibly a target-state counterpart to `fires`
  ("reached by the action"), and `fires(r)` as an atom of the condition language.
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
