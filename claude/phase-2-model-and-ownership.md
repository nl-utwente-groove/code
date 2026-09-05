# Phase 2 design: the model layer and the ownership inversion

Design record for phase 2 of the yFiles migration (gh #909), written 2026-09-05 before any
phase-2 code moved, at Arend's request. It answers two questions raised at the end of
phase 1b: where the backend-independent code still inside `AspectJModel` and friends lands,
and whether the eventual architecture has a model layer at all. Plan and ground rules:
`claude/yfiles-migration.md`; the phase-1b facade: `claude/view-facade.md`.

## The three layers

The end state has three layers per graph view, all in `gui.view` except the first:

- **Canvas** (backend, `GraphCanvas` implementations): rendering, hit-testing, selection,
  in-place editing, the backend's own geometry (node sizing, loop routing, label placement).
  For JGraph this is `JGraph` plus a thin `JModel` adapter that presents the view model's
  cells as a `GraphModel` and turns JGraph edits back into view-model changes.
- **View model** (neutral, `GraphViewModel` and role subclasses): the content shown — the
  graph, its layout map, the cells and the element-to-cell maps — and the role-specific
  semantics of that content: how an aspect graph is rebuilt from edited cells, how an LTS
  is loaded incrementally under a state bound, which errors decorate which cell.
- **Controller** (neutral, `GraphViewController` and role subclasses): actions, menus,
  display options, layouters, the simulator binding. One per canvas, long-lived.

Why a separate model layer rather than folding content into the controller: models are
swapped while the controller stays (`showGraph`, the LTS reload, the editor's preview clone);
models exist detached from any canvas (`StateDisplay` builds the start-state model and reads
its cells' visuals before showing it — the Ferryman NPE of slice 3 was exactly this); the
label trees track model identity; the exporters read models. None of that involves a canvas
or a controller. The phase-1b decision to answer `getTypeGraph`/`getResourceModel` from the
cell rather than the canvas is the same principle one level down.

## What moves where

| Today (backend) | Lands in | Notes |
|---|---|---|
| `JModel`: `loadGraph`, `addElements`, `addNode`/`addEdge`, `computeJVertex`/`computeJEdge`, `prepareInsert`/`doInsert`, random positions for unlaid-out nodes | `GraphViewModel` | The orchestration `LTSJModel` overrides (`addNodes`/`addEdges`) moves with it, so the split does not cut it; the residue note of phase 1b that deferred this for that reason is thereby resolved |
| `JModel`: `isMergeBidirectionalEdges`/`isMergeAllEdges` | `GraphViewModel` | read from the controller's options |
| `AspectJModel`: grammar, `syncGraph`, the modification counter and its listeners, derived resource model and type graph, `loadViewErrors`, node numbering, resource properties, `beingEdited`, qualified name, `loadGraph` | `AspectGraphViewModel` | `syncGraph` needs only cells, `applyEditableLabels`, the grammar and `setJCellMaps`, all neutral already |
| `LTSJModel`: `GTSListener`, exploring buffer, state bound, accept tests, `reloadGraph`, `loadGraph` | `LTSGraphViewModel` | accept tests read the LTS controller's options; refresh, layout and scroll requests go through the controller |
| `JModel`: `getRoots`, `DefaultGraphModel` plumbing (ports, insert/remove/edit, attribute maps, undoable edits), the `fireGraphChanged` override, `vetoFireGraphChanged` | stays backend | the JGraph event adapter; it *detects* that an edit was structural and asks the view model to `syncGraph` |
| `AspectJModel`: `insert`/`remove`/`cloneCells`/`acceptsSource` overrides, `cloneWithNewGraph` | stays backend | JGraph clipboard and connection semantics; the clone asks the view model for fresh node numbers |

Two inversions make this possible:

1. **Cells.** The view model creates cells, so the backend contributes a cell store:
   ```
   interface CellStore<G> {                     // implemented by the backend model adapter
       ViewVertex<G> newVertex(Node node);       // bound to the store, initialised
       ViewEdge<G> newEdge(@Nullable Edge edge);
       void insert(List<ViewVertex<G>> vertices, List<ViewEdge<G>> edges,
                   List<Connection<G>> connections, boolean replace);   // one undoable edit
       Collection<? extends ViewCell<G>> getCells();   // the z-ordered cells, authoritative
   }
   ```
   The backend cell classes stay (`AJCell` and role subclasses); the neutral cell interfaces
   are their contract. `getCells()` comes from the store because the element-to-cell maps
   are not authoritative in the editor: a canvas-side insert (a new node, a paste) exists as
   a cell before `syncGraph` rebuilds the maps from the cells.
2. **Direction.** Today `JModel` owns its `GraphViewModel`; afterwards the view model is the
   content and the backend adapter observes it. Construction stays with the canvas
   (`newModel()` creates the adapter, which creates its view model with the controller and
   itself as store), until the ownership inversion of slice 2.2 moves it to the controller.

The view model knows its controller (for options and for refresh/layout/scroll requests
during incremental LTS updates), as `JModel` knows its `JGraph` today. It never names a
backend type.

## Ownership inversion (slice 2.2)

Displays and tabs construct a controller (per role), and the controller obtains its canvas
from a backend factory chosen once at start-up:

```
interface GraphBackend {                        // one implementation per backend, selected at start-up
    AspectGraphCanvas newAspectCanvas(AspectGraphViewController controller);
    LTSGraphCanvas newLTSCanvas(LTSGraphViewController controller);
    CtrlGraphCanvas newCtrlCanvas(CtrlGraphViewController controller);
    GraphCanvas<Graph> newPlainCanvas(GraphViewController<Graph> controller);
}
```

`JGraphPanel` becomes a neutral `GraphPanel` wrapping `canvas.getComponent()`; the
`getJGraph()`/`getJModel()` accessors on displays and tabs become `getCanvas()`/
`getViewModel()` typed by role; `GraphPreviewDialog`, `Imager` and the three headless tests
go through the backend factory. Backend selection is a system property or preference with
JGraph as default; no runtime switching (decided in the plan). After 2.2 the architecture
allowlist holds only `GraphEditorTab`.

## Modules and the yFiles canvas (slices 2.3, 2.4)

2.3 restructures into core + `backend-jgraph` + optional `backend-yfiles` (Maven profile;
the yFiles jar only ever comes from a local repository, per the license ground rules).
2.4 implements `GraphCanvas` on yFiles' `GraphComponent` for the viewer roles, with cells as
yFiles items carrying the neutral cell objects as tags, yFiles layouts writing through the
`LayoutMap`, and the `BEZIER`/`SPLINE` line styles rendered through the neutral
`InterpolatingBezier`. The LTS canvas must handle the incremental `insert` path at the
scale measured in the spike.

## Consequence for phase 3

With edits as view-model operations, the GROOVE-owned undo model of phase 3 lives in the
model layer: an edit is a change to the view model that both backends reflect, and undo
replays it. `GraphEditorTab`'s `GraphUndoManager` coupling is the last thing that needs
JGraph's edit objects, and it goes when that model exists.

## Slicing

1. **View-model split** (backend-independent): `CellStore`, the insertion machinery and
   role subclasses of `GraphViewModel`; `JModel` and subclasses become adapters with
   delegating stubs, so the eleven remaining client files compile unchanged.
2. **Ownership inversion**: `GraphBackend`, controller-owned canvases, neutral panel and
   accessors, allowlist down to `GraphEditorTab`.
3. **Modules**: core, `backend-jgraph`, optional `backend-yfiles`.
4. **yFiles read-only canvas**: graph tabs, state display, LTS; layouts.

Slices 1 and 2 need no yFiles code and can run in any session; 3 and 4 need the jar and
Arend driving.

## State of work

**Slice 1 done (2026-09-05, branch `view-model-split`).** `CellStore<G>` (neutral) is
implemented by `JModel`: `newVertex`/`newEdge` create factory cells bound to the model,
`insertCells` is the former `doInsert` (one `DefaultGraphModel` edit), `getCells` are the
roots. `GraphViewModel` owns the loading and insertion machinery (`loadGraph`,
`addElements`, `addNodes`/`addEdges` as overridable hooks, `addNode`/`addEdge`,
`computeVertex`/`computeEdge`, the pending-cell bookkeeping, random positions for unlaid-out
nodes) and reads the merge options from its controller; `getCells()` now comes from the
store, which is what makes `syncGraph` correct in the editor. `AspectGraphViewModel` holds
the grammar, `syncGraph`, the modification counter, the derived resource model and type
graph, error decoration, node numbering (`createAspectNode`, batched through
`startNodeNumbering`/`stopNodeNumbering` for the clipboard), properties, the `beingEdited`
flag and the qualified name; `LTSGraphViewModel` is the `GTSListener` with the exploring
buffer, state bound, accept tests and incremental `addNodes`/`addEdges`. `AspectViewVertex`
gained `setNodeFixed()`. The three JGraph models keep their `DefaultGraphModel` overrides
(`insert`, `remove`, `cloneCells`, `acceptsSource`, `fireGraphChanged`) and delegate
everything else, so the eleven remaining client files compile unchanged; the view model
is created by `createViewModel()` in the `JModel` constructor with the canvas' controller.
Nothing in `gui.view` names a backend type. Not moved: `cloneWithNewGraph` (it needs a new
backend model; goes with the ownership inversion).

**Asserting accessors (decided 2026-09-05).** `GraphViewModel` has no public
`getNonNullGraph()`: the graph is `@Nullable` only between construction and the first
`loadGraph`, which every construction path performs at once, so a view model without a graph
is never a meaningful object. Callers outside the model assert locally with their own reason
(the exporter, the forest layouter, the LTS filter); inside `AspectGraphViewModel` the
asserting accessors for graph and grammar stay as private helpers. Slice 2 makes the graph a
constructor argument, after which `getGraph()` is `@NonNull` and the helpers go. The canvas
is different: an empty canvas is a real state, so `getViewModel()` stays `@Nullable` there and
`getNonNullViewModel()` is up for the same review in slice 2.
