# State of the exploration feature model branch (2026-07-20)

Note to a future Claude session. Companion to
[exploration-feature-model-plan.md](exploration-feature-model-plan.md), which holds the
feature model, the phase plan and the decision log; this note records the *as-built*
state, the invariants discovered along the way, and where to pick up.

## Status: branch programme complete, awaiting review/merge

Phases 1–4 of the plan are implemented on `worktree-explore-feature-model`
(nine commits, `895dc3068..ca388bedc`). The full test suite **including slow tests**
(`mvn test -Dexcluded.test.groups=`) passes at 409 tests. The feature model is now the
only user-facing way to express exploration — GUI dialog, CLI and grammar properties —
while the legacy Strategy/Acceptor machinery still executes underneath. Phases 5
(parametric engine) and 6 (demolition) are future branches.

## As-built map

- `explore.config` — the model. `ExploreKey` (14 keys), `Setting` (record: `Kind` +
  content), one kind enum per key (`NextState`, `Successor`, `Frontier`, `Heuristic`,
  `Cost`, `Goal`, `Outcome`, `Result`, `Count`, `Bound`, `Persistence`, `Collapse`,
  `Matcher`, `Algebra`), `SettingParser`/`SettingKindMap`/`Null`, and `ExploreConfig`
  (EnumMap; text form = space-separated `key=value`, non-default entries only;
  `check()` = cross-feature consistency only — *realisability* is the converter's job).
- `explore.config.ExploreTypeConverter` — bidirectional partial bridge to legacy
  `ExploreType` (`toExploreType` / `toConfig`); single place that knows what the legacy
  engine can realise. Everything inexpressible errors with an explanation.
- `gui.dialog.ExploreConfigDialog` — replaces `ExplorationDialog`; rows per key,
  dependency-aware enabling, preview + "Runs as", buttons enabled via conversion +
  `ExploreType.test(grammar)`. Stores defaults as config
  (`SimulatorModel.doSetDefaultExploreConfig`).
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
single-successor on unrestricted frontier, result=trace, persistence=none,
collapse/algebra overrides (kinds `grammar` = inherit), goal=graph, goal=ltl/ctl
(stay with the CheckLTL/CTL actions), iterative deepening (`+inc`), bound=size,
`applied`+violate (legacy ruleapp has no polarity), condition bound + depth bound
together (BOUND is a single key). Legacy without feature equivalent: `state`,
`minimax`, `remote` (CLI-only per Arend), LTL strategies, `cycle` acceptor.

## Open threads for later phases

- Phase 5: one parametric frontier-based search algorithm; then the unsupported list
  above becomes implementable feature by feature; revisit LTL/CTL goals; possible
  `rule` → `condition` goal rename when rule conditions become their own resource kind;
  structured formula atoms `r` / `applied(r)`.
- Phase 6: delete `explore.encode`, `explore.prettyparse`, `Serialized`,
  `ExploreType`, `StrategyValue`/`AcceptorValue`, legacy property key, `-s/-a/-r`.
  Note: `EncodedTypeEditor` now hosts the colour constants of the deleted
  `ExplorationDialog`; the `encode` editors are unreachable from the GUI already.
- Randomness features (`next=random`, `successor=*-random`) must respect the pending
  deterministic-seeding design (see memory: randomness-seeding-design).

## Working agreements in force

Worktree `.claude/worktrees/explore-feature-model`; detach HEAD when handing over for
Eclipse review, re-attach on "continue". No pom or generated-code changes on this
branch ⇒ Eclipse refresh suffices after merge. Commits: house style, no trailers.
