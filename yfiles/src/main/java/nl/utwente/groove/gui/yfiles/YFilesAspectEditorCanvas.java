/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.yfiles;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.KeyEventArgs;
import com.yworks.yfiles.view.ModifierKeys;
import com.yworks.yfiles.view.Mouse2DEventArgs;
import com.yworks.yfiles.view.MouseButtons;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.PortDropInputMode;
import com.yworks.yfiles.view.input.NodeDropInputMode;
import com.yworks.yfiles.view.input.LabelDropInputMode;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.DoubleClickPolicy;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IHandleProvider;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPositionHandler;
import com.yworks.yfiles.view.input.MoveInputMode;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import com.yworks.yfiles.view.input.MultiplexingInputMode;
import com.yworks.yfiles.view.input.TextEditorInputMode;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.AspectViewEdge;
import nl.utwente.groove.gui.view.CellStore.Connection;
import nl.utwente.groove.gui.view.EditableLabels;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.gui.view.GraphFragment;
import nl.utwente.groove.gui.view.GraphViewModel;
import nl.utwente.groove.gui.view.LabelCompletion;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Fonts;

/**
 * The yFiles canvas of the graph editor: the aspect canvas with an editor input
 * mode, whose gestures are those of the JGraph editor and whose every edit is
 * an operation of the view model, recorded in its history.
 * <ul>
 * <li>A double click on empty canvas adds a vertex and opens its in-place editor;
 * a double click on a cell opens the cell's in-place editor, with the labels selected.
 * <li>A single click on a vertex selects it and starts <i>click-click</i> edge
 * creation: the edge follows the mouse until a click on the target vertex, or
 * on empty canvas for a self-loop; Escape cancels. The second click of a double
 * click on the vertex ends the creation as the vertex's editor, not as a loop.
 * <li>Alt-click on the selected edge removes its point at the click, Alt-click
 * elsewhere adds a point to the selected edge.
 * <li>Dragging moves cells and edge points, and drags an edge end to another
 * vertex; each gesture becomes one edit when it finishes.
 * <li>The in-place editor is yFiles' text editor showing the cell's editable
 * labels, with GROOVE's label completion on Ctrl+Space; Enter commits, Shift+Enter
 * and Ctrl+Enter break the line, as in the JGraph editor.
 * <li>A selected edge shows handles for its points; its end handles show only while it
 * is the only cell selected. An edge without points is not movable, so no move cursor
 * appears over it. The main label of an edge is dragged to a new position, an edit of
 * the label position; the labels of an edge follow its geometry while it is dragged.
 * <li>The grid snaps vertex centres and edge points to its lines.
 * </ul>
 * Deletion, undo, copy and paste are the tab's actions, working on the model.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
final class YFilesAspectEditorCanvas extends YFilesAspectCanvas {
    /** Creates a canvas as the editing canvas of a given controller. */
    YFilesAspectEditorCanvas(AspectGraphViewController controller) {
        super(controller);
    }

    // ---------- the input mode ----------

    /*
     * Creates the editor input mode. Called by the superclass constructor: the
     * fields set here must have no initialisers, which would reset them.
     */
    @Override
    protected MultiplexingInputMode createInputMode() {
        var result = new GraphEditorInputMode() {
            /*
             * An edge without points has nothing to move: no move cursor over it. Of the
             * labels only the main label of an edge moves; a node label must not claim
             * the drag that moves its node.
             */
            @Override
            @NonNullByDefault({})
            protected boolean shouldMove(IModelItem item) {
                if (item instanceof ILabel label) {
                    return YFilesCellStore.isMainEdgeLabel(label);
                }
                return super.shouldMove(item)
                    && !(item instanceof IEdge edge && edge.getBends().size() == 0);
            }

            /*
             * Nothing is dropped from a palette: the drop modes are given nothing to
             * install. Installing a drop mode creates a drop target, which needs a
             * desktop, and the editor canvas must also work without one (headless tests).
             */
            @Override
            protected NodeDropInputMode createNodeDropInputMode() {
                return new NodeDropInputMode() {
                    @Override
                    @NonNullByDefault({})
                    public void install(IInputModeContext context, ConcurrencyController controller) {
                        // nothing to install
                    }

                    @Override
                    @NonNullByDefault({})
                    public void uninstall(IInputModeContext context) {
                        // nothing to uninstall
                    }
                };
            }

            @Override
            protected LabelDropInputMode createLabelDropInputMode() {
                return new LabelDropInputMode() {
                    @Override
                    @NonNullByDefault({})
                    public void install(IInputModeContext context, ConcurrencyController controller) {
                        // nothing to install
                    }

                    @Override
                    @NonNullByDefault({})
                    public void uninstall(IInputModeContext context) {
                        // nothing to uninstall
                    }
                };
            }

            @Override
            protected PortDropInputMode createPortDropInputMode() {
                return new PortDropInputMode() {
                    @Override
                    @NonNullByDefault({})
                    public void install(IInputModeContext context, ConcurrencyController controller) {
                        // nothing to install
                    }

                    @Override
                    @NonNullByDefault({})
                    public void uninstall(IInputModeContext context) {
                        // nothing to uninstall
                    }
                };
            }
        };
        GraphItemTypes cells = GraphItemTypes.NODE.or(GraphItemTypes.EDGE);
        result.setSelectableItems(cells);
        result.setClickableItems(cells);
        result.setMarqueeSelectableItems(cells);
        result.setSelectablePredicate(selectablePredicate());
        result.setFocusableItems(GraphItemTypes.NONE);
        result.setToolTipItems(GraphItemTypes.NONE);
        result.setPopupMenuItems(GraphItemTypes.NONE);
        configurePopupMenu(result.getPopupMenuInputMode());
        configureMarquee(result.getMarqueeSelectionInputMode());
        configurePan(result.getMoveViewportInputMode());
        // the editing operations of yFiles' own are off: GROOVE's actions and
        // gestures do the editing, through the view model
        result.setCreateNodeAllowed(false);
        result.setDeletableItems(GraphItemTypes.NONE);
        result.setClipboardOperationsAllowed(false);
        result.setUndoOperationsAllowed(false);
        result.setGroupingOperationsAllowed(false);
        result.setReparentNodesAllowed(false);
        result.setReverseEdgeAllowed(false);
        result.setLabelEditableItems(GraphItemTypes.NONE);
        result.setAddLabelAllowed(false);
        result.setEditLabelAllowed(false);
        result.setEditLabelOnDoubleClickAllowed(false);
        // moving cells, edge points and edge labels, and reconnecting edge ends, by
        // dragging; edge points are added by Alt-click, not by dragging on the edge
        result.setMovableItems(cells.or(GraphItemTypes.BEND).or(GraphItemTypes.LABEL));
        result.setShowHandleItems(GraphItemTypes.EDGE.or(GraphItemTypes.BEND));
        result.getCreateBendInputMode().setEnabled(false);
        result.getMoveUnselectedInputMode().setEnabled(true);
        // a drag moves the cells, or, with Control held, copies them
        configureMove(result.getMoveInputMode());
        configureMove(result.getMoveUnselectedInputMode());
        result.getHandleInputMode()
            .addDragFinishedListener((src, args) -> recordMove(result.getHandleInputMode().getAffectedItems()));
        // the labels follow the edges while their points are dragged
        result.getHandleInputMode()
            .addDraggedListener((src, args) -> followDrag(result.getHandleInputMode().getAffectedItems()));
        // a dragged edge label lands as an edit of the label position
        var moveLabel = result.getMoveLabelInputMode();
        moveLabel.setMovingUnselectedLabelsAllowed(true);
        moveLabel.addDragStartedListener((src, args) -> this.movedLabel = moveLabel.getMovedLabel());
        moveLabel.addDragFinishedListener((src, args) -> recordLabelMove());
        moveLabel.addDragCanceledListener((src, args) -> this.movedLabel = null);
        result.addEdgePortsChangedListener((src, args) -> {
            IEdge edge = args.getItem();
            SwingUtilities.invokeLater(() -> recordReconnection(edge));
        });
        // edge creation by the click-click gesture only, started on a vertex click
        var createEdge = result.getCreateEdgeInputMode();
        createEdge.setPrepareRecognizer(IEventRecognizer.NEVER);
        createEdge.setCreateBendAllowed(false);
        createEdge.setSelfloopsAllowed(true);
        // a click on empty canvas ends the edge as a self-loop: the premature end is
        // only consulted while the pointer is not forced to snap to a port candidate
        createEdge.setForcingSnapToCandidateEnabled(false);
        createEdge.setPrematureEndHitTestable(IHitTestable.ALWAYS);
        createEdge.setEdgeCreator(this::createEdge);
        // the finishing click is remembered for the edge creator, which tells the second
        // click of a double click on the source vertex by it (the component's last mouse
        // event is not yet the one being handled)
        IEventRecognizer finish = createEdge.getFinishRecognizer();
        createEdge.setFinishRecognizer((source, args) -> {
            boolean finished = finish.isRecognized(source, args);
            if (finished) {
                this.finishingEvent = args instanceof Mouse2DEventArgs mouse
                    ? mouse
                    : null;
            }
            return finished;
        });
        // the preview edge looks like the edge to be created: a line from the vertex to
        // the pointer, as in the JGraph editor, hidden while the pointer is on the vertex
        createEdge.getEdgeDefaults().setStyle(CellStyles.EDGE_STYLE);
        // clicks: the editor's gestures, then the Swing listeners of the component
        result.getClickInputMode()
            .setDoubleClickPolicy(DoubleClickPolicy.INITIAL_SINGLE_AND_DOUBLE_CLICK);
        result.addItemClickedListener((src, args) -> {
            itemClicked(args);
            forwardClick(args);
        });
        result.addItemDoubleClickedListener((src, args) -> {
            itemDoubleClicked(args);
            forwardClick(args);
        });
        result.addCanvasClickedListener((src, args) -> {
            canvasClicked(args);
            forwardClick(args);
        });
        result.addMultiSelectionStartedListener((src, args) -> beginSelectionBatch());
        result.addMultiSelectionFinishedListener((src, args) -> endSelectionBatch());
        // the in-place editor commits into the model
        var text = result.getTextEditorInputMode();
        // Enter commits, Shift+Enter and Ctrl+Enter break the line, as in the JGraph
        // editor: the stop gesture takes a plain Enter only, and the line breaks are key
        // bindings of the text area (see startEditing), which consume the key, rather
        // than yFiles' line break gesture
        text.setStopRecognizer(PLAIN_ENTER);
        text.setLineBreakRecognizer(IEventRecognizer.NEVER);
        text.addTextEditedListener((src, args) -> commitLabels(args.getText()));
        text.addEditingCanceledListener((src, args) -> {
            this.editedCell = null;
            // a cell created for the editor is withdrawn (an edge) or kept (a vertex)
            getNonNullViewModel().settlePendingInsertion();
        });
        this.editorMode = result;
        return result;
    }

    /** The modifier keys that make an Enter something other than a commit. */
    private static final int MODIFIERS = InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK
        | InputEvent.ALT_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK;
    /** Recognizes the press of Enter without modifier keys: the key that commits the in-place editor. */
    private static final IEventRecognizer PLAIN_ENTER = (source, args) -> {
        if (!(args instanceof KeyEventArgs keyArgs)) {
            return false;
        }
        KeyEvent event = keyArgs.getKeyEvent();
        return event.getID() == KeyEvent.KEY_PRESSED && event.getKeyCode() == KeyEvent.VK_ENTER
            && (event.getModifiersEx() & MODIFIERS) == 0;
    };

    /** Binds Shift+Enter and Ctrl+Enter of a text area to a line break. */
    private static void bindLineBreaks(JTextArea area) {
        InputMap keys = area.getInputMap(JComponent.WHEN_FOCUSED);
        keys
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK),
                 DefaultEditorKit.insertBreakAction);
        keys
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK),
                 DefaultEditorKit.insertBreakAction);
    }

    /** The editor input mode; set by {@link #createInputMode()} during construction. */
    private @Nullable GraphEditorInputMode editorMode;
    /** The label being dragged, from the start of the drag to its end. */
    private @Nullable ILabel movedLabel;
    /** The mouse event that finished the edge creation gesture in progress, if any. */
    private @Nullable Mouse2DEventArgs finishingEvent;

    /** Sets the label whose drag {@link #recordLabelMove()} records; for the tests. */
    void setMovedLabel(@Nullable ILabel label) {
        this.movedLabel = label;
    }

    /** Returns the editor input mode, which is set during construction. */
    GraphEditorInputMode editorMode() {
        var result = this.editorMode;
        assert result != null; // set during construction
        return result;
    }

    /* Edge ends may be dragged to any vertex of the shown model. */
    @Override
    public void setViewModel(@Nullable GraphViewModel<AspectGraph> model) {
        finishEditing();
        super.setViewModel(model);
        if (model != null) {
            var decorator = storeOf(model).getMaster().getDecorator().getEdgeDecorator();
            decorator
                .getEdgeReconnectionPortCandidateProviderDecorator()
                .setImplementation(IEdgeReconnectionPortCandidateProvider.ALL_NODE_CANDIDATES);
            // the end handles of an edge show only while it is the only cell selected
            decorator
                .getEdgePortHandleProviderDecorator()
                .hideImplementation(edge -> hasMultipleSelectedCells());
            // a selected edge shows the handles of its points
            decorator.getHandleProviderDecorator().setFactory(YFilesAspectEditorCanvas::bendHandles);
            // only the main label of an edge can be dragged
            storeOf(model)
                .getMaster()
                .getDecorator()
                .getLabelDecorator()
                .getPositionHandlerDecorator()
                .hideImplementation(label -> !YFilesCellStore.isMainEdgeLabel(label));
        }
    }

    /** Returns a handle provider yielding the handles of the bends of an edge. */
    private static IHandleProvider bendHandles(IEdge edge) {
        return context -> {
            List<IHandle> result = new ArrayList<>();
            for (IBend bend : edge.getBends()) {
                Object found = bend.lookup(IHandle.class);
                if (found instanceof IHandle handle) {
                    result.add(handle);
                }
            }
            return result;
        };
    }

    /** Tests if more than one cell is selected. */
    private boolean hasMultipleSelectedCells() {
        return getGraphComponent().getSelection().size() > 1;
    }

    /* The handles depend on the selection: those of an edge go when a second one is selected. */
    @Override
    protected void selectionFlushed() {
        requeryHandles();
    }

    /*
     * The handles depend on the geometry too: an added point gets its handle at once, a
     * removed one loses it, and the handles of the other points are those of the new
     * bends (the store recreates the bends of an edge whose points changed).
     */
    @Override
    void cellsModified(List<ViewCell<AspectGraph>> cells) {
        super.cellsModified(cells);
        requeryHandles();
    }

    /** Has the editor input mode recompute its handles, if that mode is already installed. */
    private void requeryHandles() {
        var mode = this.editorMode;
        if (mode != null) {
            mode.requeryHandles();
        }
    }

    /* The editing gestures follow the mode: all in edit mode, moving in selection mode, none in preview. */
    @Override
    protected void configureMode(GraphViewMode mode) {
        super.configureMode(mode);
        boolean editing = mode == GraphViewMode.EDIT_MODE;
        boolean moving = editing || mode == GraphViewMode.SELECT_MODE;
        var editor = editorMode();
        editor.getMoveInputMode().setEnabled(moving);
        editor.getMoveUnselectedInputMode().setEnabled(moving);
        editor.getHandleInputMode().setEnabled(moving);
        editor.getMoveLabelInputMode().setEnabled(moving);
        editor.getCreateEdgeInputMode().setEnabled(editing);
        if (!editing) {
            finishEditing();
        }
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        if (!editable) {
            finishEditing();
        }
    }

    // ---------- clicks ----------

    /**
     * Tests if a click event is an unmodified left-button click on an editable canvas in
     * edit mode.
     */
    private boolean isPlainLeftClick(ClickEventArgs args) {
        return getMode() == GraphViewMode.EDIT_MODE && isEditable()
            && (args.getMouseButtons().value() & MouseButtons.LEFT.value()) != 0;
    }

    /** Tests if a given modifier key was held down during a click event. */
    private boolean hasModifier(ClickEventArgs args, ModifierKeys key) {
        return (args.getModifiers().value() & key.value()) != 0;
    }

    /** A click on a vertex starts edge creation; Alt-click adds or removes an edge point. */
    private void itemClicked(ItemClickedEventArgs<IModelItem> args) {
        if (!isPlainLeftClick(args) || args.getClickCount() != 1) {
            return;
        }
        var cell = YFilesCellStore.cellOf(args.getItem());
        if (cell == null) {
            return;
        }
        if (hasModifier(args, ModifierKeys.ALT)) {
            editPoint(cell(cell), args.getLocation());
        } else if (cell instanceof ViewVertex && !hasModifier(args, ModifierKeys.CONTROL)
            && !hasModifier(args, ModifierKeys.SHIFT) && args.getItem() instanceof INode node) {
            this.startClick = new Click(System.currentTimeMillis(), args.getLocation());
            // after the click has been handled, so that the gesture can take the input
            SwingUtilities.invokeLater(() -> startEdge(node));
        }
    }

    /** A double click on a cell opens its in-place editor. */
    private void itemDoubleClicked(ItemClickedEventArgs<IModelItem> args) {
        if (!isPlainLeftClick(args)) {
            return;
        }
        var cell = YFilesCellStore.cellOf(args.getItem());
        if (cell instanceof AspectViewCell aspectCell) {
            startEditing(aspectCell);
        }
    }

    /** A double click on empty canvas adds a vertex; Alt-click adds a point to the selected edge. */
    private void canvasClicked(ClickEventArgs args) {
        if (!isPlainLeftClick(args)) {
            return;
        }
        if (hasModifier(args, ModifierKeys.ALT)) {
            var selected = getSelectedCell();
            if (selected != null && args.getClickCount() == 1) {
                editPoint(selected, args.getLocation());
            }
        } else if (args.getClickCount() == 2 && !hasModifier(args, ModifierKeys.CONTROL)
            && !hasModifier(args, ModifierKeys.SHIFT)) {
            addVertex(args.getLocation());
        }
    }

    /**
     * Adds a point to the selected edge at a location, or removes the point there
     * if the clicked cell is the selected edge itself.
     */
    private void editPoint(ViewCell<AspectGraph> clicked, PointD location) {
        var selected = getSelectedCell();
        if (!(selected instanceof AspectViewEdge edge)) {
            return;
        }
        Point2D at = new Point2D.Double(location.getX(), location.getY());
        if (clicked == edge) {
            getController().removePoint(edge, at);
        } else {
            getController().addPoint(edge, at);
        }
    }

    // ---------- vertices and edges ----------

    /** Adds a vertex at a location and opens its in-place editor. */
    void addVertex(PointD location) {
        finishEditing();
        AspectGraphViewModel model = getNonNullViewModel();
        AspectVertexCell vertex = model.newVertex(model.createAspectNode());
        vertex.setNodeFixed();
        vertex.putVisual(VisualKey.NODE_POS, snap(location));
        // the vertex and its first label are one edit, settled when the editor closes
        model.insertPending(List.of(vertex), List.of(), List.of());
        select(List.of(vertex));
        startEditing(vertex);
        if (!isEditingLabel()) {
            model.settlePendingInsertion();
        }
    }

    /** Snaps a location to the grid, if the grid is on. */
    private Point2D snap(PointD location) {
        double x = location.getX();
        double y = location.getY();
        if (this.gridEnabled) {
            x = Math.round(x / GRID_SIZE) * GRID_SIZE;
            y = Math.round(y / GRID_SIZE) * GRID_SIZE;
        }
        return new Point2D.Double(x, y);
    }

    /** Starts click-click edge creation at a node, if no other gesture is under way. */
    private void startEdge(INode node) {
        var createEdge = editorMode().getCreateEdgeInputMode();
        if (!createEdge.isEnabled() || createEdge.isCreationInProgress() || isEditingLabel()
            || YFilesCellStore.cellOf(node) == null) {
            return;
        }
        try {
            createEdge.doStartEdgeCreation(new DefaultPortCandidate(node));
        } catch (IllegalStateException exc) {
            // another gesture holds the input: no edge creation from this click
        }
    }

    /**
     * Creates the edge of a finished edge creation gesture through the view model:
     * the callback of the edge creation mode.
     * @param target the target port candidate, or {@code null} if yFiles found none;
     * consulted only if the finishing mouse event is unknown, see
     * {@link #vertexAt(PointD)}
     */
    private @Nullable IEdge createEdge(IInputModeContext context, IGraph graph,
                                       IPortCandidate source, @Nullable IPortCandidate target,
                                       IEdge template) {
        var sourceVertex = vertexOf(source.getOwner());
        if (sourceVertex == null) {
            return null;
        }
        var finishing = this.finishingEvent;
        this.finishingEvent = null;
        var start = this.startClick;
        this.startClick = null;
        // the vertex under the finishing click is the end of the gesture, as in the
        // JGraph editor, rather than yFiles' target candidate, which is only found
        // within the snap distance of a vertex's port: a click at the border of a wide
        // vertex ends on that vertex too
        AspectVertexCell end = finishing == null
            ? target == null
                ? null
                : vertexOf(target.getOwner())
            : vertexAt(finishing.getLocation());
        if (end == sourceVertex) {
            // the gesture ended on its own vertex: no edge, as in the JGraph editor, where
            // a self-loop takes a gesture ending on empty canvas; the finishing click
            // may be the second of a double click on the vertex, which opens the
            // vertex's editor once the gesture has finished
            if (finishing != null && start != null && isDoubleClick(start, finishing)) {
                SwingUtilities.invokeLater(() -> startEditing(sourceVertex));
            }
            return null;
        }
        // no vertex under the finishing click: a self-loop
        AspectVertexCell targetVertex = end == null
            ? sourceVertex
            : end;
        finishEditing();
        AspectGraphViewModel model = getNonNullViewModel();
        AspectEdgeCell edge = model.newEdge(null);
        // a single, empty label so the edge will be displayed
        edge.getEditableLabels().add("");
        Point2D from = sourceVertex.getVisuals().getNodePos();
        List<Point2D> points = new ArrayList<>();
        points.add(new Point2D.Double(from.getX(), from.getY()));
        if (targetVertex == sourceVertex && finishing != null) {
            // a loop bulges towards the pointer, as in the JGraph editor: the location
            // of the finishing click is its control point
            PointD at = finishing.getLocation();
            points.add(new Point2D.Double(at.getX(), at.getY()));
            points.add(new Point2D.Double(from.getX(), from.getY()));
        } else {
            Point2D to = targetVertex.getVisuals().getNodePos();
            points.add(new Point2D.Double(to.getX(), to.getY()));
        }
        edge.putVisual(VisualKey.POINTS, points);
        // the edge and its first label are one edit, settled when the editor closes
        model
            .insertPending(List.of(), List.of(edge),
                           List.of(new Connection<>(edge, sourceVertex, targetVertex)));
        select(List.of(edge));
        // the in-place editor opens once the gesture has finished
        SwingUtilities.invokeLater(() -> {
            if (model.getCells().contains(edge)) {
                startEditing(edge);
                if (!isEditingLabel()) {
                    model.settlePendingInsertion();
                }
            }
        });
        return (IEdge) YFilesCellStore.itemOf(edge);
    }

    /**
     * Returns the vertex cell of a given port owner, or {@code null} if the owner is not a
     * node with such a cell.
     */
    private static @Nullable AspectVertexCell vertexOf(@Nullable IPortOwner owner) {
        return owner instanceof INode node
            && YFilesCellStore.cellOf(node) instanceof AspectVertexCell vertex
                ? vertex
                : null;
    }

    /**
     * Returns the topmost vertex cell shown at a world location, or {@code null} if
     * there is none: the hit test of an edge creation gesture's end.
     */
    @AIGenerated("Claude Fable 5.1, 2026-10")
    private @Nullable AspectVertexCell vertexAt(PointD location) {
        for (IModelItem item : getGraphComponent().getGraphModelManager().hitElementsAt(location)) {
            if (YFilesCellStore.cellOf(item) instanceof AspectVertexCell vertex) {
                return vertex;
            }
        }
        return null;
    }

    // ---------- moves ----------

    /**
     * Configures a move mode: a drag records the move of the dragged items when it
     * ends; with Control held at its start, the items stay put and a preview of their
     * copies is dragged instead, which the end of the drag turns into copies (see
     * {@link #copyMoved}).
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    private void configureMove(MoveInputMode mode) {
        mode.addQueryPositionHandlerListener((src, args) -> {
            if (isControlDown()) {
                IPositionHandler moving = args.getPositionHandler();
                if (moving == null) {
                    moving = mode.getPositionHandler();
                }
                if (moving != null) {
                    args.setPositionHandler(new CopyDragHandler(mode, moving));
                    args.setHandled(true);
                }
            }
        });
        // the labels follow the edges while these are dragged
        mode.addDraggedListener((src, args) -> followDrag(mode.getAffectedItems()));
        mode.addDragFinishedListener((src, args) -> {
            var preview = this.copyPreview;
            this.copyPreview = null;
            if (preview == null) {
                recordMove(mode.getAffectedItems());
            } else {
                copyMoved(mode.getAffectedItems(), preview.getDx(), preview.getDy());
            }
        });
    }

    /**
     * Indicates if a mouse event, following a given click, makes that click a double
     * click: within the desktop's double-click interval and a few pixels of the click.
     * The clicks are counted here rather than taken from yFiles, which reports the
     * finishing click of the gesture as a first click (seen with synthetic clicks; the
     * reason is unknown).
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    private boolean isDoubleClick(Click click, Mouse2DEventArgs event) {
        if (System.currentTimeMillis() - click.time() > multiClickInterval()) {
            return false;
        }
        var component = getGraphComponent();
        PointD from = component.toViewCoordinates(click.location());
        PointD to = component.toViewCoordinates(event.getLocation());
        return from.distanceTo(to) <= MULTI_CLICK_DISTANCE;
    }

    /** Returns the desktop's double-click interval, in milliseconds. */
    private static long multiClickInterval() {
        return Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval") instanceof Integer interval
            ? interval
            : DEFAULT_MULTI_CLICK_INTERVAL;
    }

    /** Double-click interval when the desktop does not say, in milliseconds. */
    private static final long DEFAULT_MULTI_CLICK_INTERVAL = 500;
    /** Distance, in view pixels, within which two clicks make a double click. */
    private static final double MULTI_CLICK_DISTANCE = 4;

    /** A click, by its time and its world location. */
    private record Click(long time, PointD location) {
        // no members beyond the components
    }

    /** The click that started the edge creation gesture in progress, if any. */
    private @Nullable Click startClick;

    /** Indicates if the Control key is down in the last mouse event. */
    private boolean isControlDown() {
        var event = getGraphComponent().getLastMouse2DEvent();
        return event != null
            && (event.getModifiers().value() & ModifierKeys.CONTROL.value()) != 0;
    }

    /** The preview of the copy drag in progress, if any; see {@link CopyDragHandler}. */
    private @Nullable CopyDragPreview copyPreview;

    /**
     * Position handler of a drag with Control held: leaves the items where they are
     * and moves a preview of their copies instead. The handler of the move proper is
     * wrapped, and initialised and cancelled around the drag, so that the mode
     * registers the affected items as usual; it never moves them.
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    private final class CopyDragHandler implements IPositionHandler {
        /** Creates a handler wrapping the position handler of a move. */
        CopyDragHandler(MoveInputMode mode, IPositionHandler moving) {
            this.mode = mode;
            this.moving = moving;
        }

        /** The move input mode whose drag is wrapped. */
        private final MoveInputMode mode;
        /** The wrapped position handler, which moves the items proper. */
        private final IPositionHandler moving;

        @Override
        public IPoint getLocation() {
            return this.moving.getLocation();
        }

        @Override
        @NonNullByDefault({})
        public void initializeDrag(IInputModeContext context) {
            this.moving.initializeDrag(context);
            var preview = new CopyDragPreview(this.mode.getAffectedItems());
            preview.show();
            YFilesAspectEditorCanvas.this.copyPreview = preview;
        }

        @Override
        @NonNullByDefault({})
        public void handleMove(IInputModeContext context, PointD originalLocation,
                               PointD newLocation) {
            var preview = YFilesAspectEditorCanvas.this.copyPreview;
            if (preview != null) {
                preview
                    .moveTo(newLocation.getX() - originalLocation.getX(),
                            newLocation.getY() - originalLocation.getY());
            }
        }

        @Override
        @NonNullByDefault({})
        public void cancelDrag(IInputModeContext context, PointD originalLocation) {
            this.moving.cancelDrag(context, originalLocation);
            var preview = YFilesAspectEditorCanvas.this.copyPreview;
            YFilesAspectEditorCanvas.this.copyPreview = null;
            if (preview != null) {
                preview.hide();
            }
        }

        /* The preview is hidden; the DragFinished listener of the mode takes it up for the copy. */
        @Override
        @NonNullByDefault({})
        public void dragFinished(IInputModeContext context, PointD originalLocation,
                                 PointD newLocation) {
            // the items never moved: the wrapped handler is cancelled, not finished
            this.moving.cancelDrag(context, originalLocation);
            var preview = YFilesAspectEditorCanvas.this.copyPreview;
            if (preview != null) {
                preview.hide();
            }
        }
    }

    /**
     * Translucent preview of the copies of a drag with Control held: the dragged
     * vertices, the edges between them and the labels of both, drawn as the items
     * are but at the dragged offset.
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    private final class CopyDragPreview implements IVisualCreator {
        /** Creates the preview of the copies of given dragged items. */
        CopyDragPreview(Iterable<IModelItem> items) {
            Set<INode> nodes = new LinkedHashSet<>();
            for (IModelItem item : items) {
                if (item instanceof INode node) {
                    nodes.add(node);
                }
            }
            Set<IEdge> edges = new LinkedHashSet<>();
            IGraph graph = getShownGraph();
            for (INode node : nodes) {
                for (IEdge edge : graph.edgesAt(node)) {
                    if (nodes.contains(edge.getSourceNode())
                        && nodes.contains(edge.getTargetNode())) {
                        edges.add(edge);
                    }
                }
            }
            this.nodes = nodes;
            this.edges = edges;
        }

        /** The dragged vertices. */
        private final Set<INode> nodes;
        /** The edges between the dragged vertices. */
        private final Set<IEdge> edges;
        /** The dragged offset, horizontally. */
        private double dx;
        /** The dragged offset, vertically. */
        private double dy;
        /** The canvas object showing this preview, while it is shown. */
        private @Nullable ICanvasObject object;

        /** Shows the preview on the canvas, above the graph. */
        void show() {
            this.object = getGraphComponent()
                .getInputModeGroup()
                .addChild(this, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
        }

        /** Takes the preview off the canvas. */
        void hide() {
            var object = this.object;
            this.object = null;
            if (object != null) {
                object.remove();
                repaint();
            }
        }

        /** Moves the preview to a given offset from the items. */
        void moveTo(double dx, double dy) {
            this.dx = dx;
            this.dy = dy;
            repaint();
        }

        /** Returns the horizontal offset of the preview. */
        double getDx() {
            return this.dx;
        }

        /** Returns the vertical offset of the preview. */
        double getDy() {
            return this.dy;
        }

        @Override
        @NonNullByDefault({})
        public IVisual createVisual(IRenderContext context) {
            VisualGroup group = new VisualGroup();
            for (IEdge edge : this.edges) {
                var style = edge.getStyle();
                add(group, style.getRenderer().getVisualCreator(edge, style).createVisual(context));
            }
            for (INode node : this.nodes) {
                var style = node.getStyle();
                add(group, style.getRenderer().getVisualCreator(node, style).createVisual(context));
            }
            for (IEdge edge : this.edges) {
                addLabels(group, edge.getLabels(), context);
            }
            for (INode node : this.nodes) {
                addLabels(group, node.getLabels(), context);
            }
            group.setTransform(AffineTransform.getTranslateInstance(this.dx, this.dy));
            return new Ghost(group);
        }

        @Override
        @NonNullByDefault({})
        public IVisual updateVisual(IRenderContext context, @Nullable IVisual oldVisual) {
            if (oldVisual instanceof Ghost ghost) {
                ghost.group.setTransform(AffineTransform.getTranslateInstance(this.dx, this.dy));
                return ghost;
            }
            return createVisual(context);
        }

        /** Adds the visuals of given labels to the preview group. */
        private void addLabels(VisualGroup group, Iterable<ILabel> labels,
                               IRenderContext context) {
            for (ILabel label : labels) {
                var style = label.getStyle();
                add(group, style.getRenderer().getVisualCreator(label, style).createVisual(context));
            }
        }

        /** Adds a visual to the preview group, unless it is {@code null}. */
        private void add(VisualGroup group, @Nullable IVisual visual) {
            if (visual != null) {
                group.add(visual);
            }
        }
    }

    /** A group of visuals painted translucently. */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    private static final class Ghost implements IVisual {
        /** Creates a ghost of a given group of visuals. */
        Ghost(VisualGroup group) {
            this.group = group;
        }

        /** The group of visuals painted translucently. */
        final VisualGroup group;

        @Override
        @NonNullByDefault({})
        public void paint(IRenderContext context, Graphics2D g) {
            Composite composite = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, GHOST_ALPHA));
            this.group.paint(context, g);
            g.setComposite(composite);
        }

        /** Opacity of a preview. */
        private static final float GHOST_ALPHA = 0.5f;
    }

    /**
     * Copies the vertices among the items of a drag, and the edges between them, to a
     * given offset from where they are, as one edit, and selects the copies: the end
     * of a drag with Control held, whose preview was dragged to that offset.
     * @return {@code false} if no vertices were among the items, so that nothing was copied
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    boolean copyMoved(Iterable<IModelItem> items, double dx, double dy) {
        AspectGraphViewModel model = getNonNullViewModel();
        List<ViewCell<AspectGraph>> copied = new ArrayList<>();
        for (IModelItem item : items) {
            if (item instanceof INode node
                && YFilesCellStore.cellOf(node) instanceof AspectVertexCell vertex) {
                copied.add(vertex);
            }
        }
        if (copied.isEmpty()) {
            return false;
        }
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell edge && copied.contains(edge.getSourceVertex())
                && copied.contains(edge.getTargetVertex())) {
                copied.add(edge);
            }
        }
        select(model.insertFragment(GraphFragment.of(copied), dx, dy));
        return true;
    }

    /*
     * Extends the recorded move with the reconnection of an edge whose end was dragged
     * to another vertex, which lands in the same edit as the visual changes.
     */
    @Override
    @SuppressWarnings("unchecked") // the cells of this canvas are over AspectGraph
    protected void applyMove(Map<ViewCell<AspectGraph>,VisualMap> change,
                             Collection<IEdge> edges) {
        ViewEdge<AspectGraph> reconnected = null;
        IEdge reconnectedItem = null;
        for (IEdge edge : edges) {
            var cell = YFilesCellStore.cellOf(edge);
            if (cell instanceof ViewEdge<?> edgeCell && cell.getItem() == edge
                && isReconnected(edgeCell, edge)) {
                assert reconnected == null : "Gestures reconnect one edge at a time";
                reconnected = (ViewEdge<AspectGraph>) edgeCell;
                reconnectedItem = edge;
            }
        }
        if (reconnected != null && reconnectedItem != null) {
            reconnect(reconnected, reconnectedItem, change);
        } else {
            super.applyMove(change, edges);
        }
    }

    /** Records the new position of the dragged edge label (see {@link #recordLabelMove(ILabel)}). */
    void recordLabelMove() {
        ILabel label = this.movedLabel;
        this.movedLabel = null;
        if (label != null) {
            recordLabelMove(label);
        }
    }

    /** Records the reconnection of an edge whose end was dragged, if not recorded yet. */
    @SuppressWarnings("unchecked") // the cells of this canvas are over AspectGraph
    void recordReconnection(IEdge edge) {
        var cell = YFilesCellStore.cellOf(edge);
        if (cell instanceof ViewEdge<?> edgeCell && cell.getItem() == edge
            && isReconnected(edgeCell, edge)) {
            Map<ViewCell<AspectGraph>,VisualMap> change = new LinkedHashMap<>();
            VisualMap visuals = new VisualMap();
            visuals.setPoints(route(edge));
            change.put(cell(cell), visuals);
            reconnect((ViewEdge<AspectGraph>) edgeCell, edge, change);
        }
    }

    /** Indicates if the ends of an edge item differ from those of its cell. */
    private static boolean isReconnected(ViewEdge<?> cell, IEdge edge) {
        return cell.getSourceVertex() != YFilesCellStore.cellOf(edge.getSourceNode())
            || cell.getTargetVertex() != YFilesCellStore.cellOf(edge.getTargetNode());
    }

    /** Reconnects an edge cell to the ends of its item, with visual changes, as one edit. */
    @SuppressWarnings("unchecked") // the cells of this canvas are over AspectGraph
    private void reconnect(ViewEdge<AspectGraph> cell, IEdge edge,
                           Map<ViewCell<AspectGraph>,VisualMap> change) {
        var source = YFilesCellStore.cellOf(edge.getSourceNode());
        var target = YFilesCellStore.cellOf(edge.getTargetNode());
        if (source instanceof ViewVertex<?> && target instanceof ViewVertex<?>) {
            getNonNullViewModel()
                .reconnect(cell, (ViewVertex<AspectGraph>) source,
                           (ViewVertex<AspectGraph>) target, change);
        }
    }

    // ---------- in-place editing ----------

    /* Opens yFiles' text editor on the editable labels of the cell. */
    @Override
    public void startEditing(ViewCell<AspectGraph> cell) {
        if (!(cell instanceof AspectViewCell aspectCell) || !isEditable()) {
            return;
        }
        finishEditing();
        TextEditorInputMode text = editorMode().getTextEditorInputMode();
        JTextArea area = text.getTextArea();
        var completion = this.completion;
        if (completion == null) {
            this.completion = completion = new LabelCompletion(area);
        }
        completion.setLabels(LabelCompletion.labelsFor(getNonNullViewModel().getTypeGraph()));
        area.setFont(Fonts.getLabelFont().deriveFont(cell.getVisuals().getFont()));
        // bound here rather than once at construction: the text area of the editor mode
        // is not the one it later edits in (found on screen, where a binding made at
        // construction did not take)
        bindLineBreaks(area);
        area.setText(aspectCell.getEditableLabels().toEditString());
        area.selectAll();
        Point2D at = editorLocation(cell);
        text.setLocation(new PointD(at.getX(), at.getY()));
        this.editedCell = aspectCell;
        notifyEditingStarted(cell);
        text.setEditing(true);
        // the labels are selected, ready to be typed over: again now that the editor
        // has shown the text area, which places the caret, and once more after the
        // events queued before it opened have run
        area.selectAll();
        SwingUtilities.invokeLater(area::selectAll);
    }

    /** Returns the location of the in-place editor for a cell: the top-left of a vertex, the label position of an edge. */
    private Point2D editorLocation(ViewCell<AspectGraph> cell) {
        var item = YFilesCellStore.itemOf(cell);
        if (item instanceof INode node) {
            PointD topLeft = node.getLayout().getTopLeft();
            return new Point2D.Double(topLeft.getX(), topLeft.getY());
        } else if (item instanceof IEdge edge && edge.getLabels().size() > 0) {
            PointD centre = edge.getLabels().getItem(0).getLayout().getCenter();
            return new Point2D.Double(centre.getX(), centre.getY());
        } else if (item instanceof IEdge edge) {
            List<Point2D> points = route(edge);
            Point2D first = points.get(0);
            Point2D last = points.get(points.size() - 1);
            return new Point2D.Double((first.getX() + last.getX()) / 2,
                (first.getY() + last.getY()) / 2);
        }
        return new Point2D.Double();
    }

    /**
     * Commits the text of the in-place editor as the labels of the edited cell.
     * A cell created for the editor is settled with it: recorded with its first
     * label, or withdrawn if it is an edge that got none.
     */
    private void commitLabels(String text) {
        var cell = this.editedCell;
        this.editedCell = null;
        AspectGraphViewModel model = getNonNullViewModel();
        if (cell != null && !text.equals(cell.getEditableLabels().toEditString())) {
            var labels = new EditableLabels();
            labels.load(text);
            model.changeLabels(cell, labels);
        } else {
            model.settlePendingInsertion();
        }
    }

    /* As the JGraph editor: editing controller, not in preview mode. */
    @Override
    public boolean hasActiveEditor() {
        return getController().isEditing() && getMode() != GraphViewMode.PREVIEW_MODE;
    }

    /** Indicates if the in-place editor is open on a cell. */
    boolean isEditingLabel() {
        var editor = this.editorMode;
        return editor != null && editor.getTextEditorInputMode().isEditing();
    }

    @Override
    public void finishEditing() {
        var editor = this.editorMode;
        if (editor != null && editor.getTextEditorInputMode().isEditing()) {
            editor.getTextEditorInputMode().stop();
        }
    }

    /** The cell whose labels are being edited in place; {@code null} if none. */
    private @Nullable AspectViewCell editedCell;
    /** The label completion on the text editor; created on first use. */
    private @Nullable LabelCompletion completion;

    // ---------- the grid ----------

    /* Snaps vertex centres and edge points to the grid, and shows it. */
    @Override
    public void setGridEnabled(boolean enabled) {
        this.gridEnabled = enabled;
        var snapContext = this.snapContext;
        var gridVisual = this.gridVisual;
        if (snapContext == null || gridVisual == null) {
            var grid = new GridInfo(GRID_SIZE);
            snapContext = new GraphSnapContext();
            // the grid is the only thing to snap to
            snapContext.setCollectingNodeSnapLinesEnabled(false);
            snapContext.setCollectingNodePairSnapLinesEnabled(false);
            snapContext.setCollectingNodePairCenterSnapLinesEnabled(false);
            snapContext.setCollectingNodePairSegmentSnapLinesEnabled(false);
            snapContext.setCollectingEdgeSnapLinesEnabled(false);
            snapContext.setCollectingPortSnapLinesEnabled(false);
            snapContext.setCollectingNodeSizesEnabled(false);
            snapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(grid));
            snapContext.setBendGridConstraintProvider(new GridConstraintProvider<>(grid));
            snapContext.setPortGridConstraintProvider(new GridConstraintProvider<>(grid));
            gridVisual = new GridVisualCreator(grid);
            this.gridObject = getGraphComponent()
                .getBackgroundGroup()
                .addChild(gridVisual, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
            this.snapContext = snapContext;
            this.gridVisual = gridVisual;
        }
        editorMode()
            .setSnapContext(enabled
                ? snapContext
                : null);
        gridVisual.setVisible(enabled);
        repaint();
    }

    /** If {@code true}, the grid is shown and dragged items snap to it. */
    private boolean gridEnabled;
    /** The snap context of the grid; {@code null} until the grid is first enabled. */
    private @Nullable GraphSnapContext snapContext;
    /** The visual of the grid; {@code null} until the grid is first enabled. */
    private @Nullable GridVisualCreator gridVisual;
    @SuppressWarnings("unused")
    /** The canvas object of the grid visual; {@code null} until the grid is first enabled. */
    private @Nullable ICanvasObject gridObject;
    /** Spacing of the grid, as in the JGraph backend. */
    private static final double GRID_SIZE = 10;
}
