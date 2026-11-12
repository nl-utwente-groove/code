# Sequence-valued components of user types (gh #931, first part): proposal

Proposal of 2026-11-11; decisions 1, 2 and 5 were taken by Arend on the same day and are
recorded as such. The proposal itself was merged into `multi-valued-operators` (the second
part, see [multi-valued-operators-state.md](multi-valued-operators-state.md)) on 2026-11-12.
The implementation is on a new branch `sequence-components` off that merge (bffb7791d): the
two parts touch the same files (`Operator`, `UserSignature`, `AlgebraFamily`), and the
fixtures of this part use `range`.

**State (2026-11-12):** slice 1 done; slices 2 to 5 open. The sequence parameter of a
`@UserOperation` (decision 3) is not yet confirmed by Arend and belongs to slice 2.

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

## Decisions

1. **Carrier: four GROOVE-provided final classes** `IntSeq`, `RealSeq`, `BoolSeq`,
   `StringSeq` (decided). Primitive array storage, no mutators, content-based
   `equals`, cached content-based hash (house pattern of `ANode`), defensive copies in
   `of(...)` and `toArray()`. Operations for user code: `of`, `size`, `get`, `with(i, v)`,
   `append(v)`, `toArray`. A common sealed interface `Seq` (`size()`, boxed `value(i)`)
   serves GROOVE's own generic code. The record check is then
   `rc.getType() == IntSeq.class`: no generic reflection, nothing a raw type can subvert.
   Rejected: one generic `Seq<T>`. It would read consistently with the `Set<Integer>` of
   part 2, but a raw-typed `Seq` built in a user operation can hold elements of the wrong
   type or `null`, which only shows at the point of use.
2. **Package `nl.utwente.groove.annotation`** (decided): user classes then import from one
   package only, and that package may one day be released as a component of its own.
   Consequence: the carrier classes depend on nothing outside `annotation` and the JDK. In
   particular `Seq` does not expose a `Sort` (`LayeringTest` puts `annotation` below
   `algebra` anyway); the mapping from carrier class to element sort lives in `algebra`,
   next to `Sort.toSort(Type)`. Today the package is not free of such dependencies
   (`Help` uses `util.HTMLConverter`, `Pair`, `Exceptions`; `OpSymbol` uses
   `util.parse.OpKind`), so a separate release needs more than this.
3. **A sequence type is allowed in last parameter position only**: as the last component of
   a record (issue), and likewise as the last parameter of a `@UserOperation`, which thereby
   takes a variable number of trailing arguments, `horner(x, 1, 2, 3)` for
   `static int horner(int x, IntSeq coeffs)`. The first draft excluded operations without a
   reason: constructors and methods go through the same `Operator(Executable)` and the same
   `UserOperation.invoke`, so the tail of decision 4 covers both at no extra cost.
   The position rule is what makes the flattened syntax unambiguous without a sequence
   literal, and so without sequence-typed expressions. What stays impossible follows from
   that: a sequence is not a sort, so no node or expression holds one. An operation cannot
   *return* a sequence (only a record containing one), and a sequence parameter is fed by
   listing the elements, never by handing over another record's component; an operation
   that needs the component of `p` as a whole is an instance method of the record, where
   the Java code sees the `IntSeq`.
   This is the explicit, ordered, same-level kind of variable argument. gh #930 (a user
   operation folding the values of a quantified level) is the other kind and has to mark
   its parameter differently; keeping `IntSeq` for the explicit tail leaves that open.
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
   overloading by arity. The suffix `Size` is decided (`coeffsSize`).
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
   receiver unchanged, `get` out of range throws. *Done* (`annotation.Seq` and the four
   classes, `test.annotation.SeqTest`). Settled while writing: reals compare as
   `Double.equals` does (`NaN` equals itself, `0.0` differs from `-0.0`), which is how a
   record compares a `double` component; `StringSeq` rejects `null` elements at run time,
   since user code is not null-checked; the classes carry `util.AIGenerated`, a class-file
   reference outside the package but no run-time dependency.
2. **Tail operators, record check, constants.** `Operator.hasTail`/`getParamSort`, the arity
   sites of decision 4, `UserSignature.parseUserClass` (sequence component accepted in last
   position only, as is a sequence parameter of an operation), packing of the tail on
   invocation, printing. Tests: `UserSignatureTest` (operator shape; an operation with a
   sequence parameter; load failures for a sequence that is not last, two sequences, a
   sequence as operation result), `ExpressionTest`-level parse and print round trips
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

The three marked "reproduced" were run on this branch's base on 2026-11-11.

- gh #936, reproduced: `UserAlgebra.toValidSymbol` quotes string components without
  escaping, so a value holding a `"` prints as a symbol that does not parse back (a lone
  `\` happens to survive; the `toValidTerm` route escapes correctly). `StringSeq` elements
  will go through the same code.
- gh #934 (filed earlier): name clashes between user operators of *different* classes are
  not detected, the later class silently wins. The generated `cSize` names make a clash
  slightly more likely.
- gh #935: `CallExpr.isUserConstant` assigns its result twice, losing the constructor test.
  No effect at present, as the method has no callers.
- gh #937, reproduced: `POINT` is admitted with user operations by the grammar-property
  checker, but no `user` algebra is registered for it; a host graph or rule with a user
  value fails with a `NullPointerException`.
- Not filed, reproduced: a constructor call with more arguments than components, all of
  them constants (`UserTypeIntBool(1,true,3)`), throws `ArrayIndexOutOfBoundsException`
  from `ExprTree.addArg` instead of a `FormatException`: the array `userConstArgs` is
  sized by the arity and written before `checkCall` counts the arguments. With a
  non-constant argument the array is dropped first and the message is the proper one.
  Left to slice 2 by decision (2026-11-12): it replaces the array by a list, which removes
  the overrun, and adds the all-constant case to its parse tests as the regression test.
