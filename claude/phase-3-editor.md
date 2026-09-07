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
3. **3c — the clipboard**: the GROOVE transferable and the paste edit on both backends.

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

Next: 3b, the yFiles editor canvas.
