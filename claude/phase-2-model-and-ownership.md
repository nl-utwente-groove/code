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
   ~~The backend cell classes stay (`AJCell` and role subclasses); the neutral cell interfaces
   are their contract.~~ *Superseded in slice 4: the cells are neutral classes
   (`gui.view.cell`), the backends show them through items of their own, see below.*
   `getCells()` comes from the store because the element-to-cell maps
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
allowlist holds only `AspectEditorTab`.

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
replays it. `AspectEditorTab`'s `GraphUndoManager` coupling is the last thing that needs
JGraph's edit objects, and it goes when that model exists.

## Slicing

1. **View-model split** (backend-independent): `CellStore`, the insertion machinery and
   role subclasses of `GraphViewModel`; `JModel` and subclasses become adapters with
   delegating stubs, so the eleven remaining client files compile unchanged.
2. **Ownership inversion**: `GraphBackend`, controller-owned canvases, neutral panel and
   accessors, allowlist down to `AspectEditorTab`.
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
`getNonNullViewModel()` is up for the same review in the next slice.

**Slice 2 done (2026-09-05, branch `ownership-inversion`).** `GraphBackend` (neutral) is the
canvas factory with one method per role; `GraphBackend.instance()` selects the backend once,
by `ServiceLoader` discovery: what is on the module path (or, for the installed application,
the class path via `META-INF/services`) is what is available, so a distribution without the
yFiles module has one backend and nothing to configure, and `gui.view` never names a backend.
A provider that fails to instantiate is logged and skipped, so a licensing failure of the
yFiles backend falls back to JGraph instead of refusing to start. Arend rejected the first
version, a JVM property: the choice is not a launch parameter, and availability decides
before preference does. When a second backend exists (slice 4), a persisted user preference
chooses among the discovered ones, applied at the next start, with yFiles the default when
present. `JGraphBackend` is the JGraph implementation. `GraphViewController` is abstract, constructed with just the simulator, and
creates its canvas on first `getCanvas()` through the abstract `createCanvas(backend)`; the
canvas attaches itself in its constructor (`attachCanvas`), so listener installation during
construction can already ask the controller for its canvas, and a canvas that fails to attach
is an `IllegalStateException`. The role configuration moved to the controllers:
`AspectGraphViewController(simulator, DisplayKind, editing)` holds the graph role, the
for-state flag and the editing flag; `getGraphRole()` is a controller method and the canvas
default delegates to it; `isForState()` left the canvas interface. `PlainGraphViewController`
serves the preview dialog's fallback. View models are created by the canvas
(`newViewModel()`, role-covariant) and shown with `setViewModel()`, whose JGraph
implementation sets the model's `CellStore` (the `JModel` adapter) as the JGraph model, so the
`newModel()`/`setModel()` idiom of the displays became `newViewModel()`/`setViewModel()`
without a backend type. `JGraphPanel` became the neutral `GraphPanel`. `AspectViewTab` no longer
listens to JGraph's undoable edits to store layout changes; it stores the graph on the
canvas listener's `cellsChanged`, skipping changes while the model is loading, which is the
same condition the undo listener achieved by being registered only after loading. The
architecture allowlist holds only `AspectEditorTab` (phase 3). Not done: the tests still
name the display kinds, and `AspectEditorTab` casts the controller's canvas to `AspectJGraph`
for its undo manager. On Arend's review: the four graph-showing components (`StateDisplay`,
`LTSDisplay`, `AspectViewTab`, `AspectEditorTab`) implement `GraphDisplay<G>` in `gui.display`,
with `getController()` and `getGraphPanel()` as the primitives and `getCanvas()`/
`getViewModel()` as defaults through the controller; the components specialise the return
types to their role. That replaced the `getCanvas().getController()` round trips the rename
had left, and the `instanceof` chains over the four classes in `DisplaysPanel`,
`ExportAction` and `Simulator`.

**Slice 3 done (2026-09-05, branch `backend-module`), narrower than planned.** The plan
said core + `backend-jgraph` + optional `backend-yfiles`; two facts changed that. The JGraph
backend cannot leave the core module while `AspectEditorTab` needs JGraph's undo objects
(phase 3), so a `backend-jgraph` module has nothing to be separate from yet. And turning the
repository into a Maven reactor with a root aggregator is the phase-5 work of the module
split (gh #887), with seven recorded build-side work items (flatten plugin, release-reactor
couplings, fixture literals, per-module test categories, resources, javadoc, tooling) that
Arend has put on hold behind a package restructuring; the yFiles slice should not pull that
in. What the license constraints actually require is only that the yFiles backend is an
optionally compiled unit. So `yfiles/` is a **separate Maven project inside the repository**,
on the pattern of `release/`: artifact `groove-yfiles`, depending on the
`nl.utwente.groove:groove` artifact and on `com.yworks.yfiles:yfiles-for-java-swing` from
the developer's local repository. The version of the core dependency is the pom's own
`revision` property, whose default is kept equal to the main pom's (release checklist), so
that in Eclipse m2e's workspace resolution binds the dependency to the open `groove`
project (no artifact, changes visible at once) and on the command line the installed
artifact is found; `-Drevision` remains for release scripts. Against a stale installed
artifact, the guard is that a command-line build of the unit first installs the core from
the repository root (tests and javadoc skipped; `-Dgroove.install.skip=true` opts out); an
m2e lifecycle mapping keeps that step out of Eclipse. The runtime
license file is copied to the class-path root from a directory named by the property
`yfiles.license.dir`, so it never enters the repository. The unit runs on the class path
without `module-info`: the yFiles documentation does not state the jar's module name, and the
installed application runs from the class path anyway. It provides `YFilesBackend` as a
`GraphBackend` service (`META-INF/services`); the canvases are slice 4, until then the
provider refuses to create them and the unit is on no launch's class path.
`GraphBackend` gained `getName()` and a fixed `RANKING` (yFiles before JGraph) among the
discovered backends, so presence of the unit selects it; the persisted user preference is
still slice 4. Not done: Eclipse project files and JDT null-analysis settings for the new
project, and the null-check skill covers only the main tree.

**Slice 4 plan (2026-09-05).** The role cells (`AspectJVertex`, `AspectJEdge`, the LTS and
control cells) turned out to contain no JGraph at all, only their base classes did: the
`DefaultGraphCell` superclass, the ports carrying the edge structure, and the attribute map
derived from the visuals. Single inheritance kept a yFiles canvas from reusing them. Two
options were put to Arend: (A) neutral cell classes with the JGraph cells delegating to them
(sixty forwarding methods per role, two objects per cell, a back-link for structure);
(B) the neutral cells are the only cells, own their structure, and both backends show them
through items of their own. **Decision B** (Arend, 2026-09-05). Sub-slices: 4a neutral
cells; 4b a yFiles canvas for aspect graphs, read-only, on the spike's style mapping;
4c LTS and control canvases; 4d yFiles layouts through the backend layouter palette.

**Slice 4a done (branch `yfiles-canvas`).** `gui.view.cell` holds `AViewCell`,
`AViewVertex`, `AViewEdge` and the role cells `AspectVertexCell`, `AspectEdgeCell`,
`LTSVertexCell`, `LTSEdgeCell`, `CtrlVertexCell`, `CtrlEdgeCell`, `PlainVertexCell`,
`PlainEdgeCell`: the old logic with the JGraph accessors replaced. A cell belongs to a view
model (`setViewModel` rebinds a pasted cell), reaches its controller through it, and carries
the backend item that shows it (`getItem`/`setItem`). Structure is neutral: a vertex owns
its context (incident edge cells, insertion-ordered), an edge its end vertices;
`AViewEdge.setSource`/`setTarget` register the edge with the vertices. The view model creates
the cells (`newVertex`/`newEdge`, through the abstract `createVertexCell`/`createEdgeCell` of
the role models; `CtrlGraphViewModel` and `PlainGraphViewModel` exist for that), so
`CellStore` lost its factory methods and keeps `insertCells` and `getCells`. Annotating the
neutral classes required annotating the cell interfaces, which made three genuinely nullable
members explicit (`getEdge()`, `getNodeIdString()`, the `getDirect` argument).

On the JGraph side the cells are wrappers: `JCell` (abstract, `DefaultGraphCell`), `JVertex`
(with the port) and `JEdge` (implementing JGraph's `Edge`); each holds its neutral cell and is
its item. The wrapper derives the attribute map from the cell's visuals, rebuilt when the
cell is re-initialised with a fresh visual map; it forwards JGraph's user object, through
which an in-place edit arrives (`DefaultGraphModel.valueForCellChanged` calls
`setUserObject`), to the editable labels of an aspect cell; and `JEdge.setSource`/`setTarget`
mirror every JGraph connection on the neutral edge, so the neutral structure follows edits,
removal (JGraph disconnects removed edges through its connection set) and undo (which
reconnects the same way). Cloning a wrapper clones its cell (`AspectJModel.cloneCells` then
rebinds and renumbers as before). `JModel` creates the wrappers in `insertCells`, keeps them
as roots, and unwraps in `getCells()`; `JGraph` unwraps at its 29 cell-identity sites
(`JCell.of` and `JCell.items` are the two directions) and creates the view model of a model
through `createViewModel(store)`, which replaced the `JGraphFactory` and the per-canvas
factories. The views (`JVertexView`, `JEdgeView`) keep `getCell()` for the JGraph item and
gained `getViewCell()`. The old cell classes and `JGraphFactory` are deleted; the backend
package is 1300 lines lighter and holds no cell logic any more.

**Review findings on 4a (2026-09-06).** Arend's click-through found the UI still handing
neutral cells to JGraph's selection model and to `startEditingAtCell` (single-click selection
invisible, double-click editing dead, an NPE on the missing root handle); fixed by mapping
through `JCell.of`/`JCell.items` at the seven sites in `JGraphUI`. The undo symptoms he saw
(phantom undo steps, redo restoring an edge but not its label) are older than the branch: the
selection listener showed selected cells through `GraphLayoutCache.setVisible`, which posts an
undoable layout-cache edit even for visible cells, and the redo of such an edit re-selects (the
cache selects inserted cells in editing mode) and truncates the redo history. The listener now
shows hidden cells only; `EditorUndoTest` replays add-edge, select, label-edit, undo, undo,
redo, redo headless. Lesson for the yFiles canvas: selection must never enter the model's edit
history. The full run with the `GuiTest` category also caught a regression of slice 1:
`LTSGraphViewController.reactivate` fetched the model from the canvas while `LTSDisplay` loads
a new model detached, so the assertion in `getNonNullViewModel()` killed the simulator model's
transaction and every GUI test after it; `reactivate` now takes the model. Note that the
`GuiTest` classes run headless under surefire here after all, so they are part of the handover
bar, and that surefire's `-Dtest` takes comma-separated classes (`+` matches nothing and passes
silently).

**Slice 4b done (2026-09-06, branch `yfiles-canvas-aspect`).** The read-only aspect canvas
on yFiles: `YFilesAspectCanvas` (final) on the abstract `YFilesCanvas<G>`, its store
`YFilesCellStore<G>`, and the rendering in `CellNodeStyle`, `CellEdgeStyleRenderer` and
`CellStyles`, all in the optional unit. Decisions:

- *Composition, not inheritance.* `GraphComponent.getGraph()` and `getSelection()` are final
  and clash with the facade's methods of the same names, so the canvas owns an inner
  `Viewer extends GraphComponent` (overridden only for the hatch overlay and the tooltips)
  and hands it out as `getComponent()`. The component scrolls and zooms itself, so
  `GraphCanvas` gained `hasOwnScrolling()` (default false) and `GraphPanel` places such a
  component directly instead of in a scroll pane; those are the only core changes.
- *The cell is the item's tag, the item is the cell's item.* The store keeps a master
  `DefaultGraph` whose nodes, edges and labels are tagged with the cells (labels with a
  `Placement` record naming the label's role and position, because yFiles label-model
  parameters are not documented as comparable) and shows it through a `FilteredGraphWrapper`
  whose predicates are the cells' visibility: hiding is a predicate change, not a removal, and
  `refresh` re-reads the visuals into the geometry (`updateItem`) and re-evaluates the
  predicates. Selection never touches the graph (the 4a lesson); the one loss against JGraph
  is that selecting a hidden cell cannot show it.
- *Stateless styles reading the visuals at paint time.* `updateVisual` re-reads the cell's
  `VisualMap` on every repaint, which is fine for rule-sized graphs and is to be revisited
  for the LTS in 4c. The JGraph rendering rules are reproduced from `JVertexView`/`JEdgeView`:
  the 6px extra border inside the bounds, where adornments, emphasis and the error overlay
  are painted; the insets of `computeInsets`; emphasis as line width +2 with a darkened fill
  and a dashed selection border; loops through `LoopRouter` with the three-point
  perpendicular shape; BEZIER through `InterpolatingBezier`; Manhattan paths; SPLINE
  approximated by yFiles' corner smoothing. Edge label positions map JGraph's
  `(permille, signed distance)` onto `EdgePathLabelModel`, with the distance corrected by
  half the label height because yFiles measures to the label border.
- *The `edit` funnel* applies the visuals, updates the items, synchronises the layout map
  and fires `cellsChanged`; it is not undoable (phase 3).
- *Backend delegation.* `YFilesBackend` builds the viewing aspect canvas on yFiles and takes
  the editor's aspect canvas and the LTS, control and plain canvases from the JGraph backend
  found as a service (no dependency on `gui.jgraph`), so a launch with the unit on the class
  path works as a whole until 4c and phase 3 replace them.

`YFilesCanvasTest` (seven headless tests on the ferryman start graph: tagged items and
geometry, selection mirrored into emphasis with one event per change, hidden cells leaving
the shown graph, hit testing, Spring layout through the edit funnel, image export, a detached
model with items) passes with the licensed jar. Process note: the tests need
`-Dyfiles.license.dir` and the licensed library at run time; the auto-mode classifier refused
Claude's run, so Arend ran them (PowerShell needs the `-D` arguments quoted). Inputs used
for the yFiles side, per the ground rules: the bundled developer guide and Javadoc, the demo
sources (tutorial01 step 6, tutorial02 steps 8 and 21, viewer/graphviewer, imageexport,
tooltips, input/popupmenu, singleselection, the SVG export), the spike, and compiler errors.

Residues: the perimeter "drop" logic and adornment corrections of JGraph's
`getPerimeterPoint` are omitted, as are the Manhattan end-point shifts; the inner line of
double-lined edges has no arrowhead; `JAttr`'s constants are duplicated in `CellStyles`;
`getBackendLayouters()` is empty (4d); the persisted backend preference is not done;
right-click selection behaviour and scroll/zoom parity are unverified in the Simulator, which
is Arend's click-through (Eclipse: import `yfiles/` as a Maven project and put it on the
launch's class path).

**Review round on 4b (2026-09-06).** Arend's first click-through: no edges at all, no popup
menu; selection, tooltips, scrolling and zooming fine. Two causes and a lesson about the
tests. (1) The store created the yFiles edge before connecting the cell to its end vertices,
and a `FilteredGraphWrapper` evaluates its predicate at creation, so the edge counted as
invisible (an edge without source is); and the value stayed cached, because connecting an
edge never marked `VISIBLE` stale (nor did a vertex's context change, on which a data node's
visibility depends). JGraph never asked before the whole insertion had executed, which is why
that backend never noticed. The neutral cells now mark the visibility stale on connect and
disconnect, and the store connects all cells of an insertion before creating any item.
(2) The popup menu was shown from a Swing mouse listener on the `GraphComponent`, which does
not receive the events; it now goes through yFiles' popup-menu input mode, whose PopulateMenu
event supplies the menu to fill and the queried location (the documented way), with
`PopupMenuItems` left at NONE so that the item-specific event is never involved. The lesson:
the headless tests looked at the master graph through the cells' items and counted nodes in
the shown graph, never edges, and never painted. `YFilesCanvasTest` now counts the shown
edges, checks that a filtering `TypeTree` on the controller hides nothing, and probes a pixel
of the exported image just outside a node on the longest straight edge.
`YFilesSimulatorTest` goes further and launches a Simulator (preferences kept in memory by a
copy of the GUI suite's factory, installed through surefire's `argLine`): it checks the state
display, the host tab and a rule tab (which adds the level tree) for shown cells and for a
painted edge in both the creating and the updating paint pass, and opens the popup menu with
a `Robot` right click. It shows a Simulator window while it runs, and it is the check to run
when an Eclipse launch and the Maven build seem to disagree.

**Review round 2 on 4b (2026-09-07, branch `yfiles-canvas-fidelity`).** Arend's second
click-through, with edges and popup menu working, listed rendering differences and a crash on
switching grammars. The diagnosis was done by rendering the same rules with both backends to
images (a throwaway probe test, not kept) and reading JGraph's sources, which are in the local
repository as a sources jar. Findings and fixes:

- *Edge label placement.* JGraph places an edge label by distributing the stored permille
  along the whole polyline and offsetting perpendicularly; GROOVE's `JEdgeView.getLabelVector`
  override only affects the label handle, since label transforms are off. The TikZ exporter
  already carried a port of that computation; it is now `EdgeGeometry.labelPosition` in
  `gui.view`, used by both. The yFiles store computes the point and expresses it relative to
  the port line with a `FreeEdgeLabelModel` parameter (yFiles' ratio/distance convention was
  checked to coincide with JGraph's, screen y down), so the label follows its edge; the label
  of a loop, whose ports coincide, goes on the loop path with `EdgePathLabelModel`.
- *Edge label look.* JGraph draws edge labels in the LAF's default label font (bold under
  Metal, plain under FlatLaf), ignores the font visual for edges (the `REGULAR` look's italic
  is why `!moored` came out italic on yFiles), and paints an opaque box with a one-pixel
  margin behind them. The yFiles edge label style now does the same; node labels keep the
  font visual.
- *Dashes.* yFiles' `DashStyle` is in units of the pen width (the docs do not say so; the
  embargo look's width 5 made the 2-2 pattern five times too long); the pattern is divided by
  the width.
- *Curves.* JGraph computes a Bezier curve from the points where the edge leaves its end
  nodes, not from the centres, which changes the shape; the renderer now clips the end points
  to the node outline first (through the node style's `IShapeGeometry`). And yFiles derives
  arrows from straight segments (`GeneralPath.getTangent` "treats Bézier curves as linear"), so
  a curve up to the end point lost its arrowhead; each curve now starts and ends with a
  two-pixel straight piece along its tangent, which is also where JGraph takes the arrow
  direction from (the last control point).
- *Grammar switch crash.* The store evaluated the cells' visuals while inserting them into a
  model not yet shown; visuals consult the canvas' label tree, which still served the previous
  model, and for a grammar with another type graph the `TypeFilter` asserted (an NPE with
  assertions off). JGraph never evaluates before painting, when the tree has adopted the
  model. The store therefore leaves its items unconfigured while detached (placeholder layout,
  no labels, filter predicates true) and configures them all on `attach`, which the canvas
  performs *after* firing `viewModelChanged`, so the label tree has adopted the model by
  then; visibility is invalidated at that point too. `LabelTree.isIncluded(cell)` also
  answers true for cells of another model, as a guard.
- *Watermark.* The academic license paints a watermark along the top of every canvas; the
  content margins and the initial view point keep the graph 40px below it (Arend's
  alternative, a permanent transformation between graph and canvas coordinates, was judged
  overkill by him).

Not reproduced: the slightly larger space above node labels that Arend sees. Pixel dumps of
the two renderings show identical text placement inside the node box under Metal; a
screenshot from the Simulator (FlatLaf, possibly HiDPI) is needed. `YFilesSimulatorTest`
now also switches grammars and collects event-thread exceptions; its right-click test
assumes a visible desktop (a locked screen captures black and delivers no clicks).

**Review round 3 on 4b (2026-09-07, branch `yfiles-canvas-look`).** Four remarks on the
merged round-2 result, three of them changes:

- *Dashes.* Dividing the pattern by the width was not enough: probing yFiles pens at
  several widths, caps and patterns showed that they also cap every dash by half the width,
  whatever `setEndCap` says, so the embargo look's 2-2 pattern at width 5 comes out solid.
  A pen cannot render GROOVE's absolute patterns on thick edges. The pen of a dashed edge is
  therefore transparent (keeping its width for hit testing) and the renderer's decoration
  paints the shaft with the same `BasicStroke` (butt caps, absolute dashes) that draws the
  node borders. A test samples a dashed edge of `arrive-empty` for dashes and gaps.
- *Arrow heads.* Arend wants the embargo head filled, in both backends: the `EMBARGO` look's
  target end is now `ARROW` (JGraph draws a 10-by-10 head on the width-5 shaft, which is
  acceptable). yFiles' built-in arrows scale length and width together (a `TRIANGLE` at
  scale 1 measures 12 by 7), which cannot give a head that clears a thick shaft, so
  `CellArrow` implements `IArrow` with JGraph's geometry: length is the end's size, width is
  at least that and at least the line width plus a 3px margin on either side; closed heads
  stop the edge rendering at their base (`getLength`), simple heads let it run to the tip.
  The anchor/direction contract (tip at the anchor, head extending back along the
  direction) is the developer guide's custom-arrow section.
- *Label boxes.* Edge labels are opaque in the background colour of the canvas rather than
  white; the style is cached per colour, and a background change re-styles the edge labels
  of the shown model (states and disabled start graphs have coloured backgrounds).
- *Fonts.* Not changed: Arend asks to be consulted where JGraph's choices are idiosyncratic
  rather than to copy them. The open question is whether edge labels should honour the font
  visual (the `REGULAR` look's italic for regular-expression edges, which JGraph ignores only
  because its edge labels never received the visual), and if so whether a plain negation
  such as `!moored` should count as regular.
