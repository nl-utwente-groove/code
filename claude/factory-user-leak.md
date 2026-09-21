# Factory user leak

Design note for fixing the memory leak in `util.Factory`'s dependency tracking (finding
3.11 of `claude/exploration-performance.md` on branch `worktree-exploration-performance`).
gh #919. Branch `factory-user-leak`, off master. Status: implemented (weak user sets plus
plain fields in `RuleApplication`), awaiting review.

## Mechanism

`Factory.lazy(supplier)` returns a factory whose `create()` registers itself in the static
`builders` set for the duration of the supplier call. Every `Factory.get()` (and `set()`)
executed meanwhile calls `addBuilders()`, which puts each current builder into the read
factory's `users` set and the read factory into the builder's `used` set. The edges exist
so that `reset()` on a factory can reset every factory built from its value.

The `users` set was a strong `HashSet`, recorded regardless of the lifetimes of the two
owners. When a short-lived owner built inside a factory that read a long-lived one, the
long-lived factory's `users` set pinned the short-lived factory, its value, and (through
the supplier lambda) the owner, until the long-lived factory was reset or died. Nothing on
any exploration path calls `reset()`.

## Survey of use sites (2026-09-20)

130 `Factory.lazy` sites, no other `Factory` subclasses. Owner lifetimes:

| Owner lifetime | Sites | Reads a longer-lived factory inside the build? |
|---|---|---|
| Per rule application (`transform/RuleApplication`: `match`, `morphism`, `effect`, `comatch`) | 4 | Yes: `match` reads `Rule.prover` and the `Prover` matchers; `effect` reads `Rule.modifying`, `creatorNodes`, `creatorEdges`, `simpleCreatorEdges`, `complexCreatorEdges`, `creatorVars`, `creatorEnds`, `eraserNodes`, `eraserEdges`, `eraserNonAnchorEdges`, `mergers`, `anchor` (about 14 grammar-lifetime factories), plus the GTS-lifetime `RandomOracle.random` for rules with indeterminate operators; `comatch` reads `Rule.anchor`; `morphism` nests the `effect` build, so both were registered on every read `effect` made. |
| Per GUI tree refresh (`gui/tree/MatchTreeNode.text`, `RecipeOngoingTreeNode.recipeTransition`) | 2 | Yes: `text` reads `Switch.assignSource2Par`/`assignSource2Init` (grammar) and `Rule` factories through `isModifying()` and transition text. Leaked one node per refresh into the grammar. |
| GTS lifetime (`RandomOracle.random`) | 1 | No; it is a retainer, not a victim. |
| Grammar lifetime (`Rule` ×32, `Prover`, `AspectGraph`/`AspectEdge`/`AspectNode`, resource models, `TypeGraph`, control `Template`/`Location`/`Switch`/`Term`/`Derivation`/`NestedCall`/`Assignment`, and `control/instance` `Frame`/`Step`, which are pooled in the grammar's `Automaton`) | ~100 | Two read static factories: `AspectGraph.normalGraph` reads `Sort.operatorMap`, and the `Prover` matchers read `AlgebraFamily.userOps` (USER-sort operators only). Bounded per grammar load, but the static factories accumulated every loaded grammar's aspect graphs and matchers across reloads. |
| Static (`algebra`, `GrammarKey`, `ResourceProperties`, `EdgeRole`, `Keywords`, `FileType`, `Exporters`, parser singletons, ...) and one-per-component GUI listeners | ~25 | No. |

`lts`, `verify`, `graph/iso`, `grammar/host`, `match/plan`, `RuleEvent`/`BasicEvent`,
`Proof`, `GraphState`, `StateCache`, `GTS` hold no `Factory` at all. Events are pooled per
GTS (`transform/Record`). So `RuleApplication` was the only per-transition owner on the
exploration path, and the leak per application was the whole application object graph.

## What reset propagation is used for

Callers of `Factory.reset()`, and what the propagation must reach:

- `UserSignature.operators` (static), reset by `setUserClass` when a new user-operation
  class is loaded: must reach `Operator.ops` and `Sort.operatorMap` for `USER`. This is the
  reset the tracking was written for (commit 2232fcd44). `UserSignature.addUser` is a
  parallel hand-written mechanism for caches that read the plain `getMethods()` map.
  `setUserClass` is called only from `SystemStore.loadProperties`, i.e. on a store load,
  and a property edit that changes a reload key goes through `doReload()`. So a user-class
  change always coincides with a fresh store, grammar model, aspect graphs, grammar and
  matchers: the grammar-level users of the static factories are being discarded at the
  moment the reset would reach them.
- `Location.varIxMap`, reset by `setVars` during control compilation: must reach the
  `Location`/`Switch`/`Step`/`Frame` factories if already built.
- `AspectGraph.sortMap`/`nodeIdMap`, reset on re-normalisation and `setTypeSortMap`: must
  reach `AspectEdge` and `AspectNode` factories that read the sort map, and through them
  `AspectGraph.normalGraph`.
- Resource-model factories (`HostModel`, `CompositeHostModel`, `CompositeTypeModel`,
  `ControlModel`), reset on grammar-model invalidation: must reach the derived models.
- `NestedCall.recipe`, `SymbolTable.sortMap`, `ExploreType.grammarErrors`: no dependants.

No reset ever targets a `Rule`, `Prover` or `RandomOracle` factory, so the edges recorded
from rule applications served nothing.

## Options

**(a) Plain lazily-initialised fields in `RuleApplication`.** `@Nullable` field plus a
null check per accessor (all four values are non-null, so no set flag is needed). Fixes
the exploration leak and saves eight allocations and four global-lock acquisitions per
application. On its own it leaves the general hazard in `Factory` and the GUI site.

**(b) An untracked `Factory` variant.** A second constructor of `lazy` whose factory takes
no part in dependency tracking, used for owners shorter-lived than what their supplier
reads. First implementation of this branch. Rejected on review: once the general
retention is handled by (c), the variant is only a performance device for the hot path,
and there plain fields are cheaper still; it also cannot express the long-to-static cases,
where the builder must keep its edges towards its peers (`normalGraph` depends on the
graph's own sort map) while the static read side should not retain it.

**(c) Weakly held user sets.** `users` as a `WeakHashMap`-backed set; `used` stays strong.
Semantics become "a reset propagates to every user still alive", which is right for every
lifetime combination without classifying owners: static users of statics stay reachable
from their own static fields, grammar-internal chains keep working because the peers are
alive together, and dead readers vanish. It also cures the long-to-static retentions. The
cost objection (weak entries and GC reference processing on the hot path) falls away once
`RuleApplication` no longer uses `Factory` at all; the remaining `get()` calls inside
builds happen once per grammar object or control frame.

**Chosen: (c) plus (a).** `Factory` keeps a single behaviour, and the hot-path fix is
local to the one per-transition owner. Identity-hash iteration order of the user set is
unchanged from the previous `HashSet`, and reset order is irrelevant, so determinism is
unaffected.

**Follow-up idea (Arend, 2026-09-21): resettable versus frozen factories.** Edges are only
useful between two resettable factories. A frozen factory (`reset()` throws) needs no
stored edges in either direction, but must still be registered as a builder so that every
read inside its build can check that the read factory is frozen too; a frozen build reading
a resettable factory is exactly the stale-value hazard that plain fields hide, and the
check makes it fail loudly. It complements (c) rather than replacing it: the aspect-graph
edge to `Sort.operatorMap` is legitimately resettable-to-resettable and only weak sets stop
it retaining. The check would first flag `Rule.prover`, since the matchers read
`AlgebraFamily.userOps`. Needs an audit of the 130 sites with a resettable default; its
own issue and branch.

## Tests

- `test/util/FactoryTest` (new): a build reading another factory is recorded as its user
  and reset with it; `set()` records builders; a user that is otherwise unreachable is
  collected and no longer counted, and a reset afterwards still works. Verified to fail
  with a strong `HashSet`. `getUserCount()` exists for these assertions.
- `test/rule/RuleApplicationRetentionTest` (new): applies `creators/createNode`, computes
  match, target and morphism (not the comatch, which needs the added-node array only the
  LTS path records), drops the application and asserts a weak reference to it clears while
  the grammar stays alive. Verified to fail against the `Factory.lazy` fields.
- Retention measurement stays a scratch probe (explore, drop the GTS, two `System.gc()`,
  used heap), numbers below and in the commit body; a heap-growth assertion is too
  environment-dependent for the suite.

## Global monitor (finding 1.3)

`Factory.get()` still enters the static `lock` on every read. `RuleApplication` no longer
takes it at all; the remaining hot callers listed under 1.3 (`Step`, `Frame`) are
unchanged.

## Measurement

`junit/samples/generate-binary-tree.gps`, depth-first, `next=newest cost=uniform
bound=cost:8 persistence=none`, one `Grammar` reused over three runs, each GTS dropped,
used heap after two `System.gc()` with only the grammar alive (`-Xmx6g`, Java 21):

| | baseline | after run 1 | after run 2 | after run 3 |
|---|---|---|---|---|
| before fix | 8 MB | 569 MB | 1107 MB | 1621 MB |
| after fix | 8 MB | 8 MB | 8 MB | 8 MB |

Run time per exploration: 3.6 s before, 2.0 to 2.5 s after (measured with the test suite running alongside, so the spread is noise; the strong-set churn on the application path is gone).
