# Recipe out-parameters vs. deletion: the StateCache:803 assert is reachable

Investigation of the assert in `StateCache.RecipeTarget.getOutValuesFromFinalTrans`
(`src/main/java/nl/utwente/groove/lts/StateCache.java:803`), added during the EEA
adoption (gh #881) with the comment "out-parameters are not deleted by the final
step". Question: is that invariant enforced anywhere, or can user input violate it?

**Answer: it is violable.** Three distinct reachable `AssertionError`s were
demonstrated by exploration (with `-ea`, as surefire enables) of small legal
grammars that load and compile without errors. Nothing in the control compiler
(`control/template`, `CtrlChecker`) or the rule model restricts what a recipe's
final step may delete; deletion of the bound node is a runtime property of the
match, so no static check could catch it in general.

## Mechanics

Out-parameter values of a completed recipe are computed in two independent ways:

1. **Final-transition path** (`getOutValuesFromFinalTrans`, the assert site):
   reconstructs the call stack of the *source* state of the final step, applies the
   step's push frame, and reads the out-parameter slots via
   `Location.assignFinal2Par().lookup(stack)`. The values are *source-graph* nodes.
   They are then mapped through `partial.getMorphism().nodeMap()` — but only if
   `!isIdentity()`. Two problems:
   - `assignFinal2Par` yields `Binding.Source.NONE` for **in-parameters**, which
     the `Valuator` evaluates to `null` (documented contract: null for in-slots,
     see `RecipeTransition.computeArguments`). The mapping lambda nevertheless does
     `nodeMap.get(n)` on those nulls and asserts the result non-null.
   - A node **deleted** by the final step has no entry in the application morphism
     (`RuleApplication.computeMorphism` only enters surviving nodes), so
     `nodeMap.get` returns null for it.
   - `Morphism.isIdentity()` is true for a purely-deleting application (deleted
     nodes simply have no entry; all present entries are identical). So in the
     pure-deletion case the mapping is *skipped* and the deleted source node is
     silently kept as the out-value.

2. **Target-state path** (`getOutValuesFromTarget`): reads the out-parameter slots
   from the target state's prime call stack. That stack was built by
   `MatchApplier.computeTargetStack`, which maps every entry through
   `RuleEffect.mapNode` — deleted nodes become **null** there. So this path yields
   `null` for a deleted out-parameter.

The de-facto semantics of the whole call-stack machinery is therefore
"deleted ⇒ null (undefined)"; the assert (and the `isIdentity` shortcut) are the
only places contradicting it. Merged-away nodes are unproblematic on both paths:
`MergeMap.getNode` and the application morphism both map them to the surviving node.

## Demonstrated crashes

All three grammars: start graph with three nodes flagged `a`, `b`, `c`;
rule `bind(out node x)` matches the `a`-node, deletes flag `a`, `parout:0` on it.
A merger (`new:=` edge between the `b`- and `c`-node) is used to make the final
step's morphism non-identity; an isomorphism-collapse symmetry transition would
do the same without mergers.

**F1 — the question asked: out-parameter deleted by the final step.**
`finish(node x)` = eraser node with `par:0`, plus the b/c merger.

```
recipe r(out node x) { bind(out x); finish(x); }
r(_);
```

`AssertionError` at StateCache:803 during exploration: the out-slot holds the
deleted source node, which has no image in the (non-identity) morphism.
Without the merger the morphism counts as identity, exploration succeeds, and the
recipe transition is labelled `r(n0)` where `n0` **does not exist in the target
state** — a silently dangling argument, inconsistent with the target-state path
which would yield null for the same situation.

**F2 — false alarm on plain in-parameters, no deletion anywhere.**

```
recipe s(node y) { merge; }
node z; bind(out z); s(z);
```

Same `AssertionError` at StateCache:803: the in-parameter slot is null by the
`Source.NONE` contract, and `nodeMap.get(null)` returns null. Without `-ea` the
null passes through unchanged, which is exactly the documented downstream
contract — this one is purely an over-strict assert.

**F3 — collateral find: `MergeMap.getNode` asserts on null stack entries.**
`assert key instanceof HostNode` (MergeMap.java:56) fails for `null`. Reached via
`MatchApplier.computeTargetStack` → `CallStack.map` → `RuleEffect.mapNode`
whenever a *merging* rule is applied while any in-scope control variable is
unbound (e.g. `node z; r(out z);` — `z` is null until the recipe completes; a
top-level `node x; merge; bind(out x);` triggers it just as well, no recipe
needed). Without `-ea`, `HashMap.get(null)` → null → `internalToExternal(null,
null)` → null: correct null-propagation. Also an over-strict assert.

## Options for the deletion semantics (F1) — decided 2026-08-29

1. **Out-parameter becomes undefined (null)** — consistent with what the stack
   machinery already does everywhere else. **CHOSEN (Arend, 2026-08-29)** and
   implemented, see below.
2. **Runtime error** (FormatException-like) when a recipe completes with a deleted
   out-parameter value. Rejected: the recipe body itself executed successfully;
   aborting exploration for this seems disproportionate, and the target-state path
   would need the same detection.
3. **Static rejection** by the control compiler: not viable — whether the deleted
   node coincides with the bound one is a runtime property of the match.

## Implementation (2026-08-29, this branch)

**Null out-parameters (F1, F2, F3).** `getOutValuesFromFinalTrans` now maps the
out-values through the transition morphism unconditionally: a node deleted by the
final step has no image and becomes null, a merged node follows the merge, and a
target-state isomorphism is applied as before; the former `isIdentity()` shortcut
was what silently kept dangling deleted nodes. `Assignment.map` and
`CallStack.map` pass null entries through instead of feeding them to the mapping —
the latter fixes F3 at its root (the null never reaches `MergeMap.getNode`),
so the type assert there stays. Argument consumers audited for null:
`Action.toLabelString` already rendered null as `_`; `Proposition.toArg` converts
null to the wildcard argument (matching the label-text round trip);
`RecipeEvent.compareTo` orders null arguments first (deterministically);
`Arrays`-based equality/hashing was already null-safe.

**Recipe boundary check.** Rule applicability for undefined in-arguments now
applies at the recipe boundary too: `MatchCollector.extractBinding` evaluates the
new `Step.getRecipeParAssign()` — the recipe-call parameter assignment of a step
that enters a recipe, composed through outer entered procedure switches exactly
like `Step.getParAssign()`, empty for steps not entering a recipe — and declares
the step matchless if any non-`NONE` binding evaluates to null. Consequences: a
recipe called with a null in-argument no longer starts (no transient exploration,
no absent states), and a body path avoiding the variable no longer makes the
recipe applicable despite the undefined argument. The gate skips `NONE` bindings;
this is exact, not a limitation: `NONE` arises only for the recipe call's own
out-parameters and wildcard arguments, where skipping is required. All static
routes to a `NONE` on an in-argument slot are closed by the control compiler —
wildcards are incompatible with in-only formals (`UnitPar.compatibleWith` /
`CtrlArg.Wild.inOnly()`), and possibly-uninitialised variables (including an
enclosing procedure's not-yet-assigned out-parameter) are rejected by the
initialised-variables analysis ("Variable x may not have been initialised",
`CtrlHelper.checkVar`); via composition, an inner in-argument can only reference
the entered outer procedure's start-location variables, which are exactly its
in-parameters, themselves fed from in-only (hence non-`NONE`) arguments. The only
runtime nulls reaching a recipe in-argument are deletion-produced variable
values, which the gate catches — also through a simultaneous function entry
(verified: `function f(node u) { r(u); }` with a nulled argument leaves the
recipe unstarted).

**Tests.** `junit/control/nullargs.gps` + `RecipeNullArgsTest` cover the plain
null-variable deadlock, the boundary check (recipe must not start), both
out-parameter deletion variants (merger/non-identity and pure-delete/identity
morphism, both formerly broken in different ways), and an in-parameter surviving
a merging final step (the F2 false alarm).

## Null-variable semantics outside recipes (verified 2026-08-29)

Follow-up question (Arend): is "deleted parameter node ⇒ variable null ⇒
consuming calls inapplicable" already the established semantics for plain
control variables? **Yes, verified in code and by exploration.**

- Code: `MatchCollector.extractBinding` evaluates each in-parameter binding of a
  step from the call stack; `isCompatible` returns false for a null value, upon
  which `extractBinding` returns null and `computeMatches` skips match search
  entirely — the call simply has no matches. The binding is computed for the
  *innermost rule call* of the step (`Step.getParAssign` chains the rule's
  parameters through the entered procedure switches), so the gate triggers
  exactly when the step's **rule** consumes the null value.
- Exploration (`node x; bind(out x); del(x); use(x);` where `del` erases its
  argument node): after `del(x)`, `use(x)` never matches; the state deadlocks,
  no crash, no error. Deleted-parameter-node ⇒ null is effected by
  `MatchApplier.computeTargetStack` / `RuleEffect.mapNode`.

This supports **option 1** for F1: a deleted out-parameter binding becoming null
is the same rule applied at the recipe boundary.

### Recipe calls with null in-arguments: no boundary check (verified)

Because the null gate lives on the innermost *rule* call only, a recipe invoked
with a null in-argument is **not** inapplicable as a whole — contrary to the
recipes-are-atomic-rules principle. Verified with
`recipe r(node y) { grow; use(y); }` called as `r(x)` after `del(x)`
(`grow` independent of `y`): the recipe *starts*, `grow` fires as an inner step,
`use(y)` then has no match, and the stuck inner state is marked transient+absent,
so no recipe transition arises. The LTS-visible outcome equals "inapplicable",
but only via the post-hoc absence machinery: the partial execution is explored
and discarded, and a body with an alternative path avoiding `y` would complete —
making the recipe applicable despite the null argument.

Related compiler behaviour: a wildcard argument for a recipe in-parameter
(`r(_)`) is statically rejected ("Recipe r(node) not applicable for arguments
(null)"), so the intent that in-parameters be bound exists — it just cannot see
runtime nulls.

Possible follow-up (separate concern from F1): an up-front applicability check —
when computing matches for a step that *enters* a recipe, treat the step as
matchless if any in-argument of the entered recipe call evaluates to null
(extending `MatchCollector.extractBinding` to the entered procedure switches of
`Step.getStack`, not just the innermost rule call). This would make recipe
applicability uniform with rule applicability and avoid the wasted transient
exploration. Behavioural change: recipes that today complete despite a null
in-argument via a body path that avoids the variable would become inapplicable.

### Pitfall for test grammars

`other` (like `any`) is a control-language keyword — a rule named `other` is
never callable by name; a call `other` expands to the set of not-explicitly-
called rules, which (when empty) compiles to Delta and then trips the "recipe
may fail to terminate" check. Cost an hour of confusion; don't name fixture
rules `any` or `other`.
