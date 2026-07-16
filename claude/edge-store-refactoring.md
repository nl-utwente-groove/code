# Design note: the StoreFactory edge store and the per-factory perfect hash

*Status (2026-07-16): steps 1–2 landed on `master`; step 3 committed on branch
`worktree-numbered-edge-split`; steps 4–7 planned. The as-built design (the pool
layering and the perfect-hash invariant) is stable; the migration below is the agreed
direction for making edge identity number-based and retiring `graph.multi`.*

## The problem that started it

`StoreFactory` keeps canonical representatives of nodes and edges. Its edge pool (a
`TreeHashSet`) originally overrode equality to source/label/target only. Removing that
override (so *non-simple* edges could compare correctly, including their number) broke
`PlainEdge`, whose `equals` is **object identity** with asserts that identity coincides
with content equality. That invariant holds only *because* the factory canonicalises: once
the pool compares by element equality, a freshly created candidate never equals the pooled
edge, pooling silently degrades, and the identity asserts fire (49 test failures).

The lesson is that the pool's job — find the canonical representative for a *not-yet-canonical*
candidate — is not the same relation as the edge class's own `equals`, and must not be
expressed through it.

## The layering as built (step 1, master)

- `StoreFactory.createEdgeStore()` returns a `TreeHashSet` whose equality/hash defer to
  `areStoredEqual`/`getStoredCode` — **content-based** (source, target, label), deliberately
  independent of the edge class's `equals`. `getStoredCode` mirrors `AEdge.computeHashCode`.
- `HostFactory` refines `areStoredEqual` with the edge **type** (`getType()`): host edges with
  equal labels may still have distinct types under subtyping, and a content-only pool would
  silently hand back an edge of the wrong type for the same source/label/target.
- `PlainFactory` needs **no** override — the content-based base pool subsumes its former one.

The edge store is also number-indexed: an `edges` array symmetric to `nodes`, with
number-preserving `createEdge(source, label, target, nr)` variants. **Simple edges** are
pooled by content (content takes precedence over a requested number); **non-simple edges**
bypass the pool and are identified by number (`getEdge(nr)`), enabling true parallel edges.
`containsEdge` is a side-effect-free O(1) array probe.

## The key invariant: the edge number is a perfect hash per factory

For edges handed out by one `StoreFactory`, **same number ⟺ `equals` ⟺ `==`**. Sketch:
registration enforces one edge per number slot; simple edges are deduplicated by content via
the pool before registration; non-simple equality includes the number by definition
(`AEdge.equals`). Two caveats, both handled:

1. It is **assert-guarded, not hard-guarded**. A numbered `createEdge` with an in-use number
   and different content only trips asserts (`registerEdge`, `storeEdge`); without `-ea` it
   would silently overwrite.
2. It **requires homogeneous simplicity per factory**. A mixed factory could hold a simple and
   a non-simple edge with the same content under different numbers — equal in one direction
   only, since `AEdge.equals` checks `this.isSimple()`. Hence `isSimple` was pulled up from
   `HostFactory` to `StoreFactory` (a final constructor flag; `PlainFactory` passes `true`),
   and `storeEdge` asserts `edge.isSimple() == isSimple()`.

## Rejected alternative: HASHCODE_EQUATOR for the pool

Tempting given the perfect hash, but unsound. The pool's lookup key is a *fresh candidate*
whose number differs from the canonical edge's — the canonical number is the **output** of
canonicalisation, so neither number nor identity can drive the lookup; only content can.
`HASHCODE_EQUATOR` keys on `hashCode()` (not injective) with `allEqual() == true`, so any
32-bit collision silently conflates distinct edges with no check (not even the asserts run).
One `HostFactory` is shared per GTS; the birthday bound gives ~50% collision odds at ~77k
edges — routine exploration sizes. Hash-only equality is sound only as an approximate filter
verified downstream, which the pool is not.

## The companion invariant in isomorphism checking (step 2, master)

`IsoChecker.areGraphEqual` has a fast path that compares graphs by their element sets before
falling back to certificate-based iso checking. Because every factory numbers from 0, this
fast path is only sound between graphs over the **same** element factory; it is now guarded by
`dom.getFactory() == cod.getFactory()`. A parallel fix made the fast path compare **node** sets
as well as edge sets (it previously compared node counts only, so graphs differing solely in
isolated nodes' types were wrongly declared equal — which could in principle mis-collapse GTS
states). This guard becomes load-bearing once edge identity goes number-based (steps 4/6).

## Open goal: subsume `graph.multi`

`MultiGraph`/`MultiEdge`/`MultiFactory`/`MultiLabel`/`MultiMorphism`/`MultiNode` are a
hand-rolled precursor of non-simple edges (`MultiEdge.isSimple() == false`, equals by number).
They are **not dead**: `GTSFragment.toPlainGraph(LTSLabels, ExploreResult)` builds a
`MultiGraph` for LTS serialisation (used by `LTSReporter` — the Generator CLI and
`SaveLTSAsAction` — and by `ExplorationTest`). The subsumption plan is to give plain graphs a
simplicity flag, switch that one call site to a non-simple `PlainGraph`, and delete the six
`graph.multi` classes plus their `module-info` export.

## Migration path (agreed direction; one branch per step, fast suite green after each)

1. **Pool self-contained** — StoreFactory edge store gets its own content-based
   `getCode`/`areEqual`; HostFactory refines with type; delete PlainFactory override.
   *(done — master)*
2. **Guard iso fast-path** — `areGraphEqual` requires the same factory; also compares node sets.
   *(done — master)*
3. **Hierarchy split, semantics frozen** — `NumberedEdge extends Edge` carries the number; the
   number field moves to a new `ANumberedEdge`; `getNumber` leaves `Element`/`Edge` (nodes keep
   it). Numbered edges are exactly `PlainEdge`, `DefaultHostEdge`, `MultiEdge`, `AspectEdge`,
   `AttrEdge`; `TypeEdge`/`RuleEdge`/`RegEdge`/`ALabelEdge` and the LTS transitions stay on an
   unnumbered content-identified base. *(done — branch `worktree-numbered-edge-split`)*
4. **Identity switch** (the deep step) — `ANumberedEdge` equality becomes class + number, hash
   `spread(number)` (the `ANode` pattern); drop `PlainEdge`'s identity-equals + asserts and
   `DefaultHostEdge`'s type-check-in-equals. Requires the full slow suite (Exploration,
   Determinism, IO) plus a Generator benchmark for the performance claim.
5. **Demote `isSimple` off the edge** — remove `Edge.isSimple`; simplicity stays a
   factory/graph property, selecting pool-vs-number in `storeEdge`.
6. **Per-graph PlainFactory** — retire the global singleton (precedent: `AspectGraph`'s
   per-graph factory); simplicity in the constructor. This also removes a monotone retention
   leak: the singleton keeps every plain node/edge ever created for the JVM lifetime.
7. **Subsume `graph.multi`** — non-simple per-graph plain factory for LTS export; delete the
   six multi classes; verify the LTS save path (slow suite).

Dependencies: 3 after 1; 4 after 1–3; 5–7 after 4 in order (7 needs 6).

## Why this shape (design rationale worth keeping)

- **`Element.getNumber()` is a node truth over-promoted onto edges.** A node has no content
  besides its number (`ANode` equality is already class + number); an edge's number is a *name*
  for content held elsewhere, and the interface promise "number + type uniquely defines the
  element" is simply false for the unnumbered edge classes. Edge numbers never leave the JVM
  (GXL/Aut serialisation writes node numbers only), so relocating them is serialization-safe.
- **A survey of all correctness-relevant equality comparisons found them same-factory.** GTS
  collapsing uses the single per-GTS `HostFactory`; all plain graphs share the global
  `PlainFactory` singleton; `AGraph` has no `equals`; store/model change detection is by name.
  The only cross-factory equality use is the `areGraphEqual` fast path — hence the step-2 guard.
  This is what makes number-based edge identity safe: within a factory it is invisible (content
  equality already implies the same object/number), and the one cross-factory site is guarded.
- **`PlainFactory` conflates two roles.** `StoreFactory` provides (a) cross-graph object sharing
  — which pays for host graphs, where millions of delta-linked states share one factory — and
  (b) canonical dedup, which underpins the perfect hash. For plain graphs (transient
  aspect↔GXL conversion vehicles) role (a) is negative: the singleton is an unsynchronised
  global that never releases anything. Per-graph plain factories (step 6) pool at graph scope
  and give the non-simple factory slot that step 7 needs.

## Open decisions

- Whether step 4 keeps a cached-hashCode field on `ANumberedEdge` or computes `spread(number)`
  live (a node-style number hash is cheap enough that caching may not pay).
- Whether to push edge numbering all the way up to `ElementFactory` (committing `RegFactory`,
  `TypeFactory`, `RuleFactory`, `AspectFactory`, `MultiFactory`) or keep it scoped to the
  hot-path `Store`/`Numbered` edge classes. Step 3 chose the scoped shape; the broader push
  remains a possible follow-up.
