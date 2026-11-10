# Multi-valued operators (gh #931, second part): handoff state

Branch `multi-valued-operators`, off master 530f0ab7b. Transient: delete when the last
phase is merged.

## Goal

Operators whose result is a collection of values of one sort, matched once per element:
the dual of the set operators (`sum`, `bigmax`, ...), which fold the matches of a quantified
level into a value. The first part of gh #931 (sequence-valued components of user types)
builds on this and is not started.

## Decisions (2026-11-10, with Arend)

- **Bag, no order.** The outcome is a bag: repeated values yield repeated matches, the order
  of the values is not promised and not observable. Order that matters is carried as data:
  quantify over `range(size(x))` and access by index. Declared type is `List<T>`, as for the
  parameters of the set operators.
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
  a signature may not overload an operator name (`Operator.getOperatorMethod`). **The name
  `interval` and its inclusive upper bound are Claude's choice, not yet confirmed.**

## Phase 1 (done on this branch)

- `Operator.isMultiValued()`, detected from a `List<X>` return type of a signature method.
- `GSignature`/`IntSignature`: `range`, `interval`; `IntInterval` is the lazy list view the
  Java and Big algebras return.
- `OperatorNodeSearchItem.MultiValuedOperatorNodeRecord` (a `MultipleRecord`), chosen when
  the target is neither constant nor found at activation time; it handles a run-time seed
  by membership.
- `AlgebraFamily.compute` refuses a multi-valued call (control arguments, host graphs);
  `NormalAspectGraph.addCall` rejects it outside rules.
- Fixture `junit/rules/multiValued.gps` (rules `pick`, `span`, `spawn`, `clones`, `total`,
  `member`, `absent`) in `RuleApplicationTest`, with POINT and TERM tests; golden in
  `junit/rulecompilation/rules/multiValued.txt`.

## Phase 2 (next): user operations

- `Operator(Executable)`: a `List<T>` return type with `T` a boxed system sort or a
  `@UserType` class sets `multiValued`; `Sort.toSort` must map `Integer`, `Double`, `Boolean`.
- `UserSignature.checkMethod`: accept that return type; reject it on constructors and in
  combination with `indeterminate`.
- `userOps` fixture: an operation that returns a list with a repeated value, under `forall`
  with `sum` and with a creator. **This is the first test of bag multiplicity**; `range`
  cannot produce repetitions. Phase 1 showed that `forall` over `range(3)` with an
  unrelated creator makes three nodes although the sub-events are equal, so repetitions
  are expected to behave alike, but that is untested.
- A `DeterminismTest` row over a grammar using `range`.

## Phase 3: documentation

- `@UserOperation` javadoc, the user manual (website repository, `manual/`), CHANGES.
- gh #930 (fold direction for user operations) is independent; its "sort the values before
  the call" does not extend to user-type elements.

## Found in passing, not touched

`TermIntAlgebra.bigmax` builds a `PROD` term and `bigmin` a `MIN` term; both look wrong.
