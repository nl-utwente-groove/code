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

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.HtmlLabelStyle;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.layout.ElementLayout;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.CellStore;
import nl.utwente.groove.gui.view.EdgeGeometry;
import nl.utwente.groove.gui.view.LoopRouter;
import nl.utwente.groove.gui.view.ParallelEdges;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.gui.view.cell.AViewCell;
import nl.utwente.groove.gui.view.cell.AViewEdge;
import nl.utwente.groove.gui.view.cell.AViewVertex;
import nl.utwente.groove.util.AIGenerated;

/**
 * The yFiles backend's store of the cells of a view model: a yFiles graph holding a
 * node or edge for every cell, with the cell as the item's tag and the item as the
 * cell's backend item, and a filtered view of that graph through which the canvas shows
 * only the cells whose visuals say they are visible.
 * <p>
 * The store translates the cells' visuals into yFiles geometry: the node layout is the
 * text-fitted visible size centred on the cell's position; the bends of an edge are the
 * interior points of the cell, with the default loop shape of {@link LoopRouter} for a
 * loop without bends of its own; the labels are HTML labels placed by the cells' label
 * positions. The styles read everything else from the cells at painting time, so a
 * changed cell only needs {@link #updateItem} for its geometry and text.
 * @param <G> the type of graph displayed
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
final class YFilesCellStore<G extends Graph> implements CellStore<G> {
    /** Creates an empty store. */
    YFilesCellStore() {
        this.master = new DefaultGraph();
        this.view = new FilteredGraphWrapper(this.master, this::isVisible, this::isVisible);
    }

    /** The graph holding an item for every cell. */
    private final DefaultGraph master;
    /** The view of the master graph holding only the visible cells. */
    private final FilteredGraphWrapper view;

    /** Returns the graph through which the canvas shows the store: the visible cells only. */
    IGraph getView() {
        return this.view;
    }

    /** Returns the graph holding all cells. */
    IGraph getMaster() {
        return this.master;
    }

    /**
     * Tests if the cell of a given item is visible; items without cell are not.
     * While the store is detached, all cells count as visible: the visuals are not
     * evaluated until the model is shown (see {@link #attach}).
     */
    private boolean isVisible(IModelItem item) {
        var cell = cellOf(item);
        return cell != null && (this.canvas == null || cell.getVisuals().isVisible());
    }

    /** Releases the filtered view; call when the store is discarded. */
    void dispose() {
        this.view.dispose();
    }

    /**
     * Records the canvas showing this store's model, or {@code null} if the model is
     * not shown; the store notifies it of insertions.
     */
    void attach(@Nullable YFilesCanvas<G> canvas) {
        this.canvas = canvas;
        if (canvas != null) {
            configureItems();
        }
    }

    /** Indicates if the store is attached to a canvas, so that its items are configured. */
    private boolean isAttached() {
        return this.canvas != null;
    }

    /**
     * Configures all items from their cells' visuals, and evaluates the visibility.
     * Called when the store gets attached to a canvas: the visuals may depend on the
     * canvas' surroundings (its label tree, for one), which only know the cells of a
     * model once it is shown, so a detached store leaves its items unconfigured, as the
     * JGraph backend evaluates nothing before it paints.
     */
    @SuppressWarnings("unchecked")
    private void configureItems() {
        List<ViewCell<G>> cells = new ArrayList<>();
        for (INode node : this.master.getNodes()) {
            var cell = cellOf(node);
            if (cell != null) {
                cells.add((ViewCell<G>) cell);
            }
        }
        for (IEdge edge : this.master.getEdges()) {
            var cell = cellOf(edge);
            if (cell != null) {
                cells.add((ViewCell<G>) cell);
            }
        }
        for (var cell : cells) {
            cell.setStale(VisualKey.VISIBLE);
        }
        for (var cell : cells) {
            updateItem(cell);
        }
        refreshVisibility();
    }

    /** The canvas showing this store's model, if any. */
    private @Nullable YFilesCanvas<G> canvas;

    /**
     * Returns the cell shown by a given item, if any: the tag of a node or edge, or
     * the cell of the owner of a label.
     */
    static @Nullable AViewCell<?> cellOf(@Nullable IModelItem item) {
        if (item instanceof ILabel label) {
            return cellOf(label.getOwner());
        }
        return item != null && item.getTag() instanceof AViewCell<?> cell
            ? cell
            : null;
    }

    /** Returns the item showing a given cell, if the cell is shown by this backend. */
    static @Nullable IModelItem itemOf(ViewCell<?> cell) {
        return ((AViewCell<?>) cell).getItem() instanceof IModelItem item
            ? item
            : null;
    }

    // ---------- the cell store ----------

    @Override
    public void insertCells(List<? extends ViewVertex<G>> vertices,
                            List<? extends ViewEdge<G>> edges, List<Connection<G>> connections,
                            boolean replace) {
        if (replace) {
            for (INode node : this.master.getNodes()) {
                unlink(node);
            }
            for (IEdge edge : this.master.getEdges()) {
                unlink(edge);
            }
            this.master.clear();
        }
        // connect the cells before creating any item: the filtered view evaluates
        // the visibility of an item at creation, and visibility derives from the structure
        for (Connection<G> connection : connections) {
            var cell = (AViewEdge<G>) connection.edge();
            cell.setSource((AViewVertex<G>) connection.source());
            cell.setTarget((AViewVertex<G>) connection.target());
        }
        List<ViewCell<G>> inserted = new ArrayList<>(vertices.size() + edges.size());
        for (ViewVertex<G> vertex : vertices) {
            createNode((AViewVertex<G>) vertex);
            inserted.add(vertex);
        }
        for (Connection<G> connection : connections) {
            createEdge(connection);
            inserted.add(connection.edge());
        }
        assert inserted.size() == vertices.size() + edges.size() : "Edges without connection";
        var canvas = this.canvas;
        if (canvas != null) {
            canvas.cellsInserted(inserted);
        }
    }

    /** Detaches the cell of an item about to be removed from its item. */
    /*
     * Removes the items of the cells and disconnects the edge cells, so that
     * they can be inserted (and connected) again by an undo.
     */
    @Override
    public void removeCells(Collection<? extends ViewCell<G>> cells) {
        List<ViewCell<G>> removed = new ArrayList<>();
        List<INode> endNodes = new ArrayList<>();
        for (var cell : cells) {
            var item = itemOf(cell);
            if (item == null) {
                continue;
            }
            if (item instanceof IEdge edge) {
                endNodes.add(edge.getSourceNode());
                endNodes.add(edge.getTargetNode());
            }
            unlink(item);
            this.master.remove(item);
            if (cell instanceof AViewEdge<G> edge) {
                edge.setSource(null);
                edge.setTarget(null);
            }
            removed.add(cell);
        }
        updateSiblingLabels(null, endNodes.toArray(new INode[0]));
        var canvas = this.canvas;
        if (canvas != null) {
            canvas.cellsRemoved(removed);
        }
    }

    /*
     * The edge gets a new item between the new end nodes, and its cell the new ends;
     * the new item is selected if the old one was, as the user sees one edge.
     */
    @Override
    public void reconnectEdge(ViewEdge<G> edge, ViewVertex<G> source, ViewVertex<G> target) {
        var cell = (AViewEdge<G>) edge;
        var canvas = this.canvas;
        boolean selected = canvas != null && canvas.isSelected(edge);
        if (canvas != null) {
            canvas.beginSelectionBatch();
        }
        try {
            var oldItem = itemOf(cell);
            if (oldItem instanceof IEdge oldEdge) {
                INode oldSource = oldEdge.getSourceNode();
                INode oldTarget = oldEdge.getTargetNode();
                unlink(oldEdge);
                this.master.remove(oldEdge);
                updateSiblingLabels(null, oldSource, oldTarget);
            }
            cell.setSource((AViewVertex<G>) source);
            cell.setTarget((AViewVertex<G>) target);
            createEdge(new Connection<>(edge, source, target));
            if (canvas != null && selected) {
                canvas.setSelected(edge, true);
            }
        } finally {
            if (canvas != null) {
                canvas.endSelectionBatch();
            }
        }
        if (canvas != null) {
            canvas.cellsModified(List.of(edge));
        }
    }

    /*
     * The cells take their visuals and their items follow: the vertices first,
     * so that the edges (whose label placement depends on the end nodes' positions)
     * are updated against the final positions, not a transient in which two end
     * nodes may coincide.
     */
    @Override
    public void applyVisuals(Map<? extends ViewCell<G>,VisualMap> changes) {
        List<ViewCell<G>> modified = new ArrayList<>();
        for (var entry : changes.entrySet()) {
            entry.getKey().putVisuals(entry.getValue());
        }
        for (var cell : changes.keySet()) {
            if (cell instanceof ViewVertex) {
                updateItem(cell);
                modified.add(cell);
            }
        }
        for (var entry : changes.entrySet()) {
            var cell = entry.getKey();
            if (!(cell instanceof ViewVertex)) {
                updateItem(cell);
                modified.add(cell);
                if (entry.getValue().containsKey(VisualKey.POINTS)
                    && itemOf(cell) instanceof IEdge edge) {
                    // routed or unrouted now: the fan-out of its parallels changes
                    updateSiblingLabels(edge, edge.getSourceNode(), edge.getTargetNode());
                }
            }
        }
        var canvas = this.canvas;
        if (canvas != null) {
            canvas.cellsModified(modified);
        }
    }

    /**
     * Clears the back-reference to a removed item from its cell, unless the cell has
     * meanwhile been linked to another item.
     */
    private static void unlink(IModelItem item) {
        var cell = cellOf(item);
        if (cell != null && cell.getItem() == item) {
            cell.setItem(null);
        }
    }

    /* The z-order of the JGraph backend: the edges first, so they are at the back. */
    @Override
    public Collection<? extends ViewCell<G>> getCells() {
        List<ViewCell<G>> result = new ArrayList<>();
        for (IEdge edge : this.master.getEdges()) {
            addCell(result, edge);
        }
        for (INode node : this.master.getNodes()) {
            addCell(result, node);
        }
        return result;
    }

    /** Adds the cell of a given item to a list, if the item has a cell. */
    @SuppressWarnings("unchecked")
    private void addCell(List<ViewCell<G>> cells, IModelItem item) {
        var cell = cellOf(item);
        if (cell != null) {
            cells.add((ViewCell<G>) cell);
        }
    }

    // ---------- nodes ----------

    /** Creates the node showing a given vertex cell. */
    private void createNode(AViewVertex<G> vertex) {
        boolean attached = isAttached();
        INode node = this.master
            .createNode(attached
                ? layout(vertex)
                : UNCONFIGURED_LAYOUT, CellStyles.NODE_STYLE, vertex);
        vertex.setItem(node);
        if (attached) {
            updateNodeLabel(node, vertex);
        }
    }

    /** Layout of a node whose store is detached; replaced when the store gets attached. */
    private static final RectD UNCONFIGURED_LAYOUT = new RectD(0, 0, 1, 1);

    /** Computes the layout of a vertex: its visible size, centred on its position. */
    private static RectD layout(ViewVertex<?> vertex) {
        Dimension2D size = CellStyles.nodeSize(vertex);
        Point2D pos = vertex.getVisuals().getNodePos();
        return RectD
            .fromCenter(new PointD(pos.getX(), pos.getY()),
                        new SizeD(size.getWidth(), size.getHeight()));
    }

    /**
     * Brings the item of a cell up to date with the cell's visuals: the layout and
     * label of a node, the bends and labels of an edge.
     */
    void updateItem(ViewCell<G> cell) {
        var item = itemOf(cell);
        if (item instanceof INode node) {
            updateNode(node, (AViewVertex<G>) cell);
        } else if (item instanceof IEdge edge) {
            updateEdge(edge, (AViewEdge<G>) cell);
        }
    }

    /** Refreshes the visibility of all cells in the view. */
    void refreshVisibility() {
        this.view.nodePredicateChanged();
        this.view.edgePredicateChanged();
    }

    /** Brings a node in line with its vertex cell: its layout, if that changed, and its label. */
    private void updateNode(INode node, AViewVertex<G> vertex) {
        RectD layout = layout(vertex);
        if (!layout.equals(node.getLayout().toRectD())) {
            this.master.setNodeLayout(node, layout);
        }
        updateNodeLabel(node, vertex);
    }

    /** Creates, updates or removes the label of a node, according to the vertex text. */
    private void updateNodeLabel(INode node, AViewVertex<G> vertex) {
        String html = CellStyles.nodeHtml(vertex);
        updateLabel(node, new Placement(LabelKind.MAIN, 0, 0), html, InteriorLabelModel.CENTER,
                    CellStyles.nodeLabelStyle(vertex.getVisuals().getFont()));
    }

    /** The labels of an item, identified by their placement. */
    private enum LabelKind {
        /** The main label. */
        MAIN,
        /** The extra label at the source end of an edge. */
        SOURCE,
        /** The extra label at the target end of an edge. */
        TARGET;
    }

    /**
     * Placement of a label, kept as the label's tag: which label it is, and (for edge
     * labels) the position along the path and the distance from it that its layout
     * parameter encodes. The yFiles parameters are not comparable, so the tag tells
     * whether the parameter of an existing label is still the wanted one.
     */
    private record Placement(LabelKind kind, double ratio, double distance) {
        // no members
    }

    /**
     * Creates, updates or removes a label of an item.
     * @param html the label text; if empty, the label is removed
     * @return the label, if any
     */
    private @Nullable ILabel updateLabel(ILabelOwner owner, Placement placement, String html,
                                         ILabelModelParameter parameter, HtmlLabelStyle style) {
        ILabel existing = findLabel(owner, placement.kind());
        if (html.isEmpty()) {
            if (existing != null) {
                this.master.remove(existing);
            }
            return null;
        }
        if (existing == null) {
            return this.master.addLabel(owner, html, parameter, style, null, placement);
        }
        if (!html.equals(existing.getText())) {
            this.master.setLabelText(existing, html);
        }
        if (existing.getStyle() != style) {
            this.master.setStyle(existing, style);
        }
        if (!placement.equals(existing.getTag())) {
            this.master.setLabelLayoutParameter(existing, parameter);
            existing.setTag(placement);
        }
        return existing;
    }

    /** Finds the label of an item of a given kind, if any. */
    private static @Nullable ILabel findLabel(ILabelOwner owner, LabelKind kind) {
        for (ILabel label : owner.getLabels()) {
            if (label.getTag() instanceof Placement placement && placement.kind() == kind) {
                return label;
            }
        }
        return null;
    }

    // ---------- edges ----------

    /** Creates the edge showing a given, already connected edge cell. */
    private void createEdge(Connection<G> connection) {
        var cell = (AViewEdge<G>) connection.edge();
        assert cell.getSourceVertex() == connection.source()
            && cell.getTargetVertex() == connection.target() : "Edge cell not connected";
        INode sourceNode = (INode) itemOf(connection.source());
        INode targetNode = (INode) itemOf(connection.target());
        assert sourceNode != null && targetNode != null : "End vertices not shown yet";
        IEdge edge = this.master.createEdge(sourceNode, targetNode, CellStyles.EDGE_STYLE, cell);
        cell.setItem(edge);
        if (isAttached()) {
            updateEdge(edge, cell);
            updateSiblingLabels(edge, sourceNode, targetNode);
        }
    }

    /**
     * Re-places the labels of the edges at given nodes other than a given edge: the
     * labels of unrouted parallel edges follow the fan-out of their edges
     * ({@link ParallelEdges}), which changes when an edge between the nodes comes, goes,
     * or gets points of its own.
     */
    @SuppressWarnings("unchecked")
    private void updateSiblingLabels(@Nullable IEdge edge, INode... nodes) {
        if (!isAttached()) {
            return;
        }
        for (INode node : nodes) {
            if (!this.master.contains(node)) {
                continue;
            }
            for (IEdge sibling : this.master.edgesAt(node)) {
                if (sibling != edge && cellOf(sibling) instanceof AViewEdge<?> cell) {
                    updateEdgeLabels(sibling, (AViewEdge<G>) cell);
                }
            }
        }
    }

    /**
     * Brings an edge in line with its cell: its bends and its labels. A loop with fewer than
     * three points first gets the default loop shape of {@link LoopRouter}, which is stored
     * on the cell and so becomes ordinary bends.
     */
    private void updateEdge(IEdge edge, AViewEdge<G> cell) {
        VisualMap visuals = cell.getVisuals();
        List<Point2D> points = visuals.getPoints();
        if (cell.isLoop() && points.size() < 3) {
            // give the loop its default shape, stored as ordinary bends
            INode node = edge.getSourceNode();
            Rectangle2D bounds = node.getLayout().toRectD().toRectangle2D();
            points = LoopRouter.route(cell, bounds);
            cell.putVisual(VisualKey.POINTS, points);
        }
        updateBends(edge, points);
        updateEdgeLabels(edge, cell);
    }

    /** Sets the bends of an edge to the interior points of its cell, if they differ. */
    private void updateBends(IEdge edge, List<Point2D> points) {
        int bendCount = Math.max(0, points.size() - 2);
        var bends = edge.getBends();
        boolean same = bends.size() == bendCount;
        for (int i = 0; same && i < bendCount; i++) {
            IBend bend = bends.getItem(i);
            Point2D point = points.get(i + 1);
            same = bend.getLocation().getX() == point.getX()
                && bend.getLocation().getY() == point.getY();
        }
        if (!same) {
            this.master.clearBends(edge);
            for (int i = 0; i < bendCount; i++) {
                Point2D point = points.get(i + 1);
                this.master.addBend(edge, new PointD(point.getX(), point.getY()));
            }
        }
    }

    /** Creates, updates or removes the main and end labels of an edge. */
    private void updateEdgeLabels(IEdge edge, AViewEdge<G> cell) {
        VisualMap visuals = cell.getVisuals();
        HtmlLabelStyle style = CellStyles.edgeLabelStyle(visuals.getFont(), labelBackground());
        List<Point2D> points = pathPoints(edge);
        Point2D start = null;
        Point2D end = null;
        if (cell.getViewModel().getController().isShowArrowsOnLabels()) {
            start = centre(edge.getSourceNode());
            end = centre(edge.getTargetNode());
        }
        String html = CellStyles.labelHtml(visuals.getLabel(), visuals.getForeground(), start, end);
        Point2D labelPos = visuals.getLabelPos();
        int rank = ParallelEdges.rank(cell);
        updateEdgeLabel(edge, LabelKind.MAIN, html, labelPos, points, rank, style);
        String sourceLabel = visuals.getEdgeSourceLabel();
        updateEdgeLabel(edge, LabelKind.SOURCE, sourceLabel == null
            ? ""
            : sourceLabel, IN_MULT_POS, points, rank, style);
        String targetLabel = visuals.getEdgeTargetLabel();
        updateEdgeLabel(edge, LabelKind.TARGET, targetLabel == null
            ? ""
            : targetLabel, OUT_MULT_POS, points, rank, style);
    }

    /** Returns the background colour of edge labels: that of the canvas, or white. */
    private Color labelBackground() {
        var canvas = this.canvas;
        Color result = canvas == null
            ? null
            : canvas.getComponent().getBackground();
        return result == null
            ? Color.WHITE
            : result;
    }

    /** Re-styles the labels of all edges; to be called when the canvas background changes. */
    @SuppressWarnings("unchecked")
    void refreshEdgeLabels() {
        if (!isAttached()) {
            return;
        }
        for (IEdge edge : this.master.getEdges()) {
            var cell = cellOf(edge);
            if (cell != null) {
                updateEdgeLabels(edge, (AViewEdge<G>) cell);
            }
        }
    }

    /** Re-places the labels of given edges against their current geometry. */
    @SuppressWarnings("unchecked")
    void refreshEdgeLabels(Iterable<IEdge> edges) {
        if (!isAttached()) {
            return;
        }
        for (IEdge edge : edges) {
            if (cellOf(edge) instanceof AViewEdge<?> cell) {
                updateEdgeLabels(edge, (AViewEdge<G>) cell);
            }
        }
    }

    /** Tests if a label is the main label of an edge, the one the user may move. */
    static boolean isMainEdgeLabel(ILabel label) {
        return label.getOwner() instanceof IEdge
            && label.getTag() instanceof Placement placement && placement.kind() == LabelKind.MAIN;
    }

    /** Returns the points of the path of an edge: the port locations and the bends. */
    static List<Point2D> pathPoints(IEdge edge) {
        List<Point2D> result = new ArrayList<>();
        for (var point : edge.getPathPoints()) {
            result.add(new Point2D.Double(point.getX(), point.getY()));
        }
        return result;
    }

    /** Returns the centre of a node. */
    private static Point2D centre(INode node) {
        PointD result = node.getLayout().getCenter();
        return new Point2D.Double(result.getX(), result.getY());
    }

    /**
     * Creates, updates or removes a label of an edge at a relative position.
     * The position is interpreted as the JGraph backend does, so that stored layouts
     * look the same: a distance along the polyline through the points, in permille of
     * its length, and a perpendicular offset; the label is centred at the resulting
     * point. That point is expressed relative to the line between the ports, so that
     * the label follows its edge; the label of a loop, whose ports coincide, is placed
     * at the absolute point instead, and re-placed with every change of the loop's
     * geometry. The label of an unrouted edge with parallels is shifted with its edge
     * (see {@link ParallelEdges}).
     * @param edge the edge
     * @param kind which label of the edge
     * @param html the label text; if empty, the label is removed
     * @param relative the relative position
     * @param points the points of the edge's path
     * @param rank the parallel rank of the edge
     * @param style the label style
     */
    private void updateEdgeLabel(IEdge edge, LabelKind kind, String html, Point2D relative,
                                 List<Point2D> points, int rank, HtmlLabelStyle style) {
        PointD source = edge.getSourcePort().getLocation();
        PointD target = edge.getTargetPort().getLocation();
        double dx = target.getX() - source.getX();
        double dy = target.getY() - source.getY();
        double length = Math.hypot(dx, dy);
        Point2D at = EdgeGeometry.labelPosition(relative, points);
        Placement placement;
        ILabelModelParameter parameter;
        if (length < MIN_LENGTH) {
            // the anchor of a free label is its lower left corner
            SizeD size = CellStyles.measure(html, style);
            double x = at.getX() - size.getWidth() / 2;
            double y = at.getY() + size.getHeight() / 2;
            placement = new Placement(kind, x, y);
            parameter = FreeLabelModel.INSTANCE.createAbsolute(new PointD(x, y));
        } else {
            placement = new Placement(kind, at.getX(), at.getY());
            // decompose the offset from the source port along and across the port line
            double ox = at.getX() - source.getX();
            double oy = at.getY() - source.getY();
            double ratio = (ox * dx + oy * dy) / (length * length);
            double distance = (ox * -dy + oy * dx) / length;
            if (rank != 0) {
                Point2D shift = ParallelEdges.shift(rank, dx, dy, Double.MAX_VALUE);
                distance += (shift.getX() * -dy + shift.getY() * dx) / length;
            }
            parameter = FreeEdgeLabelModel.INSTANCE.createEdgeAnchored(ratio, distance, 0);
        }
        updateLabel(edge, placement, html, parameter, style);
    }

    /**
     * Length below which the ports of an edge are considered to coincide: the
     * anchored placement divides by the length, which must stay well away from
     * zero, as happens transiently when a vertex is moved onto another.
     */
    private static final double MIN_LENGTH = 1;

    /** Relative position of the source multiplicity label. */
    private static final Point2D IN_MULT_POS = new Point2D.Double(ElementLayout.PERMILLE * 90 / 100, -11);
    /** Relative position of the target multiplicity label. */
    private static final Point2D OUT_MULT_POS = new Point2D.Double(ElementLayout.PERMILLE * 10 / 100, -11);
}
