# Set-valued operators (gh #931, second part): handoff state

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
- **Any level.** A set-valued call needs no level check of its own. On the level of its
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
- **Indeterminate set-valued user operations are forbidden** (phase 2 check): indeterminate
  operators are not supported on quantified levels anyway (gh #911).
- Built-in: `range(n)` = 0..n-1, empty for `n <= 0`. A two-argument `interval(lo, hi)` was
  implemented and removed again (2026-11-11): it is `lo + range(hi - lo + 1)`, membership
  is `lo <= x` and `x <= hi`, and it needed a name of its own because a signature may not
  overload an operator name (`Operator.getOperatorMethod`). Rule `span` in `setValued.gps`
  shows the replacement, with the set-valued call nested in an expression.

## Decision of 2026-11-12: set results of user operations withdrawn

Arend questioned whether set-valued operators and sequences are one mechanism too many.
Outcome:

- **Withdrawn: user operations that return `Set`** (phase 2). With sequences as sorts
  (gh #942) an operation returns a sequence value and a rule ranges over that, which
  makes the `Set` result a second way of doing the same. Nothing of it is released, so
  withdrawing is free; removing it after 7.7.0 would be a break. No use case needs it in
  7.7.0 (Arend): several results of a user type can wait for gh #942.
- **Kept: `range` and the set-valued matching mechanism** (phase 1). The first part
  quantifies over elements with `range(coeffsSize(p))`, so the mechanism is needed in any
  case. `Operator.isSetValued` then holds for system operators only.
- **Not added: an operation result that is a sequence and is ranged over.** It needs no
  sort, but it pre-empts gh #942: there `IntSeq digits(int)` yields a value, and
  `horner(x, digits(n))` or `digits(n) == digits(m)` type-check under both readings, so
  7.7.0 grammars would change meaning without an error. Ranging over the elements of a
  sequence becomes a built-in element enumerator of gh #942 (section "Ranging over the
  elements" of that issue).
- **Until gh #942**, an operation with several results returns a record with a sequence
  component, and the rule ranges over `range(cSize(d))` with `cAt(d, i)`.

The decisions above on sets, order, levels, membership and anchors stay in force for
`range`. The removal is not done yet. It goes on branch `withdraw-set-results`, cut from
`sequence-components` after slice 2 of the first part (1576fa09f), as one commit:

- `Operator.getElementType` and its use in the user-operation constructor, including the
  check on `indeterminate`;
- the element-type check in `UserSignature.checkMethod`;
- `UserOpsExample.digits` and `letters`; rules `digitSum`, `digitNodes`, `letters` in
  `junit/rules/userOps.gps` and their goldens; `UserSignatureTest.testSetValued`;
- the set-valued paragraph of the `@UserOperation` javadoc.

A user operation with any `Collection` return type becomes a load error, which keeps
the form reserved. `junit/rules/setValued.gps` and `junit/samples/range-cells.gps` use
`range` only and stay.

Documentation: in `CHANGES.md` the 7.7.0 entry names `range` only. On the website branch
`set-valued-operators`, the subsection "Set-valued user operations" of `manual/data.md`
goes; the section "Set-valued operations" stays, for `range`.

## Phase 1 (done on this branch)

- `Operator.isSetValued()`, detected from a `Set<X>` return type of a signature method.
- `GSignature`/`IntSignature`: `range`; `IntInterval` is the lazy set view the
  Java and Big algebras return.
- `OperatorNodeSearchItem.SetValuedOperatorNodeRecord` (a `MultipleRecord`), chosen when
  the target is neither constant nor found at activation time; it handles a run-time seed
  by membership.
- `AlgebraFamily.compute` refuses a set-valued call (control arguments, host graphs);
  `NormalAspectGraph.addCall` rejects it outside rules.
- Fixture `junit/rules/setValued.gps` (rules `pick`, `span`, `spawn`, `clones`, `total`,
  `member`, `absent`) in `RuleApplicationTest`, with POINT and TERM tests; golden in
  `junit/rulecompilation/rules/setValued.txt`.

## Phase 2 (done on this branch, withdrawn 2026-11-12, removal pending): user operations

- `Operator.getElementType(Method)`: a return type assignable to `Set` with a class as
  type argument makes a user operation set-valued; `Integer`, `Double`, `Boolean` map to
  the system sorts. A wildcard or raw set, a collection that is not a set, and `indeterminate = true`
  are errors.
- `UserSignature.checkMethod` checks the element type against the declared user classes.
- `UserOpsExample.digits` (distinct digits) and `letters` (user-typed values); rules `digitSum`,
  `digitNodes`, `letters` in `junit/rules/userOps.gps`; `UserSignatureTest.testSetValued`.
- `junit/samples/range-cells.gps` (17 states, 33 transitions) as a `DeterminismTest` row.
- `@UserOperation` javadoc.

## Phase 3 (done, awaiting review): documentation

- `release/include/CHANGES.md`: new top section "Release 7.7.0, forthcoming". Arend confirmed
  7.7.0 (2026-11-11) and holds the release until the first part of gh #931 is done as well;
  the pom revision is untouched.
- Web manual: website repository `C:\Groove\nl-utwente-groove.github.io`, branch
  `set-valued-operators` off `main`, worktree `C:\Groove\worktrees\set-valued-operators`.
  `manual/data.md` gets a section "Set-valued operations" (levels, membership, algebras)
  and a subsection "Set-valued user operations" (set only, positional idiom for
  occurrences, deterministic order), marked "(since release 7.7.0, forthcoming)". No
  figures; `ref-operators.md` is generated and has to be regenerated at release time.
- gh #930 (fold direction for user operations) is independent; its "sort the values before
  the call" does not extend to user-type elements.

## Found in passing, not touched

`TermIntAlgebra.bigmax` builds a `PROD` term and `bigmin` a `MIN` term: filed as gh #932.
