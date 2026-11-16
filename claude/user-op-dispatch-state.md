# Shared user operations through user interfaces (gh #933): handoff state

Branch `user-op-dispatch`, off `release-7` (7.7.2-SNAPSHOT) and merged with it on
2026-11-16. Transient: delete when merged.

The last two commits (2026-11-16) came in through a branch `user-seq-of-interface`,
since merged: the nullness errors that Eclipse reported on the record fixtures, and
`UserSeq` of a user interface.

## Goal

Let several user types have fields and operations of the same name, with discipline:
a name is shared only by user types that implement a `@UserInterface` which declares it.

## History

Slices 1 and 2 (983b3799d, b3766eba1) let any instance members of user types share a
name if they agreed on sorts, with a list of implementations per operator and a
dispatch on the class of the first argument written by hand. Arend reconsidered on
2026-11-16: that imposes no discipline on equally named operations. Slice 3 replaces
it by user interfaces. What survives of slices 1 and 2: `UserSignature.Impl` (now one
per operator), the accessor kind living there instead of in `Operator`, and the
compile-time check with its hook in `ExprTree.toCallExprs`.

## Design

- **`@UserInterface`** (package `annotation`) marks a public Java interface. Its
  non-static `@UserOperation` methods, abstract or default, are operations; the
  operator is built from the interface method with the interface as receiver.
- **Java dispatches.** `AlgebraFamily.UserOperation` holds one `Impl` and invokes the
  interface method on the first argument. `checkReceiver` only produces the message
  for a value of a type that does not implement the interface.
- **Sharing rule** (`UserSignature.implementsInterfaceOp`): a record component or
  method of a user type, or a method of a user interface, whose name is that of an
  operation of a user interface above it is no operator of its own. It has to override
  the declaring method (`overrides`: same arity, parameter and return types the same
  or narrower, which covers generic interfaces), only one member per type may do so,
  and an annotation on it has to agree on `indeterminate`. Any other namesake is a
  duplicate (`addOperator`), as on `release-7`.
- **The implementing method needs no `@UserOperation`.** Bridge methods are skipped
  when a class is scanned: the compiler copies the annotations of an override with a
  narrower return or parameter type to its bridge.
- **Sequence-valued fields** (Arend, 2026-11-16: under the same rule). A parameterless
  `@UserOperation` method of a user interface with a system sequence type declares the
  field: it yields `cAt` and `cSize` and reserves `c`. An implementor need not
  implement it by a record component.
- **Interfaces are discovered** through the `implements`/`extends` clauses of the
  listed classes (`addUserInterfaces`); listing them is allowed. They may extend one
  another; an operation belongs to the topmost interface that declares it
  (`findInterfaceOp`).
- **`Sort.toSort` maps a user interface to `user`**, so it can be a parameter or result
  type.
- **A `UserSeq` tail can have a user interface as element type** (Arend, 2026-11-16:
  a user expects the supertype to work there). `UserSeq<T>` lost its bound
  `T extends Record`, since a Java bound cannot say "record or interface"; that a
  `UserSeq` is not one of a system sort is now checked in `Operator.getTailType`,
  which also takes the raw type of `UserSeq<I<?>>` for a generic interface. A type
  variable as element type is still rejected. The price: the erased signatures of
  `UserSeq` changed (`get` returned `Record`, now `Object`), so user classes compiled
  against 7.7.0 or 7.7.1 that use `UserSeq` fail to link until recompiled; this is in
  CHANGES.
- **The compile-time check works with upper bounds.** `getUserClass` yields the return
  type of the implementing method, which may be an interface; `isCompatible` accepts
  an argument unless no loaded user type can be both what is required and what the
  argument is known to be. With a known first argument, `Impl.refine` takes the
  overriding method of that class, for its narrower result and (for a generic
  interface) parameter types. So `count(push(x,1))` is now rejected, and
  `sameItems(Stack(..), Queue(..))` is rejected for a self-typed interface.
- Static operations and constructors are unchanged: sole owners of their name.

## Gates (2026-11-16)

Default suite 894 run, 0 failures, 2 skipped (`GrammarsTest`,
which skips in a worktree), counted over the reports written by that run. The 918
reported for slice 3 was counted over all files in `target/surefire-reports`, which
included stale reports of slow and GUI tests; the comparable figure there is 891.
ecj null analysis on the touched files, with 3.42.0 (the harness version) and with
3.44.0: no errors, no warnings; 3.44.0 over the whole test tree: the four task tags of
the baseline only. No GUI tests: nothing under `gui/` is touched.

After the merge of `null-check-ecj-3_44` (the harness at ecj 3.44.0, and with it
`release-7` up to the design note of gh #944): the whole-project check gives the
baseline of 14 problems, none of them in the code of gh #933. The merge brought no
Java changes, so the tests were not rerun.

## Not done

1. End-to-end grammar test, if wanted (a new `junit/rules` grammar needs a
   `RuleCompilationTest` golden).
2. Web manual section (on the website-updates list).
3. The gh #944 design note (`claude/user-value-typing.md`) assumes that there is no
   subtyping among user types. That no longer holds: revise it before deciding on it.

## Hazards

- ecj skips null analysis of a file with a compile error: rerun after fixing one.
- `Class.getDeclaredMethods` has no defined order; error messages that depend on which
  of two clashing methods is seen first can differ between runs.
