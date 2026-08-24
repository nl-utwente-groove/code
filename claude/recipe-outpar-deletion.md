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

## Options for the deletion semantics (F1) — decision needed

1. **Out-parameter becomes undefined (null)** — consistent with what the stack
   machinery already does everywhere else. Fix `getOutValuesFromFinalTrans` to
   apply the morphism mapping unconditionally (dropping the `isIdentity` shortcut,
   which is what silently keeps dangling nodes today), let missing entries map to
   null, skip null inputs. Then audit downstream consumers of
   `RecipeTransition` arguments (label text, GUI, serialisation) for null-safety.
2. **Runtime error** (FormatException-like) when a recipe completes with a deleted
   out-parameter value. Heavy: the recipe body itself executed successfully;
   aborting exploration for this seems disproportionate, and the target-state path
   would need the same detection.
3. **Static rejection** by the control compiler: not viable — whether the deleted
   node coincides with the bound one is a runtime property of the match.

F2 and F3 are assert bugs to fix regardless of the F1 decision (skip null slots
at StateCache:801-805; null-guard `MergeMap.getNode`).

Counterexample grammars are trivially reconstructible from the descriptions above;
they should become junit fixtures (with expected outcomes) once the F1 semantics
is decided.
