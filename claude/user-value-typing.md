# Typing of user values by their Java class (gh #944)

Design note of 2026-11-15, for decision. Revised on 2026-11-16 for the outcome of
gh #933, which is on `release-7`: user types share operations through user interfaces.
Analysis only, nothing implemented.

What the revision changed, for a reader of the earlier version:

- There is subtyping now. A `user`-sorted parameter, result or declared type is a user
  type or a **user interface**; it is an upper bound, no longer an exact class
  (sections 2, 4.3, 4.5).
- The check of slice 1 exists, in a form that knows one bound per expression
  (section 3.1). The slice shrinks to a refinement that is not worth doing on its own.
- Declarations can name a user interface (4.1). That needs the simple names of user
  interfaces to be unique, which nothing ensured; the check was added the same day,
  for the release that introduces user interfaces (decision 8, taken).
- The alternatives for gh #933 and the corrections to an earlier scoping are gone
  (former sections 2.1 and 7).

The examples use the fixtures of gh #933: the user types `UserTypeStack` and
`UserTypeQueue` implement the user interface `UserContainer`, which declares `push`,
`next` and `sameItems`; `UserTypeCounter` has `count`; all three implement `UserNamed`,
which declares `name`.

## 1. Recommendation

Do both, in this order, as four slices that merge independently (section 5):

1. **Inference per rule**, as a compilation stage over the typed level patterns. No new
   syntax. Covers hand-drawn product nodes and variables used more than once, neither of
   which the check on the release line sees.
2. **Declarations in rules**: a node label `user=Stack:` in place of `user:`.
3. **Declarations in type graphs**: `user=Stack:stack` in place of `user:stack`, checked on
   host graphs when they are loaded and on every rule that writes the attribute.
4. **Declarations in control**: `user<Stack> s` in place of `user s`.

A declared type is a user type or a user interface. It is kept as its simple name, never
as a `Class`, and is resolved against `UserSignature` while a model is compiled. `Sort`
and `CtrlType` stay enums. Nothing is checked or stored on the exploration path: all of
it is model compilation.

Declarations are **strict**: a position with a declared type takes only a value whose
class is statically known to fit it. That makes a declared attribute type an invariant
of all reachable states without a run-time check. The price is that a grammar that
declares one attribute must declare what flows into it.

The decisions that are Arend's are in section 6.

## 2. Facts the design rests on

Verified by reading the code of `release-7` (b1b325b55), unless section 7 lists them.

- **Values are records; types form a hierarchy.** A value of sort `user` is an instance
  of a listed `@UserType` record, which is final. A `user`-sorted parameter or result of
  an operation is such a record or a `@UserInterface` interface (`Sort.toSort`), which
  has to be among the user classes, listed or found through the `implements` and
  `extends` clauses of the listed ones (`UserSignature.checkMethod`, `addUserInterfaces`).
  A class `B` in such a position is an upper bound. It denotes the set `S(B)` of loaded
  user types assignable to it: one type if `B` is a user type, its implementors if it
  is a user interface (`UserSignature.getUserTypes`). All reasoning below is in terms of
  such sets of user types; they are finite and known when a grammar is loaded.
- **An operation has one name and one declaration, and Java dispatches.** Operators are
  equal if their sort and name are, and a second operation of the same name is
  "Duplicate user operation" (`addOperator`), unless it implements the operation of a
  user interface above it (`implementsInterfaceOp`). `UserSignature.getImplementations`
  maps each operator to one `Impl`. For an operation of a user interface that is the
  interface method, with the interface as receiver; it is invoked on the first argument.
  `Impl.refine(T)` yields the method as overridden for a receiver type `T`, whose result
  and parameter types can be narrower (a generic interface: `push` of a stack yields a
  stack, `sameItems` of a stack takes a stack).
- **Calls in expressions are checked against the classes, with one bound per
  expression** (`UserSignature.getUserClass`, `checkCall`, from `ExprTree.toCallExprs`).
  The bound of a constant or constructor call is its user type; that of another call is
  the result type of its implementation, refined for the bound of the receiver if that
  is known; a variable or field has none. An argument is rejected if no loaded user
  type fits both its bound and the class required (`isCompatible`), which is
  `S(required) ∩ S(actual) = ∅`.
- **Simple names of user types and user interfaces are unique.** The constructor
  operator of a user type is named by `getSimpleName()`, so two user types of the same
  simple name are duplicate operations. A user interface with the simple name of
  another, or of a user type, is an error of its own (`UserSignature.checkTypeNames`,
  added for this note).
- **An inapplicable operation yields a value, not a failed match.** A first argument
  that does not implement the receiver is refused by `UserOperation.checkReceiver`;
  another argument of the wrong class makes `Method.invoke`, the bridge method of a
  generic interface or `UserSeq.copyOf` throw. `Operation.apply` turns each into
  `ErrorValue` (`NaU`), and `OperatorNodeSearchItem` makes a value node of it. A rule
  with a call that its implementation cannot take still matches and writes `NaU`.
- **Product nodes drawn by hand are checked on sorts only**
  (`AspectNode.checkSignature`). An expression written as text is normalised from a
  parsed expression (`NormalAspectGraph.addCall`) and so passes the check above;
  `PatternBuilder` makes an `OperatorNode` of either.
- **Control arguments are expressions.** `in_arg` in `Ctrl.g` admits operators and calls
  over control variables; `CtrlHelper.parseExpr` parses them with the sort map of the
  symbol table, so the check above runs there as well, with every variable unbounded.
  A user constant can be passed (`r(UserTypeStack("s",1))`).
- **Control typing is by `CtrlType`, an enum** with one `USER` value. `CtrlVar` and
  `UnitPar` hold a `CtrlType`; `UnitPar.compatibleWith` compares by equality. A rule
  parameter takes its type from the sort of its `VariableNode` (`UnitPar.RulePar`).
- **One data type node per sort.** `TypeFactory` creates one per `Sort` value; an
  attribute declaration `user:stack` becomes a `TypeEdge` from the owner to that node
  (`TypeModel.processModelEdge`). A declared type can therefore only refine the edge.
- **Derived states are not re-checked for sorts.** `TypeGraph.check` at run time covers
  multiplicities and containment only. That attribute values have the declared sort
  follows from the static typing of rules and host graphs.
- **Host graph values are closed constructor terms**, with constant arguments of system
  sorts (`ExprTree`, `Aspect.isForNode`). Their user type is always known.
- **User operations require the default algebra family** (`GrammarKey.UserOperationsChecker`,
  `ConfiguredExploreType.checkAlgebra`), so no other family needs considering.
- **Label syntax.** After a sort prefix, `ContentKind.parse` requires `:`; anything else
  is the error "Prefix user should be followed by ':'". So `user=Name:` is free in all
  three graph roles. `user:Name` is not: in a type graph it declares an attribute called
  `Name`; in a rule it is an operator name if `Name` is one, which every user type name
  is as a constructor, and otherwise an expression, a reference to the node with id
  `Name`; in a host graph it is parsed as a constant.

## 3. Question 1: what can be inferred with no new syntax

### 3.1 Expressions: what is there, and what sets would add

The check of the release line (section 2) is this proposal's first slice with the sets
restricted to those that one class or interface denotes. It rejects
`count(push(x,1))`: "User operation 'count' is not implemented for a value of type
UserContainer, only for UserTypeCounter". It is sound in the same sense: a rejected call
yields `NaU` for every valuation. It applies wherever expressions are parsed, rules and
control arguments, and it stops at variables and fields.

Computing a set of user types per expression instead of one bound would be more
precise in one situation: the result type of an interface operation is declared wide,
every implementor narrows it, and the receiver is known only by its bound. With an
operation `rename` of `UserNamed` that every user type implements with itself as
result, `count(rename(push(x,1),"a"))` has bound `UserNamed` for the argument of
`count` and is accepted, where the set is stack or queue and would reject it. That is
too little to be a slice. The sets are needed in 3.2, where uses are intersected, and
the expression check can be put on them there at no extra cost.

The narrowing step that 3.2 needs, given an operator and a set per argument:

- the feasible receiver types are those `T` in the set of the first argument for which
  every other argument has a type in its set that fits the parameter of `Impl.refine(T)`
  (every position of a variable argument tail has the element type as its parameter);
  for an operation without a receiver there is the one implementation, feasible or not;
- the set of the first argument is cut down to the feasible receiver types, that of
  another argument to the types that fit the parameter for some feasible receiver type,
  and the result to the types that fit the result type for some feasible receiver type.

This forgets which receiver type goes with which argument and result type: after
`sameItems(push(x,1),y)` the sets of `x` and `y` are both stack or queue, not "the same
of the two". That loses precision, not soundness.

### 3.2 Inference per rule

Where: a stage of `RuleCompiler` after `PatternTyper`, over the typed level patterns. It
needs the `OperatorNode`s with their arguments and target, the type edges of attribute
edges (for slice 3), and the level structure. Errors are reported on the aspect elements
through the model map, as the other stages do.

Every `user`-sorted node `n` gets a set `C(n)` of user types, read as: the types `n` can
have in a match of its level in which no operation on that level yields `NaU`.

- Start: the user type of a constant; all user types otherwise (later: the set of the
  declared type).
- An operator node narrows the sets of its arguments and its target as in 3.1. If no
  receiver type is feasible, the operation is an error. Repeat to a fixed point; the
  sets only shrink.
- An empty `C(n)` is an error: no user type of `n` satisfies all operations on it.

A use may narrow a node **only if it sits on the level that binds the node**. An
operation on a universal sub-level is not evaluated when the sub-level has no match, and
one in an embargo does not fail the rule, so neither says anything about the value
outside. A rule with `next(x)` under one `forall` and `count(x)` under another is
correct if `x` is a counter and the first has no match. So:

- a sub-level, and an embargo, takes the sets of its parent as given and narrows only
  the nodes it binds itself;
- an operation there that has no feasible receiver type under those sets is still an
  error, as it is in the expression check regardless of context: the sub-level then
  always writes `NaU`, or the embargo is vacuous.

Soundness: an error means that every match of that level yields `NaU` somewhere.
Nothing is reported for an operation that fails for some types of its argument and not
for others; that is a "may fail", and without declarations nearly every operation on a
variable is one.

Messages, in the wording of the existing check (`describeType` prints a user interface
with its implementors):

- "User operation 'count' is not implemented for a value of type UserTypeStack or
  UserTypeQueue, only for UserTypeCounter", on the operator edge and the argument node.
- "No user type fits all uses of this value: 'next' takes UserContainer (implemented by
  UserTypeStack, UserTypeQueue), 'count' takes UserTypeCounter", on the node and the
  operator edges.

This covers hand-drawn product nodes and variables used more than once, and for rules
subsumes the expression check. It stops at the rule boundary: an attribute read from
the host graph and a rule parameter start as all user types.

It can reject rules that load today and write `NaU`. The expression check of gh #933
set that precedent; see decision 2.

### 3.3 Control programs

Arguments are covered by the expression check. Beyond that, types would have to flow:
out-parameter of a rule, control variable, in-parameter of the next rule, through
recipes and functions. That is feasible as a per-variable union over all assignments,
but not recommended:

- it needs the inferred sets of rule parameters in the rule signatures, which makes the
  signature depend on the rule body;
- through procedures it is an interprocedural fixed point;
- the sets are those of 3.2, valid only for runs without `NaU`, so an error in `r2(s)` may
  really be a mistake in the rule that produced `s`. The message lands far from the cause.

Declared variable types (slice 4) do the same job per call, with the message at the call.

### 3.4 Host graphs and the start graph

Every user value in a host graph is a closed constructor term.
There are no operators and no variables, so there is nothing to infer. What a host
graph could contribute is the set of user types found under each attribute, as the seed
of a whole-grammar analysis: types in the start graph plus types written by any rule.
Not recommended: the start graph is exchangeable (several can be enabled, and the
generator takes another), every rule depends on all others, and a wrong write is
reported at the reads. A declaration in the type graph is the modular form of the same
information.

### 3.5 Summary

| Check | Sound | Stops at |
|---|---|---|
| Expressions, one bound (on the release line) | yes | variables, fields |
| Per rule, sets (3.2) | yes, per level | attributes read, parameters |
| Control flow (3.3) | only for runs without `NaU` | not recommended |
| Whole grammar (3.4) | depends on the start graph | not recommended |

## 4. Question 2: explicit declaration

Feasible. Four places, one representation.

### 4.1 Syntax

Graph labels: `user=Stack:`, the form of `del=q:` and `exists=q:`. The name is the simple
name of a user type or of a user interface (`user=Container:`).

- Rule node: `user=Stack:` for a variable; `user=Stack:push(x,1)` for an expression node,
  which then asserts the type of the outcome.
- Type graph: `user=Stack:stack`.
- Host graph: not allowed; a constant names its user type.
- Operator edges: not allowed; the operator determines the types.

Control: `user<Stack> s`, also in parameters (`out user<Stack> s`). After the keyword
`user` nothing but an identifier can follow today, so `<` there clashes neither with
atomic blocks nor with the comparison operator. `user Stack s` is equally unambiguous
(two tokens of lookahead in a grammar with `k=4`).

Rejected: the bare name as a type (`Stack:` in labels, `Stack s` in control). It reads
best, but a label then parses differently depending on the loaded classes, a missing
class gives "Can't parse prefix" instead of "Unknown user type", a class may be named
like an aspect keyword, and in control a declaration `a.b.C x` and a call `a.b.C(...)`
differ only after an unbounded prefix. Also rejected: `user:Stack`, which is taken
(section 2).

### 4.2 Representation

The declared type is the simple name, a `String`, wherever a model keeps it:

- on the sort aspect of a label. An aspect has one content today, and a sort aspect uses
  it for the field name, constant or expression, so the name needs a second slot;
- on the `TypeEdge` of an attribute declaration, outside its equality. Equal edges with
  different declared types are an error when type graphs are composed, and so is a
  subtype that redeclares an inherited attribute with another type, a narrower one
  included: the attribute is written as well as read;
- in `SortMap`, per variable and per field (`AspectGraph.createSortMap`,
  `TypeGraph.getTypeSortMap`, `SymbolTable.computeSortMap`), so that the expression
  check sees declared variables. The check takes the sort map, which
  `ExprTree.toCallExprs` has at hand. `Variable` and `FieldExpr` stay as they are;
- in `CtrlVar` and `UnitPar`, next to the `CtrlType`.

`UserSignature` resolves a name to the class on demand; an unknown name is "Unknown user
type 'Stak'". For a user type that can go through the constructor operator. For a user
interface it needs a map from simple names; that those are unique among user types and
user interfaces together is checked already (section 2). Names survive a reload, which recompiles the models anyway
(`userOperations` is a reload key). No `Class` enters a compiled rule, a signature or a
hashed collection.

### 4.3 What a declaration means

A declared type `D` stands for the set `S(D)`: one user type, or the implementors of a
user interface. A declared user type is exact, since records are final.

- **As knowledge**: `C(n)` of a declared node, of the target of a declared attribute
  edge and of a declared control variable starts as `S(D)`. Every operation on it is
  then checked against that, in the expression check (where `D` is the bound) and
  in 3.2.
- **As obligation**, where a value enters a declared position: the set of the value has
  to be included in `S(D)`.
  - a host graph value under a declared attribute, checked in `TypeGraph.analyzeHost`:
    "Value UserTypeQueue("q") of 'stack' should have type UserTypeStack";
  - a creator edge of a declared attribute: `C(target) ⊆ S(D)`. "Value of type
    UserTypeQueue assigned to 'stack', which is declared as UserTypeStack", or, if the
    set is larger, "The type of the value assigned to 'stack' is not known to be
    UserTypeStack; declare the node as user=UserTypeStack:";
  - a node declared on an expression or operator target: likewise;
  - a control argument for a declared parameter. `UnitPar.compatibleWith` compares types
    by equality today; with declared types it has to follow the direction of the flow:
    for an input parameter the type of the argument has to fit that of the parameter
    (`S(argument) ⊆ S(parameter)`), for an output parameter that of the parameter has to
    fit the variable. The existing message gets the names, "Rule push(user<Stack>,int)
    not applicable for arguments (user<Queue>,int)". The type of a rule parameter is the
    declared type of its node, never an inferred one, so that the signature does not
    depend on the body. An undeclared parameter takes any user value, as now; an
    undeclared variable passed to a declared parameter is an error under strict
    declarations.

Inclusion is what subtyping asks of a strict declaration: a value known to be a stack
can go into a position declared as `Container`, a value known to be a container cannot
go into one declared as `Stack`. For that last case there is no cast and none is
proposed: the rule has to declare the node it reads from as a stack.

Strictness is what makes the second group worth having. If an unknown value were
admitted into a declared position, a declared attribute could hold another type and the
knowledge derived from it would be a guess. With it, the type of a declared attribute
is an invariant: the start graph satisfies it and every rule preserves it. This is how
sorts are guaranteed today, and it needs no run-time check.

Not enforced at run time, then. The exceptions are error values: an operation that
throws writes `NaU` into a declared attribute. That is so for every sort, and an error
argument makes every operation yield the error before its implementation is invoked
(`Operation.apply`).

### 4.4 Grammar version and existing grammars

Existing grammars declare nothing and behave as now, apart from the new errors of 3.2.
A grammar that uses the new syntax should get version 3.13: the precedents are 3.11 for
the `halt` keyword and 3.12 for `mult=`. Its effect is small, because
`Version.compareGrammarVersion` compares the major digit only: an older build opens such
a grammar without a warning, reports the new labels as "Prefix user should be followed
by ':'" and the control declarations as syntax errors, and stamps its own version on
the next edit. No `repairVersion` step is needed, since nothing changes meaning.

### 4.5 User interfaces

They are what makes a declaration more than a tag. Without them a declared type was
exact and every operation on a declared value had one implementation. With them:

- a declaration `user=Container:` is useful where a rule works on stacks and queues
  alike: it admits `push` and `next` and rules out `count`, and it is the weakest
  declaration under which the rule is free of "may fail" operations;
- a value declared by an interface keeps its bound through an operation whose result
  type is the interface or its type parameter (`push`), so chains of such operations
  stay declared without naming a user type;
- a type graph can declare an attribute as `Container` and have subtypes of the owner
  node differ in what they store only by convention; a narrower redeclaration in a
  node subtype is not allowed (4.2).

Not proposed: a declaration by several interfaces at once (a value that is both `A` and
`B`). The sets handle it, the syntax does not; a Java interface that extends both does.

### 4.6 Display

- A variable node shows "x : **user**" (`LabelValue.getDataLine`, from a map per sort).
  With a declared type it shows "x : **Stack**".
- A type graph attribute shows "stack : **user**" (`AspectEdge.toLine`), then
  "stack : **Stack**".
- The type tree skips sort nodes; nothing changes there.
- Syntax help for sort prefixes is written out in `AspectKind.computeHelp` under `BOOL`;
  `USER` has none. The new form needs an entry.

`LabelValue` is in `gui/look`, so slices 2 and 3 fall under the GUI test gate and the
yFiles backend gate.

### 4.7 Performance and determinism

Everything runs while models are compiled. `MatchCollector.isCompatible`,
`UserOperation.invoke`, `ValueNode` and the search items are untouched, and no state or
event grows. The inference visits each operator node a few times per rule.

Sets of user types must be kept and printed in the order in which `UserSignature` lists
the user types (`getUserTypes`, a list), never in a `HashSet` of classes:
`Class.hashCode` is identity-based, which would reorder error messages from run to run.
The sets do not outlive compilation.

## 5. Slices

Each leaves the tool consistent and can be released on its own; all are cut from the
release line.

1. **Rule inference.** `UserSignature.getUserClasses` and the narrowing step of 3.1, next
   to `getUserClass` and `checkCall`, which can then be put on sets as well; a new stage
   in `grammar/model`; fixtures with hand-drawn product nodes, a variable with
   conflicting uses, and the quantifier and embargo cases of 3.2. A new `junit/rules`
   grammar needs a `RuleCompilationTest` golden.
2. **Declared rule nodes.** Label syntax and the second slot on the sort aspect; the
   name-to-class map in `UserSignature`; the type in
   `SortMap`, as the bound of a variable in the expression check; initial sets and the
   assertion in the stage of slice 1; display; help; grammar version 3.13.
3. **Declared attributes.** `TypeEdge` type, composition and inheritance conflicts,
   `analyzeHost`, the write obligation, the field entries of the sort map, display.
4. **Declared control variables.** `var_type` in `Ctrl.g` and `CtrlChecker.g`,
   `CtrlTree.getCtrlType`, `CtrlVar`, `UnitPar`, the directed `compatibleWith`,
   `SymbolTable`.
5. Web manual, batched with the other website updates.

Slices 3 and 4 are independent of each other; both need 2. Slice 1 can be dropped
without affecting 2 to 4 if declarations are thought sufficient: the expression check
then works with the declared bound of a variable, product nodes drawn by hand stay
unchecked, and the write obligation of slice 3 has to be defined on bounds instead of
sets, which is the same thing less precisely.

## 6. Decisions for Arend

1. **Go-ahead and extent.** All four slices, or inference only (1), or declarations
   only (2 to 4), or stop after declarations in rules and type graphs (no control).
2. **Severity of the inferred errors** (slice 1). They can reject rules that load today
   and write `NaU`. Recommended: errors, as the expression check gives since gh #933.
   Alternative: warnings.
3. **Strict or lenient declarations.** Recommended: strict (4.3). Lenient would reject
   only a value known not to fit; no forced annotations, but a declared type is then an
   assumption and the checks built on it can be wrong.
4. **Label syntax**: `user=Stack:` (recommended) or another form.
5. **Control syntax**: `user<Stack> s` (recommended) or `user Stack s`.
6. **Grammar version 3.13** with the first syntax slice, or stay on 3.12.
7. **Display**: the type name in place of `user` (recommended), or both.
8. **Unique simple names of user interfaces**: taken on 2026-11-16, required from
   release 7.8.0 on, the release that introduces user interfaces, so that it cannot
   break a grammar that loaded before.

## 7. Not verified

Nothing was compiled or run for this note.

- The facts on user interfaces, the implementations and the expression check in
  section 2 are those of the code of gh #933 as merged into `release-7`; the names of
  the methods cited were checked against it for this revision.
- The other facts of section 2 are as they were collected for the first version, on
  `release-7` before gh #933, and were not reread. gh #933 did not touch the files
  concerned, except `ExprTree.toCallExprs`, where it added the call of the check.
- That `user=Name:` produces the quoted error, and what `user:Name` does on a rule node,
  are read from `ContentKind.parse`, `Aspect.isForNode` and `AspectEdge.set`.
- That the stage of slice 1 can report on aspect elements from where it sits is taken
  from the class comment of `RuleCompiler` and the way `PatternBuilder` reports.
- The lookahead claims for `Ctrl.g` were not tried in ANTLR.
- The control, type graph, grammar version and display facts were collected by
  sub-agents with file and line references. Of those, `var_type` in `Ctrl.g`,
  `UnitPar.compatibleWith`, `Version.compareGrammarVersion` and the attribute edge in
  `TypeModel.processModelEdge` were reread; `CtrlHelper`, `SymbolTable`, `TypeGraph`,
  `TypeFactory`, `SystemStore` and `LabelValue` were not.
- The narrowing step of 3.1 and the directed compatibility of control arguments in 4.3
  are reasoning; no code was written to try them.
