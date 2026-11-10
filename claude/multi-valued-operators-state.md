# Multi-valued operators (gh #931, second part): handoff state

Branch `multi-valued-operators`, off tag `release-7_6_1` (ecab903cd), the yFiles-free line:
until Arend gives the go-ahead on yFiles, extensions are built there and merged into master
afterwards. The API differs from master in places (no `GrammarModel.toGrammar(family)`, no
`DebugFlag`). Transient: delete when the last phase is merged.

## Goal

Operators whose result is a set of values of one sort, matched once per element:
the dual of the set operators (`sum`, `bigmax`, ...), which fold the matches of a quantified
level into a value. The first part of gh #931 (sequence-valued components of user types)
builds on this and is not started.

## Decisions (2026-11-10, with Arend)

- **Set, deterministic order without meaning.** The outcome is a `Set<T>` (user operations
  may declare a subtype such as `SortedSet`; any other collection type is an error). A match
  cannot tell repeated values apart: equal sub-matches have equal proofs, and proofs form a
  set, so a bag was honoured by the fold operators but not by rule effects (`sum` over the
  digits of 1211 gave 5, a creator under `forall` made two nodes). Requiring a set makes the
  loss visible to the author of the operation. To quantify over occurrences, quantify over
  positions: `range(size(x))` plus access by index, which also carries the order as data.
- **The iteration order must be deterministic** (the same in every run), also for user
  operations. This cannot be checked; it is documented in the `@UserOperation` javadoc
  (use `LinkedHashSet` or a sorted set, not `Set.of`) and has to go into the manual.
  Whether a varying order is in fact observable has not been tested.
- **Any level.** A multi-valued call needs no level check of its own. On the level of its
  arguments it is an existential choice (one rule match per element); under `forall` it
  quantifies over the elements; in a NAC it tests that no element qualifies. This falls out
  of the existing level handling in `NormalAspectGraph.addCall` and `PatternBuilder`.
- **Known target = membership test.** If the target is a constant, already matched, or
  seeded, the item is singular and tests `outcome.contains(value)`.
- **No anchor change.** The target is not added to the anchor. Matches that differ only in
  an element that no modification depends on are the same event, as for reader nodes.
  A value that a modification uses is anchored through the modifier ends as before.
- **Errors.** An error value in the arguments, or an exception in the operation, yields the
  error value as the single element, so that the error stays visible.
- **POINT** collapses the outcome to the single point value (so sub-match counts differ
  from the concrete run); **TERM** is rejected in `PatternBuilder.addOperator`.
- **Indeterminate multi-valued user operations are forbidden** (phase 2 check): indeterminate
  operators are not supported on quantified levels anyway (gh #911).
- Built-ins: `range(n)` = 0..n-1 and `interval(lo, hi)` = lo..hi inclusive. Two names because
  a signature may not overload an operator name (`Operator.getOperatorMethod`). Both are
  empty when the bound is too low (`range(n)` for `n <= 0`, `interval(lo, hi)` for `hi < lo`).

## Phase 1 (done on this branch)

- `Operator.isMultiValued()`, detected from a `Set<X>` return type of a signature method.
- `GSignature`/`IntSignature`: `range`, `interval`; `IntInterval` is the lazy set view the
  Java and Big algebras return.
- `OperatorNodeSearchItem.MultiValuedOperatorNodeRecord` (a `MultipleRecord`), chosen when
  the target is neither constant nor found at activation time; it handles a run-time seed
  by membership.
- `AlgebraFamily.compute` refuses a multi-valued call (control arguments, host graphs);
  `NormalAspectGraph.addCall` rejects it outside rules.
- Fixture `junit/rules/multiValued.gps` (rules `pick`, `span`, `spawn`, `clones`, `total`,
  `member`, `absent`) in `RuleApplicationTest`, with POINT and TERM tests; golden in
  `junit/rulecompilation/rules/multiValued.txt`.

## Phase 2 (done on this branch): user operations

- `Operator.getElementType(Method)`: a return type assignable to `Set` with a class as
  type argument makes a user operation multi-valued; `Integer`, `Double`, `Boolean` map to
  the system sorts. A wildcard or raw set, a collection that is not a set, and `indeterminate = true`
  are errors.
- `UserSignature.checkMethod` checks the element type against the declared user classes.
- `UserOpsExample.digits` (distinct digits) and `letters` (user-typed values); rules `digitSum`,
  `digitNodes`, `letters` in `junit/rules/userOps.gps`; `UserSignatureTest.testMultiValued`.
- `junit/samples/range-cells.gps` (17 states, 33 transitions) as a `DeterminismTest` row.
- `@UserOperation` javadoc.

## Phase 3: documentation

- The user manual (website repository, `manual/`) and CHANGES; include the positional
  idiom for occurrences and the deterministic-order requirement.
- gh #930 (fold direction for user operations) is independent; its "sort the values before
  the call" does not extend to user-type elements.

## Found in passing, not touched

`TermIntAlgebra.bigmax` builds a `PROD` term and `bigmin` a `MIN` term: filed as gh #932.
