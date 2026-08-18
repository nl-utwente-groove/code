# RuleModel split: design note

*Analysis of 2026-08-18 (Claude session). Follow-up to the package-structure
observation that `RuleModel` (3005 lines) is overly large; scope here is the
internal structure of the RULE aspect-graph-to-`Rule` translation, not
package layering.*

## Diagnosis

`RuleModel` has two jobs: being a `ResourceModel` (lifecycle, staleness
tracking, property accessors, GUI views — ~600 lines) and being a compiler
from aspect graph to `Rule` (~2400 lines, as inner classes). The compiler is
the intractable part, for five specific reasons:

1. **Stages are numbered, not named.** `Level1`–`Level4` encode a real
   pipeline: quantification-tree discovery (`LevelTree.buildTree`) →
   element-to-level distribution (`Level1`) → untyped pattern construction
   with LHS/RHS/NAC split (`Level2`) → typing (`Level3`) → condition
   assembly (`Level4` + `computeRule`). The names force re-derivation of
   this every time.
2. **Data and passes are conflated.** Each `LevelN` class holds both the
   per-level data and the code of one pass; the handoff is a
   copy-constructor. `Level4`'s constructor is *pure* field copying — it
   exists as a class only because its behaviour (assembly) differs; its data
   is `Level3`'s. Three near-identical field lists (`lhs`, `rhs`, `nacs`,
   `countNode`, `outputNodes`, `colorMap`, `isRule`) are the tell.
3. **Inner classes over mutable outer state.** The stages reach into
   `RuleModel.this` for `ruleFactory`, `modelMap` (mutated *during* `Level2`
   via `getNodeImage`), grammar properties, type graph, `createErrors()`.
   Outer fields are assigned mid-`compute()`. Nothing is independently
   testable, and `@NonNullByDefault` is blocked — the fields are
   unavoidably null until compute runs.
4. **Errors live in three coordinate systems** — normalised aspect
   elements, untyped rule elements, typed rule elements — pulled back by
   `applyInverse` calls scattered across four places (twice in the
   `LevelTree` constructor, end of `computeRule`, the `createErrors`
   override). Correct attribution depends on where in the pipeline an
   exception escapes.
5. **Validation is interleaved with construction** (`testAsProperty`,
   `checkAttributes`, `checkVariables`, `checkTypeSpecialisation`, merger
   checks, `checkRegExprErasure`, parameter checks), some throwing
   immediately, some accumulating.

The codebase contains the answer in miniature: HOST went through this
extraction already — `HostModel` is a 160-line adapter,
`HostModelMorphism` a package-private translation object with
`source`/`map`/`target`/`errors`. RULE needs the same move at 20× the size,
so one class won't do.

## Target structure

New package-private top-level classes in `grammar.model` (same package as
`HostModelMorphism`; keeps access to model internals, hides the machinery):

| Class | Replaces | Role |
|---|---|---|
| `RuleModel` (~500 lines) | outer class | `ResourceModel` adapter only: lifecycle, role/priority/policy/injective accessors, GUI views; `compute()` delegates to the compiler |
| `RuleCompiler` | `LevelTree` + `computeRule` | orchestrator; explicit inputs (grammar model, source, role); the *single* error-pullback point; exposes rule, model map, type map, level map |
| `LevelIndexTree` | `buildTree` + index maps | the quantification-level index tree: a *data structure* with a constructing factory (`LevelIndexTree.from(normalSource)`), not a transformer class. (Renamed from the earlier `LevelSchema` proposal, which was ambiguous between data and pass — review comment, 2026-08-18.) |
| `LevelDistribution` | `Level1` | element-to-level assignment, variable maps, match counts |
| `LevelPattern` | field lists of `Level2/3/4` | the inter-stage **value class**: `index`, `parent`, `lhs`, `rhs`, `nacs`, `countNode`, `outputNodes`, `colorMap`, `isRule`. (Not a `record`: contents are deliberately mutable — graphs are extended during assembly — and identity semantics are wanted, so the generated value-equality members would all have to be overridden away; review decision, 2026-08-18. The `mid` graph turned out to be write-only and was removed in step 2.) |
| `PatternBuilder` | `Level2` | aspect elements → untyped `LevelPattern`s; NAC cell partition; attribute/variable checks |
| `PatternTyper` | `Level3` | `LevelPattern` → typed `LevelPattern` via `TypeGraph.analyzeRule`; specialisation/merger checks; returns the typing morphism |
| `ConditionAssembler` | `Level4` + tree knitting | eraser-conflict import, condition/rule construction, embargoes, condition-tree wiring; `checkRegExprErasure` moves here |
| `SignatureExtractor` | `Parameters` | unchanged logic, explicit inputs |

Central idea: separate the **data** (one `LevelPattern` record) from the
**passes** (builder, typer, assembler). That kills the copy-constructors
and the `Level3`/`Level4` distinction outright; a "stage" becomes a phase
of the compiler rather than a Java type.

Error handling gets one discipline: every pass reports errors in its own
element vocabulary; `RuleCompiler` composes the backward chain
(typing-morphism inverse ∘ untyped-model-map inverse ∘ normal-to-original
map) and applies it exactly once, at the boundary.

Secondary wins:

- `@NonNullByDefault` becomes possible — the compiler's result arrives
  atomically; `RuleModel`'s null-until-computed fields become lazy caches,
  matching `HostModel`.
- GUI level display (`getLevelTree()`, used by `gui.tree.RuleLevelTree`)
  needs only the distribution phase's output; per-phase outputs make the
  level view available even for rules with late-stage (e.g. typing) errors.
- Per-phase testability, currently zero.

## Rejected alternatives

- **Moving the compiler to `grammar.rule`**: it needs the model context
  (`ModelMap`, grammar model, resource properties); the
  `HostModelMorphism` precedent and the layering doc both put translations
  in `grammar.model`.
- **Fusing building and typing into one pass**: typing genuinely needs
  complete untyped per-level patterns (parent typing flows down the level
  tree); the passes are sequential by nature.
- **A full IR/visitor redesign**: `LevelPattern` *is* the intermediate
  representation; anything more is machinery without payoff.

## Migration plan

Four independently mergeable steps, decreasing in mechanicalness:

1. **The `HostModelMorphism` move**: extract `compute()` plus all inner
   classes into `RuleCompiler`, replacing `RuleModel.this` accesses with
   explicit constructor inputs. Behaviour-identical; `RuleModel` drops to
   ~500 lines. `Index` and `RuleModelMap` stay nested in `RuleModel` (the
   GUI imports `RuleModel.Index`), the compiler refers to them.
   `@NonNullByDefault` is deliberately *not* added to the moved code in
   this step — annotating 2400 moved lines would drown the review; it
   lands with step 3/4 when classes get their final shape.
2. **Data/pass split**: introduce `LevelPattern`, dissolve `Level4` into
   the assembler, collapse the copy-constructors.
3. **File split + renames**: per-phase top-level classes with real names.
   Naming rule settled during step-1 review: classes named for *data* (the
   level index tree, the level patterns) are value types constructed by
   factories; classes named for *passes* (builder, typer, assembler) hold
   only per-run scratch state.
4. **Error-pullback unification** — last, because it is the only step with
   real misattribution risk, and it is much easier to verify once the
   phases are explicit.

## Verification gates

Rule structure (condition order, anchor content) feeds exploration
determinism, and the pipeline is full of order-bearing collections
(`TreeMap` level maps, `LinkedHashSet` eraser sets, sorted NAC cells).
Every step must preserve iteration orders verbatim. Gates per step: fast
suite + full suite incl. `ExplorationTest` (grammar-smoke) + `null-check`
(zero new warnings) ; `determinism-check` for any step that touches
order-bearing collections (2 and 4 in particular).

## Status

- Step 1 done on branch `rulemodel-split` (2026-08-18): `RuleCompiler`
  extracted, `RuleModel` down to 527 lines. Moved code textually unchanged
  except three `RuleModel.this` qualifiers and two requalified `@see` tags;
  the compiler mirrors the original accessor names over explicit
  constructor inputs. Verified: full suite incl. slow groups (593 tests,
  0 failures), ecj null-check clean on both files.
- Step 2 done (2026-08-18): `LevelPattern` value class introduced as the
  inter-stage currency; `Level3` reduced to the typing pass (produces a
  pattern, checks walk the pattern parent chain); `Level4` dissolved into
  compiler-level assembly methods parameterized by the pattern. De-classing
  verified mechanically by diffing the old region under the systematic
  substitutions. Bonus finds: the `mid` graph (LHS∩RHS) was write-only and
  is removed; the errors set in `buildLevels3` was dead. Verified: full
  suite incl. slow groups at exact baseline (593 tests), ecj null-check
  clean, determinism checklist (no new hash use, collection types and
  iteration orders preserved verbatim).
- Step 3 done (2026-08-18), in five commits:
  - 3a: the ancestor-eraser sets moved out of `LevelPattern` into per-index
    maps (assembly-phase scratch, not pattern data — follow-up to the
    record discussion); `LevelPattern` is now construction-complete.
  - Data classes extracted: `LevelIndexTree` and `LevelDistribution` (both
    with `from` factories per the naming rule) and `LevelPattern` as
    top-level files.
  - Pass classes extracted: `PatternBuilder` (Level2 + allocator + NAC cell
    partition) and `PatternTyper` (Level3); the inter-stage currency is now
    uniformly `LevelPattern` (untyped → typed). `LevelTree` dissolved into
    `compile()`. Side effect: the GUI level view survives late-stage
    compile failures, as planned.
  - `ConditionAssembler` (assembly methods + condition-tree knitting +
    `checkRegExprErasure` + the eraser maps from 3a) and
    `SignatureExtractor` (ex `Parameters`) extracted; `RuleCompiler` is a
    ~350-line orchestrator.
  - `@NonNullByDefault` added to the three data classes. The pass classes
    and `RuleCompiler` stay unannotated: their null discipline is entangled
    with the error handling that step 4 reworks, so annotation lands there.
  - Verified: full suite incl. slow groups at baseline, ecj per-file clean
    and whole-project at the documented 17-problem baseline, determinism
    checklist (collection types and iteration orders preserved; eraser maps
    are `TreeMap<Index,…>` with `LinkedHashSet` values filled in the
    original sequence).
- Step 4 not started.
