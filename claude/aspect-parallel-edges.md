# Parallel edges at the aspect level: decided architecture

Status: design decided (2026-07-19). Step 1 — the `RuleGraph` parallel-edge
representation (numbered `RuleEdge`s with explicit parallel indices,
non-simple rule graphs, index-preserving morphisms and typing) and
plan-engine matching of parallel bundles (including edge injectivity) — is
implemented on branch `parallel-edges`; RETE matching and the MULT
aspect are pending. Builds on the GXL serialisation work in
[parallel-edge-serialisation.md](parallel-edge-serialisation.md).
An earlier implementation that made `AspectGraph` itself a multigraph was
rolled back — see the final section for what it was and why it was rejected.

## The architecture: counts as syntax, parallel edges as semantics

`AspectGraph` — the gateway to the GUI and the user-facing representation of
host, rule and type graphs — stays **simple**. Edge multiplicity is expressed
in the concrete syntax by a new **MULT aspect** (working syntax `mult=k:`),
and is **expanded into genuine parallel edges** when the aspect graph is
compiled into its semantic counterpart:

- `RuleModel` compilation expands counted rule edges into parallel `RuleEdge`s
  in a non-simple `RuleGraph`;
- `HostModelMorphism` expands counted host edges into parallel `HostEdge`s in
  a non-simple `HostGraph` (the typed-level multigraph support merged earlier
  is exactly the substrate for this).

The reverse direction (displaying a semantic multigraph, e.g. an LTS state)
**aggregates**: parallel copies collapse into one aspect edge whose label is
decorated with `(x2)` etc., the same convention already used for parallel
transitions in the LTS display.

### Why the semantic level must have real parallel edges, not counts

In the category of multigraphs, edges carry identity: a morphism maps edges to
edges, so a 2-edge parallel bundle in a rule has 4 morphisms onto a 2-edge
host bundle, 2 of them injective. Match counts and the injective/non-injective
distinction are semantically load-bearing. Treating counts as the semantics
would silently replace the standard theory; treating them as concrete syntax
that expands away preserves it. A pleasant corollary: most semantic questions
about parallel edges in rules need no ad-hoc decisions — they become theorems
of standard multigraph matching:

- `del:mult=2:a` with only one host copy has no injective match → rule
  inapplicable;
- `not:mult=2:a` expands to a 2-edge embargo bundle → "no injective image of
  2 copies" = "at most 1 copy exists" — counting NACs for free;
- ranges, if ever wanted: `mult=m..n:` on a reader = m parallel readers plus
  an (n+1)-copy embargo.

### Why counts are the right *concrete* syntax

- A jCell's user object is a set of label lines, so duplicate lines on one
  edge collapse by construction; drawn parallel edges would need separate
  overlapping jEdges — awkward to create, unreadable to view. One annotated
  label line fits the aspect model.
- `AspectGraph` stays simple, so cross-instance element equality
  (gh #806/#809, see below) is preserved exactly.
- Labels are just strings: the annotation round-trips through every existing
  pipeline (GXL, copy-paste, display) with no serialisation change.
- Backward compatible: no annotation means multiplicity 1.

### Syntax: a separate aspect, not content on the role aspects

The role aspects `use:`, `del:`, `new:`, `cnew:`, `not:` already carry
content: `ContentKind.LEVEL`, the quantifier-level binding (`use=q:`). Their
`=`-slot is taken. So multiplicity gets its own `AspectKind` in its own
category, combinable with role prefixes the way `in=`/`out=`/`part:` combine
on type edges: `new:mult=2:a`, `del:mult=2:a`, and bare `mult=2:a` on host
edges (which have no role prefix to extend). `ContentKind.MULTIPLICITY`
(already used by the type-graph `in=`/`out=` aspects) can be reused; note the
documentation must distinguish the two: `in=`/`out=` constrain incident edge
counts per node, `mult=` states parallel copy counts per edge.

## Recorded decisions (user, 2026-07-19)

- **`cnew:mult=2:a`** expands to 2 adder edges, i.e. NAC "at most 1 copy
  exists" plus "create 2 more". Deemed sufficiently intuitive; no special
  treatment.
- **No cap or warning on large multiplicities**: `mult=1000:a` is one label
  but a thousand edges per state — a user who writes that deserves what they
  get.
- **Aggregated display** appends `(x2)` etc. to the label, mirroring the
  existing parallel-transition convention in the LTS view.
- **Implementation order**: (1) parallel-edge support in `RuleGraph` first;
  (2) then, as a separate step, a worked example introducing the MULT aspect.

## Concerns to address during implementation

- **Edge-injectivity in matching** — *resolved for the plan engine*. In
  simple patterns, edge-injectivity of a match follows from node-injectivity
  (a host edge is determined by its content), so it was never checked. For
  non-simple patterns it is not implied: parallel rule edges enumerate the
  same candidate host edges and could bind the same copy. Following the
  standard morphism semantics, non-injective matching (the default) *allows*
  this — a k-bundle maps into an n-bundle in n^k ways — while injective
  matching admits only the n!/(n-k)! injective assignments. The plan engine
  now enforces the latter with a used-edges set in `Search.putEdge`
  (mirroring the used-nodes set for node injectivity) plus the corresponding
  backtracking dependencies in `SearchPlan`, both active only for injective
  matching of non-simple patterns, so simple matching pays nothing.
  `ParallelEdgeMatchingTest` pins these counts. The RETE engine does not yet
  handle parallel rule edges; before the MULT step activates non-simple rule
  graphs, RETE must either be taught the same or guarded.
- **Match relevance prunes reader-bundle symmetry for free.** The plan
  engine only distinguishes matches that differ on *relevant* elements —
  those bound to the rule anchor or condition output nodes; matches that
  differ only in irrelevant images collapse to one witness. Eraser edges are
  anchors, so distinct host copies deleted remain distinct matches (as they
  must — the transformations differ); a parallel *reader* bundle, by
  contrast, is typically not in the anchor, so the k! symmetric ways of
  binding it collapse automatically. The feared symmetry blowup is thereby
  confined to eraser bundles, where it is semantically meaningful.
- **Counting NACs presuppose edge-injective embargo matching** — *open
  decision for the MULT step*. The reading "`not:mult=2:a` = at most 1 copy"
  holds only if the embargo bundle must match injectively; under the default
  non-injective matching, both embargo edges can bind the same host copy,
  making `not:mult=2` equivalent to `not:mult=1`. NAC subconditions
  currently inherit the rule's injectivity property. Standard theory (NACs
  as "no injective image") argues for always matching embargo bundles
  edge-injectively; uniformity with GROOVE's non-injective default argues
  against. To be decided when the MULT aspect gives users a way to write
  such NACs.
- **Determinism of match order.** Matches that differ *only* in which
  parallel host copy they bind are content-identical; the canonical match
  order (`MatchCollector.canonicalise`, cf. the ferryman-flake analysis) must
  tie-break on edge identity, i.e. edge numbers enter an order-bearing
  decision. That is only sound if those numbers are stable across state-cache
  reconstruction.
- **Creator-edge number stability.** `RuleEffect.addCreateEdge` derives
  created edges from the factory per event derivation; after a GC-induced
  cache collapse, re-derivation must yield the *same* edge identities, or
  reconstructed graphs number their parallel copies differently and the
  previous point breaks. This was observed live on this branch with the
  (rolled-back) non-simple host compilation; it is the ferryman flake's
  shape one level down and must be solved before `DeterminismTest` and
  parallel edges can coexist.
- **Symmetry blowup, accepted.** A k-copy eraser bundle onto a k-copy host
  bundle yields k! injective matches, all leading to isomorphic states; iso
  collapse merges the states but the matcher does the work and transitions
  multiply.
- **Display mapping.** Aggregation is many-to-one, so match highlighting and
  element-keyed GUI state must map each semantic edge to its aggregated
  aspect edge.
- **Parallel creation absorbed today.** With simple rule graphs, a creator
  edge parallel to a reader edge between the same nodes is pooled with the
  reader at rule compilation time and drops out of the creator set — the
  compiled rule creates nothing (verified by probing
  `Rule.getCreatorEdges()`). This is the behaviour the RuleGraph step must
  replace.

## Rejected alternative: AspectGraph as a multigraph (rolled back)

Commits 16413901c and 722ac7032 (reverted in 98757f8f1) made `AspectEdge` a
`NumberedEdge` whose number was a *parallel index* (0 for all edges of a
simple graph and for the first copy of each content triple; 1, 2, … for
duplicates, assigned by a per-graph counting factory), so that parallel
aspect edges could coexist in the set-based edge store. It worked for
host-graph pass-through (a `.gst` with `edgeids="true"` survived load →
aspect → host compilation → exploration), but was rejected because:

- it solved only pass-through — rule-level parallelism, in particular
  parallel *creation*, remained inexpressible without also making rule
  graphs multigraphs, which the counted syntax needs anyway;
- the editor cannot naturally create or show parallel edges (set-based user
  objects; overlapping jEdges);
- cross-instance equality of aspect elements was only best-effort for
  parallel bundles, where the counted design preserves it exactly.

The history that shaped it remains relevant should anyone revisit: aspect
edges were once numbered in creation order, and gh #806/#809 (Nov 2024,
e489d84e7) showed that GUI state — label-tree filters, layout — is keyed by
graph elements across *distinct* `AspectGraph` instances of the same resource
(display tab vs. editor tab), so numbers participating in `AspectEdge`
equality must be stable across independent reconstructions. Creation-order
numbers are not. Keeping `AspectGraph` simple sidesteps the issue entirely.
