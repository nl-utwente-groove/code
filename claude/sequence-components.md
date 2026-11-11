# Sequence-valued components of user types (gh #931, first part): proposal

Proposal of 2026-11-11, nothing implemented. Branch `sequence-components`, stacked on
`multi-valued-operators` (the second part, awaiting review, see
[multi-valued-operators-state.md](multi-valued-operators-state.md)): the two parts touch the
same files (`Operator`, `UserSignature`, `AlgebraFamily`), and the fixtures of this part use
`range`. Part 2 is merged first; if its review changes it, this branch is rebased.

## Goal

A `@UserType` record may end in one component that holds a variable number of values of one
system sort. Rules can construct such a value, read an element by position, and read the
length; quantification over the elements is `range` over the positions (part 2), as decided
there. Running example:

```java
@UserType
public record Poly(String name, IntSeq coeffs) {}
```

| Rule-side form | Meaning |
|---|---|
| `user:Poly("p", 1, 2, 3)` | constant, sequence written as a flattened tail |
| `Poly(n, a, b)` | constructor call: fixed prefix, any number of tail arguments |
| `coeffs(p, i)` | element at position `i`, counting from 0; error value if out of range |
| `coeffsSize(p)` | number of elements |

## Decisions proposed

1. **Carrier: four GROOVE-provided final classes** `IntSeq`, `RealSeq`, `BoolSeq`,
   `StringSeq`, as the issue prefers. Primitive array storage, no mutators, content-based
   `equals`, cached content-based hash (house pattern of `ANode`), defensive copies in
   `of(...)` and `toArray()`. Operations for user code: `of`, `size`, `get`, `with(i, v)`,
   `append(v)`, `toArray`. A common sealed interface `Seq` (`size()`, boxed `value(i)`,
   element `Sort`) serves GROOVE's own generic code. The record check is then
   `rc.getType() == IntSeq.class`: no generic reflection, nothing a raw type can subvert.
   Alternative, **for Arend to decide**: one generic `Seq<T>`. New argument in its favour
   since the issue was written: part 2 made `Set<Integer>` the type of set-valued
   operations, so `Seq<Integer>` would read consistently, and values are boxed on GROOVE's
   side anyway. Against: a raw-typed `Seq` built in a user operation can hold elements of
   the wrong type or `null`, which only shows at the point of use; four small classes cost
   some repetition but exclude that by construction. Recommendation: stay with four.
2. **Package `nl.utwente.groove.algebra`**, next to `IntInterval`. It is exported, and it is
   where `UserOpsExample` lives. `annotation` (the issue's suggestion) is exported too but
   would get value classes under a name that promises annotations. Minor; either works.
3. **At most one sequence component, in last position** (issue). It is what makes the
   flattened constant and constructor syntax unambiguous without a sequence literal, and so
   without sequence-typed expressions. A sequence type anywhere else, in particular as a
   parameter or return type of a `@UserOperation`, stays an error ("cannot be converted to
   GROOVE sort", already raised by `Operator.toSort`): sequences exist only inside records.
4. **The constructor tail is a new operator property, separate from `isVarArgs`.** Call it
   `Operator.hasTail()`: the last parameter sort is the element sort, and
   `allowsArgCount(n)` is `n >= getArity() - 1`. A positional `getParamSort(i)` (clamped to
   the last sort for a tail operator) replaces `getParamSorts().get(i)` at the sites that
   index by argument position.
   This corrects the issue text, which expected every `isVarArgs` site to need a
   distinction. The survey shows the opposite split. The `isVarArgs` sites
   (`NormalAspectGraph.addCall`, `TypeGraph`, `PatternBuilder.addOperator`,
   `LevelDistribution`, `OperatorNode.isSetOperator`, `Condition`,
   `OperatorNodeSearchItem`) all encode "one argument, filled from a universal sub-level";
   the tail arguments are ordinary same-level arguments, so with a separate flag these
   sites stay untouched (`PatternBuilder` and the search item take the argument list as it
   comes, without an arity check). What changes is the *arity* sites:
   - `Operator.allowsArgCount`, `util.parse.Op.allowsArgCount`, `ExprTree.ExprOp` (carries
     the flags);
   - `ExprTree.newCallExpr` and `ExprTreeParser.checkCall` (sort per argument);
   - `ExprTree.userConstArgs` (an array sized by the arity; becomes a list);
   - `CallExpr.isTypeCorrect`;
   - `AspectNode` product-node check (`opSignature.equals(signature)`);
   - `AlgebraFamily.UserOperation.invoke` (packs the tail into the carrier before
     `newInstance`);
   - `UserAlgebra.toValidSymbol` and `toValidTerm` (flatten the tail);
   - `Operator.toString` (`user:Poly(string,int...)`, visible in the rule-compilation
     goldens) and `Algebras.createUserHelp`/`computeOpDocMap`.
5. **Generated accessors, backed by the record's own accessor method.** For a sequence
   component `c` two operators are registered: `c : (user, int) -> elem` and
   `cSize : (user) -> int`. Both map to `rc.getAccessor()` in
   `UserSignature.getMethods()`, so the `Map<Operator,Executable>` shape and its consumers
   (`checkUserClass`, the grammar-property checker, the help) are unchanged; the operator
   itself records which of the two it is, and `UserOperation.invoke` applies `value(i)` or
   `size()` to the accessor's result. An index out of range must be turned into the error
   value there: `Operation.apply` catches only reflective and `IllegalArgument` exceptions.
   Rejected: changing the registry's value type to an operation abstraction of its own
   (wider ripple for two operators); leaving the accessors to the user as `@UserOperation`
   methods (works with no mechanism, but then a sequence component is unreachable by
   default and every record names them differently).
   The name `cSize` is forced to be derived: user operators are keyed by name, without
   overloading by arity. **Open: the suffix** (`coeffsSize`; `coeffsLength`, `sizeOfCoeffs`
   are the alternatives). Cheap to change before 7.7.0, not after.
6. **No element enumerator.** The issue's `coeffsElem(p)` would be a set-valued operator,
   and since part 2 decided on sets it would silently drop repeated elements. `range` over
   the positions is the idiom; nothing is added.
7. **Algebra families: nothing new.** The generated operators are user operations and
   inherit their status: DEFAULT works, TERM and BIG are rejected by
   `GrammarKey.UserOperationsChecker`.

## Slices

Each is one or a few commits, mergeable on its own.

1. **Carrier classes** with unit tests: equality and hash by content, hash equal across
   instances and cached, `of(array)` and `toArray()` copy, `with`/`append` leave the
   receiver unchanged, `get` out of range throws.
2. **Tail operators, record check, constants.** `Operator.hasTail`/`getParamSort`, the arity
   sites of decision 4, `UserSignature.parseUserClass` (sequence component accepted in last
   position only), constructor packing, printing. Tests: `UserSignatureTest` (operator
   shape; load failures for a sequence that is not last, two sequences, a sequence as
   operation parameter or result), `ExpressionTest`-level parse and print round trips
   including the empty tail (`Poly("p")`) and a sequence-only record (`Bag()`).
3. **Generated accessors**, with clash detection against the record's other operators and
   the out-of-range error value.
4. **Fixtures.** An example record in `algebra/` next to `UserTypeIntBool`, with
   user-written `append`; rules in `junit/rules/userOps.gps` for constant, constructor,
   element, size, out of range, `forall` over `range(coeffsSize(p))`, and `sum` over the
   elements; golden in `junit/rulecompilation`. A small sample grammar in which sequences
   grow along two confluent routes as a `DeterminismTest` row: it shows that equal
   sequences collapse to one state.
5. **Documentation.** `@UserType` javadoc; the "Release 7.7.0, forthcoming" section of
   `CHANGES.md`; a manual section in `manual/data.md` on a website branch stacked on
   `set-valued-operators`, including the correction of the limitation "nor of an array or
   list type".

Gates before handover: `null-check`, `determinism-check`, `grammar-smoke`, full suite. No
`gui/` file is expected to change (the help is built in `algebra.Algebras`); if one does,
the GUI tests apply.

## Risks and things not verified

- That host value nodes of sort `user` are identified through `equals` of the Java value
  was not checked in the code; existing records could not work otherwise. Slice 4's
  confluence grammar tests it.
- `with` and `append` copy the array, so a rule that extends a sequence costs time linear
  in its length per application. Acceptable for the intended sizes; not measured.
- A long sequence makes a long value-node label in the GUI. Not addressed.
- Whether `Bag()` (a zero-argument constructor call) passes the parser is expected, since
  the zero-argument user operation `one()` exists, but untested for constants.

## Found in passing, not part of this work

- `UserAlgebra.toValidSymbol` quotes string components without escaping, so a value holding
  a `"` or `\` prints as a symbol that does not parse back. `StringSeq` elements go through
  the same code; fix there as a separate commit, or file it.
- Name clashes between user operators of *different* classes are not detected:
  `setUserClass` and `checkUserClass` merge the per-class maps with `putAll`, so the later
  class silently wins, although the manual states that the names must be distinct. The
  generated `cSize` names make a clash slightly more likely.
- `CallExpr.isUserConstant` assigns its result twice, losing the constructor test.
- `POINT` is admitted with user operations by the grammar-property checker, but no `user`
  algebra is registered for it; by reading, a `user`-sorted expression would fail there.
  Not reproduced.
- More constant arguments than components in a constructor call (`UserTypeIntBool(1,true,3)`)
  should overrun `ExprTree.userConstArgs` before the count check. Not reproduced; slice 2
  removes the array.
