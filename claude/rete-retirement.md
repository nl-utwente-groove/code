# Retirement of the RETE matching engine

Status: retired (user decision, 2026-07-20). The engine was unmaintained,
saw no regular use (all three RETE strategies were development-only, hidden
from the standard exploration dialog), and had fallen behind the semantics
of the plan engine — most importantly the DPO identification condition
(see [eraser-injectivity.md](eraser-injectivity.md)), where RETE retained
the old delete-wins behaviour. Keeping ~10k lines compiling but unreachable
was rejected: every refactoring would have paid to keep code alive whose
semantics silently rot, with no test keeping it honest.

## What was removed

One commit, containing only deletions (so that a plain `git revert`
resurrects everything for as long as it still applies):

- package `nl.utwente.groove.match.rete` (38 classes), and its
  `exports`/`opens` lines in `module-info.java`;
- `explore.strategy.ReteStrategy`, `ReteLinearStrategy`,
  `ReteRandomLinearStrategy`;
- `explore.StrategyValue` constants `RETE`/`RETE_LINEAR`/`RETE_RANDOM`
  (keywords `rete`, `retelinear`, `reterandom`), their templates, and
  their entries in `DEVELOPMENT_ONLY_STRATEGIES`;
- `explore.config.Matcher.Key.RETE` (exploration-configuration key
  `rete`);
- `graph.GraphRole.RETE` (role `rete`, only used to render RETE networks
  for debugging);
- a `ReteSearchEngine` mention in the `match.SearchStrategy` javadoc;
- tests: `test.match.rete.ReteNetworkTest`, the sixteen `"rete"` lines of
  `ExplorationTest`, and `DeterminismTest.testReteEngineDeterminism`.

Old command lines or saved configurations using the `rete*` strategy
keywords now fail with the generic unknown-strategy error.

## How to revive

- The parent of the deletion commit is tagged **`rete-final`**: the last
  mainline tree containing the engine.
- While the surroundings still fit, `git revert` of the deletion commit
  restores everything, including tests and registrations.
- After drift, restore the package wholesale
  (`git checkout rete-final -- src/main/java/nl/utwente/groove/match/rete`
  plus the strategy classes and test sources) and re-add the registrations
  listed above by hand.
- Note: branch `parallel-edges` adapted RETE *mechanically* to numbered
  rule edges (end-renaming, commit b2b8fb6e9) after the point where this
  retirement branched off. If that branch has merged by revival time, the
  last pre-deletion mainline tree contains the adapted files — prefer
  those over the tag where they differ.

## Semantic-debt ledger

What a revived engine must implement to be *correct*, beyond compiling —
the state of play at retirement time; later design notes extend this list
implicitly:

- **DPO identification condition** (all of `eraser-injectivity.md`):
  eraser edges and nodes must be matched injectively with respect to all
  other elements, within and across quantification levels, under the
  match-level-filtering semantics (an overlapping candidate sub-match is
  not a legal match). In the plan engine this lives in-search
  (conflict pairs, merge embargoes, root extension); RETE retained
  delete-wins throughout. The *inter-instance* check is engine-independent
  (a proof filter in `TreeMatch.traverseMatrix`) and would apply to RETE
  matches for free.
- **Compilation-level artefacts of the same work**: conditions may now
  contain imported ancestor root elements, ancestor-eraser marks
  (`Condition.getAncestorEraserEdges`) and synthesized merge embargoes;
  a revived engine must honour their semantics, not merely tolerate them.
- **Non-simple patterns**: parallel rule-edge bundles (numbered
  `RuleEdge`s) including edge-injective matching under the injectivity
  property; the plan engine's `Search.putEdge` used-edges machinery is the
  reference. RETE never supported bundle matching.
- **MULT aspect** (planned at retirement time): counted edges expand into
  parallel bundles at compilation, so non-simple patterns become
  user-reachable; see [aspect-parallel-edges.md](aspect-parallel-edges.md).
