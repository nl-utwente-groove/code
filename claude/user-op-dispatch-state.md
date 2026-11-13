# Run-time dispatch for user operations (gh #933): handoff state

Branch `user-op-dispatch`, off `release-7` (7.7.2-SNAPSHOT). Transient: delete when merged.

## Goal

Let several user types have fields and operations of the same name. An operator (sort
`user` plus name) is backed by a list of implementations; applying it selects one by the
type of the first argument.

## Decisions

- **Only instance methods of user types share a name** (Arend, 2026-11-13): record
  component accessors, generated sequence accessors (`xsAt`, `xsSize`) and non-static
  `@UserOperation` methods. The receiver is the one dispatch key. Static operations and
  constructors stay sole implementations; a namesake is a duplicate.
- **Same signature on the level of sorts**: arity, parameter sorts, result sort, variable
  argument tail, `indeterminate` (`Operator.hasSameSignature`). So `int` versus `IntSeq`
  in last position is an error.
- **Classes behind `user`-sorted non-receiver parameters and results are not compared**
  (the permissive variant; confirmed by Arend, 2026-11-13). Reasons: binary operations on
  the own type (`Vec2.plus(Vec2)` next to `Vec3.plus(Vec3)`) need it, results need it
  anyway (`push` returns the own type), and the receiver alone determines the
  implementation.
- The accessor kind moved from `Operator` to `UserSignature.Impl`.
- The compile-time check is part of this branch (Arend, 2026-11-13).
- **The class of a `user`-sorted expression is computed on demand**
  (`UserSignature.getUserClass`), not stored in the expression: it is only needed while
  a call is built, expressions are created in many places besides the parser (`bind`,
  `relabel`, the term algebras, `Constant`) and are compared by content, and the classes
  belong to a class loader that is replaced on every grammar (re)load.
- **A call is rejected only if no implementation can take it.** The implementations are
  narrowed down argument by argument (receiver first, by identity; other parameters by
  assignability); with a receiver of unknown class all of them are candidates, so
  `sameItems(x, UserTypeCounter(..))` is an error but `sameItems(x, UserTypeQueue(..))`
  is not. A call of an operator whose implementations differ in result class has no
  known class, so `count(push(x,1))` is accepted although it can never succeed.

## Done

- Slice 1 (983b3799d): `UserSignature.Impl` and `getImplementations()`, consistency
  checks in `addOperator`, dispatch in `AlgebraFamily.UserOperation.getImpl`, operator
  help with all declaring classes, fixtures `UserTypeStack`/`Queue`/`Counter`/
  `SignatureClash` and `UserOpsStaticClash`, tests in `UserSignatureTest`.
  Gates: default suite 877 run, 0 failures, 2 skipped; ecj clean on the touched files.
- Slice 2, the compile-time check: `UserSignature.getUserClass` and `checkCall`, with
  `Impl.returnClass`/`parClass`/`accepts`; called from `ExprTree.toCallExprs`, which
  rethrows the error of the check instead of the generic "not applicable" one if no
  other operator of that name fits. Tests in `UserCallCheckTest`; two cases of
  `SequenceTailTest` that relied on a constant of the wrong user type yielding an error
  value now apply the operation to values instead. CHANGES entry for gh #933 as a whole,
  under "Release 7.7.2, unreleased" (the header of `user-ops-package-wildcard`; the
  version number is Arend's call).

## Next

1. An end-to-end grammar test if wanted (a new `junit/rules` grammar needs a
   `RuleCompilationTest` golden).
2. Web manual section, in the website repository.
3. Not covered by the check, by decision or omission: operator edges on product nodes
   (`PatternBuilder`), whose arguments are nodes rather than expressions, so a constant
   argument node of the wrong user type still yields an error value; and variables
   whose class could be derived from an assignment or a typed node.

## Hazards

- Branch `user-ops-package-wildcard` (gh #939, b3e503143, not merged into `release-7`)
  also changes `UserSignature`, mostly `toClasses` and new class-path scanning; expect a
  small textual conflict around `setUserClass`/`checkUserClass`, whichever merges second.
- ecj skips null analysis of a file with a compile error: rerun after fixing one.
- Both branches add a "Release 7.7.2, unreleased" section to CHANGES: a certain,
  trivial conflict.
