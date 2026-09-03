# JGraph controller split — design note

Part of the visualization-backend decoupling (gh #909, see `claude/yfiles-migration.md`),
phase 1a step 3. Problem: `JGraph` is simultaneously the Swing component (extending
`org.jgraph.JGraph`) and the per-display application controller — simulator wiring,
display-option machinery, layouter management, menu construction, mode machinery,
action caches. The controller half is library-independent and must end up outside the
backend package; in phase 2 it becomes the neutral per-display object that owns a thin
backend canvas.

## Shape of the extraction

New class `nl.utwente.groove.gui.display.GraphViewController<G>`, created eagerly by
the `JGraph` constructor, 1:1 with the component. **Naming principle** (Arend,
2026-09-03): naming and comments outside the `gui.jgraph` package must be free of
JGraph terminology — neutral vocabulary ("graph view", "graph-view component")
describes the concepts; direct *type* references to `JGraph` remain until the facade
replaces the type. This principle applies to all future extractions. In phase 1 the *component owns the
controller* and keeps thin delegating stubs, so the ~100 external call sites do not
churn; phase 2 inverts the ownership (panels construct a controller which owns a
canvas) and retargets callers. The controller talks back to the component only through
its public API (`refreshAllCells`, `addAccelerator`, `isEnabled`, `getModel`, …) —
that set is a first approximation of the backend-canvas facade.

A constraint discovered in the code: display policy is *polymorphic* over the JGraph
hierarchy (`CtrlJGraph` overrides `isShowNodeIdentities`/`isShowLoopsAsNodeLabels` to
constants; `AspectJGraph`/`LTSJGraph` override `createPopupMenu`, `getRefreshListener`,
`getDefaultLayouter`, `getDefaultMode`). Moving polymorphic behavior requires a
parallel controller hierarchy (per graph role). To keep PRs reviewable, the split is
sliced:

1. **Slice 1 (this branch): monomorphic controller state and lifecycle.**
   Simulator wiring (`getSimulatorModel`, `getActions`, grammar properties), the
   option-listener machinery (`getOptionValue`, `addOptionListener`, listener
   registration/removal), layouter management (`getLayouter`/`setLayouter`/`doLayout`),
   the export/layout action caches, label-tree association, tooltip registration.
   Polymorphic callbacks (`getRefreshListener`, `getDefaultLayouter`, the `isShow*`
   predicates, menus, mode) stay on the JGraph hierarchy; the controller reaches them
   through the component reference. `RefreshListener` becomes a static nested class
   taking the `JGraph` as constructor argument (it was an inner class relying on the
   outer instance, which does not survive the move; the four anonymous subclass uses
   pass `this`).
2. **Slice 2: controller hierarchy + menus + mode machinery.** Introduces
   per-role controller subclasses (e.g. `AspectGraphViewController`), moves `createPopupMenu`/`createDisplayMenu`/… and
   the mode action/button machinery, plus `AspectJGraph`'s grammar field and cell-edit
   action caches (left behind by the jgraph-actions branch).
3. **Slice 3: `JModel` policy.** Edge-merging policy, layout persistence, the color
   map — the model-side counterpart, analysed separately when slice 2 is done.

## Consequences accepted

- `getRefreshListener` (and its overrides) widen from protected to public: the
  controller lives in another package and needs the callback. Same for any later
  polymorphic callback the controller consumes.
- Until slice 2, the controller is state without much behavior of its own; the payoff
  of slice 1 is that the *lifecycle* logic (option-listener registration and removal,
  the memory-leak-prone part) and the controller state leave the component class, and
  the delegation pattern is established.
- The controller is `@NonNullByDefault` (new class); `JGraph` itself stays as it is.
