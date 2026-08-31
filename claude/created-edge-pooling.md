# Cross-event pooling of content-equal created edges (gh #905)

Working notes for the repair of gh #905: in non-simple (multigraph) mode, content-equal
created edges are not shared across events, which cascades into an event explosion and
from there into isomorphism-checking overhead. Companion to `claude/iso-edge-bundles.md`
(gh #906), which addressed the complementary half of the `append` overhead.

## The mechanism (from the issue's measurement comment, verified in code)

1. **Edge minting.** In non-simple mode, `StoreFactory.storeEdge` registers every created
   edge under a fresh number; the content-keyed `edgeStore` is bypassed (it pools only in
   simple mode). Reuse happens one layer up, per event: `BasicEvent.createEdge` returns the
   cached coanchor image if absent from the source, else scans the per-event `freshEdgeList`,
   minting as a last resort.
2. **Event identity.** `BasicEvent` equality and hash are rule + anchor-image identity
   (`equalsEvent`, `computeEventHashCode`); events are pooled in `Record.normaliseEvent`.
   Anchor images include host *edges*. A match whose anchor contains an edge created along
   the exploration path gets a different event object on each path that created that edge
   separately — in `append`, `Events: 88` (simple) vs `3797` (multi).
3. **Created-node sharing dies.** Created nodes are cached per event
   (`BasicEvent.getCreatedNodes` → `HostFactory.normalise`); distinct events mint distinct
   nodes (`Coanchor reuse: 1409/38` → `116/1247`). Confluent paths then produce isomorphic
   but unequal targets; each such pair costs a certificate comparison (43% of `append`'s
   comparisons past the equality shortcut have differing *node* sets — unreachable by any
   edge-content test, including #906's bundle equality).
4. **A second, per-application leak** (not in the issue text): complex creator edges — those
   with created endpoints — are minted by `RuleEffect.addCreateEdge` via the plain factory
   `createEdge`, i.e. **fresh on every application of the same event**. So even a single
   reused event fails to reproduce its created-edge identities across applications from
   different sources. (Re-derivation of the *same* transition is unaffected: the added-edge
   identities are recorded on the state at first derivation — `StateCache.createDelta` /
   `RuleApplication(event, source, addedNodes, addedEdges)` — and replayed afterwards.)

## Design

**Factory-level content pool with an exclusion filter** (the direction sketched in the
issue, with one correction). In non-simple mode, `StoreFactory` maintains, next to the
number-indexed `edges` array, a content-indexed pool: for each (source, label/type, target)
content, the copies minted so far, in minting order. A new creation entry point

    createEdge(source, type, target, exclusion)   // exclusion: predicate over edges

returns the **first** pooled copy (in minting order) not excluded, minting and appending a
fresh copy only if all are. Choosing the *first* admissible copy makes the answer a function
of (content, exclusion) alone — independent of which event asks — which is what lets
confluent paths converge on identical target graphs.

**Correction to the issue's sketch**: the exclusion cannot be just the source graph. Within
one application (and across sub-events of a `CompositeEvent`), several creator edges may
produce the same content; each needs its own copy. The exclusion is therefore record-level:
source graph ∪ edges already allocated in the current `RuleEffect` (mirroring
`RuleEffect.containsNode` for created nodes). Erased edges are still in the source graph and
hence excluded — created copies never alias erased ones, preserving the current semantics
exactly; only the choice among admissible fresh copies changes (canonical first-minted
instead of per-event history).

Consumers:

- `BasicEvent.recordCreatedEdges` (non-simple branch): replace the cached-image +
  `freshEdgeList` logic with a pooled `createEdge` call per simple creator edge (endpoint
  images and type from the coanchor map). The per-event `freshEdgeList` machinery becomes
  dead and is removed. The per-event *cached coanchor image* cannot serve even as a fast
  path: an event whose cached image is copy #k would diverge from the canonical first-
  admissible choice when copy #j (j < k) is admissible.
- `RuleEffect.addCreateEdge` (complex creator edges): pooled call with the same record-level
  exclusion.
- `RuleToHostMap.createImage` (used via `coanchorMap.mapEdge`) keeps minting fresh where
  still reached (e.g. `computeSimpleCreatedEdges` for `conflicts()`); such mints enter the
  pool and are legitimate reuse candidates. No graph context exists there, and `conflicts()`
  is reached in deterministic exploration order, so pool content stays deterministic.

**Implementation shape in `StoreFactory`**: reuse the existing content-keyed `edgeStore`
(`TreeHashSet`) as the pool *head* index (first copy per content), plus a lazily allocated
overflow map head → list of further copies, in minting order (cf. `EdgeBundles`' lazy copy
lists). Pooled creation mints a probe edge, uses `put` to find-or-insert the head, then
scans head + overflow against the exclusion. The copy constructor already replays the
`edges` array in number order, which is minting order, so a factory copy (taken per GTS,
see `HostFactory.copy()`) rebuilds an identical pool deterministically. The pool is
lookup-only — never iterated — so hash containers are safe under the determinism rules.

### Why this preserves determinism and reconstruction

- Pool lists grow in minting order; minting happens in deterministic exploration order; the
  pooled answer is first-admissible-in-order. No iteration-order dependence anywhere.
- Cache collapse: re-derivations replay the recorded added-node/added-edge arrays
  (`DefaultGraphNextState.setAddedEdges`), so the pool is only consulted on first
  derivations. `DeterminismTest`/`CacheReconstructionTest` guard this.
- Cross-JVM: pool keys hash on node numbers / type-edge content — no identity hashes.

### Known limitations / non-goals

- **Mergers**: `RuleEffect.getAddedEdges` maps created edges through the `MergeMap`, whose
  `mapEdge` mints fresh images per application (via `AGraphMap.createImage`). Pooling that
  path needs exclusion-aware image creation inside the merge-map traversal; merger grammars
  simply keep falling back to isomorphism checking (status quo). Recorded as a follow-up,
  not in scope.
- `conflicts()` compares created vs erased edges by identity; in multi mode a created copy
  never equals an erased edge, so the check is conservative. Unchanged (pre-existing).
- `Reuse.NONE` events still pool (pooling is a factory property, node reuse an event one);
  with fresh nodes per application, complex-edge content differs anyway, and simple-creator
  reuse under NONE is harmless.

## Expected effect

`append` (SPO-multi): events back near the simple-mode count (~88), coanchor reuse gain
restored, the 43% differing-node-set comparisons largely gone, and with them most of the
remaining +50%-class overhead at `append-4-list-8`. State/transition counts must be
bit-identical (iso collapse already merged these states; pooling only converts
isomorphic-but-unequal into equal).

## Slice 0: baseline (master 9a110441a, includes all of #906)

`Generator` on `junit/samples/append.gps`, start graph `append-4-list-8`; both modes reach
31104 states / 114008 transitions. Times over three runs (first run with `-v 2`, whose
counter collection cost is negligible).

| | SPO-simple | SPO-multi |
|---|---|---|
| Time (ms) | 2699 / 2631 / 2821 (avg ~2717) | 4129 / 3778 / 3757 (avg ~3888, **+43%**) |
| Events | 88 | 3326 |
| Coanchor reuse (gain/count) | 1497/38 | **0**/1535 |
| Equal graphs | 80297 | 48475 |
| Equal bundles (#906 slice 1) | 0 | 44219 |
| Equal certificates | 2546 | 31965 |
| Equal simulation | 62 | 2465 |

Reading: #906's second-line bundle equality already answers the 44219 comparisons where
confluent paths share created *nodes* but differ in created-*edge* identity. What is left
for this issue is the differing-node-set half — 31965 certificate + 2465 simulation
answers against simple mode's 2546 + 62 — caused by the event explosion (3326 vs 88)
destroying created-node sharing (coanchor reuse gain 0 vs 1497). Success criterion:
events and coanchor reuse back near simple-mode figures, certificate/simulation counters
collapsing toward simple mode's, states/transitions bit-identical.

## Slice plan

0. This note + baseline measurements on the current master tip (`9a110441a`, includes
   #906): `Generator -v 2` on `append` in both modes — time, `Events`, `Coanchor reuse`,
   iso counters.
1. Factory pool: `StoreFactory`/`HostFactory` content pool + exclusion-filter `createEdge`
   + copy-constructor rebuild + unit tests.
2. Transform layer: `BasicEvent.recordCreatedEdges` (pooled, `freshEdgeList` removed),
   `RuleEffect.addCreateEdge` + record-level `containsEdge`.
3. Verification and measurement: ExplorationTest (identical counts), DeterminismTest,
   CacheReconstructionTest, full suite, null-check; re-measured `append` table.
4. Close-out: note update, issue comment, merge handoff.

## Slices 1+2: delivered (functional outcome)

Factory pool (commit "Added a content-indexed edge pool…") and transform layer
("Resolved created edges through the factory content pool…"). Functional result on
`append` (SPO-multi, `append-4-list-8`): **every counter is bit-identical to SPO-simple**
— Events 88 (from 3326), Coanchor reuse 1497/38 (from 0/1535), Equal graphs 80297,
bundles 0, certificates 2546, simulation 62. The event explosion and its downstream
isomorphism checking are eliminated entirely; even #906's bundle second line is idle on
this sample, since confluent paths now produce *identical* target graphs. States and
transitions unchanged (31104 / 114008).

`As-and-Bs-reg-exp-benchmark` (bfs:9, SPO-multi): 30674 states / 119843 transitions
(unchanged), Events 21, Equal graphs 59571, bundles 0, certificates 27539, simulation
2060 — **bit-identical to the baseline**: this grammar's event set is tiny either way, so
it serves purely as an overhead probe for the pool bookkeeping.

## Slice 3: verification and measured outcome

Gates (all on the branch tip): DeterminismTest, CacheReconstructionTest, IsoTest,
HostFactoryTest, StoreFactoryTest green; ExplorationTest 25/25 with unchanged counts;
full fast suite 692 tests green; ecj null-check on the five touched files clean.
The first full-suite run caught a real defect: `StoreFactoryTest.testNonSimpleNumberedReuse`
tripped the pool's append-order assert — explicitly numbered creation interleaves with
lowest-free-number minting, so registration order is *not* number order. Fixed by making
the pool canonical by number (list holds all copies incl. head, sorted; answer = lowest-
numbered admissible copy), which also removes any dependence on which copy the factory
copy-constructor elects as head.

Timings, three runs each, same machine and day (baseline = master `9a110441a`):

| run | baseline (ms) | pooled (ms) |
|---|---|---|
| append multi | 4129 / 3778 / 3757 (~3888) | 2839 / 2814 / 2811 (~2821, **−27%**) |
| append simple | 2699 / 2631 / 2821 (~2717) | 2629 / 2603 (unchanged) |
| As-and-Bs bfs:9 multi | 1657 / 1705 / 1716 (~1693) | 1697 / 1691 / 1633 (~1674, wash) |
| As-and-Bs bfs:11 multi | 6183 / 6025 / 5931 / 6044 / 6044 / 6023 (~6042) | 5998 / 6069 / 5997 (~6021, wash)¹ |

¹ An earlier pooled bfs:11 batch (6251/6215/6330) ran on a warm machine directly after
eight prior JVM runs; the re-run above is the clean comparison.

SPO-multi on `append` is now ~8% over SPO-simple (was +43%); the remaining gap is the
per-application `RuleEffect` bookkeeping plus the pooled lookups themselves. The pool
bookkeeping (one content-hash probe per non-simple edge registration) is not measurable
on the parallel-heavy As-and-Bs.

## Status

- [x] Slice 0: note committed; baselines measured (see table below)
- [x] Slice 1: factory pool + HostFactoryTest
- [x] Slice 2: transform layer (BasicEvent, RuleEffect)
- [x] Slice 3: verification + timing table (complete slow suite pending at time of writing)
- [ ] Slice 4: close-out (issue comment, merge)

## Follow-up: unifying node and edge replay (branch `edge-replay-unification`)

Created nodes and created edges are recorded and replayed by two structurally different
mechanisms, though they solve the same problem: making a re-derivation of a transition
(after state-cache collapse) reproduce the identities of the first derivation.

| | created nodes | created edges |
|---|---|---|
| first computed | eagerly, at state creation (`MatchApplier.createState`, partial `RuleEffect`, `Fragment.NODE_CREATION`/`NODE_ALL`) | lazily, at the first full delta (`StateCache.createDelta`) |
| stored on the state | final field, transition-identity-relevant (`RuleTransitionLabel` — out-parameters can expose created nodes) | nullable field, set post hoc (`setAddedEdges`); not identity-relevant |
| replay | *predefined* path: `RuleEffect(source, createdNodes)` sets `isNodesPredefined`; `BasicEvent.recordCreatedNodes` consumes the array positionally (`addCreatorNodes` cursor), skipping creation entirely | *recompute-then-substitute*: creation re-runs in full (pooled), then `RuleApplication.addEdges` discards the results, substituting content-equal recorded identities (`findAddedEdge`, linear scan + consumed flags); `computeMorphism` adds ghost-edge bookkeeping for merge images |
| extra sharing | `reuseCreatedNodes` (parent transition's array) | none needed — the #905 pool provides cross-application sharing implicitly |

The edge retrofit predates the pool: creation was then irreproducible (fresh number per
mint), and the only stable record was the post-filter, post-merge-map *output* of
`RuleEffect.getAddedEdges()`, whose elements are not positional w.r.t. creator edges. With
the pool, re-derived creation reproduces the recorded identities for everything except
merge-map images — the substitution re-finds what creation just produced.

Key enabling observation: the recorded array (`RuleApplication.getAddedEdgeArray`) *is*
the final added-edge set — post-merge-map, post-filter, including merge-induced additions.
So a replay does not need to reproduce edge *creation* at all: it can skip creation and
take the recorded array as the answer of `getAddedEdges()` directly. This sidesteps the
irreproducibility of merge-map images entirely, rather than repairing it.

Slices:

- **A — predefined edges in `RuleEffect`** (this branch): a predefined added-edge array +
  `isEdgesPredefined()`, passed by `RuleApplication.computeEffect` from its `addedEdges`
  field. When predefined, `BasicEvent.recordEffect` skips `recordCreatedEdges` (mirroring
  the `isNodesPredefined` branch, but skipping instead of cursor-consuming — there is no
  per-creator positional record to rebuild) and `getAddedEdges()` returns the recorded
  array verbatim. Erasures and the merge map are still recomputed (the delta's removals
  are deterministic and not recorded). The `findAddedEdge` substitution in `addEdges`
  becomes dead and is deleted. Removal sets replay identically because they derive from
  the anchor map, so the re-derived delta equals the recorded one by construction.
- **B — pooled merge images** (not in scope, riskier): route `MergeMap.mapEdge` image
  minting through the pool. Not needed for replay correctness after A; its value is
  deleting the ghost-edge/consumed-slot logic in `computeMorphism` (the last
  content-matching remnant) and extending cross-branch sharing to merger grammars, which
  currently keep falling back to isomorphism checking.
- **C — cosmetic symmetry**: constructor consolidation in `RuleEffect`, documentation of
  the two-phase effect lifecycle (partial node effect at state creation, full effect at
  first delta).

Deliberately kept asymmetric: eager/final vs lazy/nullable storage (the node array must
exist at state creation for the transition label; the edge array is large and only needed
for delta replay), and `reuseCreatedNodes` (no edge analogue needed, see table).

### Slices A and C: delivered

Slice A as planned: `RuleEffect(source, createdNodes, addedEdges)` (folding the old
two-argument predefined-nodes constructor, whose only caller was
`RuleApplication.computeEffect`), `isEdgesPredefined()` short-circuiting
`hasAddedEdges`/`getAddedEdges`, the `recordCreatedEdges` skip in
`BasicEvent.recordEffect`, and deletion of the `findAddedEdge` substitution from
`RuleApplication.addEdges`. `findAddedEdge`/`consumeAddedEdge` and the ghost-edge logic
remain in `computeMorphism` only (slice B territory). Of slice C, the constructor
consolidation was folded into A and the lifecycle documentation added to the `RuleEffect`
class javadoc; nothing else of C seemed worth a diff.

Verification: DeterminismTest, CacheReconstructionTest, RuleApplicationTest, IsoTest,
HostFactoryTest, StoreFactoryTest green; ExplorationTest 25/25; full fast suite 692/0/0;
ecj null-check clean on the three touched files. `CacheReconstructionTest` is the
targeted gate: its `parallel-pump`/`parallel-pump-spo` grammars combine multigraph mode
with mergers, so the predefined-edges replay is exercised together with a merge map.
`append` SPO-multi timing unchanged (2852/2801/2843 vs 2839/2814/2811 for #905 slice 3;
expected — first derivations are untouched, the win is confined to re-derivations under
memory pressure, which a plain run has none of).

Slice B remains open: `MergeMap.mapEdge` (via `AGraphMap.createImage`) still mints fresh
merge images per application. Consequences: merger grammars in multi mode keep falling
back to isomorphism checking (no cross-branch sharing), and `computeMorphism` keeps the
ghost-edge/consumed-slot substitution. A same-flavoured known gap: a
`DefaultRuleTransition` to a pre-existing target (confluent diamond) constructs its
morphism application without recorded added edges, so with a merge map its morphism may
omit merge-image edges whose fresh mints are not in the target (pre-existing behaviour,
unchanged by A).

### Interlude: are added edges (or nodes) part of transition identity?

Investigated on request; the outcome shaped the commits that follow. Within one GTS,
(source state, event, control step) uniquely determines a transition *including* its
created node and edge identities: each match is applied exactly once per state, and
creation is deterministic (fresh-node list first-admissible scan + `normalise` for
nodes, the content pool for edges) — with two caveats that do not affect per-GTS
uniqueness (`Reuse.NONE` creation is not idempotent, so there the *recording* pins the
identities; and `reuseCreatedNodes` inherits the parent's array, which need not equal a
fresh first-admissible computation). So neither array adds discriminating power to
transition identity.

Created *nodes* nonetheless stay on the transition label, and hence in its equality:
labels are interned globally across sources (`GTS.normaliseLabel`) and render
creator-bound out-parameter values into the label text, so created nodes are observably
part of the label, and an interned object must include in its equality everything it
carries. Created *edges* have no such observability (parameters cannot be edge-valued);
putting them on the label would only split label sharing across sources and force eager
edge computation for confluent-diamond transitions. A draft that did exactly that was
implemented, fully verified (all gates green), and then discarded on this analysis. The
one sound home for the edge record is the target state itself — a final per-state field
that label interning cannot touch, which is also what the delta replay actually reads
(`StateCache.createDelta` reads the state's own fields; labels are derived from them,
never the reverse).

### Eager per-state edge recording: delivered

What survived from that draft, as a commit of its own: `MatchApplier.createState`
computes the full effect eagerly and passes the added-edge array into the
`DefaultGraphNextState` constructor (final field, uniform for simple and multigraph
mode); every derivation of the target graph replays it. Deleted: `setAddedEdges`, the
recording step in `StateCache.createDelta`, and the getGraph()-before-getAddedEdges
evaluation-order subtlety. Cost-neutral, since the full effect was previously computed
immediately afterwards for the isomorphism check. Transitions to existing targets still
compute only node creation, so `Fragment` survives, shrunk to
{`NODE_CREATION`, `ALL`} (`NODE_ALL` is dead). A side effect of always computing the
full effect at state creation: the added-node array is now always the merge-mapped
`getCreatedNodeArray` — previously it was merge-mapped only when the control step had
variables (`NODE_ALL`) and raw otherwise, an inconsistency that is gone.

### Slice B: delivered

The merge branch of `RuleEffect.getAddedEdges` no longer calls `MergeMap.mapEdge`
(whose images come from the event-cached cross-application image cache, or are minted
without regard for the pool). The effect resolves redirected images itself
(`mapMergedEdge`): identity and removed-endpoint cases as before; in a non-simple graph
the image is drawn from the pool, excluding source ∪ created edges ∪ images already
handed out by the same run, with a run-local memo so an edge reached twice (incident to
two merged nodes) keeps a single image while distinct redirected edges of equal content
keep distinct parallel copies. Merge images are thereby canonical across events and
applications — the #905 confluence property extended to merger rules.

Also closed by B, a warm-cache leak: a cached image from a previous application of the
same event could lie in the *current* source graph, in which case the old
`!getSource().containsEdge(image)` guard silently dropped the merge-induced copy from
the delta — wrong under multigraph pushout semantics, where a redirected edge is always
a distinct copy. Pooled images are never in the source, so the copy is now always
added. (Simple mode keeps the old path and semantics.)

Unchanged by B, deliberately: `RuleApplication.computeMorphism`'s ghost substitution
and `DefaultRuleTransition.adaptToTarget`. Morphisms are reconstructed rarely, consume
the recorded array by content (parallel copies are interchangeable there), and cannot
reproduce the pooled assignment independently since their iteration order differs from
the effect's.
