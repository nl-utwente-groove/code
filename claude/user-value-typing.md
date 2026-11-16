# Typing of user values by their Java class (gh #944)

Design note of 2026-11-15, for decision; reworded on 2026-11-16 for the release line.
Analysis only, nothing implemented. On branch `user-value-typing`, cut from `release-7`
(21a9022ee).

The note does not build on branch `user-op-dispatch` (gh #933, unmerged), whose solution
may be abandoned. The design needs one thing from the algebra: for every user operation,
its implementations with the class of each `user`-sorted parameter and of the result. On
the release line that is one implementation per operation; under any form of gh #933 it
is several. Section 2.1 says what each outcome of gh #933 changes. The note replaces the
scoping in `claude/user-op-dispatch-state.md` on that branch, section "Beyond this
branch"; where that scoping was wrong or incomplete, section 7 says so.

The examples use the user types `UserTypeStack`, `UserTypeQueue` and `UserTypeCounter`,
with `push` and `next` on stacks and queues alike and `count` on counters. Shared names
need gh #933; on the release line every list "A or B" in a message has one element.

## 1. Recommendation

Do both, in this order, as five slices that merge independently (section 5):

1. **A class check on expressions.** Every `user`-sorted expression gets the set of
   classes its value can have, and a call that no implementation can take is rejected.
   Small, no new syntax. The release line has no such check: `count(push(x,1))` loads
   and yields `NaU`.
2. **Inference per rule**, as a compilation stage over the typed level patterns. No new
   syntax. Covers hand-drawn product nodes and variables used more than once.
3. **Declarations in rules**: a node label `user=Stack:` in place of `user:`.
4. **Declarations in type graphs**: `user=Stack:stack` in place of `user:stack`, checked on
   host graphs when they are loaded and on every rule that writes the attribute.
5. **Declarations in control**: `user<Stack> s` in place of `user s`.

A declared class is kept as its simple name, never as a `Class`, and is resolved against
`UserSignature` while a model is compiled. `Sort` and `CtrlType` stay enums. Nothing is
checked or stored on the exploration path: all of it is model compilation.

Declarations are **strict**: a position with a declared class takes only a value whose
class is statically known to be that class. That makes a declared attribute class an
invariant of all reachable states without a run-time check. The price is that a grammar
that declares one attribute must declare what flows into it.

The decisions that are Arend's are in section 6.

## 2. Facts the design rests on

Verified by reading the code of `release-7`, unless section 8 lists them.

- **User types are final and there is no subtyping among them.** A listed `@UserType`
  class must be a record (`UserSignature.parseUserClass`), and every `user`-sorted
  parameter and result of an operation must be a listed class
  (`UserSignature.checkMethod`, "not declared in systems properties"; `Sort.toSort` maps
  a class to `USER` only by its annotation). The possible classes of a value are a
  subset of the finitely many loaded user types, and a parameter class is exact.
- **An operation has one implementation, and its classes are checked nowhere.**
  `Operator` holds sorts, and two operators are equal if their sort and name are.
  `UserSignature.getMethods` maps each to one `Executable`; a second operation of the
  same name is "Duplicate user operation" (`addOperator`), whatever its classes. A call
  is checked on sorts when parsed (`ExprTree.toCallExprs`).
- **Simple names of user types are unique**, as a consequence: the constructor operator
  is named by `getSimpleName()` (`Operator`).
- **An inapplicable operation yields a value, not a failed match.** An argument of
  another class reaches `Method.invoke` in `UserOperation.invoke`, which throws;
  `Operation.apply` turns that into `ErrorValue` (`NaU`), and `OperatorNodeSearchItem`
  makes a value node of it. A rule with a call that its implementation cannot take
  still matches and writes `NaU`.
- **Product nodes are checked on sorts only**, whether drawn by hand
  (`AspectNode.checkSignature`) or normalised from an expression written as text
  (`NormalAspectGraph.addCall`); `PatternBuilder` makes an `OperatorNode` of either.
- **Control arguments are expressions.** `in_arg` in `Ctrl.g` admits operators and calls
  over control variables; `CtrlHelper.parseExpr` parses them with the sort map of the
  symbol table, so a check placed in `ExprTree.toCallExprs` runs there as well, with
  every variable of unknown class. A user constant can be passed
  (`r(UserTypeStack("s",1))`).
- **Control typing is by `CtrlType`, an enum** with one `USER` value. `CtrlVar` and
  `UnitPar` hold a `CtrlType`; `UnitPar.compatibleWith` compares by equality. A rule
  parameter takes its type from the sort of its `VariableNode` (`UnitPar.RulePar`).
- **One data type node per sort.** `TypeFactory` creates one per `Sort` value; an
  attribute declaration `user:stack` becomes a `TypeEdge` from the owner to that node
  (`TypeModel.processModelEdge`). A class can therefore only refine the edge.
- **Derived states are not re-checked for sorts.** `TypeGraph.check` at run time covers
  multiplicities and containment only. That attribute values have the declared sort
  follows from the static typing of rules and host graphs.
- **Host graph values are closed constructor terms**, with constant arguments of system
  sorts (`ExprTree`, `Aspect.isForNode`). Their class is always known.
- **User operations require the default algebra family** (`GrammarKey.UserOperationsChecker`,
  `ConfiguredExploreType.checkAlgebra`), so no other family needs considering.
- **Label syntax.** After a sort prefix, `ContentKind.parse` requires `:`; anything else
  is the error "Prefix user should be followed by ':'". So `user=Name:` is free in all
  three graph roles. `user:Name` is not: in a type graph it declares an attribute called
  `Name`; in a rule it is an operator name if `Name` is one, which every class name is
  as a constructor, and otherwise an expression, a reference to the node with id `Name`;
  in a host graph it is parsed as a constant.

### 2.1 What gh #933 changes

The design is written for a list of implementations per operation, so that it holds for
each of these outcomes. Only the first is the code as it stands.

- **Names stay unique** (the release line). Every list has one element. The set of a
  call is the one result class, a use fixes the class of its argument, and the fixed
  point of 3.2 is reached in one pass. All slices apply as written.
- **Dispatch at run time on the class of the receiver** (`user-op-dispatch`). An
  operation has one implementation per receiver class; the sets do real work. That
  branch has the check of slice 1 for the case of one known class
  (`UserSignature.getUserClass`/`checkCall`, called from `ExprTree.toCallExprs`); slice
  1 then widens it to sets, and its test `UserCallCheckTest.testUnknownClass`, which
  pins `count(push(x,1))` as accepted, changes in its last case.
- **Selection of the implementation when a rule is compiled**, from the classes of the
  arguments. Not examined, and I do not know whether it is among the alternatives under
  investigation. My expectation: the dependency then runs the other way, gh #933 needing
  the classes of this note, and only a class that is known will do: that of a constant,
  a call or a declaration. The inferred sets of 3.2 are defined by which implementations
  are feasible and cannot select one. A shared name on a variable of unknown class would
  have to be an error asking for a declaration, which makes slices 3 to 5 part of
  gh #933 and strict declarations (4.3) a requirement, not a choice.

## 3. Question 1: what can be inferred with no new syntax

### 3.1 Sets of classes per expression

A new `UserSignature.getUserClasses(expr)` yields a set: the constructed class for a
constant or constructor call; for another call, the result classes of the
implementations that accept some class of every argument; all user types for a variable
or field. The check, in `ExprTree.toCallExprs`, rejects a call for which no
implementation is left.

- Sound: a rejected call yields `NaU` for every valuation.
- Rejects `count(push(x,1))`, which the release line accepts.
- Applies wherever expressions are parsed: rules, and arguments in control.
- Stops at variables and fields, whose set is all user types.
- Message: "User operation 'count' is not implemented for a value of type UserTypeStack
  or UserTypeQueue, only for UserTypeCounter".

The narrowing step is the core that 3.2 reuses: given an operator and a set per argument,
it yields the feasible implementations, the accepted classes per argument and the result
classes.

### 3.2 Inference per rule

Where: a stage of `RuleCompiler` after `PatternTyper`, over the typed level patterns. It
needs the `OperatorNode`s with their arguments and target, the type edges of attribute
edges (for slice 4), and the level structure. Errors are reported on the aspect elements
through the model map, as the other stages do.

Every `user`-sorted node `n` gets a set `C(n)`, read as: the classes `n` can have in a
match of its level in which no operation on that level yields `NaU`.

- Start: the class of a constant; all user types otherwise (later: the declared class).
- An operator node with arguments `a0..ak` and target `t` keeps the implementations that
  accept some class of every `C(ai)`. If none is left, the operation is an error. Else
  `C(t)` is cut down to their result classes and each `C(ai)` to the classes some kept
  implementation accepts. Repeat to a fixed point; the sets only shrink.
- An empty `C(n)` is an error: no class of `n` satisfies all operations on it.

Levels are where the scoping note was wrong. A use may narrow a node **only if it sits on
the level that binds the node**. An operation on a universal sub-level is not evaluated
when the sub-level has no match, and one in an embargo does not fail the rule, so neither
says anything about the value outside. A rule with `next(x)` under one `forall` and
`count(x)` under another is correct if `x` is a counter and the first has no match. So:

- a sub-level, and an embargo, takes the sets of its parent as given and narrows only
  the nodes it binds itself;
- an operation there that has no feasible implementation under those sets is still an
  error, as it is in the expression check of 3.1 regardless of context: the sub-level
  then always writes `NaU`, or the embargo is vacuous.

Soundness: an error means that every match of that level yields `NaU` somewhere.
Nothing is reported for an operation that fails for some classes of its argument and not
for others; that is a "may fail", and without declarations nearly every operation on a
variable is one.

Messages:

- "User operation 'count' is not implemented for a value of type UserTypeStack or
  UserTypeQueue, only for UserTypeCounter", on the operator edge and the argument node.
- "No user type fits all uses of this value: 'next' takes UserTypeStack or UserTypeQueue,
  'count' takes UserTypeCounter", on the node and the operator edges.

This covers hand-drawn product nodes, variables used more than once, and for rules
subsumes 3.1. It stops at the rule boundary: an attribute read from the host graph and a
rule parameter start as all user types.

It can reject rules that load today and write `NaU`, as 3.1 can. Nothing on the release
line does that; the check on `user-op-dispatch` does, for constants. See decision 2.

### 3.3 Control programs

Arguments are covered by 3.1. Beyond that, classes would have to flow: out-parameter of
a rule, control variable, in-parameter of the next rule, through recipes and functions.
That is feasible as a per-variable union over all assignments, but not recommended:

- it needs the inferred sets of rule parameters in the rule signatures, which makes the
  signature depend on the rule body;
- through procedures it is an interprocedural fixed point;
- the sets are those of 3.2, valid only for runs without `NaU`, so an error in `r2(s)` may
  really be a mistake in the rule that produced `s`. The message lands far from the cause.

Declared variable types (slice 5) do the same job per call, with the message at the call.

### 3.4 Host graphs and the start graph

Every user value in a host graph is a closed constructor term.
There are no operators and no variables, so there is nothing to infer. What a host
graph could contribute is the set of classes found under each attribute, as the seed of
a whole-grammar analysis: classes in the start graph plus classes written by any rule.
Not recommended: the start graph is exchangeable (several can be enabled, and the
generator takes another), every rule depends on all others, and a wrong write is
reported at the reads. A declaration in the type graph is the modular form of the same
information.

### 3.5 Summary

| Check | Sound | Stops at |
|---|---|---|
| Expression sets (3.1) | yes | variables, fields |
| Per rule (3.2) | yes, per level | attributes read, parameters |
| Control flow (3.3) | only for runs without `NaU` | not recommended |
| Whole grammar (3.4) | depends on the start graph | not recommended |

## 4. Question 2: explicit declaration

Feasible. Four places, one representation.

### 4.1 Syntax

Graph labels: `user=Stack:`, the form of `del=q:` and `exists=q:`.

- Rule node: `user=Stack:` for a variable; `user=Stack:push(x,1)` for an expression node,
  which then asserts the class of the outcome.
- Type graph: `user=Stack:stack`.
- Host graph: not allowed; a constant names its class.
- Operator edges: not allowed; the operator determines the classes.

Control: `user<Stack> s`, also in parameters (`out user<Stack> s`). After the keyword
`user` nothing but an identifier can follow today, so `<` there clashes neither with
atomic blocks nor with the comparison operator. `user Stack s` is equally unambiguous
(two tokens of lookahead in a grammar with `k=4`).

Rejected: the bare class name as a type (`Stack:` in labels, `Stack s` in control). It
reads best, but a label then parses differently depending on the loaded classes, a
missing class gives "Can't parse prefix" instead of "Unknown user type", a class may be
named like an aspect keyword, and in control a declaration `a.b.C x` and a call
`a.b.C(...)` differ only after an unbounded prefix. Also rejected: `user:Stack`, which
is taken (section 2).

### 4.2 Representation

The declared class is the simple name, a `String`, wherever a model keeps it:

- on the sort aspect of a label. An aspect has one content today, and a sort aspect uses
  it for the field name, constant or expression, so the name needs a second slot;
- on the `TypeEdge` of an attribute declaration, outside its equality. Equal edges with
  different classes are an error when type graphs are composed, and so is a subtype
  that redeclares an inherited attribute with another class;
- in `SortMap`, per variable and per field (`AspectGraph.createSortMap`,
  `TypeGraph.getTypeSortMap`, `SymbolTable.computeSortMap`), so that the expression
  check sees declared variables. The check takes the sort map, which
  `ExprTree.toCallExprs` has at hand. `Variable` and `FieldExpr` stay as they are;
- in `CtrlVar` and `UnitPar`, next to the `CtrlType`.

`UserSignature` resolves a name to the class on demand, through the constructor
operator; an unknown name is "Unknown user type 'Stak'". Names survive a reload, which
recompiles the models anyway (`userOperations` is a reload key). No `Class` enters a
compiled rule, a signature or a hashed collection.

### 4.3 What a declaration means

A declared class is exact, since there are no subtypes. It is what a dispatch on the
class of the receiver would select on.

- **As knowledge**: `C(n)` of a declared node, of the target of a declared attribute
  edge and of a declared control variable starts as that one class. Every operation on
  it is then checked exactly, in 3.1 and 3.2.
- **As obligation**, where a value enters a declared position:
  - a host graph value under a declared attribute, checked in `TypeGraph.analyzeHost`:
    "Value UserTypeQueue("q") of 'stack' should have type UserTypeStack";
  - a creator edge of a declared attribute: `C(target)` must be that class. "Value of
    type UserTypeQueue assigned to 'stack', which is declared as UserTypeStack", or, if
    the set is larger, "The type of the value assigned to 'stack' is not known to be
    UserTypeStack; declare the node as user=UserTypeStack:";
  - a node declared on an expression or operator target: likewise;
  - a control argument for a declared parameter: `UnitPar.compatibleWith` compares the
    names, giving the existing message with the classes in it, "Rule push(user<Stack>,int)
    not applicable for arguments (user<Queue>,int)". The class of a rule parameter is the
    declared class of its node, never an inferred one, so that the signature does not
    depend on the body. An undeclared parameter takes any user value, as now.

Strictness is what makes the second group worth having. If an unknown value were
admitted into a declared position, a declared attribute could hold another class and the
knowledge derived from it would be a guess. With it, the class of a declared attribute
is an invariant: the start graph satisfies it and every rule preserves it. This is how
sorts are guaranteed today, and it needs no run-time check.

Not enforced at run time, then. The exceptions are error values: an operation that
throws writes `NaU` into a declared attribute. That is so for every sort, and an error
argument makes every operation yield the error before its implementation is invoked
(`Operation.apply`).

### 4.4 Grammar version and existing grammars

Existing grammars declare nothing and behave as now, apart from the new errors of 3.1
and 3.2. A grammar that uses the new syntax should get version 3.13: the precedents are
3.11 for the `halt` keyword and 3.12 for `mult=`. Its effect is small, because
`Version.compareGrammarVersion` compares the major digit only: an older build opens such
a grammar without a warning, reports the new labels as "Prefix user should be followed
by ':'" and the control declarations as syntax errors, and stamps its own version on
the next edit. No `repairVersion` step is needed, since nothing changes meaning.

### 4.5 Subclasses and interfaces

None exist and none can be written (section 2). If a common interface of several user
types were admitted later as a parameter type or a declared type, it would denote the
set of the records implementing it; the sets of 3.1 and 3.2 handle that as they are. It
would need `checkMethod` relaxed. Not part of this proposal.

### 4.6 Display

- A variable node shows "x : **user**" (`LabelValue.getDataLine`, from a map per sort).
  With a declared class it shows "x : **Stack**".
- A type graph attribute shows "stack : **user**" (`AspectEdge.toLine`), then
  "stack : **Stack**".
- The type tree skips sort nodes; nothing changes there.
- Syntax help for sort prefixes is written out in `AspectKind.computeHelp` under `BOOL`;
  `USER` has none. The new form needs an entry.

`LabelValue` is in `gui/look`, so slices 3 and 4 fall under the GUI test gate and the
yFiles backend gate.

### 4.7 Performance and determinism

Everything runs while models are compiled. `MatchCollector.isCompatible`,
`UserOperation.invoke`, `ValueNode` and the search items are untouched, and no state or
event grows. The inference visits each operator node a few times per rule.

Class sets must be kept and printed in the order in which `UserSignature` lists the
user types (it is insertion-ordered), never in a `HashSet` of classes: `Class.hashCode`
is identity-based, which would reorder error messages from run to run. The sets do not
outlive compilation.

## 5. Slices

Each leaves the tool consistent and can be released on its own. None depends on
`user-op-dispatch`; all can be cut from the release line. The first two meet gh #933 in
`UserSignature` and `ExprTree`, so whichever of the two lands second has to adapt (2.1).

1. **Expression sets.** The classes of the implementations in `UserSignature` (today it
   keeps the `Executable` only), `getUserClasses`, the narrowing core, the check in
   `ExprTree.toCallExprs`; a test class with the fixtures for it.
2. **Rule inference.** New stage in `grammar/model`; fixtures with hand-drawn product
   nodes, a variable with conflicting uses, and the quantifier and embargo cases of 3.2.
   A new `junit/rules` grammar needs a `RuleCompilationTest` golden.
3. **Declared rule nodes.** Label syntax and the second slot on the sort aspect; the
   class in `SortMap`; initial sets and the assertion in the stage of slice 2; display;
   help; grammar version 3.13.
4. **Declared attributes.** `TypeEdge` class, composition and inheritance conflicts,
   `analyzeHost`, the write obligation, the field entries of the sort map, display.
5. **Declared control variables.** `var_type` in `Ctrl.g` and `CtrlChecker.g`,
   `CtrlTree.getCtrlType`, `CtrlVar`, `UnitPar`, `compatibleWith`, `SymbolTable`.
6. Web manual, batched with the other website updates.

Slices 4 and 5 are independent of each other; both need 3. Slice 2 can be dropped
without affecting 3 to 5 if declarations are thought sufficient, but then product nodes
drawn by hand stay unchecked.

## 6. Decisions for Arend

1. **Go-ahead and extent.** All five slices, or inference only (1 and 2), or stop after
   declarations in rules and type graphs (no control).
2. **Severity of the inferred errors** (slices 1 and 2). They can reject rules that load
   today and write `NaU`. Recommended: errors, as the check on `user-op-dispatch` gives.
   Alternative: warnings.
3. **Strict or lenient declarations.** Recommended: strict (4.3). Lenient would reject
   only a value known to be of another class; no forced annotations, but a declared
   class is then an assumption and the checks built on it can be wrong.
4. **Label syntax**: `user=Stack:` (recommended) or another form.
5. **Control syntax**: `user<Stack> s` (recommended) or `user Stack s`.
6. **Grammar version 3.13** with the first syntax slice, or stay on 3.12.
7. **Display**: the class name in place of `user` (recommended), or both.

## 7. Corrections to the scoping in the state note

The state note is `claude/user-op-dispatch-state.md` on branch `user-op-dispatch`; it is
not on the release line.

- "Intersect what producers yield with what uses require" is unsound across quantifier
  levels and embargoes; uses narrow a node only on the level that binds it (3.2).
- The inferred set is not the set of classes a value can have, but the set for which
  nothing on its level fails. That is enough to report certain failures, and too weak
  to export across rules (3.3).
- Control arguments are full expressions, so the expression check is not a remnant
  there but the only check, and declared control variables need the class in `SortMap`.
- `user:Name` without arguments is not free in any graph role; `user=Name:` is.
- Subtypes and interfaces among user types cannot occur, so a declared class is exact.
- No run-time enforcement is needed if declarations are strict.

## 8. Not verified

Nothing was compiled or run for this note.

- The note was written against `user-op-dispatch` and reworded for `release-7`. Reread
  on `release-7` for that: `UserSignature` (`parseUserClass`, `checkMethod`,
  `addOperator`), `Operator.equals`, `UserOperation.invoke`, `Operation.apply` and
  `ExprTree.toCallExprs`. The other facts concern files that are the same on both
  branches, going by `git diff --stat`, except `AspectEdge` and `LabelValue` (4.6), where
  the release line adds the display strings of gh #938; that difference was read as a
  diff and does not touch what 4.6 describes.
- The third outcome in 2.1 is reasoning, not an analysis of code.
- That `user=Name:` produces the quoted error, and what `user:Name` does on a rule node,
  are read from `ContentKind.parse`, `Aspect.isForNode` and `AspectEdge.set`.
- That the stage of slice 2 can report on aspect elements from where it sits is taken
  from the class comment of `RuleCompiler` and the way `PatternBuilder` reports.
- The lookahead claims for `Ctrl.g` were not tried in ANTLR.
- The control, type graph, grammar version and display facts were collected by
  sub-agents with file and line references. Of those, `var_type` in `Ctrl.g`,
  `UnitPar.compatibleWith`, `Version.compareGrammarVersion` and the attribute edge in
  `TypeModel.processModelEdge` were reread; `CtrlHelper`, `SymbolTable`, `TypeGraph`,
  `TypeFactory`, `SystemStore` and `LabelValue` were not.
