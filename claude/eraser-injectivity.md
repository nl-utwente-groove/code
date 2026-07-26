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

## The quantifier semantics: match-level filtering (user, 2026-07-20)

Designing step 3 surfaced a fork: what does an eraser overlap involving a
forall instance mean for the amalgamated application? Case matrix for a
forall level (e = eraser, kernel = the parent match):

| case | overlap | resolution |
|---|---|---|
| A | instance e vs same instance element | in-search: not a legal match |
| B | instance e vs kernel e | in-search (root extension): not a legal match |
| C | instance e vs kernel reader | in-search (root extension): not a legal match |
| D | kernel e vs instance reader | in-search (root extension): reroute or drop |
| E | instance e vs other-instance e | post-hoc proof filter: **invalidate** |
| F | instance reader vs other-instance e | permitted |

**Decided: match-level filtering.** The identification condition is a
*matching* condition: a candidate sub-match that identifies an eraser with
any other element of its own (root-extended) pattern is simply not a legal
match — like a NAC violation — and hence not an instance; a forall over an
empty legal-instance set is vacuously satisfied. Rejected alternatives,
recorded for the theory-minded: *morphism-strict* (any overlapping morphism
of any instance class invalidates) makes deleting rules under broad foralls
inapplicable in most hosts, since readers can typically roam onto a deleted
element's image; *class-strict* (an instance class **forced** into an
overlap invalidates, an avoidable overlap reroutes) was on the table but
the user chose uniform match-level filtering. Note the determinism
constraint that shaped the options: eraser images are anchors and stable
across state-cache reconstruction, reader images in a representative match
are not (cf. the ferryman analysis), so only eraser–eraser overlaps can be
checked post-hoc (E); cross-instance reader–eraser overlap (F) is
undetectable deterministically and stays permitted — the deleted element
wins there, an accepted residue confined to cross-instance reads (this also
covers the implicit deletion of a node's incident edges vs. another
instance's explicit edge eraser, which reduces to F).

## Step 3, implemented: cross-level injectivity via root extension

`ConditionSearchItem.PatternRecord.createContextMap()` seeds the child
search with the images of the condition **root graph** — nodes, *edges* and
variables — and seeded edge images land in the child's `edgeImages` array,
which the conflict machinery reads. So the mechanism is **root extension**:
`RuleModel.Level4.importEraserConflicts` (run top-down over the level tree,
before any condition is built) walks each level's ancestor chain and, for
every ancestor element whose image may coincide with an eraser of this
level (or ancestor eraser that may coincide with any element of this
level), adds that element as a *reader* (LHS + RHS) to every level from
just below the ancestor down to this one. The child search then sees the
ancestor image seeded; because a condition needs its root elements bound
before it runs, the ancestor always binds the conflicting element first.
No anchor inflation, no change to event identity. Imported ancestor
*erasers* are additionally recorded — on the `Condition`
(`addAncestorEraserEdges`) for edges, consumed by
`SearchPlan.computeEraserConflicts`; level-locally for nodes, consumed by
`addEraserNodeEmbargoes` — so they take part in conflict generation as
erasers. Pairs of elements both shared with the parent level are skipped:
they are checked at the ancestor level where both first coexist. Under
injective matching no conflicts are generated, but the root extension still
matters: seeded images enter the search's used-nodes/used-edges sets, which
is exactly what makes injective matching subsume the cross-level condition.
Note `canShareImage` moved from `SearchPlan` to `RuleEdge` so that rule
compilation can use it.

## Step 4, implemented: inter-instance eraser overlap invalidates

Two individually-legal instances whose *eraser images* coincide cannot be
arbitrated by match-level filtering (dropping either would be arbitrary),
so the whole amalgamated application is invalid. Enforcement is a proof
filter, not an in-search check: exists-alternatives are only resolved when
a `TreeMatch` is expanded into `Proof`s (one proof = one amalgamated
application, `TreeMatch.traverseMatrix`), so the filter lives there —
active only for top rules with eraser-bearing subrules
(`Rule.hasEraserSubRules`) — and rejects proofs in which two (sub)proofs
claim the same eraser node or edge image. Eraser images are anchors, so
the check is deterministic. This placement also catches collisions between
instances of *different* quantifiers and across nesting branches, which a
per-quantifier check after `findAll` would miss.

Fixtures (`erasers.gps`, one per case family, each `-0`/`-1` start pair
verified against engine-generated ground truth): `eraseForallReader`
(case D: forced instance drops, forall vacuous; avoidable overlap
reroutes), `eraseForallOnReader` (case C: "delete all a-edges" skips the
kernel-read edge), `eraseForallEraser` (case E: two instances deleting the
same edge make the rule inapplicable), `eraseForallNode` (node variant of
C/D via merge embargo against the imported kernel eraser node).

## Step 5, implemented: regular expressions vs. erasers (2026-07-26)

The machinery above covers rule edges with a *host edge image*. Composite
regular expressions (sequence, closure, inverse, general choice) bind only
their end nodes; the host edges witnessing the matched path are untracked,
so the identification condition cannot be enforced for them — erasure of a
witness silently proceeds (SPO residue). Tracking all witnesses at match
time was rejected outright (user). The resolution has three parts.

**Atom choices get real edge images.** A choice between atoms (`a|b`,
uniformly binary or flag) is the one composite whose every witness is a
single host edge, so it is matched like a guarded wildcard:
`ChoiceEdgeSearchItem` (an `Edge2SearchItem` subclass minus the variable
plumbing of `VarEdgeSearchItem`) binds the image, and the existing conflict
machinery covers it with no further change. `RuleEdge.hasEdgeImage` (now
public) includes atom choices; `canShareImage` intersects possible image
label sets. **Gated on multigraph mode** (user decision): with an edge
image, distinct witnesses of a forall-quantified choice count as distinct
instances — consistent with atom edges in multigraphs, but an observable
change — so simple-graph mode keeps the automaton item and its counting
bit-for-bit. Compiling `a|b` into an unnamed guarded wildcard was rejected:
`isWildcard()` is load-bearing across typing, display and validation.
Inverse atoms `-a` are an analogous future widening (image with flipped
ends); until then they stay untracked. Fixture `parallelChoice.gps`;
programmatic pins in `ChoiceEdgeMatchingTest`.

**Remaining composites are statically checked** (`ignoreRegExp`, default
false — the check is active when `parallelEdges` is set and `ignoreRegExp`
is not). `RuleModel.checkRegExprErasure` reports a rule error iff an
untracked positive regexpr edge can traverse an edge type that the rule may
erase. Design points, all user-decided:

- *Traversability is positional*, not label-based: `RegAutCoverage`
  explores the product of the label automaton with the type graph between
  the edge's end node types (forward + backward marking), so an unguarded
  wildcard traverses only what can occur between the node types at its
  position, and only edge types on genuinely accepting paths count.
- *Erased edge types* are the matching types of eraser edges plus the
  incident edge types of eraser nodes' matching types (node deletion
  erases incident edges) — the precision option; under `checkDangling`
  eraser nodes contribute nothing, since deletion of unmatched edges is
  then forbidden anyway. Whole quantification tree × whole tree, both
  directions: amalgamation lets erasers at any level destroy witnesses at
  any other level.
- *Exempt*: `=` traverses nothing; `!r` and NAC-internal expressions are
  immune (erasure cannot invalidate an established negative condition);
  everything with an edge image is covered by the match-time machinery.
- *Independent of `matchInjective`*: that property is shorthand for global
  node injectivity only and does not constrain untracked witnesses.

Fixtures `regExprErasure.gps` / `regExprErasureIgnored.gps` with
`RegExprErasureCheckTest`; `wildcardPositional` pins that a label-based
approximation would over-flag.

**Discovered in passing: `parallelEdges` was never wired.** The grammar
key predates GitHub, but `HostModelMorphism` unconditionally built simple
host graphs, and the GTS host factory (hence the engine mode of every
rule) derives from the start graph — so no disk-loaded grammar ever ran
in multigraph mode; all prior multi-mode work was exercised
programmatically. Wiring it exposed a second latent bug:
`HostGraphMorphism.createImage` recreated edge images through the factory
without a number, which in a non-simple factory mints a fresh parallel
copy — multigraph clones lost all edge identity and application deltas
silently erased nothing. Fixed by returning the key edge itself when its
end nodes map to themselves.

**Deferred (user, 2026-07-22: bring back up later): dynamic censored
re-match.** Re-running the automaton on the host minus the erased edges
(anchor-derived, hence deterministic) would turn the flag into a real
strict/sloppy semantics switch. Only sound for regexprs at the same or
deeper quantification level than the erasers, whose censoring context is
fixed when legality is decided (cf. case C); a kernel-level regexpr vs.
instance erasers fails because witness destruction can be joint across
instances — no coherent per-instance legality verdict.
