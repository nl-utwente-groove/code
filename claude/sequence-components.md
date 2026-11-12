# Sequence-valued components of user types (gh #931, first part): proposal

Proposal of 2026-11-11; decisions 1, 2 and 5 were taken by Arend on the same day and are
recorded as such. The proposal itself was merged into `multi-valued-operators` (the second
part, see [multi-valued-operators-state.md](multi-valued-operators-state.md)) on 2026-11-12.
The implementation is on a new branch `sequence-components` off that merge (bffb7791d): the
two parts touch the same files (`Operator`, `UserSignature`, `AlgebraFamily`), and the
fixtures of this part use `range`.

**State (2026-11-12):** slice 1 done and reviewed, with `Seq` made generic in its element
type on Arend's remark; slices 2, 3 and 4 done (2026-11-12); slice 5 open. The
sequence parameter of a `@UserOperation` (decision 3) was confirmed by Arend.

**Handoff (2026-11-12, end of session).** Start here in a fresh session.
- *Branches.* `sequence-components` is at 8a8d8675a: slices 1 to 4, the move of the
  user-class fixtures to `test.algebra.user`, and (merged by Arend on 2026-11-12) the
  one-enumerator restriction, the fix of the collection operator over a node identifier
  and the element-of rendering. On top of it, `enumeration-membership`, with the tests
  and open questions of the item "Enumerations that are read" below; the worktree
  `.claude/worktrees/sequence-components` is detached at its tip.
- *Element-of rendering: done* (2026-11-12), on branch `element-of-rendering`, stacked
  on `single-enumerator-per-expression`, two commits. The syntax is unchanged; where a
  right-hand side is an enumeration, the display has the element-of sign (U+2208,
  `Unicode.ELEMENT_OF`):
  - "is an enumeration" means "contains an enumerator call" (Arend, 2026-11-12), so
    `lo + range(n)` is one. It is `Expression.isEnumeration`, on top of
    `Expression.getEnumeratorCount`, which replaces the private count of
    `NormalAspectGraph` (test: `ExpressionTest.testEnumerations`);
  - node form (`gui/look/LabelValue.getDataLine`): `x : int = range(a.n)` became
    `x : int ∈ range(a.n)`. A value node without an identifier shows the bare
    expression, as before;
  - `let`-edge form (`AspectEdge.getAssignLine`): `x → range(a.n)` became
    `x ∈ range(a.n)`, with the role prefix and colour unchanged. Arend asked what such
    an edge means and whether it should be forbidden. It is the main use of
    enumerators, in fixtures since phase 1 of the second part: on an existential level
    a choice, one transition per element (`pick`, `span` in `enumerators.gps`); under
    `forall:` on a creator node, one new node per element (`spawn`, and `fill` in
    `range-cells.gps`). So no restriction was added. That the arrow is replaced, and
    not kept in front of the sign, is a proposal: Arend has not seen it;
  - a `test`-edge that compares a field of its own node with an enumeration
    (`test:x == range(n)`) is displayed like an assignment and gets the sign too: it is
    a membership test. A test in any other form (`a.x == range(n)`, `range(n) > lo`)
    keeps its operator; not asked;
  - test of the edge forms: `EnumeratorExpressionTest.testDisplay`. The node form has
    no test that runs without a display; it was checked in Imager output of `totalVar`,
    `pick` and `span`;
  - gates on the tip: full suite without GUI tests (890 run, 2 skipped in
    `GrammarsTest`; three more than the two added tests account for, not looked
    into), GUI tests (15 run in 4 classes, none skipped), null-check pass. The
    yFiles backend was not built against it: this line has no backend.
- *Enumerations that are read* (asked by Arend, 2026-11-12; checked by match counts,
  `EnumeratorMembershipTest`). An enumeration that is read stands for some element,
  also under a universal quantifier, which ranges over the nodes for which some element
  qualifies:
  - a reader edge to an enumeration node is a membership test, and such a node as an
    embargo tests that the value is no element. Both are still displayed with an arrow
    (`x → range(a.n)`, in `AspectEdge.toLine`), not with the element-of sign;
  - `test:x == range(n)` is membership. A test with another operator holds if it holds
    for some element (`range(n) > x`), so `test:x != range(n)` means "some element
    differs from `x`", not "`x` is no element";
  - `not:let` does not exist ("Conflicting aspects").
  Open, for Arend to decide; nothing of it is built:
  - an embargo edge to an enumeration node that is a reader means "some element is not
    the value", as for any embargo edge between readers, and is displayed like the
    embargo node. Proposed: an error that asks to make the node an embargo too; the
    alternative is to pull such a node into the embargo silently;
  - **`not:test` negates twice, also without an enumerator and also in release 7.6.1**:
    `not:test:x == 1` is found exactly where `x` is 1. `addNestedNode` makes the test
    structure an embargo (since 176495578, 2024-02) and the `test`-edge case of
    `doNormalise` requires the outcome `!nac`, so false. With an enumerator the result
    is "holds for all elements", and the display `! x ∈ range(n)` is wrong for it.
    Proposed fix, as a bug of its own off the release line: outcome `true` inside the
    embargo, which for an enumeration gives "holds for no element". Not filed;
  - display of a test that calls an enumerator. Proposed: name the element,
    `∃ i ∈ range(a.n) : i > x`, with `∄` for the negated test once that is
    fixed; `x ∈ range(a.n)` stays for the equality with a field. Alternative: allow
    an enumerator in a `test`-edge only in that equality, and have the rule name the
    element as a node for anything else.
- *Next: slice 5* (documentation), now also covering the one-call restriction, the
  named-variable form of the sum (`coeffSumVar`, which replaces the product-node form
  as the one to show) and the element-of rendering. The website branch
  `set-valued-operators` has none of these yet.
- *Open, not filed:* nothing restricts several enumerators that are written as product
  nodes by hand; Arend was told and has not asked for it.

**Sequences as sorts are a later extension (gh #942), decided 2026-11-12.** Making the
sequence types real sorts (`intseq` and so on) was weighed against the design below, in
which a sequence is not a sort. The outcome: this design goes ahead, shaped so that sorts
can be added without breaking grammars. Rules to keep while implementing:
- the tail is defined as "the last parameter is a sequence type, the call lists its
  elements"; with sorts, the same operator would also accept one sequence-sorted argument;
- nothing user-visible may assume that a sequence is not a sort; the one name that sorts
  would want, the bare component name, stays free (decision 5).

**`UserSeq<T>`, decided 2026-11-12:** a fifth carrier, for sequences of user-type values,
allowed as the last parameter of a `@UserOperation` only. Not as a record component: Arend
rules out user-typed components of user types altogether. Arend also named it as a result
type, but a result needs a sort to live in, so that waits for gh #942 like any other
sequence result.

**User operations with several results, decided 2026-11-12:** the `Set` results of the
second part are withdrawn (see multi-valued-operators-state.md) and no sequence result
takes their place before gh #942. `range` stays, so the slices below are unchanged. An
operation that needs several results returns a record with a sequence component.

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
| `coeffsAt(p, i)` | element at position `i`, counting from 0; error value if out of range |
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
   component `c` two operators are registered: `cAt : (user, int) -> elem` and
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
   Both names are derived (decided 2026-11-12: `coeffsAt`, `coeffsSize`), and the bare name
   `c` is reserved: no operator of that name is generated and a user operation may not
   take it, so that a later sequence sort can make `c(p)` yield the sequence (gh #942).
   A derived name is forced for the size anyway: user operators are keyed by name, without
   overloading by arity. The suffix `Size` is decided (`coeffsSize`).
6. **No element enumerator.** The issue's `coeffsElem(p)` would be an enumerator that
   yields a set, like `range`, and so would drop repeated elements. `range` over the
   positions is the idiom; nothing is added. An enumerator that keeps repeated elements
   needs the position in the match and is part of gh #942 (decided 2026-11-12).
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
   *Done*, with `UserSeq<T>` as a fifth carrier for operation parameters (tests:
   `UserSignatureTest.testSequenceTail`/`testSequenceErrors`, `SequenceTailTest`; fixtures
   `UserTypePoly`, `UserTypeBag`, `UserOpsSeq` and three erroneous ones in `test/algebra/user`).
   As implemented:
   - the arity of a tail operator counts the tail as one parameter;
     `Operator.getParamSort(i)` and `getParamSorts(argCount)` give the sorts per argument,
     and `Operator.getTailType(Executable)` is the one place that maps a sequence class to
     its element type (for `UserSeq<T>`, the type argument);
   - the tail is packed in `AlgebraFamily.UserOperation.packTail`; a user value of another
     type than `T` gives an error value there, since all user types share one sort;
   - a sequence-valued field has no accessor operator until slice 3;
   - under JDT null analysis, a class that names `UserSeq<SomeType>` has to be
     `@NonNullByDefault` (or annotate the type argument): the type parameter of `UserSeq`
     has a non-null bound, and an unannotated argument is an error, not a warning. This
     only concerns code compiled with that analysis, such as the fixtures;
   - the help marks the tail parameter with a star (`horner(x,coeffs*)`), because the
     syntax text of `Help` deletes dots;
   - the overrun of `ExprTree.userConstArgs` (see the end of this note) is gone, with
     `SequenceTailTest.testConstantErrors` as its regression test.
   Not covered yet: tail operators in rules. The route from aspect graphs to operator
   nodes was not run with a tail operator; slice 4's fixtures are the first to do so.
3. **Generated accessors**, with clash detection against the record's other operators and
   the out-of-range error value. *Done* (tests: `UserSignatureTest.testSequenceAccessors`/
   `testSequenceAccessorClashes`, `SequenceTailTest.testAccessors`/`testAccessorErrors`;
   fixtures `UserTypeTags`, `UserTypeSeqClash`, `UserOpsSeqName`). As implemented:
   - `Operator.SeqAccessor` (`AT`, `SIZE`) names the two accessors and derives their
     operator names; `Operator.getSeqAccessor()` is non-null for a generated operator, and
     `AlgebraFamily.UserOperation.accessSeq` evaluates it on the result of the field's
     accessor method. `Operator.getSeqElementType(Class)` now is the one place that maps
     a system-sort sequence class to its element type; `getTailType` builds on it;
   - a position out of range, a value of another user type and a field without a value
     (`null`, possible from user code) all give an error value;
   - a clash with a generated name is reported by the existing duplicate check, which
     names the field in its message; it works in both orders (another field first, an
     annotated method last) and across classes;
   - the reserved field name is checked after all classes are parsed
     (`UserSignature.parseUserClasses`), since the operation that takes the name may be
     in a class listed before the record. It also catches a plain field of that name in
     another user type;
   - two user types with a sequence field of the same name clash on the generated
     names, as two plain fields of the same name do (gh #934);
   - the generated accessors have no entry in the operator help, like the accessors of
     plain fields (`Algebras.createUserHelp` only documents constructors and annotated
     methods). Open for slice 5: whether to document them, their names being derived.
4. **Fixtures.** An example record in `algebra/` next to `UserTypeIntBool`, with
   user-written `append`; rules in `junit/rules/userOps.gps` for constant, constructor,
   element, size, out of range, `forall` over `range(coeffsSize(p))`, and `sum` over the
   elements; golden in `junit/rulecompilation`. A small sample grammar in which sequences
   grow along two confluent routes as a `DeterminismTest` row: it shows that equal
   sequences collapse to one state.
   *Done* (2026-11-12), without a change to the main tree. As implemented:
   - the record is the existing `test.algebra.user.UserTypePoly`, with `append` added, and not
     a new class in the main `algebra` package: Arend asked on the same day to get the
     example classes out of the main tree (see the end of this note).
     `userOps.gps` and the sample name the class in the test tree, so they load only
     where the test classes are on the class path: Maven and the JUnit launches, but not
     `Launch Simulator`, which excludes test code;
   - rules in `junit/rules/userOps.gps`: `polyConst` (constant in a rule and in host
     graphs, also with an empty tail), `polyCreate` (constructor call with a tail of two
     and with an empty one), `polyProd` (the same call as an explicit product node),
     `coeffsAt` (in range, and two out-of-range starts that give an error value),
     `coeffsSize`, `append`, `coeffCells` (one created node per position under `forall`),
     `coeffSum` (`sum` over the elements). The repeated element in the fixtures of the
     last two (`5, 6, 5`) shows that positions, not values, are enumerated: three nodes,
     sum 16;
   - tail operators needed no code change on the route from aspect graphs to operator
     nodes, in the `let` form or as a product node;
   - `coeffSum` is written with explicit nodes. `sum` and the other collection operators
     fold the values of a universal level, so their argument has to live one level below
     the node that receives the result. `coeffSum` is that construction: under `forall:`,
     `i` is an element of `range(coeffsSize(p))` and `e` is `coeffsAt(p, i)`; the `sum`
     product node takes `e` and delivers its result outside the quantifier.
     `let:sum = sum(coeffsAt(p, range(coeffsSize(p))))` is rejected, and rightly so ("Set
     operator argument ... should be one level deeper"): it has no quantifier, and an
     enumerator in argument position does not supply one. The form without product nodes
     names the element (rule `coeffSumVar`): a value node under `forall:` with the labels
     `id:e` and `int:coeffsAt(t.p, range(coeffsSize(t.p)))`, and `sum(e)` outside the
     quantifier. It needed a fix on branch `single-enumerator-per-expression`:
     `NormalAspectGraph.getLevel` had no case for a variable, so a collection operator
     over a node identifier threw "Unreachable location", with or without an enumerator
     (regression fixtures `intSumVar` in `sums.gps`, `totalVar` in `enumerators.gps`).
     The manual of slice 5 can show this form. Arend weighed a new label
     `int:x in range(n)` and decided against it (2026-11-12): the two separate labels
     suffice, and only the display changes, to an element-of sign in place of `=` where
     the right-hand side is an enumeration (see the handoff at the top);
   - the sample is `junit/samples/poly-growth.gps`: `step` appends one coefficient,
     `leap` two, `spread` creates a node per position. 20 states and 28 transitions,
     asserted by `ExplorationTest.testPolyGrowth`; without content equality of the
     values the state space would be a tree. It is also a `DeterminismTest` row;
   - the graphs have a hand-made grid layout, like those of `enumerators.gps`, and were
     not opened in the Simulator.
5. **Documentation.** `@UserType` javadoc; the "Release 7.7.0, forthcoming" section of
   `CHANGES.md`; a manual section in `manual/data.md` on a website branch stacked on
   `set-valued-operators`, including the correction of the limitation "nor of an array or
   list type".

Gates before handover: `null-check`, `determinism-check`, `grammar-smoke`, full suite. No
`gui/` file is expected to change (the help is built in `algebra.Algebras`); if one does,
the GUI tests apply.

## Risks and things not verified

- That host value nodes of sort `user` are identified through `equals` of the Java value
  is confirmed by slice 4's sample grammar (the state count of `poly-growth`).
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

## Example classes out of the main tree (asked 2026-11-12)

Carried out on branch `user-fixtures-to-test-tree`, stacked on `sequence-components`, as
proposed below; the count there was off by one (23 fixture classes in `test.algebra`,
26 in the new package).


The main package `nl.utwente.groove.algebra` holds three classes that are fixtures in all
but location: `UserOpsExample`, `UserTypeIntBool`, `UserTypeString`. Their only users are
`junit/rules/userOps.gps` (by name, in `userOperations`) and `RuleApplicationTest`
(`UserOpsExample.resetRandomness`). The website manual, `C:\Groove\grammars` and
`C:\Groove\samples` do not name them. They are shipped in the jar and appear in the
javadoc of an exported package. `test.algebra` in turn holds 24 fixture classes among 12
test classes.

Proposed, as a branch of its own stacked on this one (it moves files this branch adds):
- one package for all user-class fixtures in the test tree,
  `nl.utwente.groove.test.algebra.user`: the three from the main tree and the 24 from
  `test.algebra`. The tests address most of them by name through one constant
  (`UserSignatureTest.PACKAGE_NAME`); the rest become imports. `UserOpsNonPublicClass`
  is loaded by name only, so its package-private status survives the move;
- `userOps.gps` and `poly-growth.gps`: the `userOperations` line only. Rules, graphs and
  the rule-compilation golden name operators without a package;
- `Launch Simulator.launch`: `ATTR_EXCLUDE_TEST_CODE` to `false`, so that the fixture
  grammars still open in a Simulator started from Eclipse;
- a line in `CHANGES.md`: a grammar that names one of the three classes stops loading.
  They were never documented.

Rejected: a subpackage of the main tree (`algebra.example`): still shipped, and it needs
its own export in `module-info`; classes inside the grammar directory (`classes/`):
compiled binaries in the repository.
