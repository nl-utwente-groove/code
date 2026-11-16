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

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.partial.ComponentAssignmentStrategy;
import com.yworks.yfiles.layout.partial.PartialLayout;
import com.yworks.yfiles.layout.partial.PartialLayoutData;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.util.AIGenerated;

/**
 * Layouter running one of the yFiles layout algorithms on the graph shown by a
 * yFiles canvas; the yFiles backend's contribution to the layout palette.
 * <p>
 * The algorithm is applied to the shown yFiles graph, whose nodes already have
 * the rendered sizes, and its result is read back into the cells through the
 * canvas' edit funnel, so that the view model's layout map follows and the
 * items merely find themselves up to date. Vertices that are not layoutable
 * (or grayed out) keep their place: with any of those present the algorithm
 * runs as the core of a partial layout, which places the layoutable vertices
 * among the fixed remainder. Hence the layouter is its own incremental
 * version. Edge labels are not placed by the layouts; they keep their
 * position relative to the edge.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class YFilesLayouter implements Layouter {
    /** Constructs a prototype layouter of a given kind. */
    private YFilesLayouter(YFilesLayoutKind kind) {
        this(kind, null);
    }

    /** Constructs a layouter of a given kind for a given canvas; a prototype if the canvas is {@code null}. */
    private YFilesLayouter(YFilesLayoutKind kind, @Nullable YFilesCanvas<?> canvas) {
        this.kind = kind;
        this.canvas = canvas;
        if (canvas == null) {
            this.algorithm = null;
            this.panel = null;
        } else {
            var algorithm = kind.newAlgorithm();
            this.algorithm = algorithm;
            var panel = new LayoutSettingsPanel(this::start);
            kind.addSettings(panel, algorithm);
            this.panel = panel;
        }
    }

    /** The kind of layout this layouter performs. */
    private final YFilesLayoutKind kind;
    /** The canvas laid out; {@code null} for a prototype. */
    private final @Nullable YFilesCanvas<?> canvas;
    /** The configured algorithm; {@code null} for a prototype. */
    private final @Nullable ILayoutAlgorithm algorithm;
    /** The settings panel; {@code null} for a prototype. */
    private final @Nullable LayoutSettingsPanel panel;

    @Override
    public Layouter newInstance(GraphCanvas<?> canvas) {
        return new YFilesLayouter(this.kind, (YFilesCanvas<?>) canvas);
    }

    @Override
    public String getName() {
        return this.kind.getDisplayName();
    }

    @Override
    public Layouter getIncremental() {
        return this;
    }

    @Override
    public @Nullable JComponent getSettingsPanel() {
        return this.panel;
    }

    /**
     * Lays out the layoutable vertices of the canvas, on the event thread:
     * the shown graph is a live yFiles graph.
     */
    @Override
    public void start() {
        if (SwingUtilities.isEventDispatchThread()) {
            var canvas = this.canvas;
            var algorithm = this.algorithm;
            assert canvas != null && algorithm != null; // only canvas-bound instances are started
            run(canvas, algorithm);
        } else {
            try {
                SwingUtilities.invokeAndWait(this::start);
            } catch (InterruptedException exc) {
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException exc) {
                throw new IllegalStateException("Layout failed", exc.getCause());
            }
        }
    }

    /** Runs the algorithm on the shown graph of a canvas and commits the result. */
    private static <G extends Graph> void run(YFilesCanvas<G> canvas, ILayoutAlgorithm algorithm) {
        IGraph graph = canvas.getShownGraph();
        // the nodes to place are those of the layoutable vertices that are not grayed out
        Set<INode> affected = new HashSet<>();
        int fixedCount = 0;
        for (INode node : graph.getNodes()) {
            ViewVertex<G> vertex = vertexOf(node);
            if (vertex.isLayoutable() && !vertex.isGrayedOut()) {
                affected.add(node);
            } else {
                fixedCount++;
            }
        }
        if (affected.isEmpty()) {
            return;
        }
        canvas.setLayouting(true);
        try {
            // edges at placed nodes are routed anew; drop their old routes,
            // which a layout may otherwise leave in place
            List<IEdge> routed = new ArrayList<>();
            for (IEdge edge : graph.getEdges()) {
                if (affected.contains(edge.getSourceNode())
                    || affected.contains(edge.getTargetNode())) {
                    graph.clearBends(edge);
                    routed.add(edge);
                }
            }
            if (fixedCount == 0) {
                graph.applyLayout(algorithm);
            } else {
                graph.applyLayout(partial(algorithm), partialData(affected));
            }
            // read the result back into the cells
            Map<ViewCell<G>,VisualMap> change = new HashMap<>();
            for (INode node : affected) {
                ViewVertex<G> vertex = vertexOf(node);
                VisualMap visuals = new VisualMap();
                visuals.setNodePos(centre(node));
                vertex.setLayoutable(false);
                change.put(vertex, visuals);
            }
            for (IEdge edge : routed) {
                VisualMap visuals = new VisualMap();
                visuals.setPoints(route(edge));
                change.put(edgeOf(edge), visuals);
            }
            canvas.edit(change);
        } finally {
            canvas.setLayouting(false);
        }
    }

    /**
     * Wraps an algorithm as the core of a partial layout, which lays out the
     * connected groups of affected nodes and places them among the fixed nodes,
     * close to their neighbours.
     */
    private static PartialLayout partial(ILayoutAlgorithm core) {
        var result = new PartialLayout(core);
        result.setComponentAssignmentStrategy(ComponentAssignmentStrategy.CONNECTED);
        return result;
    }

    /** Creates the layout data marking the affected nodes of a partial layout. */
    private static PartialLayoutData partialData(Set<INode> affected) {
        var result = new PartialLayoutData();
        result.setAffectedNodes(affected);
        return result;
    }

    /** Returns the points of an edge as laid out: the end node centres around the bends. */
    private static List<Point2D> route(IEdge edge) {
        List<Point2D> result = new ArrayList<>();
        result.add(centre(edge.getSourceNode()));
        for (IBend bend : edge.getBends()) {
            IPoint location = bend.getLocation();
            result.add(new Point2D.Double(location.getX(), location.getY()));
        }
        result.add(centre(edge.getTargetNode()));
        return result;
    }

    /** Returns the centre of a node as laid out. */
    private static Point2D centre(INode node) {
        PointD result = node.getLayout().getCenter();
        return new Point2D.Double(result.getX(), result.getY());
    }

    /** Returns the vertex shown by a node of the shown graph. */
    @SuppressWarnings("unchecked")
    private static <G extends Graph> ViewVertex<G> vertexOf(INode node) {
        var result = (ViewVertex<G>) YFilesCellStore.cellOf(node);
        assert result != null; // every node of the shown graph shows a vertex
        return result;
    }

    /** Returns the edge cell shown by an edge of the shown graph. */
    @SuppressWarnings("unchecked")
    private static <G extends Graph> ViewEdge<G> edgeOf(IEdge edge) {
        var result = (ViewEdge<G>) YFilesCellStore.cellOf(edge);
        assert result != null; // every edge of the shown graph shows an edge cell
        return result;
    }

    /** Returns the prototype layouters of all kinds, in declaration order. */
    static List<Layouter> getPrototypes() {
        return PROTOTYPES;
    }

    /** The prototype layouters of all kinds, in declaration order. */
    private static final List<Layouter> PROTOTYPES;

    static {
        List<Layouter> prototypes = new ArrayList<>();
        for (var kind : YFilesLayoutKind.values()) {
            prototypes.add(new YFilesLayouter(kind));
        }
        PROTOTYPES = List.copyOf(prototypes);
    }
}
