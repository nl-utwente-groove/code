# Liveness-based control location variables (gh #561)

Analysis note for the `parout-liveness` branch, 2026-08-31. The branch resolves
gh #561 ("Get rid of parameter binding in target state") and, entangled with it,
a crash on control variables used inside expression arguments.

## The problem

A location's variable set determines the layout of the call-stack level that
every state carries whose prime frame sits at that location; state identity
(`GTS.StateSet.areEqual`) compares prime frames and prime call stacks modulo a
valuation-compatible graph isomorphism. `Template.initVars()` added a call's
out-variables to the call target unconditionally (`TemplateBuilder.addLocation`
had already materialised them at location creation), and spread variable sets
forward along verdicts without any dead-variable elimination. A `parout:`-bound
value therefore stayed in every subsequent prime stack even if never read
again, keeping apart states that differ only in that dead binding.

Separately, control variables inside expression arguments (`r(x - 1)`) crashed
exploration on master with an NPE in `Valuator`: `Switch` bound expression
variables to source-variable indices in its constructor, but the normalised
(quotient) template's switches are constructed *before* that template's
`initVars()` runs, so the bindings were computed against empty variable sets.
The only fixture using the syntax (`fibonacci-expressions.gcp`) is stale and no
longer compiles, which is why this went unnoticed.

## The fix

1. **Binding of expression arguments** moved from the `Switch` constructor to
   `computeAssignSource2Par`, which is lazily evaluated only after variable
   initialisation is complete. Switch equality/hashing now uniformly use the
   unbound call.

2. **`initVars()` computes liveness.** Uses are seeded — a call's in-arguments,
   the variables inside its expression arguments (`Template.getExprVars`,
   reconstructed from the expression typing plus the template owner's scope;
   previously not counted as uses at all), and the owning procedure's
   out-parameters at final locations — and propagated backward to their
   initialisation points, killed along switch links that assign them (the
   pre-existing `BackMap` machinery). Out-variables are no longer seeded at
   call targets: backward propagation adds them there exactly when live.
   `TemplateBuilder.addLocation` no longer materialises the term-key variables,
   so the bisimulation quotient can merge locations that differ only in dead
   assignment history (the `TermKey` still carries them, for location identity
   during construction).

## Load-bearing invariants

- **Verdict regions share one stack level.** Within a state, the actual frame
  moves along verdicts without touching the valuation (`Phase.getFrameStack`
  applies only the accumulated pops), so all locations in a verdict-connected
  region must have *identical* variable lists (`getVars()` is sorted, so equal
  sets suffice). The forward pass is therefore retained, including into final
  locations (procedure exits read the exit location's `assignFinal2Par` against
  the chain's stack level).

- **Joint fixpoint.** The old code ran one backward fixpoint, then one forward
  pass; forward additions were never propagated backward, so the two
  invariants (equal sets across verdicts; source support for every non-output
  target variable of a switch, asserted in `computeAssignSource2Target`) held
  by accident of the over-seeding. The passes now interleave until a joint
  fixpoint: backward propagation over a verdict backlink copies the full
  target set and forward propagation the full source set, so verdict-set
  equality holds at the fixpoint, and every addition anywhere is itself
  backward-propagated, so switch-source support is a theorem. The closure is
  monotone with a unique least solution — worklist order cannot influence the
  result (determinism).

- **Push/pop positional consistency is per-switch.** `Step.computePush`
  re-lays-out the caller's stack level to the *call switch's target* variables
  at push time (out-variables as `NONE` holes); the later pop
  (`assignFinal2Target` → `CallStack.modify`) fills exactly those holes,
  positionally against the same layout. No cross-location index invariant is
  needed, so shrinking variable sets cannot misalign procedure entry/exit.

- **Recipe out-parameters need no special liveness.** Both readback paths
  (`StateCache.RecipeTarget.getOutValuesFromTarget/-FromFinalTrans`) go through
  the recipe template's *own* final locations, whose `owner.getOutPars()`
  seeding is retained — the issue's "live until the recipe's exit frame"
  requirement is automatic. A dead binding in the *caller* is dropped after the
  recipe transition, whose label arguments are computed before the pop
  (asserted by `ParOutLivenessTest.testRecipeOut`).

## Residual retention

A dead variable is still retained throughout the verdict-connected region of
any location where it is live (the shared-stack invariant forces this), i.e.
until the next non-verdict transition after its last use. Removing that would
require re-indexing stack levels along verdicts — a change to frame identity
and state equality that gh #561 explicitly avoids.

## Fixture and differential evidence

`junit/control/parout.gps` + `ParOutLivenessTest`. The start graph's two
candidate nodes carry different flag sets: duplication only manifests when no
valuation-compatible automorphism exists (`IsoChecker.areIsomorphic` receives
the call stacks), so a symmetric start graph collapses the duplicates even on
master. On master, `testDeadOut` and `testRecipeOut` see 3 states instead of 2,
and `testExprArg` crashes; `testLiveOut` guards against over-collapse (states
remain distinct while the binding is live) and passes on both.

Verified on the branch: full suite including slow tests (710 green),
`DeterminismTest`/`CacheReconstructionTest`, `GrammarsTest` against the local
corpus checkouts, null-check at baseline. Golden updates in
`AutomatonBuildTest.testBinding` and `TemplateBuildTest.testVars` reflect the
intended drop of dead bindings.
