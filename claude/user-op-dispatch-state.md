# Shared user operations through user interfaces (gh #933): handoff state

Branch `user-op-dispatch`, off `release-7` (7.7.2-SNAPSHOT) and merged with it on
2026-11-16. Transient: delete when merged.

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
  type. A `UserSeq` tail still needs a record as element type (`T extends Record`).
- **The compile-time check works with upper bounds.** `getUserClass` yields the return
  type of the implementing method, which may be an interface; `isCompatible` accepts
  an argument unless no loaded user type can be both what is required and what the
  argument is known to be. With a known first argument, `Impl.refine` takes the
  overriding method of that class, for its narrower result and (for a generic
  interface) parameter types. So `count(push(x,1))` is now rejected, and
  `sameItems(Stack(..), Queue(..))` is rejected for a self-typed interface.
- Static operations and constructors are unchanged: sole owners of their name.

## Gates (2026-11-16)

Default suite 918 run, 0 failures, 2 skipped (`GrammarsTest`, which skips in a
worktree). ecj null analysis on the 28 touched files: no errors; the two warnings it
found in `UserSignature` were fixed and the files rechecked clean. The algebra and
rule application tests were rerun after those fixes, not the whole suite. No GUI
tests: nothing under `gui/` is touched.

## Not done

1. End-to-end grammar test, if wanted (a new `junit/rules` grammar needs a
   `RuleCompilationTest` golden).
2. Web manual section (on the website-updates list).
3. `UserSeq` of a user interface as tail type: needs the bound of `UserSeq` relaxed.
4. The gh #944 design note (branch `user-value-typing`) assumes that there is no
   subtyping among user types. That no longer holds: revise it before deciding on it.

## Hazards

- ecj skips null analysis of a file with a compile error: rerun after fixing one.
- `Class.getDeclaredMethods` has no defined order; error messages that depend on which
  of two clashing methods is seen first can differ between runs.
