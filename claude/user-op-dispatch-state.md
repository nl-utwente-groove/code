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
3. Not covered by the check: see the next section. Not to be extended on this branch.

## Beyond this branch: typing of user values (gh #944)

Decided 2026-11-14 with Arend: the check of this branch stays as it is, local to one
expression and with a class that is either one known class or unknown. Typing user
values more fundamentally is a concern of its own, filed as gh #944, for a separate session.
What this branch leaves open, and the conclusions so far (a scoping, not a design; the
points marked unverified were not checked against the code):

What the check does not see:
- Operator edges on product nodes drawn by hand. `AspectNode` compares sorts only and
  `PatternBuilder` builds the operator node; no expression is parsed. Expressions written
  as text are checked first and normalised into product nodes afterwards
  (`NormalAspectGraph.addCall`).
- A call whose implementations disagree on the result: `count(push(x,1))` is accepted
  although `push` yields a stack or a queue and `count` exists for neither
  (`UserCallCheckTest.testUnknownClass` pins this).
- Variables and fields, always.

What can be inferred without new syntax:
- A set of possible classes per expression rather than one class or unknown; a call is
  rejected if no class of the set has an accepting implementation. Small (about 30
  lines in `getUserClass`/`checkCall`), sound, closes the second gap only.
- Per rule, over the normalised graph: every `user` node gets the classes its producers
  can yield (constant, target of an operator edge) intersected with the classes its
  uses require (receiver or argument of an operator edge); an empty set is an error.
  This covers product nodes and variables used more than once, and subsumes the
  previous point for rules. Expressions outside rules (control arguments) still need
  the expression-level check.
- The limit of both: a value read from an attribute of the host graph or passed in as
  a rule parameter is unconstrained. A whole-grammar flow analysis (which classes are
  ever written to a field) is possible but not modular, and reports errors far from
  their cause; explicit declarations serve better there.

Explicit specification:
- A sort per user class is not feasible short of redoing the sort system: `Sort` is an
  enum, used as such throughout, whereas user classes are loaded per grammar.
- Feasible: an optional class name refining `Sort.USER`, kept as a name and resolved
  against `UserSignature` on demand (as `getUserClass` does, because the class loader
  is replaced on reload). Places that would carry it: attribute declarations in the
  type graph, variable nodes in rules, control variables and parameters (`var_type` in
  `Ctrl.g`, `CtrlType`), `SortMap` for the variables of an expression.
- Open: concrete syntax (whether `user:Name` without arguments is free in node labels
  is unverified); whether it needs a new grammar version; whether a declaration is also
  enforced on host graph values at run time; and how subclasses or interfaces among
  user types relate to dispatch, which is on the exact class.

## Hazards

- Branch `user-ops-package-wildcard` (gh #939, b3e503143, not merged into `release-7`)
  also changes `UserSignature`, mostly `toClasses` and new class-path scanning; expect a
  small textual conflict around `setUserClass`/`checkUserClass`, whichever merges second.
- ecj skips null analysis of a file with a compile error: rerun after fixing one.
- Both branches add a "Release 7.7.2, unreleased" section to CHANGES: a certain,
  trivial conflict.
