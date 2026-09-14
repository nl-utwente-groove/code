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
to the viewer mode, left for a small follow-up; taken up on branch `viewer-node-dragging`
after 3c, see the end of this note), yFiles' orthogonal edge editing and snap
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

**Third review round** (Arend, 2026-09-07), same branch line:

- *Labels could not be moved; dragging on an edge created a point and dragged it about.*
  yFiles' `CreateBendInputMode` creates a bend on any drag over an edge; it is off now
  (points are added by Alt-click). Labels are movable through `MoveLabelInputMode`
  (unselected labels too); only the main label of an edge has a position handler (the
  label decorator hides it for node labels and the multiplicity labels). A finished drag
  is read back from the label's centre into `LABEL_POS` through the new inverse
  `EdgeGeometry.relativePosition` (closest segment, ratio along the polyline, signed
  offset), minus the fan-out shift of a parallel edge, and recorded as a visual edit.
- *Edge points could not be dragged individually.* An edge's `IHandleProvider` (edge
  decorator factory) now yields the handles of its bends, so a selected edge shows them,
  as in JGraph; the handle mode's drag ends in the existing move recording.
- *Labels moved wildly during a drag.* Labels are anchored to the port line by yFiles
  between two placements; the editor now re-places the labels of the affected edges on
  every `Dragged` event of the move, move-unselected and handle modes.
- *Bezier with two points curved at one of them only.* Traced with a path dump: yFiles'
  `cropPath` replaces a final curve segment by a straight line to the intersection,
  which flattened the last quadratic piece and left a corner at the second bend. The
  renderer now clips every path itself at the node outlines (`clipEnds` for all styles,
  `cropPath` returns the path unchanged); yFiles' arrows sit at the path ends as before.
- *Spline edges showed dashed corners.* The decorations (selection stroke, inner line,
  dash shaft, overlay) were drawn along the unsmoothed path; they now follow
  `createSmoothedPath` with the renderer's smoothing settings. The spline itself remains
  yFiles' corner smoothing, not JGraph's interpolating spline through the points.
- *Check mark on the current line style*: done directly rather than as an issue, in
  `SetLineStyleMenu` (check-box items, the selected edge's style checked).
- *Label tree: a new edge's label missing, counts not dropping to zero.* The JGraph
  aspect canvas relays the model's graph rebuilds to the canvas listeners as
  `graphChanged`, on which the type tree rebuilds itself; the yFiles aspect canvas never
  did, so the tree only recounted on structural changes. It relays them now
  (`YFilesAspectCanvas.setViewModel`). Independently, `TypeTree.cellsChanged` rebuilds
  the tree whenever the type graph is another instance than the filter was built from
  (`TypeFilter.isFor`). Note for the record: a label that is *new to the grammar* does not
  enter the tree until the graph is saved, because the implicit type graph is built from
  the saved resources and the typing deliberately swallows the unknown label in the
  editor (`TypeGraph.analyzeHost`, "solved upon saving"); that is pre-existing behaviour,
  unchanged. Tests on both backends use an existing label.

Tests added: `EdgeGeometryTest` (inverse round trip, closest segment),
`EditorLabelTreeTest` (JGraph editor: label gone at zero occurrences, label of a new
edge appears), and in `YFilesEditorTest` the same tree scenario, the Bezier path keeping
its three curve segments, and a label drag recorded as a minor edit and placed where it
was dropped.

**Fourth review round** (Arend, 2026-09-07), same branch:

- *Nodes could not be dragged.* The label move mode (priority 39, before the move mode)
  claimed a press on a node's label, whose position handler was hidden, so nothing moved.
  `shouldMove` now denies every label but the main label of an edge, which the label
  mode honours. Covered by a Robot test in `YFilesSimulatorTest` (`editorTabDragsAVertex`),
  which needs a connected desktop.
- *Arrows ended inside the node.* With the renderer cropping the path itself, yFiles' own
  arrow room was gone: yFiles draws an arrow from the end of the path onwards over
  `IArrow.getLength()`, so the path must stop that far before the outline. `clipEnds`
  now cuts each end short by the arrow's length plus crop length; the Bezier tails do the
  same at their straight pieces.
- *Manhattan arrows never east/west; Manhattan edge selectable up to the first point only.*
  The end points now follow JGraph's `JVertexView.getPerimeterPoint`: the inner point of
  the line to the outline moves to the outer point's coordinate where that lies within
  the node's extent (less a tenth), and for a Manhattan edge a diagonal outer point is
  first pulled into horizontal reach of the source or vertical reach of the target
  (`endPoint` in the renderer, constants `DROP_FRACTION`, `MAX_RATIO_DISTANCE`). Hence a
  level last point enters sideways and the last horizontal move ends at the outline, so
  the arrow points east or west. The adornment corrections of JGraph are not ported. Hit
  testing (`getHitTestable`/`isHit`) now follows the drawn path, smoothed for splines,
  instead of the polyline through the points.
- *A new point not draggable at once; dragging a second point moved the first; a removed
  point's handle stayed.* All one cause: the handles were requeried on selection changes
  only, while a points edit recreates the bends of the edge. The editor requeries the
  handles after every `cellsModified` too.
- *Label tree disappearing momentarily before label entry.* Not reproducible headlessly.
  The one structural difference found: the yFiles component is not a validate root, so
  the in-place editor's text area being added revalidated the panels around the canvas;
  `Viewer.isValidateRoot()` now returns true. To be confirmed by Arend.
- *The Bezier cropping*: reported to yWorks as a question rather than a certain bug
  (draft handed to Arend, `yfiles-cropping-report.md` in the session scratchpad): the
  cropper docs do not promise to preserve curve segments; the observation and workaround
  are stated.
- *Check mark*: in the neutral `SetLineStyleMenu`, so JGraph has it too (confirmed).
- *Headless editor tests.* The editor input mode installs three drop modes whose
  installation creates AWT drop targets, which need a desktop; a disconnected Windows
  session made every editor test fail. The mode's factory methods now return drop modes
  that install nothing, so the editor canvas (and its tests) work headless.

**Fifth review round** (Arend, 2026-09-08), branch `editor-edit-model-review5` off the
merged `editor-edit-model`:

- *A "random" point on choosing a bent line style; Add Point not at the mouse.* The
  point-editing actions (add, remove, line style) now act at a location in graph
  coordinates that is resolved when the action fires: the location of the popup menu
  when invoked from it (the menu items hand it to the action at that moment, so a
  dismissed menu leaves nothing behind), else the current pointer location over the
  canvas (`GraphCanvas.getPointerLocation`, on both backends), else a point beside the
  first segment. The line-style actions never had a location, hence the random-looking
  point. A new point goes into the segment closest by perpendicular distance
  (`EdgeGeometry.closestSegment`), not between the pair with the smallest summed
  distance, which favoured short segments; Remove Point removes the intermediate point
  nearest to the location. Both popups now hand over graph coordinates: the JGraph popup
  passed screen coordinates and the yFiles popup view coordinates, which agreed with
  graph coordinates only at scale 1 without scrolling. Found in passing: the line-style
  action shared one `VisualMap` over the selected edges, so the points added to one
  edge were written to the next; fixed.
- *Edges between stacked or neighbouring nodes slanted, with source and target
  swapped.* The port of JGraph's "smart" perimeter rule moved each end to the
  coordinate of the *other* node's centre, so a small offset between the centres gave
  a slanted line whose ends followed the wrong node. JGraph itself masks this
  asymmetrically: its target end is computed towards the source's perimeter point,
  which makes the line straight at the target's height. Both backends now use one rule,
  `EdgeGeometry.alignedCentres`: if the nodes' extents, less a tenth at either side,
  overlap on one axis, a straight edge runs along the middle of the overlap, vertical
  or horizontal; otherwise it runs from centre to centre, with the smart rule as
  before. The fan-out shift of parallel edges is kept. `DROP_FRACTION` is now shared
  from `EdgeGeometry`. Tests on both backends (`EdgeEndsTest`, `YFilesEditorTest`).
- *FlatLaf tabs.* Unselected tabs were bare labels under an underlined selected one.
  `Options.initTabbedPaneLook` sets card-type tabs with separators on a tab area
  darkened by 8%, the selected tab in the content colour and joined to the content.
  Rendered variants were shown to Arend; the choice is his.

**Sixth review round** (Arend, 2026-09-08), branch `editor-edit-model-review6` off the
merged `editor-edit-model`:

- *Tabs*: Arend chose the second variant: card tabs with separators, the selected tab
  in the (white) component colour, no darkened tab area.
- *Line style change adds a point.* Only the curved styles (spline, Bezier) get a bend
  when the edge has none, since a curve without a bend is a straight line; orthogonal
  and Manhattan make sense without points and get none. The bend goes halfway the edge
  at a small perpendicular distance (`BEND_OFFSET`), so that it shows. The halfway
  point is computed from the *current* vertex centres (`JCellEditAction.shownPoints`):
  the stored end points of an edge do not follow its vertices on either backend (the
  views compute the ends), so the stored ends are stale after a vertex move; the same
  correction applies to the closest-segment search of Add Point. The popup location
  is no longer passed to the line-style menu. `LineStyle.isCurved` is the neutral
  classification.
- *Choosing the current line style* is no edit: edges that already have the style are
  skipped, and the change to the others is one edit rather than one per edge.
- *Line style menu with mixed selections*: enabled when any selected cell is an edge,
  applied to the selected edges only; the check mark shows the style the selected edges
  share, none if they differ.
- *Label tree blink* while a new edge has its empty label: the edge is inserted, the
  graph syncs, the resource model reports "empty edge label not allowed", and the type
  graph derived from the erroneous graph has no labels, so the tree is rebuilt empty
  until the label is committed. This is gh #913 (create-and-label as one undo step)
  seen from the tree's side. With the view-model edit history in place (slice 3a),
  the one-step version is contained: keep the new cell out of the recorded history
  and out of the synced graph until its first label is committed, and remove it when
  the editor is cancelled. Done before 3c, see the next section; the blink is gone
  with it.

## Creating and labelling a cell as one edit (gh #913)

Done on branch `editor-atomic-creation` (2026-09-08), at Arend's request before slice 3c.
The view model gains a *pending insertion* (`GraphViewModel.insertPending`): the cells are
inserted into the store, so they are shown and selectable and the in-place editor can open
on them, but the insertion is neither recorded in the history nor followed by a graph sync.
The graph, the resource model and hence the errors and the derived type graph stay as they
were, so the label tree has nothing to rebuild: the blink of an "empty edge label" error
never arises. The insertion is *settled* in one of three ways:

- `changeLabels` on a pending cell settles it together with the label change, as one
  compound history entry (insertion, then label change): one undo step removes the labelled
  cell. An edge given a blank label is withdrawn instead.
- `settlePendingInsertion` without a label (the editor cancelled, or closed with the text
  unchanged) keeps a vertex, recording its insertion as an edit of its own, and withdraws
  an edge: an edge without a label is no edge. Withdrawal removes the cells from the store
  without a record, since nothing was recorded.
- Any other edit (`doEdit`), and undo or redo, settle a pending insertion first, so the
  history never interleaves with an unsettled insertion.

Without a history (a viewer) or while loading, `insertPending` is a plain `insert`.

The backends call `insertPending` where they create a cell for the editor
(`AspectJGraph.addVertex`/`addEdge` when the editor is to open; the yFiles `addVertex` and
the edge creator), and settle it when the editor closes: on JGraph in
`JGraphUI.completeEditing`, which both the stop and the cancel path go through, after the
stopped editor's value has reached the model through the layout cache; on yFiles in the
text editor's edited and cancelled listeners. If the editor does not open at all, the
creator settles at once. The yFiles edge creator opens the editor `invokeLater`, so it
checks that the edge is still in the model then.

Tests: `EditorUndoTest` (one step for edge plus label; withdrawn on cancel, on an empty
commit and on another edit; vertex kept; the JGraph in-place editor cancelled and
stopped), `EditorLabelTreeTest` and `YFilesEditorTest` (tree unchanged while the edge is
pending; vertex and label one edit; the edge creator's pending edge and loop).

## Slice 3c: the clipboard

Done 2026-09-08 (developed on branch `editor-clipboard` off `editor-atomic-creation`, both
since folded into `editor-edit-model`; Arend's two review rounds, the drag preview and
label refresh and the paste cascade, fixed there). The plan
foresaw an `AspectGraph` fragment; what travels is a **`GraphFragment`** instead, at the
level of cells: the vertices with their editable labels and centres, and the edges
between them with labels, points, label position and line style. Reasons: a paste then
gives back exactly what was copied, including labels that do not parse (an aspect graph
would drop or alter them) and the grouping of edges into cells (which the graph does
not know); and the paste path is the editor's own creation path, labels applied on the
sync. An aspect-graph flavour for other applications can be added on top when wanted;
the text flavour is the label texts.

- `GraphFragment.of(cells)`: vertex cells and the edge cells with both ends among them.
- `GraphTransferable`: a JVM-local flavour for the fragment plus the string flavour.
- `GraphClipboard`: `copy`, `cut` (copy plus `remove`, one edit) and `paste` on an
  `AspectGraphCanvas`, through the system clipboard; a JVM-local clipboard headless,
  and the last fragment copied as fallback when the system clipboard refuses (Windows
  does, in the test JVM). `paste` puts the fragment `PASTE_OFFSET` south-east of the
  copied position and selects the pasted cells; each further paste of the same fragment
  into the same canvas steps another `PASTE_OFFSET` further (the standard cascade). A
  first version centred the fragment at the mouse pointer: with the mouse still over the
  copied cells, the copies landed on top of the originals and looked like no paste at
  all, and otherwise the position followed the mouse; rejected in review.
- `AspectGraphViewModel.insertFragment(fragment, dx, dy)`: fresh vertices with new node
  numbers and the fragment's labels, edges between them, one `insert` edit.
- `AspectEditorTab`: the cut, copy and paste actions call the clipboard instead of
  wrapping Swing's `TransferHandler` actions; the paste button follows
  `GraphClipboard.hasFragment()`. JGraph's transfer handler is no longer used for the
  actions (its drag-and-drop stays untouched).
- **Ctrl+drag** on yFiles (`YFilesAspectEditorCanvas.configureMove`): when a move mode
  queries its position handler with Control down, the canvas substitutes a
  `CopyDragHandler` that wraps the mode's own handler (initialised and cancelled
  around the drag, so that the affected items are registered as usual) and never
  moves the items; instead it drags a `CopyDragPreview`, a translucent rendering of the
  dragged vertices, the edges between them and their labels (their styles' visuals in a
  translated `VisualGroup` on the input-mode group). The mode's DragFinished listener
  then calls `copyMoved` with the preview's offset: the fragment of the vertices plus
  the edges between them is inserted at that offset, one edit, the copies selected. A
  first version moved the items themselves and put them back at the end, which
  showed no preview and was rejected in review. JGraph clones on Ctrl+drag natively
  (`setCloneable`, its `cloneCells` into `AspectJModel.insert`, which records one edit).
- Inserted cells are refreshed on the canvas after the model reacted to the edit
  (`GraphViewModel.apply`, and the settled pending insertion): the aspect model builds
  the graph elements of a fresh cell from its labels only in that reaction, and the
  yFiles store fixes the label text of an item at creation, so pasted or copied cells
  showed up unlabelled until the next edit. JGraph reads the visuals at paint time and
  never showed the gap.

Tests: `EditorClipboardTest` (core: fragment content, paste as one edit with fresh
numbers and offset positions, cut and paste, empty selection) and
`YFilesEditorTest.controlDragCopiesTheDraggedCells`.

With 3c, **phase 3 is complete** (2026-09-08). Accepted as cosmetic differences of the
yFiles editor against JGraph: edge curves are yFiles' corner smoothing rather than the
interpolating spline, and JGraph's arrow adornment corrections are not ported. Outside
the migration: gh #882 (zoom at the mouse, both backends), gh #915 (JGraph snap
semantics), gh #916 (the popup actions on the menu bar).

## Node dragging in the viewer canvases

Deferred in phase 2 slice 4c and again in 3b; taken up on branch `viewer-node-dragging` (2026-09-08).
A `GraphViewerInputMode` has no move mode of its own, and nothing in the guide or the
demos adds one to it; the stand-alone use of a `MoveInputMode` is documented (hit test,
position handler, priority, then `MultiplexingInputMode.add`, the pattern of the image
export and printing demos), so `YFilesCanvas.configureMove` adds one such mode, right before
the marquee selection in priority (the viewer mode's marquee has priority 30 and its
click mode 10, unlike the editor mode's 50 and 150 given by the guide; a first version
took the editor's move priority 40, sat behind the marquee, and never saw a press,
which Arend reported as dragging not working at all): a drag starts on a vertex that
passes the selectable predicate and moves the selection if the vertex is part of it,
else the vertex alone (the position handlers of the items combined through
`IPositionHandler.combine`), so that one mode covers what the editor's selected and
unselected move modes do; the mode is enabled in select mode and off in pan mode. The
drag ends as it does in the editor: `recordMove` and `followDrag` (labels following
the dragged edges) moved from the editor canvas into the base, generic over the graph
type, ending in the hook `applyMove`, whose default is one `edit` of the recorded
centres and points and which the editor overrides for the reconnection of a dragged
edge end. Without an edit history the change applies directly and updates the layout
map, which the view tab persists as before. Tests: `YFilesCanvasTest
.viewerMovesVerticesThroughTheEditFunnel` (headless: mode present and switched with
the view mode, hit test, a recorded move landing in the visuals, the layout map and
the incident edge, and the gesture itself as synthetic mouse events on yFiles' input
surface, which is what caught the priority) and the Robot test `YFilesSimulatorTest.viewTabDragsAVertex` on the
host view tab, which needs the canvas on screen. Not documented, and therefore not
mirrored: how the editor mode configures its two move modes, and the defaults of a bare
`MoveInputMode`.

Next: phase 4 (export and Imager on the facade).
