# Proposal: parallel-edge support in AspectGraph

Status: proposed (2026-07-18). Branch: `parallel-edges`.
Builds on the GXL serialisation work in [parallel-edge-serialisation.md](parallel-edge-serialisation.md).

## Problem

Since the serialisation change, the `isSimple` flag survives every conversion in
which `AspectGraph` participates — but the *parallel edges themselves* do not,
because `AspectEdge` cannot be parallel: it is content-identified (source,
label, target, plus the graph's external identity), so the set-based edge
containment of `NodeSetEdgeSetGraph` collapses equi-labelled duplicates
regardless of the flag. Consequently a non-simple host graph loaded from a
`.gst` file with `edgeids="true"` reaches the aspect level with its parallel
edges silently merged, and the next save writes a graph that *claims*
non-simplicity but has lost the duplicates. `AspectGraph` supports the flag
without supporting the thing the flag describes.

## History: why AspectEdge is unnumbered, and why that was right until now

`AspectEdge` *was* numbered until recently. The history is instructive because
it demarcates exactly what a re-introduction of numbers must avoid:

- Originally, aspect edges were numbered from a graph-level dispenser in
  creation order, and the number participated in equality.
- gh #806/#809 (Nov 2024, commit e489d84e7): GUI state — label-tree filters,
  layout — is keyed by graph elements and must survive the boundary between
  the display tab and the editor tab, which hold *distinct* `AspectGraph`
  instances of the same resource. Creation-order numbers differ between
  independently constructed instances, so equality-based lookups failed. The
  fix zeroed the number for all non-remark edges (remarks kept distinct
  numbers so that identical remarks between the same nodes could coexist) and
  relaxed the graph component of element equality to `externalEquals`
  (name/role/normal-status).
- Commit 93239449c (this month's numbered-edge-split work) removed the number
  entirely: identical remark edges were instead merged into one multi-line
  remark (d9c5806ae), leaving the number without any remaining purpose.

The lesson: **numbers in `AspectEdge` equality must be stable across
independent reconstructions of the same graph**, or GUI element lookups break.
Creation-order numbers are not; a constant is. The design below picks the
minimal deviation from a constant.

## Proposal: the parallel index

Make `AspectEdge` a `NumberedEdge` again (extending `ANumberedEdge`, so the
number enters equality and the cached hash code), but let the number be a
**parallel index**: the count of content-equal edges created before it by the
same factory.

- In a **simple** graph, the factory always assigns index 0. No two
  content-equal edges are ever meant to coexist, duplicates collapse in the
  edge set exactly as today, and — crucially — every edge of every existing
  grammar keeps precisely its current equality behaviour, including
  cross-instance equality between display and editor tabs. (Only the raw hash
  values change, by the `ANumberedEdge` chaining; nothing persists or
  iterates on them.)
- In a **non-simple** graph, `AspectFactory.createEdge` counts per content
  triple (source, label, target — all content-identified themselves) and
  assigns indices 0, 1, 2, …. Parallel edges are thus unequal and coexist in
  the edge set.

Cross-instance stability for non-simple graphs is best-effort: the k-th
created copy of a content triple always gets index k, so two reconstructions
agree as long as they create parallel copies in the same relative order —
which holds for all order-preserving pipelines (file order, `edgeSet()`
iteration order, jCell root order). If orders ever diverge, the failure is
confined to a parallel bundle whose members are visually indistinguishable
anyway, and degrades to the pre-existing fallback (error marked on the source
node instead of the edge).

### No pooling

Unlike `StoreFactory`, the factory does *not* pool content-equal edges in
simple mode: it keeps returning fresh instances (all with index 0), and the
set-based containment discards duplicates on add, exactly as today. Pooling
was considered and rejected: `AspectEdge` is stateful (parse status, errors),
and several call sites mutate the edge after creation (`setParsed`,
`addError`), which must not hit a shared canonical instance.

### All construction routed through the factory

Every `AspectEdge` that ends up in a graph must carry an index consistent with
that graph's factory count. The 3-argument constructor is therefore removed;
the eight direct construction sites become:

| Site | Treatment |
|---|---|
| `AspectFactory.createEdge` | the one remaining constructor call (with index) |
| `AspectGraph.mergeRemarkEdges` | factory (merged multi-line label is new content) |
| `AspectGraph.mergeGraphs` | factory of the merged graph |
| `AspectJEdge.addEdges` (2×) | factory of the graph under (re)construction |
| `AspectJVertex.loadFromUserObject` (2×) | factory of the graph under (re)construction |
| `AbsNode`/`AbsEdge.buildAspect` (io.conceptual) | factory of the target graph |
| `AspectJEdge.addEdge` error copy | 4-arg constructor, **preserving** the original's index (the copy replaces the original; a fresh index would double-count) |

The count map lives in the per-graph `AspectFactory` and is only consulted for
non-simple graphs; it is never iterated, so a `HashMap` keyed by a content
record is deterministic-safe.

## Editor round-trip: flag precedence resolved

`AspectJModel.syncGraph` rebuilds the aspect graph from the jCells on every
edit; it decided simplicity purely from the grammar's `parallelEdges`
property. That collapses a non-simple *file* opened in a grammar without the
property — the editor would destroy parallel edges the load path just
preserved. The new rule is monotone towards non-simplicity:

```
simple  =  editedGraph.isSimple()  &&  !properties.isHasParallelEdges()
```

i.e. a graph is non-simple if *either* it already was (per-file `edgeids`
flag, restored on load) *or* the grammar property asks for it. Turning the
property **on** upgrades existing graphs as they are edited; turning it
**off** does not silently re-collapse non-simple graphs (collapse is lossy —
if wanted, it must be explicit). This settles the property-vs-file-flag
precedence question left open in the serialisation note.

## Conversions audited

With the factory in place, the conversions between non-simplicity-supporting
graph versions preserve both flag and parallel edges without further work,
because they all funnel through `addEdge(source, label, target)` → factory, or
through `AGraphMap`s whose per-key image caching keeps distinct (now unequal)
parallel keys distinct:

- Plain/Attr → Aspect: `AspectGraph.newInstance` (one `addEdge` per source
  edge). Its `edgeDataMap` becomes a `LinkedHashMap` so parallel indices are
  assigned in source-graph edge order rather than hash order.
- Aspect → Plain: `toPlainGraph` via `AspectToPlainMap` (per-key images,
  non-simple `PlainFactory`).
- Aspect → Attr: `AttrGraph.newInstance` via `AspectToAttrMap` (per-key
  images, counter-numbered `AttrEdge`s).
- Aspect → Aspect: `clone`/`rename`/`unwrap` (morphism → factory),
  `relabel`/`colour` (round-trip through a plain graph, both directions
  covered above), `NormalAspectGraph` (`addEdge` → factory), `mergeGraphs`
  (factory, see table).
- Host/Type → Aspect: `GraphConverter.toAspectMap` (per-key images → factory).

Aspect → Host (`HostModelMorphism`) still compiles into a hard-coded simple
host graph; wiring the flag into grammar compilation — where it starts to
affect exploration semantics — remains the deliberate next step, out of scope
here.

## Non-goals and accepted limitations

- **Within one jEdge, duplicate label lines still collapse**: a jCell's user
  object is a set of label lines, so typing the same label twice on one edge
  yields a single aspect edge. Drawing two *separate* jEdges with equal labels
  does work in a non-simple graph — each produces its own `createEdge` call on
  sync, so both survive as parallels. GUI creation of parallel edges is thus
  possible, just not via duplicate lines on a single edge.
- **Remark merging is unchanged**: even in non-simple graphs, remark edges
  between the same node pair merge into one multi-line remark on fix. Remarks
  are annotations, not graph structure; keeping at most one per node pair is a
  display decision independent of simplicity.
- **Parallel indices are not serialised**: the GXL `id` attributes are written
  but not read back into indices (same accepted limitation as for plain edge
  numbers). Indices only need in-graph distinctness plus best-effort
  reconstruction stability, which creation order provides.
- **Rule and type compilation still collapse**: parallel edges in a RULE or
  TYPE aspect graph do not survive into `Rule`/`TypeGraph` (nor should they,
  until exploration-side semantics are defined).

## Verification plan

- New `AspectSimplicityTest`: parallel edges survive Plain → Aspect → Plain
  and Attr → Aspect; `clone` preserves them; duplicates still collapse in
  simple graphs; cross-instance equality of identically-built graphs holds
  (the gh #806/#809 guard); remark merging unaffected.
- `GxlSimplicityTest` gains an end-to-end case: non-simple GXL file → load →
  `toAspectGraph` → `toPlainGraph` retains the parallel edges.
- Full suite including slow groups.
