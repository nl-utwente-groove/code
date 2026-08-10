# Critical-pair analysis: code review and revival plan

*Review of `nl.utwente.groove.transform.criticalpair`, 2026-08-10 (Claude session).
The package implements Ruud Welling's MSc work: delete-use conflict detection via
critical-pair enumeration, plus strict-local-confluence checking. It never became
accessible functionality. Now that true multigraphs and `parallelEdges=DPO` exist
(merged 2026-08-02), reviving it is on the table. This document records the state
of the code, the findings of the review, and the agreed route.*

## State

Better than "dormant" suggests: the package compiles under the module system, all
9 of its tests pass (2026-08-10, `TestConfluence`, `TestConfluenceWithAttributes`,
`TestDeleteUse`, `TestDeleteUseWithAttributes`, fixtures under `junit/criticalpair/`),
and it has been kept alive through every refactoring since 2022. But it has zero
entry points: the only reference outside the package is the `module-info` export.
No GUI action, no CLI tool, no exploration strategy uses it.

The theory implemented: enumerate all jointly-surjective overlaps of two LHSs
(node-partition based, with data values handled in the TERM algebra), filter to
parallel-dependent pairs, then check *strict* local confluence — joinability with
commuting transformation morphisms, decided by a bounded BFS over joint derivations
plus `IsoChecker`. Strictness is the DPO-correct criterion (plain joinability of
critical pairs does not suffice for local confluence), so the confluence machinery
is actually more at home in the new DPO world than in the SPO setting it was
written for.

| Class | Role |
|---|---|
| `CriticalPair` | Pair enumeration driver + the pair itself; public API `computeCriticalPairs(...)` |
| `ParallelPair` | Node-overlap partition under construction; builds the overlap host graph + both matches |
| `ConfluenceAnalyzer` | Strict-joinability search, plus a "subsumption" shortcut |
| `ConfluenceResult` | Grammar-level verdict aggregation, lazy analysis-until-counterexample |
| `LazyCriticalPairSet`, `OrderedCriticalPairSet` | `java.util.Set` façades over lazily/size-ordered pair maps |

## Central finding: DPO both motivates and currently breaks it

- `GrammarProperties.isCheckDangling()` returns true whenever the parallel mode is
  DPO.
- `CriticalPair.canComputePairs` requires `!isCheckDangling()` — a leftover of the
  SPO framing, where "matches with dangling edges must be allowed" was a
  precondition for completeness of the enumeration.
- Consequence: every rule of a `parallelEdges=DPO` grammar is rejected;
  `ConfluenceResult` and `computeCriticalPairs(Grammar)` throw. Only the direct
  `computeCriticalPairs(rule1, rule2)` path (assert-guarded) reaches the
  identification-condition filter added 2026-07-27, which is therefore unreachable
  through the grammar-level API — and untested: none of the `junit/criticalpair`
  fixtures sets `parallelEdges`.

The correct DPO treatment is not to reject the grammar but to enforce the full
**gluing condition on the overlap**: the identification condition
(`CriticalPair.satisfiesIdentificationCondition`, done) *and* a dangling filter —
discard overlaps where one match's eraser node acquires an incident edge,
contributed by the other match, that the rule does not delete; such an overlap is
not two legal DPO applications. That filter is small and symmetric to the
identification one.

Subtlety worth a test: rule 1 deletes node *v* while rule 2 merely *reads* an edge
at *v* — under SPO the classic delete-use conflict; under DPO application 1 is
simply inapplicable at that overlap, so the pair disappears. The critical-pair set
genuinely differs between the modes.

## Multigraph gaps

The overlap construction cannot represent multigraph situations at all:

1. `ParallelPair.getCriticalPair()` creates the overlap host with
   `HostFactory.newInstance(typeFactory, true)` — `simple` hardcoded.
2. Edge images are induced purely from node groups: each rule edge maps to *the*
   host edge `(src-group, label, tgt-group)`. For a multigraph rule with two
   parallel eraser edges, both collapse onto one host edge; the identification
   filter then (correctly) kills that overlap, and the real conflict — distinct
   parallel edges, one shared — is never generated. For genuinely multigraph rules
   the analysis silently *misses* critical pairs rather than producing wrong ones.
3. Fix: an extra enumeration axis — after fixing node groups, enumerate
   identifications among edges with equal label and identified endpoints (only
   needed for non-simple edge types, confining the combinatorial cost to parallel
   bundles), and build the host with `simple=false` for parallel-edge grammars.

## Genuine bugs (pre-existing, masked by current usage)

- **`LazyCriticalPairSet.computePairs` drops its result**: it removes the tuple
  from the to-process set and returns the pairs but never stores them in
  `pairMap`, so `getPairs` returns the initial `null`. `setIterator().next()` maps
  that null to the empty set — meaning `ConfluenceResult.analyzeAll()` on a
  *fresh* instance silently analyses nothing and reports `STRICTLY_CONFLUENT`.
  The tests never see this because `checkStrictlyConfluent` first runs
  `analyzeUntil`, which uses the element iterator (whose `computeMorePairs` does
  store). A landmine for any new caller — fix before anything else.
- **`LazyCriticalPairSet.isEmpty()` logic is inverted twice**: the loop condition
  wants `!ruleTuplesToProcess.isEmpty()`, and `pairsFound |=
  computeMorePairs().isEmpty()` sets `pairsFound` when nothing was found. Also
  NPEs on the `null` map values from the constructor.
- **The "alternate method" is barely tested and known-unsound**:
  `TestConfluence.testConfluentGrammar`'s "test using efficient method" never
  actually reaches `analysePairSet` (`analyzeUntil` ignores `alternateMethod`);
  the one test that would exercise it honestly, `testPhilAlternateMethod`, is
  disabled with the comment `TODO (AR) use multi-sorted graphs!!!!`. Given its
  documented false positives when an intermediate step is not a pushout, drop the
  subsumption path rather than carry it into a user-facing feature.
- **`canComputePairs` doesn't check rule priorities** despite its own TODO saying
  they're disallowed — a prioritised grammar would be analysed as if
  priority-free, silently. Same for controlled grammars: the analysis is
  rule-system-level and ignores the control program entirely; needs at least a
  loud caveat at the surface level.
- Hygiene: static mutable counters in `ParallelPair` (`matchTargetCounter`,
  `variableCounter` — not thread-safe, state leaks across analyses); `HashSet`
  state sets in `ConfluenceAnalyzer` with identity-based `HostGraphWithMorphism`
  (dedup is a no-op and the `searchDepth` cutoff becomes order-dependent → flaky
  `UNDECIDED` verdicts); the typo `NOT_STICTLY_CONFLUENT` in a public enum (free
  to rename now, impossible after exposure); duplicated `isRhsAsNac` check; no
  `@NonNullByDefault` anywhere in the package.

## Restrictions inventory (what gates applicability)

No NACs and no quantifiers (`getSubConditions().isEmpty()`), no type-graph
inheritance, no RHS-as-NAC / creator-edges-as-NAC, TERM algebra required for
attributed rules (assert-only — should become a proper report), operator-target
nodes must be edge-free. Reasonable v1 restrictions to *keep*, provided the
surfaced feature reports precisely which rule violates what, instead of the
current single `IllegalArgumentException`.

## Route (four slices, small → large, independently mergeable)

1. **Repair** — fix the `LazyCriticalPairSet` bugs (or replace both set façades
   with a plain `Map<RulePair, Set<CriticalPair>>` behind `ConfluenceResult`; the
   `java.util.Set` contract buys nothing), instance-ify the counters, make the
   joinability search deterministic, rename the enum constant, add
   `@NonNullByDefault`. Drop the subsumption method.
2. **DPO-correct enumeration** — replace the `!isCheckDangling` precondition with
   an explicit dangling-condition filter on overlaps for DPO grammars; keep
   rejecting `checkDangling` where it is a *user-set* SPO property (there the
   completeness argument really does fail).
3. **Multigraph overlaps** — non-simple host factory + edge-identification
   enumeration for parallel bundles; new fixtures under `junit/criticalpair/`
   with `parallelEdges=SPO|DPO` pinning both the found pairs and the confluence
   verdicts (currently the whole DPO path has zero test coverage).
4. **Surface it** — a thin analysis API (`ConflictAnalysis` over a
   `GrammarModel`, with per-rule applicability diagnostics), consumed by (a) a
   Simulator action producing the classic AGG-style rule×rule conflict matrix,
   with the overlap host graphs displayable as ordinary graphs (they are tiny —
   the existing JGraph display should just work), and (b) a small picocli tool
   next to `Generator`/`ModelChecker` for scripting. GUI first — this is an
   interactive, exploratory feature.

Suggested first branch: `critical-pair-repair` (slices 1+2).
