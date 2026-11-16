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

import static nl.utwente.groove.gui.view.ViewOptions.SHOW_ANCHORS_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_ARROWS_ON_LABELS_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_BIDIRECTIONAL_EDGES_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_INTERNAL_NODE_IDS_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_USER_NODE_IDS_OPTION;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.ContextConfigurator;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.HierarchicNestingPolicy;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.ModifierKeys;
import com.yworks.yfiles.view.MouseButtons;
import com.yworks.yfiles.view.MouseWheelBehaviors;
import com.yworks.yfiles.view.PixelImageExporter;
import com.yworks.yfiles.view.ViewportChanges;
import com.yworks.yfiles.view.input.AbstractPopupMenuInputMode;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.DoubleClickPolicy;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;
import com.yworks.yfiles.view.input.MarqueeSelectionInputMode;
import com.yworks.yfiles.view.input.MoveInputMode;
import com.yworks.yfiles.view.input.MoveLabelInputMode;
import com.yworks.yfiles.view.input.MoveViewportInputMode;
import com.yworks.yfiles.view.input.MultiplexingInputMode;
import com.yworks.yfiles.view.input.PopulateMenuEventArgs;

import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.CellChange;
import nl.utwente.groove.gui.view.EdgeGeometry;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.GraphViewController;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.gui.view.GraphViewModel;
import nl.utwente.groove.gui.view.OptionRefreshListener;
import nl.utwente.groove.gui.view.ParallelEdges;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewOptions;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.util.AIGenerated;

/**
 * The yFiles implementation of {@link GraphCanvas}: a yFiles graph component showing
 * the cells of a view model through the items of a {@link YFilesCellStore}, with the
 * shared styles of {@link CellStyles} rendering the cells' visuals.
 * <p>
 * The canvas is a viewer: it selects, scrolls, zooms, hides and shows cells, applies
 * visual edits through the edit funnel and exports images, but does not edit the graph
 * (phase 3 of the migration). Selection is kept in the yFiles selection model and
 * mirrored into the {@link VisualKey#EMPHASIS} visual of the cells, from which the
 * styles paint it; it never enters an edit history.
 * @param <G> the type of graph displayed
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
abstract class YFilesCanvas<G extends Graph> implements GraphCanvas<G> {
    /**
     * Constructs a canvas as the canvas of a given controller.
     * The canvas attaches itself to the controller before installing its listeners,
     * as those ask the controller for its canvas.
     */
    protected YFilesCanvas(GraphViewController<G> controller) {
        this.controller = controller;
        controller.attachCanvas(this);
        this.component = new Viewer();
        this.inputMode = createInputMode();
        configureComponent();
        installListeners();
    }

    /** Configures the graph component. */
    private void configureComponent() {
        var component = this.component;
        component.setGraph(new DefaultGraph());
        component
            .setMouseWheelBehavior(MouseWheelBehaviors.SCROLL.or(MouseWheelBehaviors.ZOOM));
        component.setAnimatedViewportChanges(ViewportChanges.NONE);
        // the styles paint the selection and there is no focus concept
        component.getSelectionIndicatorManager().setEnabled(false);
        component.getFocusIndicatorManager().setEnabled(false);
        var manager = component.getGraphModelManager();
        manager.setHierarchicNestingPolicy(HierarchicNestingPolicy.NONE);
        // edge labels are painted below the nodes, as in the JGraph backend, so that a
        // label of an edge that passes behind a node is hidden with the edge
        manager.getEdgeLabelGroup().below(manager.getNodeGroup());
        component.setInputMode(this.inputMode);
        component.setBackground(Color.WHITE);
        // keep the graph clear of the license watermark along the top of the canvas
        component.setContentMargins(new InsetsD(WATERMARK_HEIGHT, 0, 0, 0));
    }

    /**
     * Height of the watermark that the academic license paints along the top of every
     * canvas. The content margins and the initial view point keep the graph below it.
     */
    private static final double WATERMARK_HEIGHT = 40;

    /**
     * Creates the input mode of the component; called once, during construction.
     * This implementation creates the viewer mode, see {@link #createViewerInputMode()};
     * an editing canvas creates an editor mode, configured through the same helpers.
     */
    protected MultiplexingInputMode createInputMode() {
        return createViewerInputMode();
    }

    /** Creates the input mode of the viewer: click and marquee selection, panning. */
    protected GraphViewerInputMode createViewerInputMode() {
        var result = new GraphViewerInputMode();
        GraphItemTypes cells = GraphItemTypes.NODE.or(GraphItemTypes.EDGE);
        result.setSelectableItems(cells);
        result.setClickableItems(cells);
        result.setMarqueeSelectableItems(cells);
        result.setSelectablePredicate(selectablePredicate());
        result.setFocusableItems(GraphItemTypes.NONE);
        result.setToolTipItems(GraphItemTypes.NONE);
        result.setPopupMenuItems(GraphItemTypes.NONE);
        configurePopupMenu(result.getPopupMenuInputMode());
        result.setClipboardOperationsAllowed(false);
        configureMarquee(result.getMarqueeSelectionInputMode());
        configurePan(result.getMoveViewportInputMode());
        configureMove(result);
        // clicks are forwarded to the Swing mouse listeners of the component, which
        // yFiles' own input surface (a child component) keeps from receiving them
        // (the viewer input mode handles the clicks of its click mode itself and raises
        // these item and canvas events instead; the click mode's own events stay silent)
        result.getClickInputMode()
            .setDoubleClickPolicy(DoubleClickPolicy.INITIAL_SINGLE_AND_DOUBLE_CLICK);
        result.addItemClickedListener((src, args) -> forwardClick(args));
        result.addItemDoubleClickedListener((src, args) -> forwardClick(args));
        result.addCanvasClickedListener((src, args) -> forwardClick(args));
        result.addMultiSelectionStartedListener((src, args) -> beginSelectionBatch());
        result.addMultiSelectionFinishedListener((src, args) -> endSelectionBatch());
        return result;
    }

    /** Returns the predicate for the selectable items: cells that are not grayed out. */
    protected Predicate<IModelItem> selectablePredicate() {
        return item -> {
            var cell = YFilesCellStore.cellOf(item);
            return cell != null && !cell.isGrayedOut();
        };
    }

    /** Configures the popup menu mode to show GROOVE's popup menu, populated on request at any location. */
    protected void configurePopupMenu(AbstractPopupMenuInputMode<?> popup) {
        popup.addPopulateMenuListener((src, args) -> populatePopupMenu(args));
    }

    /** Configures the marquee mode to select on a plain drag (in selection mode). */
    protected void configureMarquee(MarqueeSelectionInputMode marquee) {
        marquee.setPressedRecognizer(IEventRecognizer.MOUSE_LEFT_PRESSED);
        marquee.setDraggedRecognizer(IEventRecognizer.MOUSE_LEFT_DRAGGED);
        marquee.setReleasedRecognizer(IEventRecognizer.MOUSE_LEFT_RELEASED);
        this.marquee = marquee;
    }

    /** Configures the viewport mode to pan on a plain drag (in pan mode). */
    protected void configurePan(MoveViewportInputMode pan) {
        pan.setPressedRecognizer(IEventRecognizer.MOUSE_LEFT_PRESSED);
        pan.setDraggedRecognizer(IEventRecognizer.MOUSE_LEFT_DRAGGED);
        pan.setReleasedRecognizer(IEventRecognizer.MOUSE_LEFT_RELEASED);
        this.pan = pan;
    }

    /**
     * Adds a mode moving cells to a viewer input mode: a drag on a vertex moves it, with
     * the rest of the selection if the vertex is selected, and a drag on the main label
     * of an edge moves that label, as on the JGraph backend.
     * <p>
     * A {@link GraphViewerInputMode} has no move mode of its own (an editor mode has two,
     * one for the selected and one for the unselected items) and does not configure the
     * one added here, so this canvas provides both the test deciding where a drag may
     * start and the handler moving the items, which is the documented way to use a
     * stand-alone {@link MoveInputMode}. One mode covers both cases, as the moved items
     * are decided by the hit test anyway. Its priority puts it right before the marquee
     * selection, whichever priority that has in the viewer mode.
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    protected void configureMove(GraphViewerInputMode viewer) {
        var move = new MoveInputMode();
        // the viewer mode's marquee has priority 30 (the editor mode's has 50, its move
        // mode 40): a move mode behind the marquee never sees a press, which starts a
        // marquee instead, so the move modes go right before it
        move.setPriority(viewer.getMarqueeSelectionInputMode().getPriority() - 2);
        move.setHitTestable((context, location) -> {
            this.moveCandidate = movableItemAt(location);
            return this.moveCandidate != null;
        });
        move.addQueryPositionHandlerListener((src, args) -> {
            List<IModelItem> items = movedItems();
            List<IPositionHandler> handlers = new ArrayList<>();
            for (IModelItem item : items) {
                IPositionHandler handler = positionHandlerOf(item);
                if (handler != null) {
                    handlers.add(handler);
                }
            }
            if (!handlers.isEmpty()) {
                this.movedItems = items;
                args.setPositionHandler(IPositionHandler.combine(handlers));
                args.setHandled(true);
            }
        });
        // the labels follow the edges while these are dragged
        move.addDraggedListener((src, args) -> followDrag(this.movedItems));
        move.addDragFinishedListener((src, args) -> {
            recordMove(this.movedItems);
            this.movedItems = List.of();
        });
        move.addDragCanceledListener((src, args) -> this.movedItems = List.of());
        viewer.add(move);
        this.move = move;
        // the main label of an edge is dragged by yFiles' label move mode, as in the
        // editor (a label has no position handler of its own for the move mode above);
        // labels are never selected here, so it moves unselected labels; it comes right
        // after the move mode, so that a vertex covering a label takes the drag
        var moveLabel = new MoveLabelInputMode() {
            /* Only the main label of an edge moves, of a cell that may also be selected. */
            @Override
            @NonNullByDefault({})
            protected boolean shouldMove(IInputModeContext context, ILabel label) {
                return YFilesCellStore.isMainEdgeLabel(label) && selectablePredicate().test(label);
            }
        };
        moveLabel.setPriority(move.getPriority() + 1);
        moveLabel.setMovingUnselectedLabelsAllowed(true);
        moveLabel
            .addDragStartedListener((src, args) -> this.draggedLabel = moveLabel.getMovedLabel());
        moveLabel.addDragFinishedListener((src, args) -> {
            var label = this.draggedLabel;
            this.draggedLabel = null;
            if (label != null) {
                recordLabelMove(label);
            }
        });
        moveLabel.addDragCanceledListener((src, args) -> this.draggedLabel = null);
        viewer.add(moveLabel);
        this.moveLabel = moveLabel;
    }

    /** Returns the handler moving a given item, if the item has one. */
    private static @Nullable IPositionHandler positionHandlerOf(IModelItem item) {
        return item.lookup(IPositionHandler.class);
    }

    /**
     * Returns the topmost vertex item at a world location that may be moved, if any:
     * moving is restricted to the cells that may also be selected.
     */
    private @Nullable IModelItem movableItemAt(PointD location) {
        Predicate<IModelItem> selectable = selectablePredicate();
        for (IModelItem item : this.component.getGraphModelManager().hitElementsAt(location)) {
            if (item instanceof INode && selectable.test(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns the items that a drag starting at the candidate item moves: the selection
     * if the candidate is part of it, the candidate alone if it is not.
     */
    private List<IModelItem> movedItems() {
        List<IModelItem> result = new ArrayList<>();
        var candidate = this.moveCandidate;
        if (candidate != null) {
            var selection = this.component.getSelection();
            if (selection.isSelected(candidate)) {
                for (IModelItem item : selection) {
                    result.add(item);
                }
            } else {
                result.add(candidate);
            }
        }
        return result;
    }

    /** The input mode of the component. */
    private final MultiplexingInputMode inputMode;
    /** The marquee selection mode, if configured. */
    private @Nullable MarqueeSelectionInputMode marquee;
    /** The viewport panning mode, if configured. */
    private @Nullable MoveViewportInputMode pan;
    /** The move mode of a viewer canvas, if configured. */
    private @Nullable MoveInputMode move;
    /** The label move mode of a viewer canvas, if configured. */
    private @Nullable MoveLabelInputMode moveLabel;
    /** The label dragged by the label move mode, from the start of the drag to its end. */
    private @Nullable ILabel draggedLabel;
    /** The item under the pointer where a move may start; set by the hit test. */
    private @Nullable IModelItem moveCandidate;
    /** The items moved by the drag in progress, if any. */
    private List<IModelItem> movedItems = List.of();

    /** Returns the move mode of a viewer canvas, if there is one; for the tests. */
    @Nullable MoveInputMode getMoveMode() {
        return this.move;
    }

    /** Returns the label move mode of a viewer canvas, if there is one; for the tests. */
    @Nullable MoveLabelInputMode getMoveLabelMode() {
        return this.moveLabel;
    }

    /**
     * Forwards a click detected by the input mode to the Swing mouse listeners of the
     * component, as a mouse-clicked event in the component's coordinates. The mouse events
     * themselves go to yFiles' input surface, a child component, so a listener on the
     * component would otherwise never hear of them; the displays and tabs of the Simulator
     * do listen there, for clicks and double clicks on cells.
     */
    void forwardClick(ClickEventArgs args) {
        PointD view = this.component.toViewCoordinates(args.getLocation());
        int keys = args.getModifiers().value();
        int modifiers = 0;
        if ((keys & ModifierKeys.CONTROL.value()) != 0) {
            modifiers |= InputEvent.CTRL_DOWN_MASK;
        }
        if ((keys & ModifierKeys.SHIFT.value()) != 0) {
            modifiers |= InputEvent.SHIFT_DOWN_MASK;
        }
        if ((keys & ModifierKeys.ALT.value()) != 0) {
            modifiers |= InputEvent.ALT_DOWN_MASK;
        }
        if ((keys & ModifierKeys.META.value()) != 0) {
            modifiers |= InputEvent.META_DOWN_MASK;
        }
        int buttons = args.getMouseButtons().value();
        int button;
        if ((buttons & MouseButtons.LEFT.value()) != 0) {
            button = MouseEvent.BUTTON1;
        } else if ((buttons & MouseButtons.RIGHT.value()) != 0) {
            button = MouseEvent.BUTTON3;
        } else if ((buttons & MouseButtons.MIDDLE.value()) != 0) {
            button = MouseEvent.BUTTON2;
        } else {
            button = MouseEvent.NOBUTTON;
        }
        var event = new MouseEvent(this.component, MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(), modifiers, (int) Math.round(view.getX()),
            (int) Math.round(view.getY()), args.getClickCount(), false, button);
        for (MouseListener listener : this.component.getMouseListeners()) {
            listener.mouseClicked(event);
        }
    }

    /**
     * Installs the listeners of this canvas: the selection listener on the
     * component, and the refresh listeners on the display options.
     */
    protected void installListeners() {
        this.component.getSelection().addItemSelectionChangedListener(this.selectionListener);
        addOptionListener(SHOW_INTERNAL_NODE_IDS_OPTION);
        addOptionListener(SHOW_USER_NODE_IDS_OPTION);
        addOptionListener(SHOW_ANCHORS_OPTION);
        addOptionListener(SHOW_ARROWS_ON_LABELS_OPTION);
        addOptionListener(SHOW_BIDIRECTIONAL_EDGES_OPTION);
    }

    @Override
    public void removeListeners() {
        this.component.getSelection().removeItemSelectionChangedListener(this.selectionListener);
        getController().removeListeners();
    }

    /** Adds a refresh listener to the menu item of an option with a given name. */
    protected void addOptionListener(String option) {
        getController().addOptionListener(option);
    }

    // ---------- structure ----------

    @Override
    public GraphViewController<G> getController() {
        return this.controller;
    }

    /** The controller of this canvas. */
    private final GraphViewController<G> controller;

    @Override
    public final ViewOptions getOptions() {
        return getController().getOptions();
    }

    @Override
    public @Nullable GraphViewModel<G> getViewModel() {
        return this.model;
    }

    /** The view model shown, if any. */
    private @Nullable GraphViewModel<G> model;

    /** Returns the store of a given view model, which must have been created by this backend. */
    protected static <G extends Graph> YFilesCellStore<G> storeOf(GraphViewModel<G> model) {
        var store = model.getStore();
        assert store instanceof YFilesCellStore : "View model not created by this backend";
        return (YFilesCellStore<G>) store;
    }

    // ---------- content ----------

    @Override
    public GraphViewModel<G> showGraph(G graph) {
        var result = newViewModel();
        result.loadGraph(graph);
        setViewModel(result);
        return result;
    }

    @Override
    public GraphViewModel<G> newViewModel() {
        return createViewModel(new YFilesCellStore<>());
    }

    /** Callback factory method for the view model of the role of this canvas, over a given store. */
    protected abstract GraphViewModel<G> createViewModel(YFilesCellStore<G> store);

    @Override
    public void setViewModel(@Nullable GraphViewModel<G> model) {
        var old = this.model;
        if (model == old) {
            return;
        }
        beginSelectionBatch();
        try {
            this.component.getSelection().clear();
        } finally {
            endSelectionBatch();
        }
        if (old != null) {
            storeOf(old).attach(null);
        }
        this.model = model;
        // the listeners (the label tree among them) adopt the model before its items
        // are configured, as the cells' visuals may depend on them
        for (var listener : this.canvasListeners) {
            listener.viewModelChanged(this, old, model);
        }
        if (model == null) {
            this.component.setGraph(new DefaultGraph());
        } else {
            var store = storeOf(model);
            store.attach(this);
            this.component.setGraph(store.getView());
        }
        this.component.updateContentRect();
        this.component.setViewPoint(new PointD(0, -WATERMARK_HEIGHT));
        setEnabled(model != null);
        if (model != null && getController().isInteractive()) {
            // create the popup menu to create and activate the actions therein
            getController().createPopupMenu(null);
        }
    }

    /** Callback from the store: cells were inserted into the model shown. */
    void cellsInserted(List<ViewCell<G>> cells) {
        this.component.updateContentRect();
        var change = new CellChange<>(cells, List.of(), List.of());
        for (var listener : this.canvasListeners) {
            listener.cellsChanged(this, change);
        }
        this.component.repaint();
    }

    @Override
    public Collection<? extends ViewCell<G>> getCells() {
        var model = this.model;
        return model == null
            ? Collections.emptyList()
            : model.getCells();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable ViewCell<G> getCellAt(double x, double y) {
        PointD location = this.component.toWorldCoordinates(new PointD(x, y));
        for (IModelItem item : this.component.getGraphModelManager().hitElementsAt(location)) {
            var cell = YFilesCellStore.cellOf(item);
            if (cell != null && !cell.isGrayedOut()) {
                return (ViewCell<G>) cell;
            }
        }
        return null;
    }

    // ---------- selection ----------

    @SuppressWarnings("unchecked")
    @Override
    public List<ViewCell<G>> getSelection() {
        List<ViewCell<G>> result = new ArrayList<>();
        for (IModelItem item : this.component.getSelection()) {
            var cell = YFilesCellStore.cellOf(item);
            if (cell != null) {
                result.add((ViewCell<G>) cell);
            }
        }
        return result;
    }

    @Override
    public boolean isSelectionEmpty() {
        return this.component.getSelection().size() == 0;
    }

    /** Returns the first selected cell, if any. */
    @Nullable
    ViewCell<G> getSelectedCell() {
        var selection = getSelection();
        return selection.isEmpty()
            ? null
            : selection.get(0);
    }

    /** Tests if a given cell is selected. */
    boolean isSelected(ViewCell<G> cell) {
        var item = YFilesCellStore.itemOf(cell);
        return item != null && this.component.getSelection().isSelected(item);
    }

    /** Selects or deselects a single cell, leaving the rest of the selection as it is. */
    void setSelected(ViewCell<G> cell, boolean selected) {
        var item = YFilesCellStore.itemOf(cell);
        if (item != null && this.component.getGraph().contains(item)) {
            this.component.getSelection().setSelected(item, selected);
        }
    }

    @Override
    public void select(Collection<? extends ViewCell<G>> cells) {
        var selection = this.component.getSelection();
        beginSelectionBatch();
        try {
            selection.clear();
            for (var cell : cells) {
                var item = YFilesCellStore.itemOf(cell);
                if (item != null && this.component.getGraph().contains(item)) {
                    selection.setSelected(item, true);
                }
            }
        } finally {
            endSelectionBatch();
        }
    }

    @Override
    public void selectElements(Collection<? extends Element> elements) {
        var model = getNonNullViewModel();
        List<ViewCell<G>> cells = new ArrayList<>();
        for (var elem : elements) {
            var cell = model.getCell(elem);
            if (cell != null) {
                cells.add(cell);
            }
        }
        if (!cells.isEmpty()) {
            select(cells);
        }
    }

    @Override
    public void clearSelection() {
        beginSelectionBatch();
        try {
            this.component.getSelection().clear();
        } finally {
            endSelectionBatch();
        }
    }

    /** Starts a batch of selection changes, reported to the listeners as one. */
    protected void beginSelectionBatch() {
        this.selectionBatchDepth++;
    }

    /** Ends a batch of selection changes. */
    protected void endSelectionBatch() {
        this.selectionBatchDepth--;
        if (this.selectionBatchDepth == 0) {
            flushSelectionChanges();
        }
    }

    /** Nesting depth of selection batches. */
    private int selectionBatchDepth;

    /** Mirrors a selection change of an item into its cell's emphasis. */
    private void onItemSelectionChanged(IModelItem item, boolean selected) {
        var cell = YFilesCellStore.cellOf(item);
        if (cell == null) {
            return;
        }
        cell.putVisual(VisualKey.EMPHASIS, selected);
        if (selected) {
            // selected cells are drawn in front, as in the JGraph backend
            this.component.getGraphModelManager().toFront(item);
            if (this.scrollTarget == null) {
                this.scrollTarget = item;
            }
        }
        this.selectionChanged = true;
        if (this.selectionBatchDepth == 0) {
            flushSelectionChanges();
        }
    }

    /** Reports pending selection changes to the listeners and scrolls to the selection. */
    private void flushSelectionChanges() {
        if (!this.selectionChanged) {
            return;
        }
        this.selectionChanged = false;
        var target = this.scrollTarget;
        this.scrollTarget = null;
        if (target != null) {
            scrollToItem(target);
        }
        this.component.repaint();
        for (var listener : this.canvasListeners) {
            listener.selectionChanged(this);
        }
        selectionFlushed();
    }

    /** Hook called after a batch of selection changes was reported; does nothing by default. */
    protected void selectionFlushed() {
        // empty
    }

    /** Flag indicating that the selection changed since the last report. */
    private boolean selectionChanged;
    /** The first item selected in the current batch, to scroll to. */
    private @Nullable IModelItem scrollTarget;

    /** The listener mirroring the yFiles selection into the cells. */
    private final com.yworks.yfiles.utils.IEventListener<com.yworks.yfiles.view.ItemSelectionChangedEventArgs<IModelItem>> selectionListener
        = (src, args) -> onItemSelectionChanged(args.getItem(), args.isItemSelected());

    // ---------- refreshing ----------

    @Override
    public void refresh(Collection<? extends ViewCell<G>> cells, boolean unselectGrayedOut) {
        var model = this.model;
        if (cells.isEmpty() || model == null) {
            return;
        }
        var store = storeOf(model);
        List<ViewCell<G>> unselected = new ArrayList<>();
        for (ViewCell<G> cell : cells) {
            store.updateItem(cell);
            boolean visible = cell.getVisuals().isVisible();
            if (unselectGrayedOut && (!visible || cell.isGrayedOut())) {
                unselected.add(cell);
            }
        }
        store.refreshVisibility();
        if (!unselected.isEmpty()) {
            var selection = this.component.getSelection();
            beginSelectionBatch();
            try {
                for (var cell : unselected) {
                    var item = YFilesCellStore.itemOf(cell);
                    if (item != null && selection.isSelected(item)) {
                        selection.setSelected(item, false);
                    }
                }
            } finally {
                endSelectionBatch();
            }
        }
        this.component.updateContentRect();
        if (!isSelectionEmpty()) {
            scrollToSelection();
        }
        this.component.repaint();
        // the refreshed cells changed: the label tree recounts them
        var change = new CellChange<>(List.of(), new ArrayList<>(cells), List.of());
        for (var listener : this.canvasListeners) {
            listener.cellsChanged(this, change);
        }
    }

    @Override
    public void refreshAll(boolean unselectGrayedOut) {
        refresh(getCells(), unselectGrayedOut);
    }

    @Override
    public void setGrayedOut(Set<ViewCell<G>> cells, boolean grayedOut) {
        Set<ViewCell<G>> changed = new HashSet<>();
        for (ViewCell<G> cell : cells) {
            if (cell.setGrayedOut(grayedOut)) {
                changed.add(cell);
                if (grayedOut && cell instanceof ViewVertex) {
                    // also gray out incident edges
                    Iterator<? extends ViewCell<G>> iter = cell.getContext();
                    while (iter.hasNext()) {
                        ViewCell<G> c = iter.next();
                        if (c.setGrayedOut(true)) {
                            changed.add(c);
                        }
                    }
                } else if (!grayedOut && cell instanceof ViewEdge) {
                    // also revive end nodes
                    Iterator<? extends ViewCell<G>> iter = cell.getContext();
                    while (iter.hasNext()) {
                        ViewCell<G> c = iter.next();
                        if (c.setGrayedOut(false)) {
                            changed.add(c);
                        }
                    }
                }
            }
        }
        if (grayedOut) {
            List<IModelItem> items = new ArrayList<>();
            for (var cell : changed) {
                var item = YFilesCellStore.itemOf(cell);
                if (item != null) {
                    items.add(item);
                }
            }
            this.component.getGraphModelManager().toBack(items);
        }
        refresh(changed, false);
    }

    @Override
    public void repaint() {
        this.component.repaint();
    }

    // ---------- editing ----------

    /** Called by the store after it applied visual changes to cells. */
    void cellsModified(List<ViewCell<G>> cells) {
        this.component.updateContentRect();
        var change = new CellChange<>(List.of(), cells, List.of());
        for (var listener : this.canvasListeners) {
            listener.cellsChanged(this, change);
        }
        this.component.repaint();
    }

    /**
     * Records the positions of moved items as one visual edit: the centres of the moved
     * vertices and the points of their edges and of the moved edges, which the items
     * already show. The edit itself is left to {@link #applyMove}.
     */
    void recordMove(Iterable<IModelItem> items) {
        Map<ViewCell<G>,VisualMap> change = new LinkedHashMap<>();
        Set<IEdge> edges = new LinkedHashSet<>();
        IGraph graph = getShownGraph();
        for (IModelItem item : items) {
            if (item instanceof INode node) {
                var vertex = YFilesCellStore.cellOf(node);
                if (vertex instanceof ViewVertex<?>) {
                    VisualMap visuals = new VisualMap();
                    PointD centre = node.getLayout().getCenter();
                    visuals.setNodePos(new Point2D.Double(centre.getX(), centre.getY()));
                    change.put(cell(vertex), visuals);
                }
                for (IEdge edge : graph.edgesAt(node)) {
                    edges.add(edge);
                }
            } else if (item instanceof IEdge edge) {
                edges.add(edge);
            } else if (item instanceof IBend bend) {
                edges.add(bend.getOwner());
            }
        }
        for (IEdge edge : edges) {
            var cell = YFilesCellStore.cellOf(edge);
            if (cell instanceof ViewEdge<?> && cell.getItem() == edge) {
                VisualMap visuals = new VisualMap();
                visuals.setPoints(route(edge));
                change.put(cell(cell), visuals);
            }
        }
        applyMove(change, edges);
    }

    /**
     * Records the position of a dragged edge label as an edit of the label position of
     * its cell, in the relative convention of the layouts; the fan-out of a parallel
     * edge is taken out first, as the placement puts it back in.
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    void recordLabelMove(ILabel label) {
        if (!(label.getOwner() instanceof IEdge edge)
            || !(YFilesCellStore.cellOf(edge) instanceof ViewEdge<?> cell)) {
            return;
        }
        PointD centre = label.getLayout().getCenter();
        double x = centre.getX();
        double y = centre.getY();
        int rank = ParallelEdges.rank(cell);
        if (rank != 0) {
            PointD source = edge.getSourcePort().getLocation();
            PointD target = edge.getTargetPort().getLocation();
            Point2D shift = ParallelEdges
                .shift(rank, target.getX() - source.getX(), target.getY() - source.getY(),
                       Double.MAX_VALUE);
            x -= shift.getX();
            y -= shift.getY();
        }
        Point2D relative = EdgeGeometry
            .relativePosition(new Point2D.Double(x, y), YFilesCellStore.pathPoints(edge));
        VisualMap visuals = new VisualMap();
        visuals.setLabelPos(relative);
        Map<ViewCell<G>,VisualMap> change = new LinkedHashMap<>();
        change.put(cell(cell), visuals);
        edit(change);
    }

    /**
     * Applies the visual changes recorded for a finished move, as one edit. The editing
     * canvas extends this with the reconnection of an edge whose end was dragged.
     * @param change the visual changes of the moved cells
     * @param edges the edges affected by the move
     */
    protected void applyMove(Map<ViewCell<G>,VisualMap> change, Collection<IEdge> edges) {
        if (!change.isEmpty()) {
            edit(change);
        }
    }

    /**
     * Re-places the labels of the edges affected by a drag in progress: those at moved
     * nodes and those of moved edge points, against the geometry of the moment.
     */
    void followDrag(Iterable<IModelItem> items) {
        var model = getViewModel();
        if (model == null) {
            return;
        }
        Set<IEdge> edges = new LinkedHashSet<>();
        IGraph graph = getShownGraph();
        for (IModelItem item : items) {
            if (item instanceof INode node) {
                for (IEdge edge : graph.edgesAt(node)) {
                    edges.add(edge);
                }
            } else if (item instanceof IEdge edge) {
                edges.add(edge);
            } else if (item instanceof IBend bend) {
                edges.add(bend.getOwner());
            }
        }
        storeOf(model).refreshEdgeLabels(edges);
    }

    /** Returns the points of an edge item: the end node centres around the bends. */
    static List<Point2D> route(IEdge edge) {
        List<Point2D> result = new ArrayList<>();
        result.add(centre(edge.getSourceNode()));
        for (IBend bend : edge.getBends()) {
            IPoint location = bend.getLocation();
            result.add(new Point2D.Double(location.getX(), location.getY()));
        }
        result.add(centre(edge.getTargetNode()));
        return result;
    }

    /** Returns the centre of a node item. */
    private static Point2D centre(INode node) {
        PointD result = node.getLayout().getCenter();
        return new Point2D.Double(result.getX(), result.getY());
    }

    /** Casts a cell of this backend to a cell of the graph type of this canvas. */
    @SuppressWarnings("unchecked")
    ViewCell<G> cell(ViewCell<?> cell) {
        return (ViewCell<G>) cell;
    }

    /** Notifies the canvas listeners that the shown graph was rebuilt or reloaded. */
    protected void notifyGraphChanged() {
        for (var listener : this.canvasListeners) {
            listener.graphChanged(this);
        }
    }

    /** Called by the store after it removed cells. */
    void cellsRemoved(List<ViewCell<G>> cells) {
        this.component.updateContentRect();
        var change = new CellChange<>(List.of(), List.of(), cells);
        for (var listener : this.canvasListeners) {
            listener.cellsChanged(this, change);
        }
        this.component.repaint();
    }

    /* The viewer canvas has no in-place editor (phase 3). */
    @Override
    public void finishEditing() {
        // empty
    }

    /** Indicates if this canvas is editable. */
    public boolean isEditable() {
        return this.editable;
    }

    /* Recorded only: interactive editing is not implemented on this canvas yet. */
    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /** If {@code true}, this canvas is editable. */
    private boolean editable;

    /* Not implemented: in-place editing is part of the editor (phase 3). */
    @Override
    public void startEditing(ViewCell<G> cell) {
        // no in-place editor on this canvas
    }

    @Override
    public boolean hasActiveEditor() {
        return false;
    }

    /* Not implemented: the grid belongs to the editor (phase 3). */
    @Override
    public void setGridEnabled(boolean enabled) {
        // no grid on this canvas
    }

    // ---------- mode ----------

    @Override
    public GraphViewMode getMode() {
        var result = this.mode;
        if (result == null) {
            this.mode = result = getDefaultMode();
            configureMode(result);
        }
        return result;
    }

    @Override
    public boolean setMode(GraphViewMode mode) {
        GraphViewMode oldMode = this.mode;
        boolean result = mode != oldMode;
        if (result) {
            this.mode = mode;
            configureMode(mode);
            getController().getModeButton(mode).setSelected(true);
            this.component.setCursor(mode.getCursor());
            // fire change only if there was a previous value
            if (oldMode != null) {
                for (var listener : this.canvasListeners) {
                    listener.modeChanged(this, oldMode, mode);
                }
            }
        }
        return result;
    }

    /** Configures the input mode for a given view mode: a drag pans or selects. */
    protected void configureMode(GraphViewMode mode) {
        boolean panning = mode == GraphViewMode.PAN_MODE;
        var marquee = this.marquee;
        if (marquee != null) {
            marquee.setEnabled(!panning);
        }
        var pan = this.pan;
        if (pan != null) {
            pan.setEnabled(panning);
        }
        var move = this.move;
        if (move != null) {
            move.setEnabled(!panning);
        }
        var moveLabel = this.moveLabel;
        if (moveLabel != null) {
            moveLabel.setEnabled(!panning);
        }
    }

    /** Returns the view mode this canvas starts in. */
    public GraphViewMode getDefaultMode() {
        return GraphViewMode.SELECT_MODE;
    }

    /** The current view mode; {@code null} until first requested. */
    private @Nullable GraphViewMode mode;

    // ---------- layout ----------

    @Override
    public void setLayouting(boolean layouting) {
        this.layouting = layouting;
    }

    @Override
    public boolean isLayouting() {
        return this.layouting;
    }

    /** If {@code true}, a layout action is in progress. */
    private boolean layouting;

    /* The yFiles backend contributes the yFiles layout algorithms. */
    @Override
    public List<Layouter> getBackendLayouters() {
        return YFilesLayouter.getPrototypes();
    }

    /**
     * Returns the yFiles graph shown by the component: the visible cells' items,
     * on which the layouters work.
     */
    IGraph getShownGraph() {
        return this.component.getGraph();
    }

    @Override
    public Dimension2D getPreferredSize(ViewVertex<G> vertex) {
        return CellStyles.renderedSize(vertex);
    }

    // ---------- viewport ----------

    @Override
    public @Nullable Rectangle2D getGraphBounds() {
        if (getCells().isEmpty()) {
            return null;
        }
        this.component.updateContentRect();
        RectD result = this.component.getContentRect();
        return result.isEmpty()
            ? null
            : result.toRectangle2D();
    }

    @Override
    public double getScale() {
        return this.component.getZoom();
    }

    @Override
    public void setScale(double scale) {
        this.component.setZoom(scale);
    }

    @Override
    public void changeScale(int steps) {
        setScale(getScale() * Math.pow(ZOOM_FACTOR, steps));
    }

    /** The factor by which the zoom is adapted per step. */
    private static final double ZOOM_FACTOR = 1.4;

    /** Zooms and scrolls this canvas so that given bounds fill the view. */
    public void zoomTo(Rectangle2D bounds) {
        this.component.zoomTo(RectD.fromRectangle2D(bounds));
    }

    @Override
    public void scrollTo(Rectangle2D bounds) {
        this.component.makeVisible(RectD.fromRectangle2D(bounds));
    }

    @Override
    public void scrollTo(ViewCell<G> cell) {
        var item = YFilesCellStore.itemOf(cell);
        if (item != null) {
            scrollToItem(item);
            this.lastSelectedIndex = indexOf(cell);
        }
    }

    @Override
    public void scrollTo(Element element) {
        var cell = getNonNullViewModel().getCell(element);
        if (cell != null) {
            scrollTo(cell);
        }
    }

    /** Scrolls an item into view, if it is not there already. */
    private void scrollToItem(IModelItem item) {
        RectD bounds = boundsOf(item);
        if (bounds != null && !contains(this.component.getViewport(), bounds)) {
            this.component.makeVisible(bounds);
        }
    }

    /** Scrolls the bounds of the selection into view, if they are not there already. */
    private void scrollToSelection() {
        RectD bounds = null;
        for (IModelItem item : this.component.getSelection()) {
            RectD itemBounds = boundsOf(item);
            if (itemBounds != null) {
                bounds = bounds == null
                    ? itemBounds
                    : RectD.add(bounds, itemBounds);
            }
        }
        if (bounds != null && !contains(this.component.getViewport(), bounds)) {
            this.component.makeVisible(bounds);
        }
    }

    /** Returns the bounds of an item in world coordinates, if it is shown. */
    private @Nullable RectD boundsOf(IModelItem item) {
        ICanvasObject canvasObject = this.component.getGraphModelManager().getCanvasObject(item);
        return canvasObject == null
            ? null
            : this.component.getBounds(canvasObject);
    }

    /** Tests if a rectangle lies within another. */
    private static boolean contains(RectD outer, RectD inner) {
        return outer.contains(inner.getTopLeft()) && outer.contains(inner.getBottomRight());
    }

    @Override
    public void scrollToNextSelected() {
        List<? extends ViewCell<G>> cells = new ArrayList<>(getCells());
        int count = cells.size();
        if (count == 0) {
            return;
        }
        RectD viewport = this.component.getViewport();
        int last = Math.min(this.lastSelectedIndex, count - 1);
        int next = (last + 1) % count;
        while (next != last) {
            ViewCell<G> cell = cells.get(next);
            var item = YFilesCellStore.itemOf(cell);
            RectD bounds = item == null
                ? null
                : boundsOf(item);
            if (item != null && isSelected(cell) && bounds != null
                && !contains(viewport, bounds)) {
                this.component.makeVisible(bounds);
                this.lastSelectedIndex = next;
                break;
            }
            next = (next + 1) % count;
        }
    }

    /** Returns the index of a cell in the z-ordered cells, or {@code -1}. */
    private int indexOf(ViewCell<G> cell) {
        int result = 0;
        for (var c : getCells()) {
            if (c == cell) {
                return result;
            }
            result++;
        }
        return -1;
    }

    /** Index in the z-ordered cells of the last cell scrolled to. */
    private int lastSelectedIndex;

    // ---------- Swing ----------

    /** Returns the graph component of this canvas, for the editing subclass. */
    protected GraphComponent getGraphComponent() {
        return this.component;
    }

    /** Notifies the canvas listeners that in-place editing of a cell started. */
    protected void notifyEditingStarted(ViewCell<G> cell) {
        for (var listener : this.canvasListeners) {
            listener.editingStarted(this, cell);
        }
    }

    @Override
    public JComponent getComponent() {
        return this.component;
    }

    @Override
    public @Nullable Point2D getPointerLocation() {
        if (GraphicsEnvironment.isHeadless()) {
            return null;
        }
        Point position = this.component.getMousePosition();
        if (position == null) {
            return null;
        }
        PointD world = this.component.toWorldCoordinates(new PointD(position.x, position.y));
        return new Point2D.Double(world.getX(), world.getY());
    }

    /** The yFiles component showing the graph. */
    private final Viewer component;

    @Override
    public boolean hasOwnScrolling() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.component.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            if (!enabled) {
                this.enabledBackground = this.component.getBackground();
                setBackground(null);
            } else if (this.enabledBackground != null) {
                setBackground(this.enabledBackground);
            }
            getController().setLabelTreeEnabled(enabled);
            getController().setModeButtonsEnabled(enabled);
            getController().getModeButton(getDefaultMode()).setSelected(true);
            // retrieve the layout action to get its key accelerator working
            getController().getLayoutAction();
            this.component.setEnabled(enabled);
            this.inputMode.setEnabled(enabled);
        }
    }

    /** The background colour of the component when it is enabled. */
    private @Nullable Color enabledBackground;

    /* A null colour stands for the panel background of the look-and-feel. */
    @Override
    public void setBackground(@Nullable Color background) {
        Color color = background;
        if (color == null) {
            color = UIManager.getColor("Panel.background");
        }
        if (color == null) {
            color = Color.LIGHT_GRAY;
        }
        this.component.setBackground(color);
        var model = this.model;
        if (model != null) {
            // the edge labels are opaque, in the background colour
            storeOf(model).refreshEdgeLabels();
        }
        this.component.repaint();
    }

    @Override
    public void setOverlay(Overlay overlay) {
        if (overlay != this.overlay) {
            this.overlay = overlay;
            this.component.repaint();
        }
    }

    /** The overlay drawn over the content. */
    private Overlay overlay = Overlay.NONE;

    @Override
    public void addAccelerator(Action action) {
        Object actionName = action.getValue(Action.NAME);
        KeyStroke actionKey = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
        if (actionName != null && actionKey != null) {
            ActionMap am = this.component.getActionMap();
            am.put(actionName, action);
            InputMap im = this.component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            im.put(actionKey, actionName);
            im = this.component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            im.put(actionKey, actionName);
        }
    }

    @Override
    public void setToolTipEnabled(boolean enabled) {
        getController().setToolTipEnabled(enabled);
    }

    @Override
    public @Nullable OptionRefreshListener getRefreshListener(String option) {
        var result = this.refreshListener;
        if (result == null) {
            this.refreshListener = result = new OptionRefreshListener(this);
        }
        return result;
    }

    /** Listener that refreshes the cells when an option changes. */
    private @Nullable OptionRefreshListener refreshListener;

    /**
     * Populates the popup menu of the input mode with the items of the controller's popup
     * menu at the queried location, and requests it to be shown. The cell at the location
     * is selected first if it was not, as in the JGraph backend.
     */
    private void populatePopupMenu(PopulateMenuEventArgs args) {
        if (!getController().isInteractive() || !(args.getMenu() instanceof JPopupMenu menu)) {
            args.setShowingMenuRequested(false);
            return;
        }
        PointD world = args.getQueryLocation();
        PointD view = this.component.toViewCoordinates(world);
        var cell = getCellAt(view.getX(), view.getY());
        if (cell != null && !isSelected(cell)) {
            select(List.of(cell));
        }
        Point2D atPoint = new Point2D.Double(world.getX(), world.getY());
        JPopupMenu groovePopup = getController().createPopupMenu(atPoint).getPopupMenu();
        for (Component item : groovePopup.getComponents()) {
            menu.add(item);
        }
        args.setShowingMenuRequested(true);
    }

    // ---------- listeners ----------

    @Override
    public void addCanvasListener(GraphCanvasListener<G> listener) {
        this.canvasListeners.add(listener);
    }

    @Override
    public void removeCanvasListener(GraphCanvasListener<G> listener) {
        this.canvasListeners.remove(listener);
    }

    /** The registered canvas listeners. */
    private final List<GraphCanvasListener<G>> canvasListeners = new ArrayList<>();

    // ---------- export ----------

    @Override
    public @Nullable BufferedImage toImage() {
        Rectangle2D bounds = getGraphBounds();
        if (bounds == null) {
            return null;
        }
        var configurator = new ContextConfigurator(RectD.fromRectangle2D(bounds));
        configurator.setMargins(new InsetsD(IMAGE_MARGIN));
        var exporter = new PixelImageExporter(configurator);
        exporter.setBackgroundFill(this.component.getBackground());
        return withoutEmphasis(() -> exporter.exportToBitmap(this.component));
    }

    /** Extra space around the graph in exported images. */
    private static final int IMAGE_MARGIN = 5;

    @Override
    public void paintGraph(Graphics2D graphics) {
        Rectangle2D bounds = getGraphBounds();
        if (bounds == null) {
            return;
        }
        var configurator = new ContextConfigurator(RectD.fromRectangle2D(bounds), 1);
        IRenderContext context = configurator.createRenderContext(this.component);
        Graphics2D g2 = (Graphics2D) graphics.create();
        try {
            g2.transform(context.getToWorldTransform());
            RectD clip = context.getClip();
            if (clip != null) {
                g2.clip(clip.toRectangle2D());
            }
            withoutEmphasis(() -> {
                this.component.exportContent(context).paint(context, g2);
                return null;
            });
        } finally {
            g2.dispose();
        }
    }

    /**
     * Performs a rendering task with the emphasis of the selected cells switched off,
     * so that the selection does not show in exports.
     */
    private <T> T withoutEmphasis(java.util.function.Supplier<T> task) {
        List<ViewCell<G>> selected = getSelection();
        for (var cell : selected) {
            cell.putVisual(VisualKey.EMPHASIS, false);
        }
        try {
            return task.get();
        } finally {
            for (var cell : selected) {
                cell.putVisual(VisualKey.EMPHASIS, true);
            }
        }
    }

    /**
     * The graph component of the canvas: paints the overlay, and answers tool tip
     * queries from the cells.
     */
    private final class Viewer extends GraphComponent {
        /**
         * Registers listeners for mouse input, without which Swing's lightweight
         * dispatcher does not deliver mouse events to this component at all: the
         * yFiles input surface is a child that is not always the deepest component
         * at a point (the editor's render pane may lie above it), and the events then
         * go to the nearest ancestor that listens, which must be this component.
         * The viewer displays happened to register listeners of their own; the
         * editor tab does not.
         */
        Viewer() {
            addMouseListener(new MouseAdapter() {
                // empty
            });
            addMouseMotionListener(new MouseAdapter() {
                // empty
            });
        }

        /*
         * The component lays out its own children (the in-place editor among them):
         * their changes do not invalidate the panels around the canvas.
         */
        @Override
        public boolean isValidateRoot() {
            return true;
        }

        @Override
        @NonNullByDefault({})
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (YFilesCanvas.this.overlay == Overlay.HATCHED) {
                Values.paintHatch(this, g);
            }
        }

        @Override
        @NonNullByDefault({})
        public @Nullable String getToolTipText(MouseEvent evt) {
            var cell = getCellAt(evt.getX(), evt.getY());
            return cell != null && cell.getVisuals().isVisible()
                ? cell.getToolTipText()
                : null;
        }
    }
}
