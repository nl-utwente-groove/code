# Graph-view facade — phase 1b design note

Part of the yFiles migration (gh #909; plan and state in `claude/yfiles-migration.md`, phase-1a
record in `claude/jgraph-controller-split.md`, spike results in
`claude/yfiles-spike-findings.md`). Phase 1b defines the seam behind which the visualization
backend becomes swappable. This note is the design record: inputs, the interfaces, the
neutral geometry model, the slicing, and the decisions taken or still open.

## Inputs

**Consumer census (2026-09-03).** Outside the backend package `gui.jgraph`, 63 main-tree files
and 2 test files reference backend types (`JGraph`, `JModel`, the `AspectJ*`/`LTSJ*` cells,
`JAttr`, `JGraphMode`) or `org.jgraph` directly. What they ask for, by weight:

| need | dominant members (sites / files) |
|---|---|
| reach the controller | `JGraph.getController()` (41 / 16) |
| reach cells for graph elements | `JModel.getJCellForNode` (17 / 6), `getJCellForEdge` (12 / 4), `getJCell` (3 / 1) |
| show a graph | `newModel` + `loadGraph` + `setModel` (8+11+11 / 7) |
| selection | `setSelectionCells` (12 / 9), `getSelectionCells` (9 / 9), `addGraphSelectionListener` (9 / 7), `clearSelection`, `isSelectionEmpty`, `getSelectionCell` |
| refresh after visual changes | `refreshAllCells` (5 / 3), `refreshCells` (4 / 3), `changeGrayedOut`, `repaint`, `isModelRefreshing` |
| mode | `getMode` (6 / 3), `setMode`, mode listeners, the `JGraphMode` enum |
| layout | `doLayout` (6 / 3), `getLayouter`, `setLayouting`, `clearAllEdgePoints`; `Layouter.newInstance(JGraph)` |
| viewport | `getGraphBounds` (5 / 5), `setScale`/`getScale`/`changeScale`, `scrollRectToVisible`, `scrollTo(Element)`, `scrollToNextSelectedRoot`, `getFirstCellForLocation` |
| editing | `startEditingAtCell`, `setEditable`, `hasActiveEditor`, `isForState`, `setGridEnabled`, and the single undoable edit funnel `getNonNullModel().edit(Map<ViewCell,AttributeMap>,…)` in `JCellEditAction` |
| Swing plumbing | `addAccelerator` (8 / 4), `setToolTipEnabled` (5 / 5), `getLabelTree`/`setLabelTree`, `getOptions`, `setEnabled`, `setBackground`, `requestFocus`, `addMouseListener`, `JScrollPane(jGraph)`, two `paintComponent` overrides (hatched background), `addPropertyChangeListener(GRAPH_MODEL_PROPERTY)` |
| export | `toImage`, `paint(Graphics)`, `getGraphBounds`, `getScale`, `getName()` (the Swing component name doubles as resource name) |
| palette | `JAttr` colours and `paintHatch` in trees and displays |

Structural welds, not just calls: `gui.look.VisualAttributeMap` *extends* `org.jgraph.graph.AttributeMap`
and `gui.look.LoopRouting` *implements* `org.jgraph.graph.Edge.Routing`; nine non-backend classes
implement `org.jgraph.event.GraphSelectionListener`/`GraphModelListener`; `GraphEditorTab`
extends `org.jgraph.graph.GraphUndoManager` and reads `GraphModelChange`/`ConnectionSet`;
`ViewCell` (the neutral cell interface) declares `getJGraph()`/`getJModel()` with backend types;
`AbstractLayouter` works on JGraph `CellView`s and `LayouterItem` on the jgraph-layout
`JGraphFacade`; `GraphToTikz` uses `org.jgraph.util.Bezier`.

**Spike.** Building a yFiles graph needed nothing from JGraph beyond `VisualMap` plus two things
the JGraph *view* computes lazily and caches back into the map: the text-fitted node size
(`NODE_SIZE`/`TEXT_SIZE`) and the routed loop points (`POINTS` of loops). Everything else,
including the edge-label position, is already neutral data.

## Principles

1. **Consumer intent, not backend concepts.** Members are named for what the GUI wants
   (`showGraph`, `select`, `scrollTo`, `refresh`), never for JGraph mechanics
   (`getGraphLayoutCache`, `getSelectionModel`, `Object[]` cells, property-change constants).
   Anything a consumer only needs because JGraph made it necessary (`getDescendants`,
   `updateAutoSize`, `getPortViewAt`, `setToPreferredSize`) stays inside the backend.
2. **Neutral vocabulary** (Arend, 2026-09-03): the rendering component is the *canvas*; the
   per-display object is the *controller*; the displayed content is the *view model*; cells
   are `ViewCell`s. No "J" names outside `gui.jgraph`.
3. **The controller hierarchy is the template.** Per graph role there is a canvas
   sub-interface exactly where there is a controller subclass (`Aspect`, `LTS`, `Ctrl`), with
   covariant `getController()` / `getCanvas()` in both directions.
4. **The visual model is the contract.** Backends render *from* `VisualMap` and write derived
   values *into* it under the existing staleness protocol (`ViewCell.setStale`/`isStale`).
   The facade names which keys a backend must supply (`NODE_SIZE`, `TEXT_SIZE`) and which it
   must never touch (the `CONTROLLED` keys: `NODE_POS`, `POINTS`, `LABEL_POS`, `LINE_STYLE`,
   `EMPHASIS`, …) except through the edit funnel.
5. **One edit funnel.** Every programmatic change to controlled visuals goes through
   `GraphCanvas.edit(Map<ViewCell, VisualMap>)`, which the backend applies undoably. This is
   already the shape of `JCellEditAction.edit` and is what phase 3 builds its own undo on.
6. **Seal with a test, shrink the allowlist.** An architecture test enumerates the files
   outside the backend that still import backend or `org.jgraph` types, each tagged with the
   slice or phase that removes it; new violations fail, and so does a stale entry.

## The interfaces (package `nl.utwente.groove.gui.view`)

All `@NonNullByDefault`. `G extends Graph` throughout.

### `GraphCanvas<G>` — what the GUI needs from the rendering component

| group | members |
|---|---|
| structure | `GraphViewController<G> getController()`; `@Nullable GraphViewModel<G> getViewModel()`; `GraphViewModel<G> getNonNullViewModel()`; `@Nullable G getGraph()`; `GraphRole getGraphRole()`; `boolean hasGraphRole(GraphRole)`; `Options getOptions()` |
| content | `GraphViewModel<G> showGraph(G graph)` (replaces the `newModel`/`loadGraph`/`setModel` triple for callers that need no role-specific preparation); `boolean isModelRefreshing()` |
| cells | `@Nullable ViewCell<G> getCellAt(double x, double y)` (screen coordinates); `Collection<? extends ViewCell<G>> getCells()` |
| selection | `List<ViewCell<G>> getSelection()`; `@Nullable ViewCell<G> getSelectedCell()`; `boolean isSelectionEmpty()`; `void select(Collection<? extends ViewCell<G>>)`; `void selectElements(Collection<? extends Element>)`; `void clearSelection()` |
| refresh | `void refresh(Collection<? extends ViewCell<G>>, boolean unselectGrayedOut)`; `void refreshAll(boolean)`; `void setGrayedOut(Set<ViewCell<G>>, boolean)`; `void repaint()` |
| edit funnel | `void edit(Map<? extends ViewCell<G>, VisualMap> changes)` (undoable, controlled keys only); `void clearAllEdgePoints()` |
| mode | `GraphViewMode getMode()`; `boolean setMode(GraphViewMode)`; `GraphViewMode getDefaultMode()` |
| layout | `void setLayouting(boolean)`; `boolean isLayouting()` (layouter API itself stays on the controller: `getLayouter`/`setLayouter`/`doLayout`) |
| viewport | `Rectangle2D getGraphBounds()`; `double getScale()`; `void setScale(double)`; `void changeScale(int)`; `void zoomTo(Rectangle2D)`; `void scrollTo(Rectangle2D)`; `void scrollTo(ViewCell<G>)`; `void scrollTo(Element)`; `void scrollToNextSelected()` |
| editing | `boolean isEditable()`; `void setEditable(boolean)`; `void startEditing(ViewCell<G>)`; `boolean hasActiveEditor()`; `void setGridEnabled(boolean)` |
| measuring | `Dimension2D getPreferredSize(ViewVertex<G>)` (text-fitted, backend font metrics; the neutral layer sizes nodes with it, see below) |
| Swing | `JComponent getComponent()`; `boolean isEnabled()`; `void setEnabled(boolean)`; `void setBackground(@Nullable Color)`; `void setBackgroundPainter(@Nullable Consumer<Graphics2D>)` (the hatch overlays); `void addAccelerator(Action)`; `void setToolTipEnabled(boolean)`; `boolean getToolTipEnabled()`; `void removeListeners()` |
| listeners | `void addCanvasListener(GraphCanvasListener<G>)`; `void removeCanvasListener(…)` |
| export | `BufferedImage toImage()`; `void paintGraph(Graphics2D g)` (whole graph at scale 1, no selection or grid) |

`getLabelTree`/`setLabelTree`, `getExportAction`/`getLayoutAction`, `getRefreshListener`, the
option-listener machinery, and the `isShow*` predicates are *not* canvas members: they belong
to the controller already (phase 1a) and the remaining component stubs go away with ownership
inversion.

### `GraphCanvasListener<G>`

Replaces `org.jgraph.event.GraphSelectionListener`, `GraphModelListener`, the
`GRAPH_MODEL_PROPERTY`/`CELL_EDIT_PROPERTY`/`JGRAPH_MODE_PROPERTY` property changes and
`AspectJModel.addGraphChangeListener`. Default-method interface:

```
void viewModelChanged(GraphCanvas<G> canvas, @Nullable GraphViewModel<G> old, @Nullable GraphViewModel<G> now)
void cellsChanged(GraphCanvas<G> canvas, CellChange<G> change)   // inserted / modified / removed cells
void graphChanged(GraphCanvas<G> canvas)                         // shown graph rebuilt from the cells (editable canvases)
void selectionChanged(GraphCanvas<G> canvas)
void modeChanged(GraphCanvas<G> canvas, GraphViewMode old, GraphViewMode now)
void editingStarted(GraphCanvas<G> canvas, ViewCell<G> cell)     // in-place editor
```

`CellChange<G>` is a record of three disjoint lists (`inserted`, `modified`, `removed`);
the label trees need the distinction to maintain their filters, and JGraph's single
"changed" set (inserted ∪ removed ∪ attribute-changed) was the idiom being replaced. The
`graphChanged` event is the neutral form of `AspectJModel`'s graph-modification counter
(fired after `syncGraph` rebuilds the aspect graph from an edit, and after a reload of an
attached model); the type tree rebuilds itself on it. Listeners are registered once on the
canvas, never per content model: the backend re-adapts its model listeners in `setModel`.
Change events fired while `GraphViewModel.isLoading()` holds reflect a (re)load rather than
an edit; the flag lives on the view model, set by the backend with nesting.

### `GraphViewMode`

`JGraphMode` moved and renamed (constants `SELECT_MODE`, `PAN_MODE`, `EDIT_MODE`, `PREVIEW_MODE` unchanged, with name, accelerator,
icon and cursors); it was already library-neutral.

### Role sub-interfaces

- `AspectGraphCanvas extends GraphCanvas<AspectGraph>`: `AspectGraphViewController getController()`;
  `boolean isForState()`; the aspect-specific `selectElements` behaviour (errors map to the
  source vertex) is an implementation detail behind the base method.
- `LTSGraphCanvas extends GraphCanvas<GTS>`: `LTSGraphViewController getController()`.
- `CtrlGraphCanvas extends GraphCanvas<CtrlGraph>`: `CtrlGraphViewController getController()`.

### `ViewCell` changes

`getJGraph()` → `@Nullable GraphCanvas<G> getCanvas()`, `getJModel()` → `GraphViewModel<G>
getViewModel()`, `setJModel(JModel)` → `setViewModel(GraphViewModel<G>)`. `AspectViewCell
.getUserObject()` returns `AspectViewObject` (`AspectJObject` moved and renamed; it is a
neutral list of label texts).

### `GraphViewModel` additions

`Collection<? extends ViewCell<G>> getCells()` (all cells; no z-order guarantee),
`void setLayoutable(boolean)` (all vertices), `Map<…> getColorMap()`. The insertion machinery
and the per-role model operations (`AspectJModel.syncGraph/setBeingEdited/getResourceModel/
setGraphModified`, `LTSJModel.setStateBound/setExploring/reloadGraph/addElements`) stay on
`JModel` in phase 1b and are relocated into per-role view models in phase 2 (see the plan's
"known residues"); until then their callers are allowlisted.

## The neutral geometry model

Fixed by this note; backends translate to and from it.

- **Nodes.** `NODE_POS` is the centre; `NODE_SIZE` is derived from the label text by the backend
  (`getPreferredSize`) and cached under the staleness protocol, never persisted as authority
  (the persisted `x y w h` bounds are read for their centre only, as today). The neutral layer
  is the one that asks for the size and writes it into the map; a backend must not resize
  nodes on its own.
- **Edges.** `POINTS` in the neutral model are the *interior bend points only*. The end points
  are derived: the edge starts and ends at the centres of its end nodes and is clipped to the
  node shapes by the backend. *Persisted format unchanged for compatibility*: the writer keeps
  emitting the two end points and the reader ignores them (old GROOVE versions insist on at
  least two points).
- **Loops.** A loop with no bends gets default bends from the *neutral layer* when the cell is
  created (today: `LoopRouting` in the JGraph view, control point 35 px right of the node,
  spline). The neutral default: a three-point loop on the first free side of the node in the
  order right, top, left, bottom, at the same size; stored as ordinary bends and therefore
  persisted on the next save — files gain explicit loop points, which every GROOVE version
  reads. Backends never route loops.
- **Edge labels.** `LABEL_POS = (ratio‰, distance)`: `ratio` along the total length of the routed
  polyline (end points included), `distance` the signed perpendicular offset from the segment
  the ratio point lies on, positive on the left of the direction of travel in screen
  coordinates. This is what JGraph renders today (verified in the spike) and what yFiles'
  `EdgePathLabelModel` implements. Unchanged.
- **Line styles.** The `LineStyle` enum is the model; the numeric codes are a persistence detail
  the enum owns. Unchanged.
- **Parallel-edge offsetting** (the 4 px fan-out in `JEdgeView.getPointLocation`) is a rendering
  choice, backend-owned, not model data.

## Slicing

Each slice is one branch/PR on top of the previous, held to zero null-analysis errors and the
architecture test.

1. **`view-facade` (this branch): the definition.** The interfaces above, `GraphViewMode`,
   `JGraph implements GraphCanvas` (with `AspectJGraph`/`LTSJGraph`/`CtrlJGraph` implementing the
   role sub-interfaces), the controller hierarchy typed against the canvas interfaces,
   `ViewCell` retyped, the architecture test with the full current allowlist. Consumers that
   need only canvas members are retargeted from `JGraph<G>` to `GraphCanvas<G>`.
2. **Looks decoupling completed.** `VisualAttributeMap` moves into the backend (the backend keeps
   its own attribute-map cache per cell; `VisualMap.getAttributes()` disappears); `LoopRouting`
   is replaced by neutral default loop bends; the `JAttr` palette used outside the backend
   (`STATE_BACKGROUND`, `paintHatch`, …) moves to `gui.look.Values`; node sizing goes through
   `getPreferredSize`.
3. **Neutral listeners.** The nine `GraphSelectionListener`/`GraphModelListener` implementors and
   the property-change users switch to `GraphCanvasListener`; `TypeTree` and `LabelTree` stop
   reading `GraphModelChange`.
4. **Layouters.** `Layouter.newInstance(GraphCanvas)`; `AbstractLayouter` works on `ViewVertex`
   visuals (`NODE_POS` plus the canvas' preferred size) and commits through the edit funnel;
   backend-provided layouts (`LayouterItem`/`JGraphFacade`) become a backend contribution
   behind a small `getBackendLayouters()` hook so the yFiles backend can contribute its own.
   The algorithms are not unified across backends (Arend, 2026-09-04).
5. **Export seam.** `RasterExporter`/`GraphToVector`/`GraphToEPS/PDF/SVG` use `toImage`/
   `paintGraph`/`getGraphBounds`; `GraphToTikz` drops `org.jgraph.util.Bezier` for its own
   curve code (`InterpolatingBezier`); `JGraphExportable` becomes `CanvasExportable` and stops
   using the Swing component name.

Residues handed on: `GraphEditorTab`'s `GraphUndoManager`/`GraphModelChange`/`ConnectionSet`
use (phase 3, the editor's own undo model); `JGraphPanel` and the `getJGraph()` accessors on
displays and tabs, plus the per-role `JModel` operations (phase 2, ownership inversion and
view-model split).

## Decisions

- Taken: naming (`GraphCanvas`, `GraphViewMode`, `GraphCanvasListener`, `AspectViewObject`);
  the edit funnel; the persisted format stays as it is (no GXL version bump) while the
  in-memory model drops the end points and gains explicit loop bends.
- Open for Arend (2026-09-03): (a) the loop default (side order right, top, left, bottom;
  size as today) — any preference, e.g. always right as now? (b) whether to also stop
  *writing* the end points, which would need a GXL version bump and break reading by older
  GROOVE versions — recommended: not now. (c) whether the hatch overlays deserve the generic
  `setBackgroundPainter` hook or a boolean `setHatched` — the hook is chosen here because the
  two existing overrides paint different things.

## Follow-ups noted during the work

- **Cell user objects** (Arend, 2026-09-04): `AspectViewCell.getUserObject()`/`setUserObject(Object)`
  exposed JGraph's `DefaultGraphCell` user object, which GROOVE used to carry the editable
  label text. A very old change, never reverted; the text belongs in a dedicated,
  backend-independent field of the cell. **Done** (third commit of slice 2): the neutral
  interface has `EditableLabels getEditableLabels()`, `setEditableLabels`,
  `refreshEditableLabels` (elements → labels) and `applyEditableLabels(graph)` (labels →
  elements); the backend cells keep the labels in a field of their own and override
  `getUserObject`/`setUserObject` only as the adapter for JGraph's editing protocol
  (`DefaultGraphModel.valueForCellChanged` writes the in-place editor's text through
  `setUserObject`). Cloning now copies the labels instead of sharing them.

## State of work

**Slice 1 done (2026-09-03, branch `view-facade`).** Interfaces `GraphCanvas`,
`AspectGraphCanvas`, `LTSGraphCanvas`, `CtrlGraphCanvas`, `GraphCanvasListener`,
`OptionRefreshListener` (the former `JGraph.RefreshListener`, now neutral) and `GraphViewMode`
(the former `JGraphMode`; constant names unchanged) and `AspectViewObject` (the former
`AspectJObject`) in `gui.view`; `JGraph`/`AspectJGraph`/`LTSJGraph`/`CtrlJGraph` implement
them, with the canvas events adapted from the JGraph selection and model listeners.
`ViewCell` lost its backend-typed accessors (`getCanvas()`/`getViewModel()` instead;
`setJModel` is backend-internal on `AJCell`, taking `JModel<?>` to keep the callers'
casts checked). `GraphViewModel` gained `getCells`, `setLayoutable`, `refreshVisuals`,
`getColorMap`, to which `JModel` now delegates. The default layouter moved from the
component hierarchy to the controller hierarchy (`LTS`/`Ctrl` controllers override to
Forest). Retargeted to the canvas interfaces: the controllers, the `JCellEditAction` family
(now `GraphCanvasListener`s using the edit funnel), `SetLineStyleMenu`, `ZoomMenu`,
`ShowHideMenu`, `LayoutAction`, `ExportAction` (constructor only; it still casts to
`JGraph` for `JGraphExportable`, slice 5) and `Layouter.newInstance(GraphCanvas)` (the
layouters cast back, slice 4). Renamed on the component for consistency with the
interface: `refreshCells`→`refresh`, `refreshAllCells`→`refreshAll`,
`changeGrayedOut`→`setGrayedOut`, `scrollToNextSelectedRoot`→`scrollToNextSelected`,
`AspectJGraph.setSelectionCells(Collection<Element>)`→`selectElements`.
`ArchitectureTest` seals the boundary with an allowlist of 47 files tagged by slice/phase.

**Slice 2 done (2026-09-04, branch `looks-decoupling`, two commits).** (a) Neutral
role-specific cell interfaces `AspectViewVertex`/`AspectViewEdge`, `LTSViewVertex`/
`LTSViewEdge`, `CtrlViewVertex`, implemented by the backend cells and consumed by the look
value classes; the type graph and resource model of the shown graph come from
`AspectGraphCanvas` (for the trees) and, since the fix after slice 4, from the cell itself
(`AspectViewCell.getTypeGraph`/`getResourceModel`, for the looks: a cell's visuals are
computed before its model is shown, see below); the GUI palette and the hatch paint moved from `JAttr` to
`gui.look.Values` (state-panel colours renamed `ERROR_STATE_BACKGROUND`/
`INTERNAL_STATE_BACKGROUND`, since `Values.ERROR_BACKGROUND` already named the error-cell
colour); the two `paintComponent` overrides in the displays became a declarative
`GraphCanvas.Overlay` (`NONE`/`HATCHED`, Arend's choice over the `Graphics2D` hook; the
`TypeTree` hatch in `StateDisplay` stays a Swing override, it is not a canvas).
(b) `VisualAttributeMap` is a backend class (`gui.jgraph`) bound to a cell's `VisualMap`
through the new `VisualMap.Listener`; `VisualMap` no longer knows about attribute maps, and
`VisualAttributeMap.toAttributes` gives detached maps for edits. `LoopRouting` (the JGraph
`Edge.Routing`) is a backend adapter over the neutral `gui.view.LoopRouter`, which
implements the decided default: control point 35 px beyond the node on the first side in
the order right, top, left, bottom that no incident edge or loop occupies. Node sizing was
*not* moved: the contract that the backend supplies `NODE_SIZE`/`TEXT_SIZE` under the
staleness protocol is documented on `GraphCanvas`, and moving the sizing policy adds
nothing until a second backend exists. The allowlist is down to 32 files.

Surprises: `JGraph.getController()` is genuinely null while the `org.jgraph.JGraph`
constructor runs (it calls `setModel`), but the interface declares it non-null, so
`LTSJGraph.setModel` now asks `hasController()` instead of null-checking. ecj 3.42 reports
the `@AIGenerated` import of a *generic* interface as unused when the annotation follows
`@NonNullByDefault`; placing it first avoids the false positive.

**Slice 3 done (2026-09-04, branch `neutral-listeners`).** Every `org.jgraph` selection
and model listener outside the backend, and every user of the `GRAPH_MODEL_PROPERTY`/
`JGRAPH_MODE_PROPERTY`/`CELL_EDIT_PROPERTY` property changes, is a `GraphCanvasListener`
now: `LabelTree` (and so `TypeTree`, `LTSTree`), `RuleLevelTree`, `FindReplaceAction`,
`SelectColorAction`, `StateDisplay`, `GraphEditorTab` (mode, selection, cell-edit start;
its `GraphModelListener` for the undo model stays, phase 3), `JGraphPanel` and
`LayouterItem`. The two JGraph-specific property names and `addGraphViewModeListener` are
gone from `JGraph`. `cellsChanged` carries a `CellChange` (see the listener section) and
`graphChanged` replaces `AspectJModel.addGraphChangeListener` for the type tree (the
model's counter still exists for the model's own error reloading; `AspectJGraph.setModel`
forwards it). The loading flag moved from the two backend models (each had its own) to
`GraphViewModel.isLoading()`, set with nesting so that `AspectJModel.loadGraph`'s wider
phase survives `JModel.addElements`' inner one. `StateDisplay` no longer gets per-cell
added/removed flags from the selection event; it remembers the last selection and compares.
The trees and filters take canvases (`LabelTree(GraphCanvas)`, `TypeTree`/
`RuleLevelTree(AspectGraphCanvas)`, `LTSTree`/`LTSFilter(LTSGraphCanvas)`), and their
`getJGraph`/`getJModel` accessors became `getCanvas`/`getViewModel`. `Simulator` and
`LayoutDialog` needed only retyping to the canvas interfaces (the tab and panel accessors
still *return* backend types, phase 2, but callers no longer name them). `Imager` and
`GraphPreviewDialog` use `showGraph` instead of the `newModel`/`loadGraph`/`setModel`
triple; what remains in them is canvas construction (`new AspectJGraph(…)` etc.), so they
are re-tagged to phase 2 in the allowlist, which is down to 23 files. Incidental fix:
`AspectJGraph.removeListeners` used to *add* the colour action as selection listener before
removing it once, leaving it registered.

**Slice 4 done (2026-09-04, branch `layouter-seam`).** Decision (Arend): the layout
algorithms themselves are not unified; each backend contributes its own palette. So the
seam has two sides. (a) GROOVE's own layouters are backend-independent: `AbstractLayouter`
takes a `GraphCanvas`, reads a vertex's bounds as the `NODE_POS` centre plus the canvas'
`getPreferredSize` (the rendered size, which JGraph keeps under the confusingly named
`TEXT_SIZE`; `NODE_SIZE` is the inscription only), iterates `getCells()`, and commits node
positions and cleared edge points through the edit funnel; `SpringLayouter` and
`ForestLayouter` no longer name a backend type (`ForestLayouter.getSuggestedRoots` uses the
view model and the canvas selection). (b) The JGraph layout library's algorithms are a
backend contribution: `LayoutKind` and `LayouterItem` moved to `gui.jgraph`, and
`GraphCanvas.getBackendLayouters()` hands out their prototypes; `SetLayoutMenu` appends them
after Spring and Forest, and `LayoutDialog` refills its combo box from the focused canvas
(so a yFiles canvas will show a different list). `Layouter.getSettingsPanel()` (default
`null`) replaces `LayouterItem.getPanel()` for the dialog. `LayouterTest` runs both
GROOVE layouters headlessly through the controller and checks positions, flags and edge
points. The allowlist is down to 19 files.

**Fix on top of slice 4 (2026-09-04, same branch).** Arend's first Simulator run of the
stack (loading a grammar on `neutral-listeners`) hit a `NullPointerException` in
`AspectJGraph.getTypeGraph`: `StateDisplay` builds the start state's model and copies the
start graph's cell visuals *before* setting the model on the component, and slice 2 had made
the looks ask the canvas for the type graph and resource model. Lesson for the facade:
anything the content model knows must be reachable from the cell or the model, not only
from the canvas — a cell's visuals are legitimately computed for models that are not shown.
`AspectViewCell` now has `getTypeGraph()`/`getResourceModel()`; the canvas methods remain
for the trees. The reproducing `DetachedCellVisualsTest` also exposed a pre-facade latent
assertion in the option refresh listener (a model-less canvas registered on the display
options); it now skips canvases without a model.

**Slice 5 done (2026-09-05, branch `export-seam`).** The export path works on the canvas
interface: `CanvasExportable` (the former `JGraphExportable`) wraps a `GraphCanvas` and takes
its name from the shown graph rather than the Swing component name; `ExportKind.JGRAPH` is
`CANVAS`; `CanvasExporters` (the former `JGraphExporters`) registers the raster, vector and
TikZ exporters; `RasterExporter` uses `toImage`, the vector writers (`GraphToVector`,
EPS/PDF/SVG) use `paintGraph`/`getGraphBounds`/`getGraph`, and `GraphToTikz` reads the view
model, the controller and the `AspectViewVertex` role interface. `org.jgraph.util.Bezier` is
replaced by the neutral `gui.view.InterpolatingBezier`, an independent implementation of
the same interpolation (quadratic end segments, cubic middle segments, tangent at an
interior point parallel to the chord of its neighbours, control points at half the projected
neighbour distance) with the control-point layout the TikZ writer already expected; it keeps
double precision where JGraph truncated to integers, so TikZ coordinates of Bezier edges may
differ in the third decimal. `ExportAction` holds a `GraphCanvas`; `Imager` sizes the
component through `getComponent()`. `ImagerTest` (slow category) covers PNG, PDF, SVG and
TikZ export of the ferryman graphs; `InterpolatingBezierTest` pins the curve contract. The
allowlist is down to 11 files, all phase 2 (canvas construction and the display/tab
accessors) or phase 3 (`GraphEditorTab`): **phase 1b is complete**.
