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

## Status

- [x] Slice 0: note committed; baselines measured (see table below)
- [ ] Slice 1
- [ ] Slice 2
- [ ] Slice 3
- [ ] Slice 4
