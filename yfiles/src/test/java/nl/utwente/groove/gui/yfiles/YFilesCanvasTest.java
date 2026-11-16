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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.layout.SpringLayouter;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.CtrlGraphViewController;
import nl.utwente.groove.gui.view.EdgeGeometry;
import nl.utwente.groove.gui.view.EditableLabels;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.GraphViewContext;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.gui.view.PlainGraphViewController;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewOptions;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.gui.view.cell.AViewCell;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Checks the yFiles aspect canvas headlessly, the way the JGraph backend is checked in
 * the main project: the cells are shown by yFiles items with the cells as tags, the
 * geometry follows the visuals, selection is mirrored into the emphasis, hidden cells
 * leave the shown graph, the backend-independent layouter works through the edit
 * funnel, and export produces an image.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class YFilesCanvasTest {
    /** The GROOVE checkout the fixture grammars are read from; set by the build. */
    private static final String GROOVE_DIR = System.getProperty("groove.dir", "..");
    /** Grammar whose start graph is shown. */
    private static final String GRAMMAR = GROOVE_DIR + "/junit/samples/ferryman.gps";

    @Test
    void cellsAreShownByTaggedItems() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        assertInstanceOf(YFilesAspectCanvas.class, canvas);
        var graph = canvas.getGraph();
        assertNotNull(graph);
        int vertexCount = 0;
        int edgeCount = 0;
        for (var cell : canvas.getCells()) {
            Object item = ((AViewCell<?>) cell).getItem();
            if (cell instanceof ViewVertex<?> vertex) {
                vertexCount++;
                INode node = assertInstanceOf(INode.class, item);
                assertSame(cell, node.getTag());
                Point2D pos = vertex.getVisuals().getNodePos();
                assertEquals(pos.getX(), node.getLayout().getCenter().getX(), 0.01);
                assertEquals(pos.getY(), node.getLayout().getCenter().getY(), 0.01);
            } else if (cell instanceof ViewEdge<?> edge) {
                edgeCount++;
                IEdge yEdge = assertInstanceOf(IEdge.class, item);
                assertSame(cell, yEdge.getTag());
                assertSame(edge.getSourceVertex(), yEdge.getSourceNode().getTag());
                assertSame(edge.getTargetVertex(), yEdge.getTargetNode().getTag());
                int bends = Math.max(0, edge.getVisuals().getPoints().size() - 2);
                assertEquals(bends, yEdge.getBends().size(), "bends of " + edge);
            }
        }
        assertEquals(graph.nodeCount(), vertexCount);
        assertTrue(edgeCount > 0, "no edge cells");
    }

    @Test
    void selectionIsMirroredIntoEmphasis() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        int[] events = {0};
        canvas.addCanvasListener(new GraphCanvasListener<AspectGraph>() {
            @Override
            public void selectionChanged(GraphCanvas<AspectGraph> source) {
                events[0]++;
            }
        });
        ViewCell<AspectGraph> cell = firstVertex(canvas);
        canvas.select(List.of(cell));
        assertEquals(List.of(cell), canvas.getSelection());
        assertTrue(cell.getVisuals().isEmphasised());
        assertEquals(1, events[0], "selection events");
        canvas.clearSelection();
        assertTrue(canvas.isSelectionEmpty());
        assertFalse(cell.getVisuals().isEmphasised());
        assertEquals(2, events[0], "selection events");
    }

    @Test
    void visibleEdgesAreInTheShownGraph() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        GraphComponent component = (GraphComponent) canvas.getComponent();
        int visibleEdges = 0;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewEdge && cell.getVisuals().isVisible()) {
                visibleEdges++;
            }
        }
        assertTrue(visibleEdges > 0, "no visible edge cells");
        assertEquals(visibleEdges, component.getGraph().getEdges().size());
    }

    @Test
    void labelTreeDoesNotHideCells() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assertNotNull(startGraph);
        // the state display shows host graphs; DisplayKind.STATE has no graph role of its own
        var context = new TreeFilterContext(grammar);
        var controller = new AspectGraphViewController(context, GraphRole.HOST, false);
        AspectGraphCanvas canvas = controller.getCanvas();
        // the displays of the Simulator give the context a filtering type tree
        context.setLabelTree(new TypeTree(null, canvas, true));
        canvas.showGraph(startGraph);
        GraphComponent component = (GraphComponent) canvas.getComponent();
        int visibleNodes = 0;
        int visibleEdges = 0;
        for (var cell : canvas.getCells()) {
            if (cell.getVisuals().isVisible()) {
                if (cell instanceof ViewVertex) {
                    visibleNodes++;
                } else {
                    visibleEdges++;
                }
            }
        }
        assertEquals(startGraph.nodeCount(), visibleNodes, "visible vertex cells");
        assertTrue(visibleEdges > 0, "no visible edge cells");
        assertEquals(visibleNodes, component.getGraph().getNodes().size(), "shown nodes");
        assertEquals(visibleEdges, component.getGraph().getEdges().size(), "shown edges");
    }

    @Test
    void edgesArePainted() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        var bounds = canvas.getGraphBounds();
        assertNotNull(bounds);
        var image = canvas.toImage();
        assertNotNull(image);
        // the image has a 5px margin around the graph bounds, at scale 1
        int margin = 5;
        // find the longest straight edge and probe it just outside its source node
        ViewEdge<AspectGraph> longest = null;
        double longestLength = 0;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewEdge<AspectGraph> edge && !edge.isLoop()
                && edge.getVisuals().getPoints().size() == 2) {
                var source = edge.getSourceVertex();
                var target = edge.getTargetVertex();
                assertNotNull(source);
                assertNotNull(target);
                double length = source
                    .getVisuals()
                    .getNodePos()
                    .distance(target.getVisuals().getNodePos());
                if (length > longestLength) {
                    longest = edge;
                    longestLength = length;
                }
            }
        }
        assertNotNull(longest, "no straight edge");
        var source = longest.getSourceVertex();
        var target = longest.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        Point2D from = source.getVisuals().getNodePos();
        Point2D to = target.getVisuals().getNodePos();
        var size = canvas.getPreferredSize(source);
        double radius = Math.max(size.getWidth(), size.getHeight()) / 2 + 4;
        double dx = (to.getX() - from.getX()) / longestLength;
        double dy = (to.getY() - from.getY()) / longestLength;
        double wx = from.getX() + dx * radius;
        double wy = from.getY() + dy * radius;
        int px = (int) Math.round(wx - bounds.getX() + margin);
        int py = (int) Math.round(wy - bounds.getY() + margin);
        boolean painted = false;
        for (int x = px - 2; x <= px + 2 && !painted; x++) {
            for (int y = py - 2; y <= py + 2 && !painted; y++) {
                if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()) {
                    int rgb = image.getRGB(x, y) & 0xFFFFFF;
                    painted = rgb != 0xFFFFFF;
                }
            }
        }
        assertTrue(painted, "nothing painted near (" + px + "," + py + ") on " + longest);
    }

    @Test
    void edgeLabelsSitWhereJGraphPutsThem() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph rule = grammar.getRuleModel(QualName.name("arrive-empty")).getSource();
        var controller
            = new AspectGraphViewController(null, DisplayKind.RULE.getGraphRole(), false);
        controller.setGrammar(grammar);
        AspectGraphCanvas canvas = controller.getCanvas();
        canvas.showGraph(rule);
        int checked = 0;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewEdge<?> edge && !edge.isLoop()) {
                IEdge yEdge = (IEdge) ((AViewCell<?>) cell).getItem();
                assertNotNull(yEdge);
                List<Point2D> points = new ArrayList<>();
                for (var point : yEdge.getPathPoints()) {
                    points.add(new Point2D.Double(point.getX(), point.getY()));
                }
                Point2D expected = EdgeGeometry
                    .labelPosition(edge.getVisuals().getLabelPos(), points);
                assertEquals(1, yEdge.getLabels().size(), "labels of " + edge);
                var centre = yEdge.getLabels().first().getLayout().getCenter();
                assertEquals(expected.getX(), centre.getX(), 0.01, "label x of " + edge);
                assertEquals(expected.getY(), centre.getY(), 0.01, "label y of " + edge);
                checked++;
            }
        }
        assertTrue(checked >= 3, "checked only " + checked + " edges");
    }

    @Test
    void dashedEdgesShowDashesAndGaps() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph rule = grammar.getRuleModel(QualName.name("arrive-empty")).getSource();
        var controller
            = new AspectGraphViewController(null, DisplayKind.RULE.getGraphRole(), false);
        controller.setGrammar(grammar);
        AspectGraphCanvas canvas = controller.getCanvas();
        canvas.showGraph(rule);
        var bounds = canvas.getGraphBounds();
        assertNotNull(bounds);
        var image = canvas.toImage();
        assertNotNull(image);
        int checked = 0;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewEdge<?> edge && !edge.isLoop()
                && CellStyles.isDashed(edge.getVisuals().getDash())
                && edge.getVisuals().getPoints().size() == 2) {
                var source = edge.getSourceVertex();
                var target = edge.getTargetVertex();
                assertNotNull(source);
                assertNotNull(target);
                Point2D from = source.getVisuals().getNodePos();
                Point2D to = target.getVisuals().getNodePos();
                java.awt.Color fg = edge.getVisuals().getForeground();
                int dashes = 0;
                int gaps = 0;
                // sample the first part of the edge, clear of the label in the middle
                for (double t = 0.2; t < 0.4; t += 0.005) {
                    double wx = from.getX() + t * (to.getX() - from.getX());
                    double wy = from.getY() + t * (to.getY() - from.getY());
                    int px = (int) Math.round(wx - bounds.getX() + 5);
                    int py = (int) Math.round(wy - bounds.getY() + 5);
                    int rgb = image.getRGB(px, py) & 0xFFFFFF;
                    if (rgb == 0xFFFFFF) {
                        gaps++;
                    } else if (isNear(rgb, fg)) {
                        dashes++;
                    }
                }
                assertTrue(dashes > 0, "no dashes on " + edge);
                assertTrue(gaps > 0, "no gaps on " + edge);
                checked++;
            }
        }
        assertTrue(checked > 0, "no dashed straight edges");
    }

    /** Tests if a pixel colour is close to a given colour. */
    private static boolean isNear(int rgb, java.awt.Color color) {
        int dr = Math.abs(((rgb >> 16) & 0xFF) - color.getRed());
        int dg = Math.abs(((rgb >> 8) & 0xFF) - color.getGreen());
        int db = Math.abs((rgb & 0xFF) - color.getBlue());
        return dr + dg + db < 150;
    }

    @Test
    void plainCanvasShowsAGraph() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assertNotNull(startGraph);
        var canvas = new PlainGraphViewController(null).getCanvas();
        assertInstanceOf(YFilesPlainCanvas.class, canvas);
        canvas.showGraph(startGraph.toPlainGraph());
        GraphComponent component = (GraphComponent) canvas.getComponent();
        assertEquals(startGraph.nodeCount(), component.getGraph().getNodes().size());
        assertTrue(component.getGraph().getEdges().size() > 0, "no edges shown");
    }

    @Test
    void controlCanvasShowsAnAutomaton() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GROOVE_DIR + "/junit/samples/recipes.gps");
        var template = grammar.getControlModel().getProgram().getTemplate();
        var graph = template.toGraph(true);
        var canvas = new CtrlGraphViewController(null).getCanvas();
        assertInstanceOf(YFilesCtrlCanvas.class, canvas);
        canvas.showGraph(graph);
        GraphComponent component = (GraphComponent) canvas.getComponent();
        assertEquals(graph.nodeCount(), component.getGraph().getNodes().size());
        assertTrue(component.getGraph().getEdges().size() > 0, "no edges shown");
    }

    @Test
    void clicksReachSwingListeners() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        var received = new java.util.ArrayList<java.awt.event.MouseEvent>();
        canvas.getComponent().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            @NonNullByDefault({})
            public void mouseClicked(java.awt.event.MouseEvent e) {
                received.add(e);
            }
        });
        var world = new com.yworks.yfiles.geometry.PointD(30, 40);
        ((YFilesCanvas<?>) canvas)
            .forwardClick(new com.yworks.yfiles.view.input.ClickEventArgs(null, world,
                com.yworks.yfiles.view.ModifierKeys.CONTROL, com.yworks.yfiles.view.MouseButtons.LEFT,
                2));
        assertEquals(1, received.size(), "events received");
        var event = received.get(0);
        var view = ((GraphComponent) canvas.getComponent()).toViewCoordinates(world);
        assertEquals(Math.round(view.getX()), event.getX());
        assertEquals(Math.round(view.getY()), event.getY());
        assertEquals(2, event.getClickCount());
        assertEquals(java.awt.event.MouseEvent.BUTTON1, event.getButton());
        assertTrue(event.isControlDown(), "control modifier lost");
        assertSame(canvas.getComponent(), event.getSource());
    }

    @Test
    void backendLayoutersAreTheYFilesKinds() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        var names = canvas.getBackendLayouters().stream().map(Layouter::getName).toList();
        assertEquals(YFilesLayoutKind.values().length, names.size());
        for (var kind : YFilesLayoutKind.values()) {
            assertTrue(names.contains(kind.getDisplayName()), "missing " + kind);
        }
        // a canvas-bound instance has a settings panel, a prototype has none
        Layouter prototype = canvas.getBackendLayouters().get(0);
        assertNull(prototype.getSettingsPanel());
        assertNotNull(prototype.newInstance(canvas).getSettingsPanel());
    }

    @Test
    void layoutsMoveTheVerticesThroughTheEditFunnel() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        var graph = canvas.getGraph();
        assertNotNull(graph);
        Map<ViewVertex<?>,Point2D> before = positions(canvas);
        canvas.getNonNullViewModel().setLayoutable(true);
        layouter(canvas, YFilesLayoutKind.HIERARCHIC).start();
        Map<ViewVertex<?>,Point2D> after = positions(canvas);
        assertNotEquals(before, after, "no vertex moved");
        var layoutMap = GraphInfo.getLayoutMap(graph);
        for (var entry : after.entrySet()) {
            ViewVertex<?> vertex = entry.getKey();
            Point2D pos = entry.getValue();
            // the item follows the cell
            INode node = (INode) ((AViewCell<?>) vertex).getItem();
            assertNotNull(node);
            assertEquals(pos.getX(), node.getLayout().getCenter().getX(), 0.01);
            assertEquals(pos.getY(), node.getLayout().getCenter().getY(), 0.01);
            // the layout map of the graph follows the cell
            var layout = layoutMap.getLayout(vertex.getNode());
            assertNotNull(layout);
            assertEquals(pos.getX(), layout.getBounds().getCenterX(), 0.01);
            assertEquals(pos.getY(), layout.getBounds().getCenterY(), 0.01);
            assertFalse(vertex.isLayoutable(), "vertex still layoutable after layout");
        }
        // the edges run between the new positions, with bends where the layout put them
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewEdge<?> edge && edge.getVisuals().isVisible()) {
                List<Point2D> points = edge.getVisuals().getPoints();
                IEdge item = (IEdge) ((AViewCell<?>) edge).getItem();
                assertNotNull(item);
                assertEquals(item.getBends().size(), points.size() - 2, "bends of " + edge);
                assertEquals(after.get(edge.getSourceVertex()), points.get(0));
                assertEquals(after.get(edge.getTargetVertex()), points.get(points.size() - 1));
            }
        }
    }

    @Test
    void partialLayoutsKeepTheFixedVertices() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        // nothing is layoutable: nothing happens
        canvas.getNonNullViewModel().setLayoutable(false);
        Map<ViewVertex<?>,Point2D> before = positions(canvas);
        for (var kind : YFilesLayoutKind.values()) {
            layouter(canvas, kind).start();
            assertEquals(before, positions(canvas), "vertices moved by " + kind);
        }
        // one vertex is layoutable: the others stay
        ViewVertex<AspectGraph> free = (ViewVertex<AspectGraph>) firstVertex(canvas);
        for (var kind : YFilesLayoutKind.values()) {
            free.setLayoutable(true);
            layouter(canvas, kind).start();
            Map<ViewVertex<?>,Point2D> after = positions(canvas);
            for (var entry : before.entrySet()) {
                if (entry.getKey() != free) {
                    assertEquals(entry.getValue(), after.get(entry.getKey()),
                                 "fixed vertex moved by " + kind);
                }
            }
            assertFalse(free.isLayoutable());
        }
    }

    /** Returns a layouter of a given kind, bound to a canvas. */
    private static Layouter layouter(GraphCanvas<?> canvas, YFilesLayoutKind kind) {
        for (Layouter prototype : canvas.getBackendLayouters()) {
            if (prototype.getName().equals(kind.getDisplayName())) {
                return prototype.newInstance(canvas);
            }
        }
        throw new IllegalArgumentException("No layouter of kind " + kind);
    }

    /** Returns the positions of the vertices of a canvas. */
    private static Map<ViewVertex<?>,Point2D> positions(GraphCanvas<?> canvas) {
        Map<ViewVertex<?>,Point2D> result = new HashMap<>();
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewVertex<?> vertex) {
                Point2D pos = vertex.getVisuals().getNodePos();
                result.put(vertex, new Point2D.Double(pos.getX(), pos.getY()));
            }
        }
        return result;
    }

    /**
     * The view model's edits are applied to the yFiles store and undone from
     * it: insertion, removal (with incident edges), visual and label changes.
     */
    @Test
    @SuppressWarnings("unchecked") // the cells of an aspect canvas are over AspectGraph
    void editsAreUndoableOnTheStore() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        AspectGraphViewModel model = canvas.getViewModel();
        assertNotNull(model);
        var history = model.enableEditHistory();
        GraphComponent component = (GraphComponent) canvas.getComponent();
        int nodes = component.getGraph().getNodes().size();
        int edges = component.getGraph().getEdges().size();
        // insertion
        AspectVertexCell fresh = model.newVertex(model.createAspectNode());
        fresh.setNodeFixed();
        fresh.putVisual(VisualKey.NODE_POS, new Point2D.Double(300, 200));
        model.insert(List.of(fresh), List.of(), List.of());
        assertEquals(nodes + 1, component.getGraph().getNodes().size(), "nodes after insert");
        INode freshNode = assertInstanceOf(INode.class, fresh.getItem());
        assertEquals(300, freshNode.getLayout().getCenter().getX(), 0.01);
        var graph = model.getGraph();
        assertNotNull(graph);
        assertTrue(graph.nodeSet().contains(fresh.getNode()), "graph rebuilt after insert");
        // a visual change through the canvas' funnel
        VisualMap move = new VisualMap();
        move.setNodePos(new Point2D.Double(350, 250));
        canvas.edit(Map.of(fresh, move));
        assertEquals(350, freshNode.getLayout().getCenter().getX(), 0.01);
        history.undo();
        assertEquals(300, freshNode.getLayout().getCenter().getX(), 0.01);
        // a label change
        EditableLabels labels = new EditableLabels();
        labels.add("type:Thing");
        model.changeLabels(fresh, labels);
        assertEquals(1, freshNode.getLabels().size(), "label shown after label change");
        assertTrue(freshNode.getLabels().getItem(0).getText().contains("Thing"));
        history.undo();
        assertEquals(0, freshNode.getLabels().size(), "label gone after undo");
        history.undo();
        assertEquals(nodes, component.getGraph().getNodes().size(), "nodes after undoing insert");
        assertNull(fresh.getItem(), "removed cell still has an item");
        // removal of a vertex with edges
        ViewVertex<AspectGraph> vertex = (ViewVertex<AspectGraph>) firstVertex(canvas);
        while (!vertex.getContext().hasNext()) {
            vertex = nextVertex(canvas, vertex);
        }
        model.remove(List.of(vertex));
        assertEquals(nodes - 1, component.getGraph().getNodes().size(), "nodes after remove");
        assertTrue(component.getGraph().getEdges().size() < edges, "edges after remove");
        history.undo();
        assertEquals(nodes, component.getGraph().getNodes().size(), "nodes after undoing remove");
        assertEquals(edges, component.getGraph().getEdges().size(), "edges after undoing remove");
        assertTrue(vertex.getContext().hasNext(), "edges reconnected after undo");
        history.redo();
        assertEquals(nodes - 1, component.getGraph().getNodes().size(), "nodes after redo");
        history.undo();
        // reconnection of an edge to another target
        ViewEdge<AspectGraph> edge = null;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewEdge<?> candidate && !candidate.isLoop()) {
                edge = (ViewEdge<AspectGraph>) candidate;
                break;
            }
        }
        assertNotNull(edge, "no binary edge cell");
        var oldTarget = edge.getTargetVertex();
        var source = edge.getSourceVertex();
        assertNotNull(oldTarget);
        assertNotNull(source);
        ViewVertex<AspectGraph> other = null;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewVertex<?> candidate && candidate != source
                && candidate != oldTarget) {
                other = (ViewVertex<AspectGraph>) candidate;
                break;
            }
        }
        assertNotNull(other, "no third vertex");
        model.reconnect(edge, source, other, null);
        IEdge item = assertInstanceOf(IEdge.class, ((AViewCell<?>) edge).getItem());
        assertSame(other, item.getTargetNode().getTag(), "item target after reconnection");
        assertSame(other, edge.getTargetVertex(), "cell target after reconnection");
        assertEquals(edges, component.getGraph().getEdges().size(), "edges after reconnection");
        history.undo();
        item = assertInstanceOf(IEdge.class, ((AViewCell<?>) edge).getItem());
        assertSame(oldTarget, item.getTargetNode().getTag(), "item target after undo");
        assertSame(oldTarget, edge.getTargetVertex(), "cell target after undo");
    }

    /** Returns the vertex cell after a given one, in the canvas' cell order. */
    @SuppressWarnings("unchecked") // the cells of an aspect canvas are over AspectGraph
    private static ViewVertex<AspectGraph> nextVertex(AspectGraphCanvas canvas,
                                                      ViewVertex<AspectGraph> vertex) {
        boolean found = false;
        for (var cell : canvas.getCells()) {
            if (cell == vertex) {
                found = true;
            } else if (found && cell instanceof ViewVertex<?> next) {
                return (ViewVertex<AspectGraph>) next;
            }
        }
        throw new IllegalStateException("No vertex after " + vertex);
    }

    /**
     * The viewer canvas moves cells: its input mode carries a move mode, enabled in
     * select mode and disabled in pan mode, and the move of a vertex lands in the
     * view model as a visual change, with the incident edges following.
     */
    @Test
    void viewerMovesVerticesThroughTheEditFunnel() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        var yCanvas = assertInstanceOf(YFilesAspectCanvas.class, canvas);
        var move = yCanvas.getMoveMode();
        assertNotNull(move, "no move mode on the viewer");
        assertEquals(GraphViewMode.SELECT_MODE, canvas.getMode());
        assertTrue(move.isEnabled(), "move mode off in select mode");
        canvas.setMode(GraphViewMode.PAN_MODE);
        assertFalse(move.isEnabled(), "move mode on in pan mode");
        canvas.setMode(GraphViewMode.SELECT_MODE);
        assertTrue(move.isEnabled(), "move mode off after returning to select mode");
        // a drag starts on a vertex, not on empty canvas
        canvas.getComponent().setSize(800, 600);
        ViewVertex<AspectGraph> vertex = (ViewVertex<AspectGraph>) firstVertex(canvas);
        INode node = (INode) ((AViewCell<?>) vertex).getItem();
        assertNotNull(node);
        Point2D at = vertex.getVisuals().getNodePos();
        var hit = move.getHitTestable();
        assertTrue(hit.isHit(null, new PointD(at.getX(), at.getY())), "no move start on a vertex");
        assertFalse(hit.isHit(null, new PointD(-1000, -1000)), "move start on empty canvas");
        // the gesture itself, as mouse events on yFiles' input surface: a press on the
        // vertex must reach the move mode, not the marquee behind it, and the drag
        // moves the item and lands in the model when released
        RectD layout = node.getLayout().toRectD();
        var view = ((GraphComponent) canvas.getComponent())
            .toViewCoordinates(new PointD(at.getX(), at.getY()));
        SyntheticMouse
            .drag(canvas.getComponent(), (int) Math.round(view.getX()),
                  (int) Math.round(view.getY()), 40, 25);
        assertTrue(canvas.getSelection().isEmpty(), "the drag selected cells: marquee?");
        Point2D pos = vertex.getVisuals().getNodePos();
        assertEquals(layout.getCenter().getX() + 40, pos.getX(), 0.01);
        assertEquals(layout.getCenter().getY() + 25, pos.getY(), 0.01);
        // the new position landed in the layout map, which the view tab stores
        var graph = canvas.getGraph();
        assertNotNull(graph);
        var vertexLayout = GraphInfo.getLayoutMap(graph).getLayout(vertex.getNode());
        assertNotNull(vertexLayout);
        assertEquals(pos.getX(), vertexLayout.getBounds().getCenterX(), 0.01);
        assertEquals(pos.getY(), vertexLayout.getBounds().getCenterY(), 0.01);
        // the incident edges follow the vertex
        var edges = vertex.getContext();
        assertTrue(edges.hasNext(), "the vertex has no edges");
        ViewEdge<?> edge = edges.next();
        var points = edge.getVisuals().getPoints();
        Point2D end = edge.getSourceVertex() == vertex
            ? points.get(0)
            : points.get(points.size() - 1);
        assertEquals(pos, end, "edge end follows the vertex");
    }

    /**
     * The viewer canvas moves the main label of an edge: a drag may start on the label,
     * moves it, and lands in the view model as a change of the edge's label position,
     * which places the label where it was dropped.
     */
    @Test
    void viewerMovesEdgeLabels() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        var yCanvas = assertInstanceOf(YFilesAspectCanvas.class, canvas);
        var move = yCanvas.getMoveLabelMode();
        assertNotNull(move, "no label move mode on the viewer");
        assertTrue(move.isEnabled(), "label move mode off in select mode");
        canvas.setMode(GraphViewMode.PAN_MODE);
        assertFalse(move.isEnabled(), "label move mode on in pan mode");
        canvas.setMode(GraphViewMode.SELECT_MODE);
        canvas.getComponent().setSize(800, 600);
        ViewEdge<AspectGraph> edge = null;
        ILabel label = null;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewEdge<AspectGraph> candidate && !candidate.isLoop()
                && YFilesCellStore.itemOf(cell) instanceof IEdge item) {
                for (ILabel candidateLabel : item.getLabels()) {
                    if (YFilesCellStore.isMainEdgeLabel(candidateLabel)) {
                        edge = candidate;
                        label = candidateLabel;
                    }
                }
                if (label != null) {
                    break;
                }
            }
        }
        assertNotNull(edge, "no labelled binary edge");
        assertNotNull(label);
        PointD centre = label.getLayout().getCenter();
        assertTrue(move.getHitTestable().isHit(move.getInputModeContext(), centre),
                   "no move start on an edge label");
        // the gesture itself, as mouse events on yFiles' input surface
        Point2D before = edge.getVisuals().getLabelPos();
        var view = ((GraphComponent) canvas.getComponent()).toViewCoordinates(centre);
        SyntheticMouse
            .drag(canvas.getComponent(), (int) Math.round(view.getX()),
                  (int) Math.round(view.getY()), 40, 25);
        assertTrue(canvas.getSelection().isEmpty(), "the drag selected cells: marquee?");
        PointD placed = label.getLayout().getCenter();
        assertEquals(centre.getX() + 40, placed.getX(), 1, "x of the moved label");
        assertEquals(centre.getY() + 25, placed.getY(), 1, "y of the moved label");
        assertNotEquals(before, edge.getVisuals().getLabelPos(), "label position after the drag");
    }

    @Test
    void hiddenCellsLeaveTheShownGraph() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        GraphComponent component = (GraphComponent) canvas.getComponent();
        int shown = component.getGraph().getNodes().size();
        ViewCell<AspectGraph> cell = firstVertex(canvas);
        cell.putVisual(VisualKey.VISIBLE, false);
        canvas.refresh(List.of(cell), true);
        assertEquals(shown - 1, component.getGraph().getNodes().size());
        cell.putVisual(VisualKey.VISIBLE, true);
        canvas.refresh(List.of(cell), true);
        assertEquals(shown, component.getGraph().getNodes().size());
    }

    @Test
    void hitTestFindsTheVertexAtItsCentre() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        canvas.getComponent().setSize(800, 600);
        ViewCell<AspectGraph> cell = firstVertex(canvas);
        Point2D pos = cell.getVisuals().getNodePos();
        GraphComponent component = (GraphComponent) canvas.getComponent();
        var view = component
            .toViewCoordinates(new com.yworks.yfiles.geometry.PointD(pos.getX(), pos.getY()));
        assertSame(cell, canvas.getCellAt(view.getX(), view.getY()));
        assertNull(canvas.getCellAt(-1000, -1000));
    }

    @Test
    void springLayoutWorksThroughTheEditFunnel() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        var controller = canvas.getController();
        controller.setLayouter(SpringLayouter.PROTOTYPE);
        controller.doLayout(true);
        assertFalse(canvas.isLayouting(), "layouting flag not reset");
        Set<Point2D> positions = new HashSet<>();
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewVertex<?> vertex) {
                assertFalse(vertex.isLayoutable(), "vertex still marked for layout");
                Point2D pos = vertex.getVisuals().getNodePos();
                assertTrue(positions.add(pos), "two vertices at " + pos);
                INode node = (INode) ((AViewCell<?>) cell).getItem();
                assertNotNull(node);
                assertEquals(pos.getX(), node.getLayout().getCenter().getX(), 0.01);
            }
        }
    }

    @Test
    void exportProducesAnImage() throws IOException {
        AspectGraphCanvas canvas = showStartGraph();
        var bounds = canvas.getGraphBounds();
        assertNotNull(bounds);
        assertTrue(bounds.getWidth() > 10 && bounds.getHeight() > 10, "bounds " + bounds);
        var image = canvas.toImage();
        assertNotNull(image);
        assertTrue(image.getWidth() >= bounds.getWidth(), "image width " + image.getWidth());
    }

    @Test
    void detachedModelHasItemsAndVisuals() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assertNotNull(startGraph);
        // the state display shows host graphs; DisplayKind.STATE has no graph role of its own
        var controller = new AspectGraphViewController(null, GraphRole.HOST, false);
        controller.setGrammar(grammar);
        var model = controller.getCanvas().newViewModel();
        model.loadGraph(startGraph);
        // the model is deliberately not set on the canvas
        int count = 0;
        for (var cell : model.getCells()) {
            assertNotNull(cell.getVisuals().getLabel(), "no label for " + cell);
            assertNotNull(((AViewCell<?>) cell).getItem(), "no item for " + cell);
            count++;
        }
        assertTrue(count > 0, "start graph has no cells");
        assertNull(controller.getCanvas().getViewModel());
    }

    /** Shows the start graph of the fixture grammar on a fresh host-graph canvas. */
    private static AspectGraphCanvas showStartGraph() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assertNotNull(startGraph);
        var controller
            = new AspectGraphViewController(null, DisplayKind.HOST.getGraphRole(), false);
        controller.setGrammar(grammar);
        AspectGraphCanvas result = controller.getCanvas();
        result.showGraph(startGraph);
        return result;
    }


    /** Returns the first vertex cell of a canvas. */
    private static ViewCell<AspectGraph> firstVertex(AspectGraphCanvas canvas) {
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewVertex) {
                return cell;
            }
        }
        throw new AssertionError("no vertex cells");
    }

    /**
     * The context of a graph view that filters by label: it provides the grammar and
     * answers the filter queries from a {@link TypeTree}, as the resource displays of
     * the Simulator do. Not interactive, so it contributes no actions or menu items,
     * and leaves the export and layout-dialog actions at their null defaults.
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    private static class TreeFilterContext implements GraphViewContext<AspectGraph> {
        /** Creates a context on a given grammar, without a label tree yet. */
        TreeFilterContext(GrammarModel grammar) {
            this.grammar = grammar;
        }

        @Override
        public @Nullable GrammarModel getGrammar() {
            return this.grammar;
        }

        /** The grammar the displayed graph belongs to. */
        private final GrammarModel grammar;

        @Override
        public ViewOptions getOptions() {
            return Options.instance();
        }

        @Override
        public boolean isInteractive() {
            return false;
        }

        /**
         * Sets the label tree answering the filter queries of this context.
         * Called after the canvas exists, as the displays do.
         */
        void setLabelTree(TypeTree labelTree) {
            this.labelTree = labelTree;
        }

        /** The label tree of this context; {@code null} until set. */
        private @Nullable TypeTree labelTree;

        @Override
        public boolean isFiltering() {
            return this.labelTree != null;
        }

        @Override
        public boolean isFiltered(ViewCell<AspectGraph> cell) {
            var labelTree = this.labelTree;
            return labelTree != null && !labelTree.isIncluded(cell);
        }

        @Override
        public boolean isFiltered(Label label) {
            var labelTree = this.labelTree;
            return labelTree != null && !labelTree.isIncluded(label);
        }

        @Override
        public void setFilteringEnabled(boolean enabled) {
            var labelTree = this.labelTree;
            if (labelTree != null) {
                labelTree.setEnabled(enabled);
            }
        }
    }
}
