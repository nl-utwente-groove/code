# Dynamic censored re-match of regular expressions (gh #900)

Status: implemented on branch `regexpr-censored-match`, 2026-08-31. Resolves
the deferral recorded in [eraser-injectivity.md](eraser-injectivity.md)
("Deferred: dynamic censored re-match"). The design verdict, with the
rejected alternatives, is recorded on the issue (comment of 2026-08-31);
this note documents the implementation and its discovered subtleties.

## The semantics

Under faithful DPO matching, a positive composite regular expression edge
only matches if it has a witness path that avoids the current images of
the eraser edges at its own or an ancestor quantification level. The
discipline is selected by the enum-valued grammar property
`regExpMatching = faithful | sloppy` (default faithful; user-chosen name,
2026-08-31, replacing the boolean `ignoreRegExp`, which is translated by
`GrammarProperties.repairVersion` like the `parallelEdges` key before it):
sloppy retains pure automaton (ends-only) semantics in which a witness may
be erased by the same application. Names weighed and rejected in the
renaming discussion: `sloppyRegExp` (names the mode's quality, not its
content), `ignoreRegExpErasure` (accurate but still check-suppression
flavoured), `nonDPORegExp` (the grammar stays DPO), `preserveRegExpPaths`
(boolean naming only the faithful pole; the enum documents both poles in
the tooltip via `DocumentedEnum`). The static check
(`ConditionAssembler.checkRegExprErasure`) remains only for the
configurations with no coherent per-match verdict — erasers at a level
that is *not* an ancestor-or-self of the expression's level (deeper, or in
a sibling branch), where amalgamation makes witness destruction joint
across quantifier instances.

## Rejected designs, in brief (full argument on the issue)

- **Post-approval invalidation stage**: unsound twice over — the relevance
  mechanism reports one representative per class of matches differing in
  irrelevant images, so a post-hoc verdict discards never-enumerated valid
  class members (the step-1 lesson of the identification condition); and
  the verdict would depend on non-anchor images that are unstable under
  GC-driven state-cache reconstruction (ferryman analysis).
- **Deciding at amalgamation level** (`TreeMatch.traverseMatrix`, like the
  inter-instance eraser overlap filter): same two defects — that filter is
  legitimate only because it reads anchors exclusively.
- **Declaring the target state absent**: category error — the match must
  not exist; a computed-but-absent transition leaks into forall counting,
  recipe internals and the match-equals-legal-application invariant.
- **Censoring by erasable edge *types*** (rather than the bound eraser
  images): would forbid the expression from traversing any edge of an
  erasable type anywhere in the host — far stricter than "the host minus
  the erased edges" and rejecting matches with no actual conflict.

## Implementation

Four layers, one commit each:

1. **Automaton** (`match/automaton`): `RegAut.getMatches` gained a
   censored-set overload; the hook is a single skip in the `Recogniser`
   edge traversal. Node type tests are unaffected (node typings are not
   paths). The `DFA` caches the most recent recogniser keyed by (graph,
   censored set), so repeated queries with an unchanged censoring context
   reuse the reachability maps. The interface default supports only the
   empty set; the legacy `MatrixAutomaton` (test-only) throws otherwise.
2. **Matcher** (`match/plan`): `RegExprEdgeSearchItem` receives candidate
   censor erasers and retains those whose `getMatchingTypes()` intersect
   the expression's positional `RegAutCoverage`; both its records pass the
   current eraser images (read from the search by edge index) to the
   automaton. Candidates are the condition's own-level LHS∖RHS edges with
   a host edge image — computed from `rule.lhs()`/`rhs()` directly, *not*
   `Rule.getEraserEdges()`, which excludes the (matched) incident edges of
   eraser nodes, and those are erased too — plus the imported ancestor
   erasers (`Condition.getAncestorEraserEdges()`, seeded by root
   extension). Negated occurrences (edge embargoes, negated labels) are
   immune to erasure and get an empty candidate set, hence the new
   `positive` flag on `PlanData.createEdgeSearchItem`.
3. **Compile-time import** (`ConditionAssembler.importEraserConflicts`):
   an ancestor eraser edge is now also imported (and recorded as ancestor
   eraser) when its matching types intersect a level expression's
   coverage — previously only image-sharing conflicts with
   edge-image-bearing edges triggered the import, so an ancestor eraser
   threatening only untracked witnesses never reached the level.
4. **Static check narrowing**: erased types are collected per level and
   filtered by `Index.higherThan` (ancestor-or-self ⇒ dynamically
   censored ⇒ no error). The coverage computation is shared with the
   import trigger via a per-assembler cache in which the empty set doubles
   as the "not a censorable expression" marker.

## Scheduling: needsEdges, and why in-search enforcement is sound

The plan order is decided by the greedy comparator loop in
`PlanSearchEngine.PlanData.getPlan`, *not* by `SearchPlan.add` — the
latter's dependency list only records backjump targets. Forcing the eraser
binders before the expression therefore needed a first-class concept:
`SearchItem.needsEdges()`, mirroring `needsNodes()`. It feeds three
places: the `NeededPartsComparator` (schedules items with unmet edge needs
last, which — as for nodes — is effectively hard, since the binders
themselves need nothing), the `SearchPlan.add` dependency computation
(backjumping into the eraser bindings on a censored failure), and an
assert that every needed edge is bound earlier.

The record-replay machinery stays correct without modification: a record
is only ever `repeat()`ed when the forward walk did not pass its recorded
dependency index, and that index is the *last* binder of anything the item
needs — every successful `next()` of the dependency record resets its
influencees via the `influence` array, so a changed eraser image always
resets the expression record rather than replaying stale results. Eraser
images are anchors, so replay under unchanged dependencies is
deterministic.

## Fixtures

- `junit/rules/regExprCensor.gps` (+ `RuleApplicationTest.testRegExprCensor`):
  `censorPlus` (witness = erased edge ⇒ inapplicable; `mult=2` parallel
  copy survives via the other copy), `censorReroute` (only the eraser
  binding that leaves the witness path intact yields a match — under the
  old semantics both bindings applied), `censorAncestor` (kernel eraser
  censors a forall-sublevel `a+` per event: partial instance sets, and a
  vacuously satisfied forall that still applies). The compilation dump
  (`junit/rulecompilation/rules/regExprCensor.txt`) pins that the kernel
  eraser reaches the sublevel root through the new import trigger.
- `junit/rules/regExprCensorIgnored.gps`: sloppy counterpart — with
  `regExpMatching=sloppy`, `censorPlus` deletes its own witness.
- `junit/rules/regExprErasure.gps`: `seqThroughEraser` (same level) now
  compiles cleanly; `seqThroughSublevelEraser` (deeper) and the new
  `seqThroughSiblingEraser` (sibling forall branches) pin the remaining
  static errors, with the error message now naming the level relation.

Fixture lessons: plain GXL deduplicates identical edges, so parallel host
edges must be written with the `mult=2` aspect; and `RuleApplicationTest`
expects one result file per *event*, not per isomorphism class — two
events with isomorphic targets need two (identical) expected graphs.

## Residue

- The censored recogniser cache is single-entry; backtracking that
  alternates between two eraser images rebuilds the reachability maps each
  time. Acceptable until profiling says otherwise (censoring is only
  active for rules with a coverage∩erasure intersection).
- `MatrixAutomaton` does not support censoring (unused outside its own
  test); the RETE engine remains exempt from all DPO machinery.
- The incoherent configurations (deeper/sibling erasers) stay statically
  rejected; a per-amalgamation semantics for them was considered and
  rejected (see above), not merely postponed.
