# Proposal: parallel-edge-preserving GXL serialisation

Status: implemented on this branch (2026-07-18). Branch: `parallel-edges`.

## Problem

Since the `worktree-numbered-edge-split` merge, non-simple `PlainGraph`s (notably
exported LTSs) can hold parallel equi-labelled edges. Serialisation does not yet
respect this:

- **Saving already works.** `GxlIO.graphToGxl` iterates `edgeSet()` directly, so
  every parallel edge of a non-simple graph is written to the GXL file. (This was
  equally true in the `MultiGraph` era — existing saved LTS files *contain*
  parallel edges.)
- **Loading collapses them.** `GxlIO.loadPlainGraph` goes through
  `AttrGraph.toPlainGraph()`, which constructs an always-simple `PlainGraph`;
  `copyTo` then maps each `AttrEdge` through the simple `PlainFactory`, which
  pools content-equal edges — the second parallel edge maps to the same
  `PlainEdge` and silently disappears.

Notably, the `AttrGraph` itself is *not* where the loss happens: it is set-based,
and its `AttrEdge`s are `ANumberedEdge`s with fresh numbers from the
`AttrFactory` counter, so parallel edges are unequal and survive `gxlToGraph`
intact. The loss is localised in the single conversion `AttrGraph → PlainGraph`.

### A pre-existing inconsistency, discovered along the way

`AttrGraph` hard-codes `super(name, false)`, i.e. `isSimple() == false`, while
`AspectGraph.newInstance(Graph)` inherits `graph.isSimple()` from its argument.
Since grammar resources load via `SystemStore → AttrGraph.toAspectGraph() →
AspectGraph.newInstance(attrGraph)`, **every aspect graph loaded from a `.gps`
is currently flagged non-simple** — while `AspectGraph.createPlainGraph()` (used
by `toPlainGraph`, `relabel`, `colour`) is always simple, so the flag flips back
on the next save or edit round-trip. The flag is thus meaningless around the I/O
boundary today. This proposal fixes that as a by-product.

## Proposal

Make simplicity an explicit, round-tripped property of the serialisation,
encoded with GXL's *native* mechanism for edge identity: the `edgeids` graph
attribute.

### 1. Save side (`GxlIO.graphToGxl`)

Replace the hard-coded `gxlGraph.setEdgeids(false)` by
`gxlGraph.setEdgeids(!graph.isSimple())`. When `edgeids` is true, give each edge
element an `id` (`"e" + running index` — cheap, unique against the `"n" + nr`
node ids, and exactly what the GXL schema intends `edgeids="true"` to promise).

Simple graphs — i.e. all grammar resources — are written **byte-identically to
today** (`edgeids="false"`, no edge ids), so there is zero churn in `.gps`
directories and test fixtures.

### 2. Load side (`GxlIO.gxlToGraph`)

Record `simple = !gxlGraph.isEdgeids()` on the `AttrGraph`, with one
backward-compatibility override: **role `LTS` implies non-simple**, because LTS
files written by MultiGraph-era GROOVE (and by the current code until this
change) contain parallel edges yet say `edgeids="false"`.

### 3. `AttrGraph` records simplicity as metadata

Give `AttrGraph` a `simple` flag reported by `isSimple()`, settable alongside
`setRole` (both are file metadata). The *storage* stays exactly as it is —
set-based with number-distinguished edges — because the intermediate format must
be able to faithfully hold whatever the file contains regardless of the flag;
`NodeSetEdgeSetGraph` never consults `isSimple()` for storage decisions, so this
is safe. Defaults: `AttrGraph.newInstance(AspectGraph)` inherits the source's
flag; programmatic creation defaults to simple.

### 4. `AttrGraph.toPlainGraph()` honours the flag

`new PlainGraph(getName(), getRole(), isSimple())`. In non-simple mode the
per-graph `PlainFactory` creates a fresh numbered `PlainEdge` per `AttrEdge`
(each `AttrEdge` is a distinct key in the `AttrToGraphMap`, so there is no
image-caching collapse), and parallel edges survive. `copyTo` needs no change.

### 5. `AspectGraph.createPlainGraph()` honours the flag

`new PlainGraph(getName(), getRole(), isSimple())`. Combined with (2) and (3)
this makes the aspect-level flag truthful in both directions: grammar files load
as simple aspect graphs (fixing the always-false muddle above), and
`SystemStore.saveGraph(aspectGraph.toPlainGraph(), …)` preserves whatever the
flag genuinely is. Note the interlock: doing (5) *without* (2)+(3) would flip
every re-saved grammar file to `edgeids="true"`, because loaded aspect graphs
are currently all flagged non-simple.

### 6. Retire the `AttrFactory.instance()` singleton

Per-`AttrGraph` factory instead (a field initialised to `new AttrFactory()`;
`AspectToAttrMap` takes the graph's factory as constructor argument). Same
rationale as the `PlainFactory` singleton retirement (1f9eecc52): the JVM-wide
edge-number counter grows for the lifetime of the Simulator and leaks numbering
across unrelated graphs; a per-graph counter starting at 0 is reproducible and
self-contained. This is the last remaining factory singleton of this shape.

### 7. `AutIO` (the `.aut` format is an LTS format)

`AutIO.createGraph()` is always-simple, so parallel LTS transitions collapse on
`.aut` load just as they do on GXL load. Apply the same rule as (2): non-simple
iff the configured graph role is `LTS`. (`.aut` has no place to store the flag,
so the role-based rule is the whole story there.)

## Non-goals and accepted limitations

- **Edge numbers are not preserved** through serialisation (unlike node
  numbers). Parallel edges only need *distinctness*; the stored `id`s are not
  read back into edge numbers. If stable edge identities across save/load are
  ever needed (e.g. per-edge layout), the numbered-edge registration API
  (`StoreFactory.registerEdge` / `Dispenser.single`) is the way in — deferred
  until there is a use.
- **Old-style separate `.lyt` layout files** address edges by
  source/label/target (`AttrGraph.getEdge` uses `findFirst`), which stays
  ambiguous for parallel edges. Legacy only; LTS files carry no layout.
- **`AspectGraph` still cannot hold parallel edges itself** — `AspectEdge`
  equality is content-based, so its edge set collapses duplicates regardless of
  the flag. That is fine: grammar graphs are simple by design; only plain-level
  consumers (LTS export/import, `Viewer`, `CTLModelChecker`'s `GraphFacade`,
  `IsoChecker`) need real parallel edges, and those all work on `PlainGraph`.

## Compatibility with old GROOVE versions

**Grammar files are untouched.** Simple graphs are written byte-identically to
today (`edgeids="false"`, no edge ids), so `.gps` directories remain fully
interchangeable between old and new versions.

**New non-simple files (LTS exports) load fine in old GROOVE.** The changed
files carry `edgeids="true"` and `id` attributes on edges — both part of the
GXL 1.0 schema that old versions already ship in their JAXB binding, so
unmarshalling succeeds (and is non-validating anyway). Old load code never
reads `edgeids` and touches edge ids only in error messages. Old versions will
collapse the parallel edges on load — but that is exactly what they already do
with today's LTS files, so nothing regresses; the information loss was always
on their side.

**Old files load correctly in new GROOVE.** Grammar files (`edgeids="false"`)
load simple, as always. MultiGraph-era LTS files are the reason for the
role-based override in step 2: they contain parallel edges yet say
`edgeids="false"`, and `role="lts"` makes them load non-simple. Third-party GXL
files that happen to declare `edgeids="true"` now load non-simple; if imported
as grammar resources they are collapsed by the aspect-graph conversion as
before, so this only matters for plain-graph viewing, where honouring the
declaration is correct.

**The GXL version marker stays `"curly"`.** `isKnownGxlVersion` accepts only
exact equality, and the `$version` attribute is written into *every* file — so
bumping the version would make old GROOVE warn about all new files, including
byte-identical grammar files. Since the format change is additive, confined to
non-simple files, and gracefully handled by old versions, no bump is warranted.

## Rejected alternative: a `$simple` graph property

Simplicity could be stored as a graph attribute instead of `edgeids`. Rejected
because `edgeids` is the GXL-native declaration of exactly this semantics (edges
have identity beyond source/label/target), it comes for free in the existing
JAXB binding, and writing edge ids makes non-simple files self-describing for
external GXL tools. A property would also pollute the graph-properties
namespace, which is user-visible in the Simulator.

## Findings during implementation

- **The `parallelEdges` grammar property** (`GrammarKey.PARALLEL`, pre-GitHub
  vintage) already governs the simplicity of GUI-*created* aspect graphs
  (`NewAction`, `AspectJModel`, groovy `GraphManager`), but is not consulted on
  the load path. `NewAction` passed the property *un-negated* as the `simple`
  flag of `AspectGraph.emptyGraph` — inverted with respect to the other two
  sites, since the property's inception. Latent until now (the flag never
  influenced saving); fixed on this branch, because with simplicity-aware
  saving the inversion would have made every GUI-created graph in a default
  grammar save as `edgeids="true"`.
- **Host-graph compilation ignores aspect-graph simplicity**:
  `HostModelMorphism` hard-codes `new DefaultHostGraph(source.getName())`,
  which is simple. So the flag restored on load does not (yet) reach
  exploration semantics; wiring the `parallelEdges` property and/or the
  aspect-graph flag into host-model compilation is the natural next step
  towards exploring with parallel edges, and will need a decision on
  precedence between the grammar property and the per-file `edgeids` flag.

## Verification plan

- Round-trip test: non-simple `PlainGraph` → GXL → `PlainGraph` preserves
  parallel edges and the `isSimple` flag; a simple host graph round-trips to a
  byte-identical file.
- Regression: full suite including slow groups (`IOTest` covers I/O
  round-trips); all existing fixtures have `edgeids="false"` and grammar roles,
  so they load simple, as before the `AttrGraph.isSimple()` fix.
- Compat: load a MultiGraph-era LTS file (`edgeids="false"`, role `lts`,
  containing parallel edges) and check the parallel edges survive.
