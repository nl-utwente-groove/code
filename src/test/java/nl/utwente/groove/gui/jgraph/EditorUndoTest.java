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
package nl.utwente.groove.gui.jgraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jgraph.graph.ConnectionSet;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.CellChange;
import nl.utwente.groove.gui.view.CellStore.Connection;
import nl.utwente.groove.gui.view.EditHistory;
import nl.utwente.groove.gui.view.EditableLabels;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests the edit history of the graph editor on the JGraph backend, for the
 * edit sequence that adds an edge and gives it a label through JGraph's own
 * in-place editor path: the selection of the new edge in between must not
 * post an edit of its own, since undoing and redoing such an edit reorders
 * the history and loses the label edit; JGraph's attribute edits (a move)
 * must be recorded as minor edits; and undo and redo must replay the edits
 * on the same cells, in the graph as well as on the canvas.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class EditorUndoTest {
    /** Grammar whose start graph is edited. */
    private static final String GRAMMAR = "junit/samples/ferryman.gps";

    @Test
    void addEdgeAndEditLabelIsOneStep() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        EditHistory<AspectGraph> history = model.getEditHistory();
        assertNotNull(history, "an edited model has a history");
        assertFalse(history.canUndo());
        int[] changes = {0};
        history.addListener(() -> changes[0]++);
        var graph = model.getGraph();
        assertNotNull(graph);
        int edgeCount = graph.edgeCount();
        AspectEdgeCell edge = pendingEdge(canvas);
        JEdge<AspectGraph> jEdge = (JEdge<AspectGraph>) JCell.of(edge);
        canvas.setSelectionCell(jEdge);
        // the edge is shown but is not part of the graph nor of the history yet
        assertTrue(model.getCells().contains(edge), "the pending edge is shown");
        assertTrue(model.hasPendingInsertion());
        assertFalse(history.canUndo(), "a pending insertion is not in the history");
        assertEquals(0, changes[0]);
        graph = model.getGraph();
        assertNotNull(graph);
        assertEquals(edgeCount, graph.edgeCount(), "the graph does not have the pending edge");
        assertTrue(model.getResourceModel().getErrors().isEmpty(),
                   "no error for the pending edge's empty label");
        // what completing the in-place editor does
        canvas.getGraphLayoutCache().valueForCellChanged(jEdge, "next");
        assertFalse(model.hasPendingInsertion());
        assertEquals(1, changes[0], "creating and labelling the edge is one edit");
        assertTrue(history.isDirty());
        assertFalse(history.isDirtMinor());
        assertState(model, edge, true, "next");
        history.undo();
        // the label change is reverted before the insertion
        assertState(model, edge, false, "");
        assertFalse(history.canUndo());
        assertFalse(history.isDirty(), "dirt after undoing everything");
        history.redo();
        assertState(model, edge, true, "next");
        assertSame(jEdge, JCell.of(edge), "the edge cell got a new item on re-insertion");
        assertFalse(history.canRedo());
    }

    @Test
    void unlabelledEdgeIsWithdrawn() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        EditHistory<AspectGraph> history = model.getEditHistory();
        assertNotNull(history);
        int cellCount = model.getCells().size();
        // the editor is cancelled
        AspectEdgeCell edge = pendingEdge(canvas);
        model.settlePendingInsertion();
        assertFalse(model.getCells().contains(edge), "the cancelled edge is gone");
        assertEquals(cellCount, model.getCells().size());
        assertFalse(history.canUndo(), "nothing to undo after a cancelled edge");
        assertFalse(history.isDirty());
        // the editor commits an empty label
        edge = pendingEdge(canvas);
        canvas.getGraphLayoutCache().valueForCellChanged(JCell.of(edge), "");
        assertFalse(model.getCells().contains(edge), "the unlabelled edge is gone");
        assertFalse(history.canUndo());
        // another edit settles a pending edge too, by withdrawing it
        edge = pendingEdge(canvas);
        AspectVertexCell vertex = vertices(model).get(0);
        VisualMap change = new VisualMap();
        change.setNodePos(new Point2D.Double(300, 300));
        canvas.edit(Map.of(vertex, change));
        assertFalse(model.getCells().contains(edge), "the pending edge is gone after another edit");
        assertTrue(history.canUndo());
        assertTrue(history.isDirtMinor(), "only the move is in the history");
    }

    @Test
    void unlabelledVertexIsKept() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        EditHistory<AspectGraph> history = model.getEditHistory();
        assertNotNull(history);
        var graph = model.getGraph();
        assertNotNull(graph);
        int nodeCount = graph.nodeCount();
        AspectVertexCell vertex = model.newVertex(model.createAspectNode());
        vertex.setNodeFixed();
        vertex.putVisual(VisualKey.NODE_POS, new Point2D.Double(50, 50));
        model.insertPending(List.of(vertex), List.of(), List.of());
        graph = model.getGraph();
        assertNotNull(graph);
        assertEquals(nodeCount, graph.nodeCount(), "the graph does not have the pending vertex");
        assertFalse(history.canUndo());
        // the editor is cancelled: the vertex stays, as an edit of its own
        model.settlePendingInsertion();
        assertTrue(model.getCells().contains(vertex), "the vertex is kept");
        graph = model.getGraph();
        assertNotNull(graph);
        assertEquals(nodeCount + 1, graph.nodeCount(), "the graph has the kept vertex");
        assertTrue(history.canUndo());
        history.undo();
        assertFalse(model.getCells().contains(vertex));
        assertFalse(history.canUndo());
    }

    /**
     * Cancelling the in-place editor on the JGraph canvas withdraws the edge it was
     * opened for; stopping it with a label records the edge with that label.
     */
    @Test
    void inPlaceEditorSettlesTheNewEdge() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        EditHistory<AspectGraph> history = model.getEditHistory();
        assertNotNull(history);
        AspectEdgeCell edge = pendingEdge(canvas);
        canvas.startEditingAtCell(JCell.of(edge));
        assumeTrue(canvas.isEditing(), "in-place editing needs a displayable canvas");
        canvas.getUI().cancelEditing(canvas);
        assertFalse(canvas.isEditing());
        assertFalse(model.getCells().contains(edge), "the edge is gone after cancelling");
        assertFalse(history.canUndo());
        edge = pendingEdge(canvas);
        canvas.startEditingAtCell(JCell.of(edge));
        assertTrue(canvas.isEditing());
        canvas.stopEditing();
        assertFalse(model.getCells().contains(edge), "the edge is gone after an empty commit");
        assertFalse(history.canUndo());
    }

    /** Inserts a pending edge between the first two vertices, as AspectJGraph.addEdge does. */
    private static AspectEdgeCell pendingEdge(AspectJGraph canvas) {
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        List<AspectVertexCell> vertices = vertices(model);
        AspectEdgeCell edge = model.newEdge(null);
        edge.getEditableLabels().add("");
        edge
            .putVisual(VisualKey.POINTS,
                       List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10)));
        model
            .insertPending(List.of(), List.of(edge),
                           List.of(new Connection<>(edge, vertices.get(0), vertices.get(1))));
        return edge;
    }

    @Test
    void moveIsAMinorEdit() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        EditHistory<AspectGraph> history = model.getEditHistory();
        assertNotNull(history);
        AspectVertexCell vertex = vertices(model).get(0);
        Point2D before = vertex.getVisuals().getNodePos();
        // what the JGraph move handle does: an attribute edit through the layout cache
        Point2D after = new Point2D.Double(before.getX() + 40, before.getY() + 30);
        VisualMap change = new VisualMap();
        change.setNodePos(after);
        canvas.getNonNullModel().edit(Map.of(JCell.of(vertex), VisualAttributeMap.toAttributes(change)), null, null, null);
        assertEquals(after, vertex.getVisuals().getNodePos());
        assertTrue(history.canUndo());
        assertTrue(history.isDirty());
        assertTrue(history.isDirtMinor(), "a move is a minor edit");
        // the layout map follows
        var layout = model.getLayoutMap().getLayout(vertex.getNode());
        assertNotNull(layout);
        assertEquals(after.getX(), layout.getBounds().getCenterX(), 0.01);
        history.undo();
        assertEquals(before, vertex.getVisuals().getNodePos());
        assertFalse(history.isDirty());
        // the canvas' own funnel records too
        canvas.edit(Map.of(vertex, change));
        assertEquals(after, vertex.getVisuals().getNodePos());
        assertTrue(history.canUndo());
    }

    /**
     * Reconnecting an edge end by JGraph's own gesture (an edit with a
     * connection set, as the edge handle posts it) is an edit of the model: the
     * cell and the graph edge get the new end, and undo restores the old one.
     */
    @Test
    void reconnectionIsAnEdit() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        EditHistory<AspectGraph> history = model.getEditHistory();
        assertNotNull(history);
        // an edge between two different vertices, and a third vertex to reconnect it to
        AspectEdgeCell edge = null;
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate && !candidate.isLoop()) {
                edge = candidate;
                break;
            }
        }
        assertNotNull(edge, "no binary edge cell");
        var oldSource = edge.getSourceVertex();
        var oldTarget = edge.getTargetVertex();
        assertNotNull(oldSource);
        assertNotNull(oldTarget);
        AspectVertexCell other = null;
        for (var vertex : vertices(model)) {
            if (vertex != oldSource && vertex != oldTarget) {
                other = vertex;
                break;
            }
        }
        assertNotNull(other, "no third vertex");
        var graphEdge = edge.getEdges().iterator().next();
        // what the edge handle does on release
        ConnectionSet cs = new ConnectionSet();
        cs.connect(JCell.of(edge), ((JVertex<?>) JCell.of(other)).getPort(), false);
        canvas.getNonNullModel().edit(null, cs, null, null);
        assertSame(other, edge.getTargetVertex(), "cell target after reconnection");
        assertSame(oldSource, edge.getSourceVertex(), "cell source after reconnection");
        var newGraphEdge = edge.getEdges().iterator().next();
        assertSame(other.getNode(), newGraphEdge.target(), "graph edge target after reconnection");
        assertEquals(graphEdge.label(), newGraphEdge.label(), "graph edge label kept");
        assertTrue(history.canUndo());
        assertFalse(history.isDirtMinor(), "a reconnection is a major edit");
        history.undo();
        assertSame(oldTarget, edge.getTargetVertex(), "cell target after undo");
        assertSame(oldTarget.getNode(), edge.getEdges().iterator().next().target(),
                   "graph edge target after undo");
        history.redo();
        assertSame(other, edge.getTargetVertex(), "cell target after redo");
    }

    @Test
    void removalTakesTheIncidentEdgesAlong() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        EditHistory<AspectGraph> history = model.getEditHistory();
        assertNotNull(history);
        var graph = model.getGraph();
        assertNotNull(graph);
        int nodeCount = graph.nodeCount();
        int edgeCount = graph.edgeCount();
        int cellCount = model.getCells().size();
        // a vertex with edges
        AspectVertexCell vertex = null;
        for (var v : vertices(model)) {
            if (v.getContext().hasNext()) {
                vertex = v;
                break;
            }
        }
        assertNotNull(vertex, "no vertex with edge cells");
        int incident = 0;
        var iter = vertex.getContext();
        while (iter.hasNext()) {
            iter.next();
            incident++;
        }
        model.remove(List.of(vertex));
        assertEquals(cellCount - 1 - incident, model.getCells().size(), "cells after removal");
        graph = model.getGraph();
        assertNotNull(graph);
        assertEquals(nodeCount - 1, graph.nodeCount(), "nodes after removal");
        assertTrue(graph.edgeCount() < edgeCount, "edges after removal");
        history.undo();
        assertEquals(cellCount, model.getCells().size(), "cells after undo");
        graph = model.getGraph();
        assertNotNull(graph);
        assertEquals(nodeCount, graph.nodeCount(), "nodes after undo");
        assertEquals(edgeCount, graph.edgeCount(), "edges after undo");
        assertTrue(vertex.getContext().hasNext(), "edges reconnected after undo");
    }

    /**
     * A label change reaches the canvas listeners as a modification of the cell: the
     * label tree counts labels from those notifications.
     */
    @Test
    void labelChangeReachesTheCanvasListeners() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        List<AspectVertexCell> vertices = vertices(model);
        AspectVertexCell vertex = vertices.get(0);
        List<ViewCell<AspectGraph>> modified = new ArrayList<>();
        canvas.addCanvasListener(new GraphCanvasListener<AspectGraph>() {
            @Override
            public void cellsChanged(GraphCanvas<AspectGraph> source,
                                     CellChange<AspectGraph> change) {
                modified.addAll(change.modified());
            }
        });
        var labels = new EditableLabels();
        labels.load(vertex.getEditableLabels().toEditString() + "\nflag:extra");
        model.changeLabels(vertex, labels);
        assertTrue(modified.contains(vertex), "the relabelled cell was reported as modified");
    }

    /** Creates an editing JGraph canvas showing the fixture's start graph. */
    private static AspectJGraph editorCanvas() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assert startGraph != null; // the fixture grammar has a start graph
        var controller = new AspectGraphViewController(null, DisplayKind.HOST, true);
        controller.setGrammar(grammar);
        AspectJGraph canvas = (AspectJGraph) controller.getCanvas();
        AspectGraphViewModel model = canvas.newViewModel();
        model.setBeingEdited(true);
        model.loadGraph(startGraph);
        canvas.setViewModel(model);
        // selecting a cell scrolls it into view, which needs a viewport
        new JScrollPane(canvas);
        return canvas;
    }

    /** Returns the vertex cells of a model. */
    private static List<AspectVertexCell> vertices(AspectGraphViewModel model) {
        List<AspectVertexCell> result = new ArrayList<>();
        for (var cell : model.getCells()) {
            if (cell instanceof AspectVertexCell vertex) {
                result.add(vertex);
            }
        }
        return result;
    }

    /** Asserts the presence and the label of the edge in the model and in the graph. */
    private static void assertState(AspectGraphViewModel model, AspectEdgeCell edge,
                                    boolean present, String label) {
        assertEquals(present, model.getCells().contains(edge), "edge presence");
        assertEquals(present, edge.getSourceVertex() != null, "edge connection");
        List<String> labels = new ArrayList<>();
        for (String text : edge.getEditableLabels()) {
            labels.add(text);
        }
        assertEquals(List.of(label), labels, "editable labels");
        if (present) {
            assertEquals(1, edge.getEdges().size(), "graph edges");
            assertEquals(label, edge.getEdges().iterator().next().label().text(), "graph label");
            var graph = model.getGraph();
            assertNotNull(graph);
            assertTrue(graph.edgeSet().contains(edge.getEdges().iterator().next()),
                       "graph edge in the graph");
        }
        assertEquals(EditableLabels.class, edge.getEditableLabels().getClass());
    }
}
