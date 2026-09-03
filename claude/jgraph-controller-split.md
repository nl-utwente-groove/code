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
   the per-role controller subclasses `AspectGraphViewController` and
   `LTSGraphViewController` (created through the polymorphic factory
   `JGraph.createController`, with covariant `getController` overrides), and moves all
   menu construction, the mode action/button machinery, `AspectJGraph`'s grammar field
   and cell-edit action caches, and `LTSJGraph`'s explore/goto/checker menus and
   scroll-to-active action. Unlike slice 1, the moved methods left no delegating
   stubs: their external callers were few (~15 lines across 9 files) and were
   retargeted to `getController()`, genuinely shrinking the component API. Mode
   *state* (`setMode`/`getMode`/`getDefaultMode` and the mode property events) stays
   on the component: it is interaction state read continuously by the UI delegate,
   and Swing property-change events belong to the component. **Done.**
3. **Slice 3: `JModel` content model** (first half done). Analysis showed the
   insertion machinery (`addNode`/`addEdge`/`computeJ*`/`prepareInsert`/`doInsert`)
   is heavily entangled with subclass behavior — `LTSJModel` drives it incrementally
   per GTS event with filtering overrides, `AspectJModel.syncGraph` rewrites the
   element-to-cell maps wholesale — and with the port model (the one
   `ConnectionSet.connect` call). So slice 3 covers only the cleanly separable
   content state: the new `gui.display.GraphViewModel<G>` holds the displayed
   graph, its layout map, and the element-to-cell index, plus layout
   synchronisation; `JModel` owns one and keeps delegating accessors (many
   external callers), `AspectJModel.syncGraph` uses a bulk `setJCellMaps`, and
   `LTSJModel` iterates `getNodes()`. The insertion algorithm plus its
   `ConnectionSet` use moves in a later step, naturally combined with de-porting
   the cell interfaces (shared work: pending edge connections become neutral
   (edge, source, target) records instead of port pairs). Merging predicates,
   `getColorMap`/`setLayoutable`/`refreshVisuals` (which iterate the backend
   z-order roots) stay on `JModel` until then.

   **De-port follow-up** (done): `JCell` no longer extends `GraphCell` and
   `JVertex` lost `getPort()`, making the entire cell interface hierarchy free of
   `org.jgraph` types; incident-edge access goes through the already-neutral
   `getContext()`. `JModel` records pending edge connections as neutral
   (edge, source, target) records and converts them to a `ConnectionSet` (via
   `AJVertex` casts, the single remaining port site on the load path) only in
   `doInsert`. Physically relocating the insertion algorithm into
   `GraphViewModel` is thereby *decoupled* from de-porting and is deferred to the
   phase-2 model work: the algorithm is now backend-neutral in place, and moving
   it early would split the orchestration (`addElements`/`addNodes`/`addEdges`,
   overridden by `LTSJModel`) from its parts for no current gain.

4. **Slice 4: looks decoupling** (done). Move the polymorphic `isShow*`
   predicates into the controller hierarchy (requires a small
   `CtrlGraphViewController` for `CtrlJGraph`'s constant overrides), then flip
   `VisualValue.get(JGraph, JCell)` and the `gui.look` value classes to take the
   `GraphViewController` — making the whole looks layer a pure controller client,
   per the general principle.

## Consequences accepted

- `getRefreshListener` (and its overrides) widen from protected to public: the
  controller lives in another package and needs the callback. Same for any later
  polymorphic callback the controller consumes.
- Until slice 2, the controller is state without much behavior of its own; the payoff
  of slice 1 is that the *lifecycle* logic (option-listener registration and removal,
  the memory-leak-prone part) and the controller state leave the component class, and
  the delegation pattern is established.
- The controller is `@NonNullByDefault` (new class); `JGraph` itself stays as it is.
