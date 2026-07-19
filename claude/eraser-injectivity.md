# Injective matching of eraser edges (DPO identification condition)

Status: decided 2026-07-19; steps 1 (within-level eraser *edges*, plan-based
matcher) and 2 (within-level eraser *nodes*, compile-time merge embargoes)
implemented on branch `parallel-edges`. Related to, but distinct
from, the parallel-edge work in
[aspect-parallel-edges.md](aspect-parallel-edges.md): the machinery lives in
the same code region as the edge-injectivity support for non-simple patterns,
and parallel eraser *bundles* will rely on it.

## The requirement (user decision, 2026-07-19)

Eraser edges must always be matched injectively — *independent* of the
grammar's injectivity property — to correctly reflect double-pushout
semantics: if a deleted edge is identified with any other edge (eraser or
reader), the pushout complement is not unique. Previously GROOVE resolved
such identifications by letting deletion win (SPO-style), silently deleting
edges the rule claims to preserve, or collapsing two claimed deletions into
one. Recorded decisions:

- **Eraser vs. *any* edge**, not just eraser vs. eraser: the full
  identification condition on edges.
- **Eraser nodes get the same treatment**, but via **compile-time merge
  embargoes** (NACs) generated during rule compilation — no matcher change
  needed, and `EqualitySearchItem`s participate in the search plan with
  proper backtracking, so the approach is sound. *Approved for
  implementation (user, 2026-07-19).*
- **Cross-level injectivity must be guarded** as well (an eraser at one
  quantification level vs. an edge at another). *Approved for
  implementation (user, 2026-07-19)*, see below.
- **Inter-instance eraser overlap invalidates the amalgamated application**
  (user decision, 2026-07-19, resolving the open question below): if two
  instances of one universal quantifier map erasers to the same host
  element, the whole quantified application is invalid — DPO on the
  amalgamated rule, not shared deletion.
- **RETE is exempt for now**: that engine is unmaintained and may be retired
  altogether; it retains the old delete-wins behaviour. This subsumes the
  previously planned "guard RETE against non-simple patterns" work item.

## Step 1, implemented: within-level eraser edges in the plan engine

Design constraints discovered during investigation:

- **Enforcement must live inside the search, not post-hoc.** The relevance
  mechanism reports one representative per class of matches differing only in
  irrelevant images (`Record.repeat()` replays previous images). A filter on
  completed matches (or at Proof/RuleEvent level) would discard an invalid
  representative while its valid class members are never enumerated — the
  rule would wrongly be judged inapplicable. Inside the search, a refused
  binding triggers genuine backtracking and the valid representative is
  found. Eraser edges are anchor keys, so reported matches differing in
  eraser images remain distinct.
- **`putEdge` is hot**, so the check is statically targeted: `SearchPlan`
  computes *conflict pairs* — (eraser, other edge) pairs whose images could
  coincide, conservatively by equal type-edge labels / wildcard role
  compatibility — only for non-injective matching of rule conditions with
  erasers (injective matching subsumes the constraint; for injective
  non-simple patterns the global used-edges machinery does). The strategy
  translates them to per-edge-index `int[][] conflictIxs`; `Search.putEdge`
  refuses an image equal to a conflicting edge's current image. Zero
  overhead when there are no conflicts (one null check).
- **Backtracking dependencies are load-bearing, not an optimisation**:
  `SearchPlan.add` adds a dependency from an item binding a conflicted edge
  to earlier items binding its conflict partners, mirroring (but more
  precise than) the blanket edge-injective dependencies. Without them an
  exhausted record would jump back past the binder of the conflicting image.

Surprises hit during implementation:

- `Edge2SingularRecord.find()` **ignored the return value of `write()`**
  (safe before, because `putEdge` could never fail on simple patterns) —
  a refused binding was treated as success, leaving a null anchor image
  (NPE in `TreeMatch.computeHashCode`). Fixed; the `assert result` on the
  FULL-state re-write path doubles as a soundness check on the new
  dependencies.
- The **critical-pair construction** (`CriticalPair.computeCriticalPairs`,
  Welling's module) enumerated overlaps whose constituent matches identify
  eraser edges — under the new semantics these are not legal matches, so the
  joinability analysis (which uses the matcher) could no longer join them
  and `phil-getBoth` stopped being strictly confluent. Fixed by filtering
  pairs on the identification condition at construction. The same will be
  needed for eraser *nodes* when step 2 lands.
- Fixture `junit/rules/mergers.gps/mergeDeleteEdge` pinned the delete-wins
  outcomes (results `-0-2..-0-4`, from matches identifying the merged nodes
  and thereby the eraser with the reader edge); removed. New fixtures
  `erasers.gps/eraseReaderOverlap` and `eraseEraserOverlap` pin the new
  semantics. `erasers.gps/eraseTwoExplicit` pins the current *node*
  delete-wins behaviour (9 outcomes) and is step 2's fixture to update.

## Step 2, implemented: eraser nodes via compile-time merge embargoes

`RuleModel.Level4.addEraserNodeEmbargoes` adds, per level, a merge embargo
(`EdgeEmbargo` with an empty-label edge) for every pair of a deleted node
and another LHS node whose `getMatchingTypes()` overlap, skipped entirely
under grammar-wide injective matching (which subsumes the condition; the
planner's `createEdgeEmbargoItem` would drop the equality items anyway).
Only `DefaultRuleNode` pairs participate — value/operator nodes can never
share an image with them. The critical-pair filter learned the node
condition. Fixture fallout, each verified as a delete-wins pin:

- `erasers.gps/eraseTwoExplicit`: 9 → 6 outcomes (diagonal x=y matches gone);
- `erasers.gps/eraseCreate`: 4 → 2 (create-onto-deleted diagonal gone);
- `erasers.gps/eraseOverlap`: result `-0-1` (eraser node bound to the
  reader end of the eraser edge) gone;
- `mergers.gps/mergeDeleteNode` and `regexpr.gps/deleteANode`: the single
  match identified the eraser with a reader (via a single host candidate
  resp. a shared type wildcard `?x`), so start `-0` became inapplicable;
  per the house pattern the `-0` start stays as inapplicability witness and
  a new `-1` start pins the new semantics. The `mergeDeleteNode` old result
  showed delete swallowing the merge *and* the created node; the new one
  shows the merge retyping the surviving node (specialisation to B);
- exploration pins: samples `mergers.gps` 66/143 → 52/98 states/transitions,
  `recipes_conditions.gps` 15 → 9 resp. 2 → 1 transitions.

## Step 3, pending: cross-level injectivity

Key finding: `ConditionSearchItem.PatternRecord.createContextMap()` seeds the
child search with the images of the condition **root graph** — nodes, *edges*
and variables — and seeded edge images land in the child's `edgeImages`
array, which the conflict machinery reads. So the natural mechanism is
**root extension**, not anchor extension: during rule compilation, add each
parent edge that conflicts with a child-level eraser (and vice versa) to the
child condition's root graph (with its end nodes). The child search then
sees the parent image, the conflict-pair computation covers the pair
automatically (root edges are pattern edges), and — because a condition
needs its root elements bound before it runs — the parent always binds the
conflicting edge before the subcondition, collapsing both checking
directions into one. No anchor inflation, no change to event identity.
Root extension must propagate transitively through intermediate levels.
The same mechanism supplies cross-level *node* pairs for step 2's merge
embargoes (both nodes must be in one pattern).

## Step 4, pending: inter-instance eraser overlap within one quantifier

Two forall sub-matches may map their (non-root) eraser edges to the same
host edge; the amalgamated match then identifies two erasers. **Decided
(user, 2026-07-19): such overlap invalidates the whole quantified
application** — the identification condition is applied to the amalgamated
rule, not weakened to shared deletion. In-search machinery cannot express
constraints on match *sets*; the natural enforcement point is
`ConditionSearchItem.QuantifierRecord.find()`, after `findAll`. The check
must cover eraser edges and, once step 2 gives nodes the same semantics,
eraser nodes.
