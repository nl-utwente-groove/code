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
  (the permissive variant). Implemented on my advice, *not yet confirmed by Arend*, who
  leaned towards requiring the same classes at first. Reasons: binary operations on the
  own type (`Vec2.plus(Vec2)` next to `Vec3.plus(Vec3)`) need it, results need it anyway
  (`push` returns the own type), and the receiver alone determines the implementation.
  If he prefers the restrictive variant: one more check in `UserSignature.addOperator`,
  comparing the parameter classes of the executables, plus a fixture and an error message.
- The accessor kind moved from `Operator` to `UserSignature.Impl`.
- The compile-time check is part of this branch (Arend, 2026-11-13).

## Done

- Slice 1 (983b3799d): `UserSignature.Impl` and `getImplementations()`, consistency
  checks in `addOperator`, dispatch in `AlgebraFamily.UserOperation.getImpl`, operator
  help with all declaring classes, fixtures `UserTypeStack`/`Queue`/`Counter`/
  `SignatureClash` and `UserOpsStaticClash`, tests in `UserSignatureTest`.
  Gates: default suite 877 run, 0 failures, 2 skipped; ecj clean on the touched files.

## Next

1. Compile-time check. Every implementation has a concrete Java return class, so the class
   of a `user`-sorted call expression is known when its candidate implementations agree
   on it; constructor calls and user constants always have one; variables and fields do
   not. In `ExprTree.newCallExpr`, infer the class of each `user`-sorted argument where
   possible, filter the implementations on the receiver (and check the other `user`
   parameters of the remaining ones), and throw a `FormatException` if none is left. This
   turns latent error values in existing grammars into load errors; say so in CHANGES.
   Open: where the inferred class lives (a nullable property of `Expression`, or computed
   on demand from `UserSignature.getImplementations()`).
2. CHANGES line (7.8.0), and an end-to-end grammar test if wanted (a new `junit/rules`
   grammar needs a `RuleCompilationTest` golden).
3. Web manual section, in the website repository.

## Hazards

- Branch `user-ops-package-wildcard` (gh #939, b3e503143, not merged into `release-7`)
  also changes `UserSignature`, mostly `toClasses` and new class-path scanning; expect a
  small textual conflict around `setUserClass`/`checkUserClass`, whichever merges second.
- ecj skips null analysis of a file with a compile error: rerun after fixing one.
