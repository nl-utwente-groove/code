# Parallel edges at the aspect level: decided architecture

Status: design decided (2026-07-19); **implemented** (2026-07-26);
**rule-side use of the MULT aspect DEFERRED** (user, 2026-07-31, see the
deferral section below — the aspect remains available in host graphs).
Step 1 — the `RuleGraph` parallel-edge representation (numbered
`RuleEdge`s with explicit parallel indices, non-simple rule graphs,
index-preserving morphisms and typing) and plan-engine matching of
parallel bundles (including edge injectivity) — and step 2, the MULT
aspect itself (syntax, checks, and expansion; see the implementation
section below), are both on branch `parallel-edges`. RETE was retired
from master instead of being adapted. Builds on the GXL serialisation
work in [parallel-edge-serialisation.md](parallel-edge-serialisation.md).
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
  treatment. **Superseded (user, 2026-07-26)**: the NAC/MULT combination is
  disallowed *entirely* (for now), for any multiplicity value including 1,
  and this extends to `cnew:` because its implicit NAC would be a counting
  NAC. See the counting-NACs concern below.
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
  handle parallel rule edges; per user decision (2026-07-19) RETE is
  unmaintained and will be treated later, possibly retired altogether — see
  [eraser-injectivity.md](eraser-injectivity.md), which also covers the
  related decision that eraser edges must *always* be matched injectively
  (the DPO identification condition), implemented for the plan engine on
  this branch.
- **Match relevance prunes reader-bundle symmetry for free.** The plan
  engine only distinguishes matches that differ on *relevant* elements —
  those bound to the rule anchor or condition output nodes; matches that
  differ only in irrelevant images collapse to one witness. Eraser edges are
  anchors, so distinct host copies deleted remain distinct matches (as they
  must — the transformations differ); a parallel *reader* bundle, by
  contrast, is typically not in the anchor, so the k! symmetric ways of
  binding it collapse automatically. The feared symmetry blowup is thereby
  confined to eraser bundles, where it is semantically meaningful.
- **Counting NACs presuppose edge-injective embargo matching** — *resolved
  by prohibition (user, 2026-07-26): counting NACs are disallowed until
  further notice, and the NAC/MULT combination is disallowed **entirely**,
  not just for multiplicity ≥ 2.* The reading "`not:mult=2:a` = at most 1
  copy" holds only if the embargo bundle must match injectively; under the
  default non-injective matching, both embargo edges can bind the same host
  copy, making `not:mult=2` equivalent to `not:mult=1`. NAC subconditions
  currently inherit the rule's injectivity property, so neither reading is
  uniformly right. Rather than deciding the semantics now, the MULT
  implementation (`AspectEdge.checkMult`) rejects `mult=` in combination
  with `not:` for *any* value (including 1), and likewise with `cnew:`
  (whose implicit NAC would be a counting NAC), each with an error message
  explaining that counting NACs are not (yet) supported. Not enforced:
  the *implicit* NAC bundles arising from a creator with multiplicity under
  the `rhsAsNac` or `checkCreatorEdges` grammar properties; those NACs do
  not promise counting semantics to the user (under non-injective matching
  they degenerate to the 1-copy test, which is sound, just not counting).
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

## Implementation record (2026-07-26)

The MULT aspect is implemented as follows.

### Syntax and static checks

- `AspectKind.MULT` (name `mult`, reusing `ContentKind.MULTIPLICITY`) in its
  own `Category.MULT`, declared compatible with the ROLE and LABEL
  categories; allowed on HOST and RULE edges (not TYPE — there `in=`/`out=`
  serve a different purpose). Parser status is preserved after parsing the
  aspect, so `del:mult=2:a` and `mult=2:del:a` both parse; serialisation
  falls out of the existing MULTIPLICITY content handling (`mult=2:`).
- `AspectEdge` stores the multiplicity (`getMult()`/`getMultCount()`);
  `checkMult` (called from `checkAspects` for host and rule graphs alike)
  rejects: non-constant or zero values (ranges like `mult=1..2` would need
  the counting-NAC expansion), non-binary edges (parallel flags or node
  types are not meaningful), the NAC roles `not:` and `cnew:` entirely (see
  above), and rule labels without edge images (a regexpr path witness has
  no edge identity to multiply). Display appends `(x2)` to the label line,
  the convention intended for aggregated semantic multigraphs as well.
- Multiplicity ≥ 2 additionally requires the **parallelEdges grammar
  property**, checked during compilation (`RuleModel.Level2.processEdge`,
  `HostModelMorphism.processModelEdge`) — in a simple-graph grammar the
  copies would silently collapse, so this is an error, with a message
  naming the property.

### Expansion

- **Rules** (`RuleModel.Level2.processEdge`): the model edge's rule image
  (parallel index 0) plus `k-1` extra copies with indices `1..k-1` are each
  placed into LHS/RHS via the extracted `placeEdge` helper. The level
  graphs (`Level2.createGraph`, `Level3.createGraph`) are now created
  **non-simple iff the grammar has parallelEdges**, which is what activates
  edge-injectivity in `SearchPlan`/`PlanSearchStrategy` (both key off
  `pattern.isSimple()`).
- **Index semantics — SUPERSEDED 2026-07-27 by disjoint allocation**: the
  original scheme assigned indices per aspect edge starting at 0, letting
  distinct same-content aspect edges overlap on low indices with
  eraser-wins resolution. That encoded SPO conflict resolution into
  compilation and silently reintroduced creator absorption. Now
  (`ParallelIndexAllocator`, per rule, shared across quantification
  levels) every aspect edge gets its own disjoint index range for its
  content — the multiset reading: `use:a` + `del:a` declares 2 copies,
  `use:a` + `new:mult=2:a` creates 2 fresh copies next to the matched one
  (pinned by `creatorReaderMult`), and `use:mult=3:a` + `del:mult=2:a`
  declares 5 copies of which 2 are deleted. Embargo edges are exempt
  (they declare no copies). The SPO/DPO difference is thereby confined to
  matching and application; see eraser-injectivity.md for the three-mode
  (`parallelEdges=none|SPO|DPO`) design and gates.
- **Host graphs** (`HostModelMorphism.processModelEdge`): `k-1` further
  `addEdge` calls; the non-simple host factory mints a fresh edge number
  per call. The model map maps the aspect edge to the first copy only.
- The MULT aspect makes parallel host graphs *expressible on disk*
  (`.gst` with `mult=2:a`), which is what allows `RuleApplicationTest`
  fixtures (`junit/rules/mult.gps`) to state expected multigraph results;
  note that an expected result with two isomorphic events needs duplicate
  result files (`eraserMult-1-0/-1-1`: the two ordered assignments of the
  eraser bundle are distinct events).

### Added-edge stability under cache collapse — RESOLVED (2026-07-27)

The added edges of a transition over a non-simple host graph are now
recorded in the transition and replayed on re-derivation, mirroring the
added-nodes mechanism:

- `DefaultGraphNextState` stores a `HostEdge[] addedEdges` — and *only*
  that class: reconstruction only ever runs through a state's own primary
  transition, so the stubs, `DefaultRuleTransition` and the transition
  labels stay untouched (one null field per *state*, not per transition,
  and only in multigraph mode is it ever filled).
- `StateCache.createDelta` is the single hook: at the first derivation of
  a non-simple state it extracts `RuleApplication.getAddedEdgeArray()`
  into the state; re-derivations pass the stored array back in.
- `RuleApplication.addEdges` substitutes the recorded identities for the
  freshly minted ones by *content-matched multiset consumption* at the
  output level (the enumerated added-edge set), leaving all three minting
  sites untouched: the event-cached simple-creator sets, the per-record
  complex creator edges, and the merge-redirected images minted by
  `MergeMap.mapEdge` inside `RuleEffect.getAddedEdges`. Positional replay
  would be unsound for the merge path (its iteration order follows
  source-graph edge sets, which are not reconstruction-stable);
  content-keyed consumption is order-independent, and content-equal
  parallel copies are interchangeable at creation time, so any consistent
  assignment reproduces the recorded graph.

Findings from the investigation, worth keeping:

- **The node/edge asymmetry has a precise cause**: created-*node*
  identities are pinned *hard* — `BasicEvent.freshNodeList` is a plain
  field, and the added-node array is stored in the transition — while
  created-*edge* identities lived only in *soft* state (the event cache's
  `simpleCreatedEdgeSet`, the merge map's lazily filled edge images, the
  application record). A sweep that clears only *state* caches loses no
  edge identities (the events re-supply them); the loss occurs when the
  *event* caches are collected too, which the GC can equally do
  (`BasicEvent`'s cache reference is soft from creation).
- **Test**: `CacheReconstructionTest` explores a grammar fully, snapshots
  every state graph's node and edge identities, clears all state *and
  event* caches, re-materialises, and asserts element-identical graphs.
  Red without the fix (multigraph sample), green with; `ferryman` pins
  that simple mode holds by content pooling alone.
- **Sample** `junit/samples/parallel-pump.gps`: a multigraph grammar with
  parallel creators (`new:mult=2:`), erasers over parallel bundles, and a
  merger rule whose redirects create parallel copies.
- **Cross-run drift is inherent and out of scope**: two explorations
  sharing one grammar instance see different created-element numbers
  (the shared factory's dispensers advance monotonically), for nodes and
  edges, in simple and multi mode alike. This is why the sample could not
  be added to `DeterminismTest`: its enumeration signature includes
  content-based hashes over element numbers. The existing DeterminismTest
  samples never mint any fresh element during exploration (verified
  empirically), so they are insensitive to this. Making cross-run
  identities reproducible would require canonical per-state numbering — a
  deep change serving no user-visible property, since a fresh GTS
  renumbers states anyway. Re-loading the grammar per exploration is no
  alternative: control-automaton and type objects then differ and their
  identity-based hashes change the signature (verified with ferryman).

### Rule-side MULT deferred (user, 2026-07-31)

The `mult=k:` aspect is no longer allowed on rule edges (removed from the
RULE edge kinds; a rule use is now an aspect error). Rationale: the
use-case list had shrunk to bulk-k operations under DPO — counting NACs
were prohibited, ranges rejected, `cnew:` prohibited, SPO bundles mean
"up to k", regexpr mult was vacuous — while the comprehension and
decision-surface cost recurred with every semantics decision. Crucially,
*increments by one need no aspect*: under the disjoint index allocation,
distinct role-bearing aspect edges of the same content are distinct
copies, so `use:a` + `del:a` matches two and deletes one, and `use:a` +
`new:a` creates a fresh second copy (pinned by the reworked `mult.gps`
fixtures readerEraser and readerCreator). What is genuinely deferred:
atomic bulk-k deletion/creation, and with it any on-disk expression of
same-role bundles of size ≥ 2 (those pins now live in the programmatic
`ParallelEdgeMatchingTest` only; same-content copies can still arise
cross-level, e.g. a forall reader plus a kernel reader, so the bundle
matching machinery stays). **Host-side `mult=` stays**: it is the only
way to author multigraph host graphs on disk (the `.gst` pipeline goes
through the simple AspectGraph) and carries the value and binary-edge
checks. Reintroduction is cheap: restore MULT to the RULE edge kinds, an
expansion loop over the allocator, and the recorded checks (NAC
prohibition with an understandable message, edge-image requirement) —
all preserved in the history of this note and of `AspectEdge.checkMult`.
The counting-NAC prohibition thereby reverts to a recorded decision
without enforceable syntax.

### Open items after this step

- **Display mapping of expanded copies**: the model map maps a counted
  aspect edge to parallel copy 0 only; match highlighting for copies
  1..k-1 falls back to nothing. Aggregated display of semantic multigraphs
  (LTS states) as `(x2)`-decorated aspect edges is likewise still to do.
- `DefaultRuleTransition.getMorphism()` (GUI-informational) re-derives
  without the recorded edges, so in multigraph mode it maps onto
  content-equal rather than identical edges.
- Error contexts of host-graph mult errors reference the normalised graph's
  edges; transfer to the original graph is best-effort.

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
