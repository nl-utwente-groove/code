# Phase 3 of the yFiles migration: the editor

Design and slice record for phase 3 of gh #909 (plan in `claude/yfiles-migration.md`,
facade in `claude/view-facade.md`, phase 2 in `claude/phase-2-model-and-ownership.md`).
Written 2026-09-07, after slice 4d closed phase 2 for the read-only views.

## What the editor is today

The editor is `AspectEditorTab` (a tab of a resource display) over `AspectJGraph`, the
one canvas the yFiles backend still delegates to JGraph. Its parts, and where each one is
bound to JGraph:

- **Gestures** (`JGraphUI.MouseHandler`, JGraph-specific by construction). In edit mode: a
  single click on a vertex selects it and starts *click-click* edge creation, ended by a
  second click on the target vertex (or on empty canvas for a self-loop); a double click on
  empty canvas adds a vertex; a double click on a cell opens the in-place editor; Alt-click
  adds or removes an edge point; dragging moves the selection or an edge point; a rubber
  band selects; pan mode pans and zooms. Preview mode shows the graph as the viewer would.
- **Structural edits** (`AspectJGraph.addVertex/addEdge`, `AspectJModel.remove/insert/
  cloneCells`): JGraph cell insertion with a `ConnectionSet`, removal with incident edges,
  paste by cloning cells with fresh node numbers. Each is a JGraph `GraphModelChange`.
- **Label edits** (`MultiLinedEditor`, `JCell.setUserObject`): JGraph's in-place cell
  editor shows the cell's `EditableLabels` edit string in a text area with GROOVE's own
  autocompletion (aspect prefixes plus the type graph's labels); committing writes the text
  back through `valueForCellChanged` → `setUserObject` → `setEditableLabels`, again as a
  `GraphModelChange`.
- **Visual edits** (`GraphCanvas.edit(Map<ViewCell,VisualMap>)`, the funnel of the
  `JCellEditAction`s: edge points, label position, line style, colour): on JGraph an
  attribute edit, undoable; on the yFiles canvas applied directly, not undoable.
- **Synchronisation**: `AspectGraphViewModel.syncGraph()` rebuilds the `AspectGraph` from
  the cells (`applyEditableLabels`) with the layout map, triggered by `AspectJModel
  .fireGraphChanged` after structural or label changes; layout-only changes only update the
  layout map (`synchroniseLayout`).
- **Undo** (`AspectEditorTab.EditorUndoManager extends GraphUndoManager`): JGraph's
  undoable edits, filtered for relevance (refresh storms, empty changes) and classified as
  *minor* (layout only: no insert, remove, connection or user-object change) or *major*,
  which drives the dirt count and the "minor dirt" flag that decides whether saving needs a
  full grammar reload. `EditorUndoTest` guards the history for add-edge-then-label.
- **Clipboard**: Swing `TransferHandler` cut/copy/paste actions redirected to the JGraph,
  whose transfer handler serialises JGraph cells; paste goes through `cloneCells`.
- **Snap to grid**: `JGraph.setGridEnabled`, JGraph's own grid.

Everything else on the tab (properties panel, syntax help, label tree, status, dirt
bookkeeping) is already backend-neutral through `AspectTab`.

## Design

### The edit model is GROOVE's, in the view model

Decided in phase 2 (its "Consequence for phase 3"): an edit is a change to the view model
that both backends reflect, and undo replays it. Two alternatives were weighed again and
rejected:

- *Two native undo models* (JGraph's `GraphUndoManager`, yFiles' `UndoEngine`), each
  wrapped for the tab. Rejected: the tab keeps classifying backend edit objects, the
  minor/major and dirt semantics are implemented twice, and the JGraph backend cannot
  leave the core module while the tab needs its edit objects.
- *yFiles' graph as the source of truth while editing* (let `GraphEditorInputMode` change
  the `IGraph` and translate item events back into cells). Rejected: two sources of truth
  during every gesture, and a JGraph editor that would work differently from the yFiles
  one. The view model stays authoritative, on both backends, as it is for the viewers.

The model: `gui.view` gains

- `GraphEdit`: one undoable change to a view model, holding any of: inserted vertices and
  edges with their connections; removed cells (with the connections they had, so that
  undo re-inserts the same cell objects); visual changes per cell (old and new
  `VisualMap`); label changes per cell (old and new `EditableLabels`). It knows whether
  it is *minor* (visual changes only). Applying and reverting it is a view-model
  operation: the store inserts or removes items, cells take their visuals and labels, the
  canvas refreshes the changed cells.
- `EditHistory`: undo and redo stacks of `GraphEdit`s with listeners, the dirt count and
  the minor-dirt flag (moved from the tab), and a *recording* scope so that a gesture
  producing several changes (paste, delete with incident edges, a move of many cells)
  yields one edit.
- View-model operations that record edits when the model is being edited:
  `insert(vertices, edges, connections)`, `remove(cells)`, `changeVisuals(map)`,
  `changeLabels(cell, labels)`. `GraphCanvas.edit(map)` routes into `changeVisuals`, so the
  existing `JCellEditAction`s become undoable on every backend without change.
- `CellStore` gains `removeCells(cells)`; both stores implement it (JGraph: the model's
  `remove`; yFiles: `IGraph.remove` of the items, the cells keeping their identity for
  re-insertion).

The tab observes the history instead of JGraph's model: `EditorUndoManager` goes, and with
it the last JGraph import of `AspectEditorTab`, which empties the architecture allowlist.
`syncGraph` is triggered by the history after every non-minor edit, undo or redo, replacing
the `fireGraphChanged` classification.

JGraph's own undoable edits are still posted by `DefaultGraphModel` but nobody listens;
the editor's `addVertex`, `addEdge`, delete, paste and in-place commit call the view-model
operations instead of `model.insert`/`remove`/`valueForCellChanged` directly. On yFiles the
graph's undo engine stays disabled.

### Gestures on yFiles: yFiles' input modes, GROOVE's edits

The yFiles editor canvas configures a `GraphEditorInputMode` so that every gesture ends
in exactly one view-model operation (the documented customisation points, all input
sources permitted by the ground rules):

- **Node creation**: click-to-create off; a double click on empty canvas (the
  `CanvasClicked` event with click count 2) calls the view model's insert of a fresh
  vertex at the point, snapped to the grid if enabled; the in-place editor opens on it as
  today.
- **Edge creation**: the click-click gesture through `CreateEdgeInputMode
  .doStartEdgeCreation` on the item-clicked event of a vertex, with the drag-based start
  switched off (`setPrepareRecognizer(NEVER)`); an `EdgeCreator` callback that inserts an
  edge cell through the view model and returns its item; self-loops allowed; a click on
  empty canvas ends the gesture as a self-loop, as today. Whether the started gesture
  continues without a pressed button (the guide describes click-mode bend adding after a
  release on empty canvas) is the first thing to verify in a probe; the fallback is a
  small own input mode for the second click.
- **Moving**: `MoveInputMode` moves the items; on its drag-finished event the affected
  items' layouts are read back into one visual edit (positions and, for edges, points),
  which the store then finds already applied. The same mode, restricted to moving, gives
  the read-only viewers the node dragging Arend asked for (deferred in 4c).
- **Edge points**: `CreateBendInputMode` and the bend handles change bends; on their
  finished events the edge's points become one visual edit. Alt-click add/remove stays on
  GROOVE's actions, as today.
- **Deletion**: `deleteSelection` overridden to call the view model's remove (with incident
  edges, as `AspectJModel.remove` does).
- **Label editing**: `TextEditorInputMode` with the label-editing event configuring the
  text area to the cell's edit string, GROOVE's autocompletion key binding installed on
  the text area (the completion logic of `MultiLinedEditor` moves to a neutral
  `LabelCompletion` in `gui.view`, both editors use it), and the commit routed into
  `changeLabels` with yFiles' own label change cancelled. Opened by a double click on a
  cell, as today; F2 as yFiles' default is a bonus.
- **Preview mode**: as on JGraph, the tab swaps in a preview clone of the model; the
  canvas switches to the viewer input mode.
- **Snap to grid**: `GraphSnapContext` with grid constraint providers and a
  `GridVisualCreator` in the background group, toggled by the existing action.
- **Selection, popup, clicks**: as in the viewer canvases (4c).

### The clipboard is GROOVE's

Cut, copy and paste move an `AspectGraph` fragment with its layout: copy builds the
subgraph of the selected cells (edges only with both ends selected) through
`applyEditableLabels`, as `syncGraph` does, into a `Transferable` with a GROOVE flavour
(and the graph's plain text as the string flavour, for pasting labels elsewhere); paste
inserts the fragment's cells through the view model with fresh node numbers, offset from
the original position, as one edit. Both backends use it; JGraph's transfer handler and
yFiles' `GraphClipboard` are switched off for the editor. This replaces the serialised
JGraph cells, which were never a stable format.

## Slicing

1. **3a — the edit model** (backend-neutral, headless-testable): `GraphEdit`,
   `EditHistory`, the view-model operations, `CellStore.removeCells`, the funnel routed
   into the history; the tab on the history; JGraph's editor calling the operations;
   `EditorUndoTest` retargeted (and a backend-neutral undo test next to it). The
   architecture allowlist becomes empty. No behaviour change for the user.
2. **3b — the yFiles editor canvas**: `YFilesAspectCanvas` editable through the configured
   `GraphEditorInputMode` as above, the label completion made neutral, snap to grid, the
   backend's editor fallback removed; node dragging in the viewer canvases. Simulator tests
   for the gestures through synthetic events where yFiles honours them.
3. **3c — the clipboard**: the GROOVE transferable and the paste edit on both backends;
   copying by Ctrl+drag as well (Arend's review of 3b: copy/paste must also be invoked by
   that gesture).

Phase 4 (export and Imager on the facade) follows; the JGraph backend can then leave the
core module.

## State of work

**Slice 3a done (2026-09-07, branch `editor-edit-model` off the rebased
`yworks-migration`).** `gui.view` holds the edit model: `GraphEdit` (inserted and removed
cells with their connections, visual changes and label changes with old and new values,
`isMinor` = visuals only), `EditHistory` (undo and redo stacks, compound recording, the
dirt count and minor-dirt flag that were the tab's, listeners), and the view-model
operations `insert`, `remove` (closing over incident edges), `changeVisuals` and
`changeLabels`, all going through `doEdit` → `apply(edit, forward)`, which drives the store
(`CellStore.removeCells` and `applyVisuals` are new) and ends in `afterEdit`, where the
aspect model rebuilds its graph after every non-minor edit; the cell refresh of a label
change follows that rebuild, since the shown label comes from the rebuilt graph. The
history exists once `setBeingEdited(true)` (or `enableEditHistory`) was called and records
nothing while the model loads. `GraphCanvas.edit` is now a default method routing into
`changeVisuals`, so the existing `JCellEditAction`s, the layouters and `clearAllEdgePoints`
are undoable on every backend without change; `finishEditing` joined the contract.

The JGraph backend feeds its own gestures into the model: `JModel.edit` (JGraph's
attribute edits from the move handle, bend dragging and the in-place editor's commit)
translates the attributes into a visual or label change through the new
`VisualAttributeMap.toVisuals` and calls the view model, which applies them through
`applyVisuals` = JGraph's `super.edit`; edits with a connection set or parent map, or
during loading and refreshing, stay JGraph's own. `AspectJModel.insert` (paste, drop) and
`remove` (cut) route likewise; `AspectJGraph.addVertex/addEdge` and the tab's delete call
the operations directly. Cells keep their identity across removal and re-insertion:
`JModel.insertCells` reuses a cell's JGraph item (restoring the vertex port that removal
takes along), and `removeCells` removes the descendants too, as the old delete action did.
Reconnecting an edge end by dragging it (JGraph's edge handle, which Arend uses) is an
edit too: `GraphEdit` holds reconnections (old and new connection per edge), the store
contract has `reconnectEdge`, and `JModel.edit` translates a connection set into the view
model's `reconnect` (with the new points in the same edit); the yFiles store reconnects by
replacing the edge item. The edge cells' `setSource`/`setTarget` now accept a replacement
end. A side finding: `JEdgeView.getParRank` is asked for a disconnected edge during removal
(guarded now). JGraph still posts its `UndoableEdit`s, to nobody.

`AspectEditorTab` is on the history and the canvas contract only: no JGraph import is
left, so the architecture allowlist is empty. `EditorUndoTest` (JGraph, headless) covers
add-edge-then-label through the in-place path, a move as a minor edit with the layout map
following, and removal with incident edges undone; `YFilesCanvasTest` covers insertion,
visual, label and removal edits with undo and redo on the yFiles store.

**Slice 3b done (2026-09-07, branch `editor-edit-model`, on top of 3a).**
`YFilesAspectEditorCanvas` is the editor's canvas: `YFilesCanvas` gained an overridable
input-mode factory with the viewer's configuration split into helpers (selectable
predicate, popup, marquee, pan) that the editor's `GraphEditorInputMode` reuses. yFiles'
own editing operations are switched off (node creation, deletion, clipboard, undo,
grouping, label editing) and GROOVE's gestures are mapped onto its events: a canvas
double click inserts a vertex through the model and opens its editor; an item click on a
vertex starts `CreateEdgeInputMode.doStartEdgeCreation` (deferred with `invokeLater` so
the click has been handled) with the drag start disabled, self-loops on, a premature end
anywhere (empty canvas = loop), and an `EdgeCreator` that inserts through the model and
returns the item — the click-click gesture works natively, since the mode follows the mouse
without a pressed button and finishes on the next press; Alt-click routes to the add/remove
point actions with the world location; moves (selected and unselected items, bend handles)
become one visual edit on the drag-finished events, reading positions and bends back from
the items, which the store then finds up to date; a dragged edge end (yFiles'
reconnection port candidates on every node) becomes a reconnection with the new points in
the same edit, detected by comparing the cell's ends with the item's; the in-place editor
is `TextEditorInputMode` driven directly (`setLocation`, `setEditing`, `stop`), showing the
edit string and committing through `changeLabels` unless unchanged, with the completion
extracted from `MultiLinedEditor` into `gui.view.LabelCompletion` (shared); the grid is a
`GraphSnapContext` with grid constraint providers only and a `GridVisualCreator` in the
background group. Modes: edit mode everything, selection mode moving only, preview
nothing but selection. The backend's JGraph fallback is gone.

Three findings on the way, all fixed in the canvas:
- **Mouse input never reached the editor canvas.** Swing's lightweight dispatcher delivers
  mouse events to the deepest component at the point that listens, and yFiles' listening
  input surface is not always that component (the editor's render pane lay above it);
  the viewer displays happened to register Swing listeners on the graph component, the
  editor tab did not, so the editor got nothing, from synthetic events or from a real
  mouse. The `Viewer` now registers empty mouse and motion listeners itself.
- **Absurd content rectangles after layered layouts** (a flaky LTS test): yFiles crops
  the edge paths with the node style's `getIntersection`, which delegated to GROOVE's
  `NodeShape.getPerimeterPoint`; for some directions (axis-aligned, or from a bend on the
  border) that returns a point off the line or non-finite, and the crop then produces a
  path of 1e18 extent. The style now falls back on yFiles' outline intersection whenever
  the perimeter point is not finite or not on the line; the renderer also drops bends
  inside or on the end nodes and coinciding neighbours before curving. The store applies
  vertex visuals before edge visuals, so labels are never placed against a transient in
  which two end nodes coincide, and the coincidence threshold of the label placement is a
  pixel rather than 1e-6.
- **A gesture that ends where it started** (a click with the move-unselected mode on) posted
  an empty visual edit; `changeVisuals` now drops unchanged keys.

Tests: `YFilesEditorTest` (headless, 7 at first; 11 after the review rounds): the editor canvas, added vertices with their
editor open and the grid snapping them, the in-place editor committing into the model and
undo, the edge creator with an edge and a loop, a finished move as a minor edit with the
incident edges following, a dragged edge end as a reconnection. `YFilesSimulatorTest`
gained `editorTabEditsThroughGestures`: an editor tab opened through the display, a Robot
double click adding a vertex and a click-click adding an edge, both undone through the
history — a Robot on a shared desktop being unreliable, the first gesture is an
assumption; the LTS layout test now also asserts finite item and visual bounds. All 41
tests of the unit pass; the core GUI tests pass.

Not done in 3b: node dragging in the viewer canvases (the `MoveInputMode` is easily added
to the viewer mode, left for a small follow-up), yFiles' orthogonal edge editing and snap
lines beyond the grid (not GROOVE features), and zooming at the mouse position, which is
bullet 2 of gh #882 (to be done for both backends; on yFiles one property of the graph
component, `setCenterZoomEventRecognizer`). The snap-to-grid semantics of the JGraph
editor (corners and sizes snap, not centres) are gh #915, outside the migration.

**Review round on 3b** (Arend, 2026-09-07), fixed on the same branch:

- *An edge rerouted onto its own source was not drawn until its node was dragged.* Root
  cause in the core cell, not the canvas: `AViewEdge.isLoop` judged by the graph nodes,
  which still name the old ends until `syncGraph` rebuilds the graph after the edit, so
  the store routed the reconnected edge as a binary edge with coinciding ends (no bends,
  nothing to draw). The connection now decides where there is one. Found by a headless
  test, which stays.
- *Rerouting lost the selection* while edge creation kept it: the store replaces the item
  of a reconnected edge, and yFiles deselects removed items. The store re-selects the new
  item when the old one was selected, in one selection batch.
- *Labels of west and east loops stood at 90 degrees*: the `EdgePathLabelModel` used for
  loops rotates labels along the segment by default; switched off, JGraph never rotates.
- *The move cursor over edges with nothing to move*: `shouldMove` of the editor input
  mode now denies edges without points (an edge with points is still dragged as a whole,
  as in JGraph).
- *End handles on every selected edge*: the `IEdgePortHandleProvider` of an edge is hidden
  while more than one edge is selected (`hideImplementation` with a predicate on the edge
  decorator of the master graph), and the editor requeries the handles after every
  reported selection change (`YFilesCanvas.selectionFlushed` hook).
- *The self-loop preview was yFiles' rectangle*, unlike the loop then created: the
  creation mode's edge defaults now carry the cell edge style, so the preview has GROOVE's
  pen and arrow, and at gesture start the source vertex cell goes into the dummy edge's
  tag; the renderer routes a cell-less edge that ends on that vertex as `LoopRouter`
  will route the loop (`route(vertex, null, bounds)`, the new overload).
- *The Robot tests take over the mouse* while Arend works: they now run only with
  `-Dgroove.test.robot=true` (pom property passed to surefire) and are skipped otherwise.

**Second review round** (Arend, 2026-09-07), same branch:

- *Parallel edges overlapped completely on yFiles.* Arend chose the render-time fan-out as
  JGraph does it, over curved bulges and over yFiles' `ParallelEdgeRouter` (a layout stage
  writing real bends: wrong for interactive editing, fine inside a layout run). The rank
  computation moved from `JEdgeView.getParRank` into the neutral `gui.view.ParallelEdges`
  (rank among the unrouted edges between the same two vertices in either direction, and
  the perpendicular shift, 4 px per rank unit); `JEdgeView` delegates. The yFiles renderer
  shifts the ends of such an edge, capped by the end node's radius in the shift direction
  so that the shifted end stays inside its node and yFiles' cropping at the outline still
  works. The store shifts the labels with their edge and re-places the labels of the
  sibling edges at the end nodes whenever an edge between them comes, goes, is
  reconnected or gets or loses points (the fan-out of the siblings changes then). All
  yFiles canvases share the renderer, so the LTS's opposite-direction pairs fan out too.
- *End handles*: hidden as soon as anything more than one cell is selected (node plus
  edge included), not only for a second edge.
- *Rerouting an existing edge onto its own node had no preview*: the renderer previews an
  edge whose item is a loop while its cell is not (the drag in progress) as the loop it
  will become, `LoopRouter.route(vertex, cell, bounds)` on the node's vertex cell.
- *Edge line styles were missing* from the yFiles editor's popup menu. Not a plan gap but a
  contract mismatch: `GraphCanvas.hasActiveEditor` means "editing canvas, not in preview
  mode" in the JGraph backend and in the controller (the edit menu, value-node display
  and more hang on it), while the yFiles editor canvas had implemented it as "in-place
  editor open", so the controller's edit menu (line styles, add/remove point, reset label
  position) never appeared. The javadoc now says what it means; the yFiles editor
  returns the JGraph answer and offers `isEditingLabel()` to the tests.
- *Label tree counts off, updated at random moments.* A label change reached the canvas
  through `refresh(relabelled)`, which neither backend reported to the canvas listeners,
  so the label tree recounted only on the next structural change. Both backends' `refresh`
  now report the refreshed cells as modified (the contract says so), and the JGraph
  editor had the same defect since 3a, caught by the new parity test in
  `EditorUndoTest`. Cost: a refresh of many cells (`refreshAll`, the LTS's active-path
  refreshes) makes the label tree recompute those cells' entries; it rebuilds the tree
  only when entries actually changed.

Copy/paste (item 4 of the first round) is slice 3c.

Next: 3c, the clipboard.
