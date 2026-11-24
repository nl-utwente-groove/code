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
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;
import java.lang.reflect.InvocationTargetException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import nl.utwente.groove.gui.view.CellStore.Connection;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.gui.tree.LabelTree;
import javax.swing.tree.TreeModel;
import java.util.Set;
import java.util.HashSet;
import java.awt.geom.Point2D;
import nl.utwente.groove.util.line.LineStyle;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.look.VisualKey;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.ILabel;
import java.util.Map;
import java.util.LinkedHashMap;
import java.awt.geom.PathIterator;
import java.awt.geom.Path2D;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IEdgePortHandleProvider;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.CellChange;
import nl.utwente.groove.gui.view.EditHistory;
import nl.utwente.groove.gui.view.EditableLabels;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.gui.view.ParallelEdges;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.gui.view.cell.AViewCell;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;

/**
 * Checks the yFiles editor canvas headlessly: its gestures' effects on the
 * model, exercised through the canvas' own entry points (a vertex added at a
 * point, the edge creator, the in-place editor, a finished move, a dragged edge
 * end), each an edit of the model's history.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class YFilesEditorTest {
    /** The GROOVE checkout the fixture grammars are read from; set by the build. */
    private static final String GROOVE_DIR = System.getProperty("groove.dir", "..");
    /** Grammar whose start graph is edited. */
    private static final String GRAMMAR = GROOVE_DIR + "/junit/samples/ferryman.gps";

    @Test
    void editingControllerGetsTheEditorCanvas() throws IOException {
        var canvas = editorCanvas();
        assertEquals(GraphViewMode.EDIT_MODE, canvas.getMode());
        assertTrue(canvas.isEditable());
        assertNotNull(model(canvas).getEditHistory(), "the edited model records its edits");
        assertTrue(canvas.hasActiveEditor(), "an editing canvas has an active editor");
        assertFalse(canvas.isEditingLabel());
    }

    @Test
    void addedVertexIsAnEditWhenItsEditorCloses() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        EditHistory<AspectGraph> history = history(canvas);
        int cells = model.getCells().size();
        canvas.addVertex(new PointD(120, 80));
        assertEquals(cells + 1, model.getCells().size(), "cells after adding a vertex");
        assertFalse(history.canUndo(), "the vertex is pending while its editor is open");
        ViewVertex<AspectGraph> vertex = (ViewVertex<AspectGraph>) canvas.getSelectedCell();
        assertNotNull(vertex, "the new vertex is selected");
        assertEquals(120, vertex.getVisuals().getNodePos().getX(), 0.01);
        INode node = assertInstanceOf(INode.class, ((AViewCell<?>) vertex).getItem());
        assertEquals(120, node.getLayout().getCenter().getX(), 0.01);
        assertTrue(canvas.isEditingLabel(), "the in-place editor opens on the new vertex");
        // committing the empty text keeps the vertex, as one edit
        canvas.finishEditing();
        assertFalse(canvas.isEditingLabel());
        assertTrue(history.canUndo());
        history.undo();
        assertEquals(cells, model.getCells().size(), "cells after undoing the vertex");
        assertFalse(history.canUndo(), "the empty commit was recorded as an edit");
    }

    @Test
    void addedVertexAndItsLabelAreOneEdit() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        EditHistory<AspectGraph> history = history(canvas);
        int cells = model.getCells().size();
        canvas.addVertex(new PointD(120, 80));
        var area = canvas.editorMode().getTextEditorInputMode().getTextArea();
        area.setText("flag:fresh");
        canvas.finishEditing();
        AspectVertexCell vertex = assertInstanceOf(AspectVertexCell.class, canvas.getSelectedCell());
        assertEquals("flag:fresh", vertex.getEditableLabels().toEditString());
        assertTrue(history.canUndo());
        history.undo();
        assertEquals(cells, model.getCells().size(), "one undo removes the labelled vertex");
        assertFalse(history.canUndo());
    }

    @Test
    void gridSnapsAddedVertices() throws IOException {
        var canvas = editorCanvas();
        canvas.setGridEnabled(true);
        canvas.addVertex(new PointD(123, 77));
        ViewVertex<AspectGraph> vertex = (ViewVertex<AspectGraph>) canvas.getSelectedCell();
        assertNotNull(vertex);
        assertEquals(120, vertex.getVisuals().getNodePos().getX(), 0.01);
        assertEquals(80, vertex.getVisuals().getNodePos().getY(), 0.01);
        canvas.finishEditing();
        canvas.setGridEnabled(false);
        canvas.addVertex(new PointD(123, 77));
        vertex = (ViewVertex<AspectGraph>) canvas.getSelectedCell();
        assertNotNull(vertex);
        assertEquals(123, vertex.getVisuals().getNodePos().getX(), 0.01);
        canvas.finishEditing();
    }

    @Test
    void inPlaceEditorCommitsIntoTheModel() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        EditHistory<AspectGraph> history = history(canvas);
        AspectVertexCell vertex = (AspectVertexCell) firstVertex(canvas);
        String before = vertex.getEditableLabels().toEditString();
        canvas.startEditing(vertex);
        assertTrue(canvas.isEditingLabel());
        var area = canvas.editorMode().getTextEditorInputMode().getTextArea();
        assertEquals(before, area.getText(), "the editor shows the editable labels");
        area.setText(before + "\nflag:edited");
        canvas.finishEditing();
        assertFalse(canvas.isEditingLabel());
        assertTrue(vertex.getEditableLabels().toEditString().contains("flag:edited"),
                   "editable labels after the commit");
        var graph = model.getGraph();
        assertNotNull(graph);
        assertTrue(graph.edgeSet().stream().anyMatch(e -> e.label().text().contains("edited")),
                   "graph rebuilt with the new label");
        assertTrue(history.canUndo());
        assertFalse(history.isDirtMinor());
        history.undo();
        assertEquals(before, vertex.getEditableLabels().toEditString(), "labels after undo");
    }

    /**
     * The in-place editor opens with the labels selected, so that typing replaces them,
     * and binds Shift+Enter and Ctrl+Enter to a line break. A key binding of the text
     * area consumes the key, so that Ctrl+Enter does not reach the Simulator's
     * accelerator of that key; the bindings are exercised through the action map, as a
     * key event dispatched to a component headlessly is rerouted to the (absent) focus
     * owner. The keys themselves are covered on screen, by {@code YFilesSimulatorTest}.
     */
    @Test
    void inPlaceEditorSelectsItsTextAndBreaksLinesOnCtrlEnter() throws IOException {
        var canvas = editorCanvas();
        AspectVertexCell vertex = labelledVertex(canvas);
        String before = vertex.getEditableLabels().toEditString();
        canvas.startEditing(vertex);
        flushEventThread();
        var area = canvas.editorMode().getTextEditorInputMode().getTextArea();
        assertEquals(before, area.getText(), "the editor shows the editable labels");
        assertEquals(0, area.getSelectionStart(), "start of the initial selection");
        assertEquals(before.length(), area.getSelectionEnd(), "end of the initial selection");
        area.setCaretPosition(before.length());
        InputMap keys = area.getInputMap(JComponent.WHEN_FOCUSED);
        for (int modifiers : List.of(InputEvent.CTRL_DOWN_MASK, InputEvent.SHIFT_DOWN_MASK)) {
            KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, modifiers);
            Object key = keys.get(stroke);
            assertNotNull(key, "no binding of " + stroke);
            Action action = area.getActionMap().get(key);
            assertNotNull(action, "no action for the binding " + key);
            action.actionPerformed(new ActionEvent(area, ActionEvent.ACTION_PERFORMED, ""));
            assertTrue(canvas.isEditingLabel(), stroke + " closed the editor");
        }
        assertEquals(before + "\n\n", area.getText(), "text after the line-break bindings");
        // the editor adjusts its size to the new text on the event thread: let it,
        // before the editor closes under that task
        flushEventThread();
        canvas.editorMode().getTextEditorInputMode().cancel();
        assertFalse(canvas.isEditingLabel(), "cancelling did not close the editor");
        assertEquals(before, vertex.getEditableLabels().toEditString(), "labels after cancelling");
    }

    /**
     * A double click on a vertex opens the in-place editor on that vertex, with its
     * labels selected, although its first click started an edge: the second click ends
     * that gesture as the editor rather than as a loop. The clicks go to yFiles' input
     * surface as mouse events, and the effect of each is awaited rather than flushed for:
     * the gesture starts, ends and opens the editor in tasks on the event thread, and a
     * task queued by such a task is not awaited by the flush that ran it.
     */
    @Test
    void doubleClickOnAVertexEditsIt() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        canvas.getComponent().setSize(800, 600);
        AspectVertexCell vertex = labelledVertex(canvas);
        String labels = vertex.getEditableLabels().toEditString();
        int cells = model.getCells().size();
        var createEdge = canvas.editorMode().getCreateEdgeInputMode();
        Point2D pos = vertex.getVisuals().getNodePos();
        var view = ((GraphComponent) canvas.getComponent())
            .toViewCoordinates(new PointD(pos.getX(), pos.getY()));
        int x = (int) Math.round(view.getX());
        int y = (int) Math.round(view.getY());
        SyntheticMouse.click(canvas.getComponent(), x, y, MouseEvent.BUTTON1, 0, 1);
        waitFor("the click to start an edge", createEdge::isCreationInProgress);
        assertSame(vertex, canvas.getSelectedCell(), "the click did not select the vertex");
        SyntheticMouse.click(canvas.getComponent(), x, y, MouseEvent.BUTTON1, 0, 2);
        waitFor("the double click to end the edge", () -> !createEdge.isCreationInProgress());
        var last = ((GraphComponent) canvas.getComponent()).getLastMouse2DEvent();
        assertNotNull(last, "no last mouse event");
        assertEquals(2, last.getClickCount(), "click count of the last mouse event");
        assertEquals(cells, model.getCells().size(), "the double click added a cell");
        waitFor("the double click to open the editor", canvas::isEditingLabel);
        var area = canvas.editorMode().getTextEditorInputMode().getTextArea();
        assertEquals(labels, area.getText(), "the editor shows the vertex's labels");
        assertEquals(0, area.getSelectionStart(), "start of the initial selection");
        assertEquals(labels.length(), area.getSelectionEnd(), "end of the initial selection");
        canvas.editorMode().getTextEditorInputMode().cancel();
        assertFalse(canvas.isEditingLabel(), "cancelling did not close the editor");
    }

    /**
     * A click on a vertex, then on empty canvas, adds a self-loop on that vertex, which
     * is withdrawn again when its editor closes without a label. The clicks go to
     * yFiles' input surface as mouse events, as in {@link #doubleClickOnAVertexEditsIt}.
     */
    @Test
    @AIGenerated("Claude Fable 5.1, 2026-10")
    void clickOnEmptyCanvasEndsAnEdgeAsALoop() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        canvas.getComponent().setSize(800, 600);
        AspectVertexCell vertex = labelledVertex(canvas);
        int cells = model.getCells().size();
        var createEdge = canvas.editorMode().getCreateEdgeInputMode();
        Point2D pos = vertex.getVisuals().getNodePos();
        var view = ((GraphComponent) canvas.getComponent())
            .toViewCoordinates(new PointD(pos.getX(), pos.getY()));
        int x = (int) Math.round(view.getX());
        int y = (int) Math.round(view.getY());
        SyntheticMouse.click(canvas.getComponent(), x, y, MouseEvent.BUTTON1, 0, 1);
        waitFor("the click to start an edge", createEdge::isCreationInProgress);
        // an empty spot of the canvas, well away from the vertex
        int emptyX = -1;
        int emptyY = -1;
        for (int ey = 560; ey >= 40 && emptyX < 0; ey -= 20) {
            for (int ex = 760; ex >= 40; ex -= 20) {
                if (canvas.getCellAt(ex, ey) == null && Math.abs(ex - x) + Math.abs(ey - y) > 100) {
                    emptyX = ex;
                    emptyY = ey;
                    break;
                }
            }
        }
        assertTrue(emptyX >= 0, "no empty spot on the canvas");
        // the world location of the click, taken before the view moves for the new edge
        var world = ((GraphComponent) canvas.getComponent())
            .toWorldCoordinates(new PointD(emptyX, emptyY));
        SyntheticMouse.click(canvas.getComponent(), emptyX, emptyY, MouseEvent.BUTTON1, 0, 1);
        waitFor("the click to end the edge", () -> !createEdge.isCreationInProgress());
        waitFor("the loop to be added", () -> model.getCells().size() == cells + 1);
        var loop = assertInstanceOf(AspectEdgeCell.class, canvas.getSelectedCell(),
                                    "the new loop is selected");
        assertSame(vertex, loop.getSourceVertex());
        assertSame(vertex, loop.getTargetVertex());
        // the loop bulges towards the click: its control point is the click's location
        List<Point2D> points = loop.getVisuals().getPoints();
        assertEquals(3, points.size(), "points of the loop");
        assertEquals(world.getX(), points.get(1).getX(), 0.5, "x of the loop's control point");
        assertEquals(world.getY(), points.get(1).getY(), 0.5, "y of the loop's control point");
        waitFor("the editor to open on the loop", canvas::isEditingLabel);
        canvas.finishEditing();
        assertEquals(cells, model.getCells().size(), "cells after withdrawing the loop");
    }

    /**
     * A click on a vertex, then on the border of another vertex, adds an edge between
     * them: the border is farther from the vertex's port than the snap distance of the
     * gesture, which does not force the pointer to snap to the port.
     */
    @Test
    @AIGenerated("Claude Fable 5.1, 2026-10")
    void clickOnTheBorderOfAVertexEndsAnEdgeThere() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        canvas.getComponent().setSize(800, 600);
        List<AspectVertexCell> vertices = vertices(canvas);
        int cells = model.getCells().size();
        var createEdge = canvas.editorMode().getCreateEdgeInputMode();
        var component = (GraphComponent) canvas.getComponent();
        // the source and target vertices, moved clear of the others (which overlap in
        // this graph), in view zoomed in so that the target's border is well beyond the
        // snap distance, in view pixels
        AspectVertexCell source = vertices.get(0);
        AspectVertexCell target = vertices.get(1);
        double maxX = 0;
        for (var vertex : vertices) {
            maxX = Math.max(maxX, nodeOf(vertex).getLayout().getMaxX());
        }
        move(canvas, source, maxX + 300, 100);
        move(canvas, target, maxX + 300, 300);
        flushEventThread();
        var layout = nodeOf(target).getLayout();
        component.setZoom((createEdge.getSnapDistance() + 10) / (layout.getWidth() / 2));
        component.setCenter(new PointD(maxX + 300, 200));
        flushEventThread();
        var sourceView = component.toViewCoordinates(new PointD(maxX + 300, 100));
        int sourceX = (int) Math.round(sourceView.getX());
        int sourceY = (int) Math.round(sourceView.getY());
        var border = component
            .toViewCoordinates(new PointD(layout.getMaxX() - 1, layout.getCenter().getY()));
        int x = (int) Math.round(border.getX());
        int y = (int) Math.round(border.getY());
        assertSame(source, canvas.getCellAt(sourceX, sourceY), "the source is not in view");
        assertSame(target, canvas.getCellAt(x, y), "the border spot is not on the target");
        SyntheticMouse.click(canvas.getComponent(), sourceX, sourceY, MouseEvent.BUTTON1, 0, 1);
        waitFor("the click to start an edge", createEdge::isCreationInProgress);
        SyntheticMouse.click(canvas.getComponent(), x, y, MouseEvent.BUTTON1, 0, 1);
        waitFor("the click to end the edge", () -> !createEdge.isCreationInProgress());
        waitFor("the edge to be added", () -> model.getCells().size() == cells + 1);
        var edge = assertInstanceOf(AspectEdgeCell.class, canvas.getSelectedCell(),
                                    "the new edge is selected");
        assertSame(source, edge.getSourceVertex());
        assertSame(target, edge.getTargetVertex());
        waitFor("the editor to open on the edge", canvas::isEditingLabel);
        canvas.finishEditing();
        assertEquals(cells, model.getCells().size(), "cells after withdrawing the edge");
    }

    @Test
    void edgeCreatorInsertsAnEdgeAndALoop() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        EditHistory<AspectGraph> history = history(canvas);
        List<AspectVertexCell> vertices = vertices(canvas);
        INode source = (INode) vertices.get(0).getItem();
        INode target = (INode) vertices.get(1).getItem();
        assertNotNull(source);
        assertNotNull(target);
        var creator = canvas.editorMode().getCreateEdgeInputMode().getEdgeCreator();
        IGraph graph = canvas.getShownGraph();
        int edges = graph.getEdges().size();
        IEdge edge = creator
            .createEdge(canvas.editorMode().getCreateEdgeInputMode().getInputModeContext(),
                        graph, new DefaultPortCandidate(source), new DefaultPortCandidate(target),
                        canvas.editorMode().getCreateEdgeInputMode().getDummyEdge());
        assertNotNull(edge, "no edge created");
        assertEquals(edges + 1, graph.getEdges().size(), "edges after creation");
        AspectEdgeCell cell = assertInstanceOf(AspectEdgeCell.class, YFilesCellStore.cellOf(edge));
        assertSame(vertices.get(0), cell.getSourceVertex());
        assertSame(vertices.get(1), cell.getTargetVertex());
        assertSame(cell, canvas.getSelectedCell(), "the new edge is selected");
        assertFalse(history.canUndo(), "the edge is pending until it is labelled");
        // the in-place editor opens on the event thread once the gesture is over
        flushEventThread();
        assertTrue(canvas.isEditingLabel(), "the editor opens on the new edge");
        canvas.editorMode().getTextEditorInputMode().getTextArea().setText("fresh");
        canvas.finishEditing();
        assertEquals("fresh", cell.getEditableLabels().toEditString());
        assertTrue(history.canUndo(), "the labelled edge is one edit");
        // no edge when the gesture ends on its own vertex, as in the JGraph editor
        assertNull(creator
            .createEdge(canvas.editorMode().getCreateEdgeInputMode().getInputModeContext(),
                        graph, new DefaultPortCandidate(source), new DefaultPortCandidate(source),
                        canvas.editorMode().getCreateEdgeInputMode().getDummyEdge()),
                   "edge created by a gesture ending on its own vertex");
        assertEquals(edges + 1, graph.getEdges().size(), "edges after ending on the own vertex");
        assertFalse(canvas.isEditingLabel(), "the editor opened without a double click");
        // a self-loop, when the gesture ends on empty canvas
        IEdge loop = creator
            .createEdge(canvas.editorMode().getCreateEdgeInputMode().getInputModeContext(),
                        graph, new DefaultPortCandidate(source), null,
                        canvas.editorMode().getCreateEdgeInputMode().getDummyEdge());
        assertNotNull(loop, "no loop created");
        assertSame(loop.getSourceNode(), loop.getTargetNode());
        assertTrue(loop.getBends().size() > 0, "the loop got a shape");
        assertEquals(edges + 2, graph.getEdges().size(), "edges with the pending loop");
        // an unlabelled loop is withdrawn when its editor closes
        flushEventThread();
        assertTrue(canvas.isEditingLabel(), "the editor opens on the loop");
        canvas.finishEditing();
        assertEquals(edges + 1, graph.getEdges().size(), "edges after withdrawing the loop");
        history.undo();
        assertEquals(edges, graph.getEdges().size(), "edges after undoing the labelled edge");
        assertFalse(cell.getSourceVertex() != null && model.getCells().contains(cell),
                    "removed edge still in the model");
        assertFalse(history.canUndo());
    }

    @Test
    void finishedMoveIsAMinorEdit() throws IOException {
        var canvas = editorCanvas();
        EditHistory<AspectGraph> history = history(canvas);
        AspectVertexCell vertex = (AspectVertexCell) firstVertex(canvas);
        INode node = (INode) vertex.getItem();
        assertNotNull(node);
        // what the move gesture does before it finishes: the item moves
        IGraph graph = canvas.getShownGraph();
        RectD layout = node.getLayout().toRectD();
        graph.setNodeLayout(node, layout.getTranslated(new PointD(50, 30)));
        List<com.yworks.yfiles.graph.IModelItem> moved = new ArrayList<>();
        moved.add(node);
        canvas.recordMove(moved);
        assertEquals(layout.getCenter().getX() + 50, vertex.getVisuals().getNodePos().getX(), 0.01);
        assertTrue(history.canUndo());
        assertTrue(history.isDirtMinor(), "a move is minor");
        // the incident edges follow
        var edges = vertex.getContext();
        assertTrue(edges.hasNext());
        ViewEdge<?> edge = edges.next();
        var points = edge.getVisuals().getPoints();
        var end = edge.getSourceVertex() == vertex
            ? points.get(0)
            : points.get(points.size() - 1);
        assertEquals(vertex.getVisuals().getNodePos(), end, "edge end follows the vertex");
        history.undo();
        assertEquals(layout.getCenter().getX(), vertex.getVisuals().getNodePos().getX(), 0.01);
        assertEquals(layout.getCenter().getX(), node.getLayout().getCenter().getX(), 0.01);
    }

    @Test
    void draggedEdgeEndReconnects() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        EditHistory<AspectGraph> history = history(canvas);
        IGraph graph = canvas.getShownGraph();
        // a binary edge and a third vertex
        AspectEdgeCell edge = null;
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate && !candidate.isLoop()) {
                edge = candidate;
                break;
            }
        }
        assertNotNull(edge, "no binary edge cell");
        var source = edge.getSourceVertex();
        var oldTarget = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(oldTarget);
        AspectVertexCell other = null;
        for (var vertex : vertices(canvas)) {
            if (vertex != source && vertex != oldTarget) {
                other = vertex;
                break;
            }
        }
        assertNotNull(other, "no third vertex");
        IEdge item = (IEdge) edge.getItem();
        INode otherNode = (INode) other.getItem();
        assertNotNull(item);
        assertNotNull(otherNode);
        // what the edge end handle does: the item gets another target port
        IPort port = otherNode.getPorts().size() > 0
            ? otherNode.getPorts().first()
            : graph.addPort(otherNode);
        graph.setEdgePorts(item, item.getSourcePort(), port);
        canvas.recordReconnection(item);
        assertSame(other, edge.getTargetVertex(), "cell target after the drag");
        var newItem = assertInstanceOf(IEdge.class, edge.getItem());
        assertSame(other, newItem.getTargetNode().getTag(), "item target after the drag");
        assertTrue(history.canUndo());
        assertFalse(history.isDirtMinor(), "a reconnection is major");
        history.undo();
        assertSame(oldTarget, edge.getTargetVertex(), "cell target after undo");
    }

    @Test
    void reconnectionKeepsTheSelection() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        IGraph graph = canvas.getShownGraph();
        AspectEdgeCell edge = null;
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate && !candidate.isLoop()) {
                edge = candidate;
                break;
            }
        }
        assertNotNull(edge, "no binary edge cell");
        canvas.select(List.of(edge));
        IEdge item = (IEdge) edge.getItem();
        assertNotNull(item);
        graph.setEdgePorts(item, item.getSourcePort(), item.getSourcePort());
        canvas.recordReconnection(item);
        assertEquals(List.of(edge), canvas.getSelection(), "selection after the drag");
    }

    @Test
    void endHandlesShowForASingleSelectedEdgeOnly() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        List<AspectEdgeCell> edges = new ArrayList<>();
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate && edges.size() < 2) {
                edges.add(candidate);
            }
        }
        assertEquals(2, edges.size(), "no two edge cells");
        IEdge first = (IEdge) edges.get(0).getItem();
        assertNotNull(first);
        canvas.select(List.of(edges.get(0)));
        assertNotNull(first.lookup(IEdgePortHandleProvider.class),
                      "end handles of the only selected edge");
        canvas.select(edges);
        assertNull(first.lookup(IEdgePortHandleProvider.class),
                   "end handles of one of two selected edges");
    }

    @Test
    void draggedEdgeEndOntoItsSourceMakesALoop() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        IGraph graph = canvas.getShownGraph();
        AspectEdgeCell edge = null;
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate && !candidate.isLoop()) {
                edge = candidate;
                break;
            }
        }
        assertNotNull(edge, "no binary edge cell");
        var source = edge.getSourceVertex();
        assertNotNull(source);
        IEdge item = (IEdge) edge.getItem();
        assertNotNull(item);
        // what the edge end handle does when dropped on the source node
        graph.setEdgePorts(item, item.getSourcePort(), item.getSourcePort());
        canvas.recordReconnection(item);
        assertTrue(edge.isLoop(), "cell is a loop after the drag");
        var newItem = assertInstanceOf(IEdge.class, edge.getItem());
        assertSame(newItem.getSourceNode(), newItem.getTargetNode(), "item is a loop");
        assertEquals(1, newItem.getBends().size(), "the loop has its default control point");
        assertEquals(3, edge.getVisuals().getPoints().size(), "the cell has the loop's points");
        assertTrue(graph.contains(newItem), "the loop is shown");
    }

    @Test
    void labelChangeReachesTheCanvasListeners() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        AspectVertexCell vertex = vertices(canvas).get(0);
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

    /**
     * A Bezier edge with two points is drawn as a quadratic, a cubic and a quadratic
     * curve: the renderer crops the path itself, since yFiles' cropping turned the last
     * curve into a straight line.
     */
    @Test
    void bezierEdgeKeepsAllItsCurves() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        AspectEdgeCell edge = binaryEdge(model);
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        Point2D from = source.getVisuals().getNodePos();
        Map<ViewCell<AspectGraph>,VisualMap> change = new LinkedHashMap<>();
        VisualMap targetVisuals = new VisualMap();
        targetVisuals.setNodePos(new Point2D.Double(from.getX() + 300, from.getY() + 40));
        change.put(target, targetVisuals);
        canvas.edit(change);
        Point2D to = target.getVisuals().getNodePos();
        VisualMap edgeVisuals = new VisualMap();
        edgeVisuals.setLineStyle(LineStyle.BEZIER);
        edgeVisuals
            .put(VisualKey.POINTS,
                 List
                     .of(new Point2D.Double(from.getX(), from.getY()),
                         new Point2D.Double(from.getX() + 100, from.getY() - 80),
                         new Point2D.Double(from.getX() + 200, from.getY() + 120),
                         new Point2D.Double(to.getX(), to.getY())));
        change = new LinkedHashMap<>();
        change.put(edge, edgeVisuals);
        canvas.edit(change);
        IEdge item = (IEdge) edge.getItem();
        assertNotNull(item);
        IEdgeStyle style = item.getStyle();
        Path2D shape = new Path2D.Double();
        style.getRenderer().getPathGeometry(item, style).getPath().updatePath(shape, null);
        List<Integer> curves = new ArrayList<>();
        var iter = shape.getPathIterator(null);
        double[] coords = new double[6];
        while (!iter.isDone()) {
            int type = iter.currentSegment(coords);
            if (type == PathIterator.SEG_QUADTO || type == PathIterator.SEG_CUBICTO) {
                curves.add(type);
            }
            iter.next();
        }
        assertEquals(List.of(PathIterator.SEG_QUADTO, PathIterator.SEG_CUBICTO,
                             PathIterator.SEG_QUADTO),
                     curves, "curve segments of the path");
    }

    /**
     * A dragged edge label lands as an edit of the label position: what the label move
     * mode does, without the mouse.
     */
    @Test
    void draggedLabelMovesTheLabelPosition() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        EditHistory<AspectGraph> history = history(canvas);
        AspectEdgeCell edge = binaryEdge(model);
        IEdge item = (IEdge) edge.getItem();
        assertNotNull(item);
        ILabel label = null;
        for (ILabel candidate : item.getLabels()) {
            if (YFilesCellStore.isMainEdgeLabel(candidate)) {
                label = candidate;
            }
        }
        assertNotNull(label, "the edge has a main label");
        Point2D before = edge.getVisuals().getLabelPos();
        // what the label move mode does: the label gets a new parameter
        canvas
            .getShownGraph()
            .setLabelLayoutParameter(label,
                                     FreeEdgeLabelModel.INSTANCE.createEdgeAnchored(0.25, 20));
        PointD dropped = label.getLayout().getCenter();
        canvas.setMovedLabel(label);
        canvas.recordLabelMove();
        Point2D after = edge.getVisuals().getLabelPos();
        assertNotEquals(before, after, "label position after the drag");
        assertTrue(history.canUndo());
        assertTrue(history.isDirtMinor(), "a label move is minor");
        // the label is placed back where it was dropped
        PointD placed = label.getLayout().getCenter();
        assertEquals(dropped.getX(), placed.getX(), 0.5, "x of the placed label");
        assertEquals(dropped.getY(), placed.getY(), 0.5, "y of the placed label");
        history.undo();
        assertEquals(before, edge.getVisuals().getLabelPos(), "label position after undo");
    }

    /**
     * The label tree of the editor follows the edits: a label without remaining
     * occurrences disappears, the label of a newly created and then labelled edge
     * appears. The label used is an existing one: a label new to the grammar enters
     * its implicit type graph only when the graph is saved.
     */
    @Test
    void labelTreeFollowsTheEdits() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        TypeTree tree = new TypeTree(null, canvas, false);
        // the tree takes its model from the canvas when the canvas gets one; here it had one
        tree.updateModel();
        String label = "moored";
        assertTrue(treeLabels(tree).contains(label), "label in the tree before the edits");
        AspectEdgeCell edge = null;
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate
                && candidate.getEditableLabels().toEditString().equals(label)) {
                edge = candidate;
                break;
            }
        }
        assertNotNull(edge, "no edge cell labelled " + label);
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        model.remove(List.of(edge));
        assertFalse(treeLabels(tree).contains(label), "label without occurrences gone");
        AspectEdgeCell fresh = model.newEdge(null);
        fresh.getEditableLabels().add("");
        fresh
            .putVisual(VisualKey.POINTS,
                       List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10)));
        model
            .insertPending(List.of(), List.of(fresh),
                           List.of(new Connection<>(fresh, source, target)));
        var labels = new EditableLabels();
        labels.load(label);
        model.changeLabels(fresh, labels);
        assertTrue(treeLabels(tree).contains(label), "label of the new edge in the tree");
    }

    /** Collects the label texts shown in a tree. */
    private static Set<String> treeLabels(TypeTree tree) {
        Set<String> result = new HashSet<>();
        TreeModel treeModel = tree.getModel();
        collectLabels(treeModel, treeModel.getRoot(), result);
        return result;
    }

    /** Collects the label texts of a tree node and its descendants into a given set. */
    private static void collectLabels(TreeModel model, Object node, Set<String> result) {
        if (node instanceof LabelTree.LabelTreeNode labelNode) {
            result.add(labelNode.getEntry().getContent().text());
        }
        for (int i = 0; i < model.getChildCount(node); i++) {
            collectLabels(model, model.getChild(node, i), result);
        }
    }

    /**
     * A Manhattan edge is hit along its whole drawn path, not only along the straight
     * polyline through its points.
     */
    @Test
    void manhattanEdgeIsHitAlongItsPath() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        AspectEdgeCell edge = binaryEdge(model);
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        Point2D from = source.getVisuals().getNodePos();
        Map<ViewCell<AspectGraph>,VisualMap> change = new LinkedHashMap<>();
        VisualMap targetVisuals = new VisualMap();
        targetVisuals.setNodePos(new Point2D.Double(from.getX() + 300, from.getY() + 200));
        change.put(target, targetVisuals);
        canvas.edit(change);
        Point2D to = target.getVisuals().getNodePos();
        VisualMap edgeVisuals = new VisualMap();
        edgeVisuals.setLineStyle(LineStyle.MANHATTAN);
        Point2D bend = new Point2D.Double(from.getX() + 150, from.getY() + 100);
        edgeVisuals
            .put(VisualKey.POINTS,
                 List
                     .of(new Point2D.Double(from.getX(), from.getY()), bend,
                         new Point2D.Double(to.getX(), to.getY())));
        change = new LinkedHashMap<>();
        change.put(edge, edgeVisuals);
        canvas.edit(change);
        IEdge item = (IEdge) edge.getItem();
        assertNotNull(item);
        IEdgeStyle style = item.getStyle();
        var context = canvas.getGraphComponent().getInputModeContext();
        var hitTestable = style.getRenderer().getHitTestable(item, style);
        // the drawn path: horizontal and vertical pieces between the points
        List<Point2D> path = pathOf(item);
        assertTrue(path.size() >= 4, "path of at least three segments: " + path);
        PointD onFirstPart = midpoint(path.get(0), path.get(1));
        PointD onLastPart = midpoint(path.get(path.size() - 2), path.get(path.size() - 1));
        assertTrue(hitTestable.isHit(context, onFirstPart), "hit on the first part of the path");
        assertTrue(hitTestable.isHit(context, onLastPart), "hit on the last part of the path");
        // the straight line between the bend and the target is not the drawn path
        PointD offPath = new PointD((bend.getX() + to.getX()) / 2 - 20, (bend.getY() + to.getY()) / 2);
        assertFalse(hitTestable.isHit(context, offPath), "hit beside the drawn path");
    }

    /** Returns the point halfway between two given points. */
    private static PointD midpoint(Point2D p, Point2D q) {
        return new PointD((p.getX() + q.getX()) / 2, (p.getY() + q.getY()) / 2);
    }

    /**
     * A Manhattan edge whose last point lies level with the target enters the target
     * sideways, with a horizontal last segment, as in JGraph; and the path of an edge
     * ends before the node border, leaving room for the arrow.
     */
    @Test
    void manhattanEdgeEntersLevelTargetSideways() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        AspectEdgeCell edge = binaryEdge(model);
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        Point2D from = source.getVisuals().getNodePos();
        Map<ViewCell<AspectGraph>,VisualMap> change = new LinkedHashMap<>();
        VisualMap targetVisuals = new VisualMap();
        targetVisuals.setNodePos(new Point2D.Double(from.getX() + 300, from.getY() + 200));
        change.put(target, targetVisuals);
        canvas.edit(change);
        Point2D to = target.getVisuals().getNodePos();
        VisualMap edgeVisuals = new VisualMap();
        edgeVisuals.setLineStyle(LineStyle.MANHATTAN);
        // the bend lies level with the target, a little above its centre
        Point2D bend = new Point2D.Double(from.getX() + 150, to.getY() - 2);
        edgeVisuals
            .put(VisualKey.POINTS,
                 List
                     .of(new Point2D.Double(from.getX(), from.getY()), bend,
                         new Point2D.Double(to.getX(), to.getY())));
        change = new LinkedHashMap<>();
        change.put(edge, edgeVisuals);
        canvas.edit(change);
        IEdge item = (IEdge) edge.getItem();
        assertNotNull(item);
        List<Point2D> path = pathOf(item);
        Point2D last = path.get(path.size() - 1);
        Point2D beforeLast = path.get(path.size() - 2);
        assertEquals(beforeLast.getY(), last.getY(), 0.01, "the last segment is horizontal");
        // the path ends outside the target node, at most an arrow's length before it
        INode targetNode = (INode) target.getItem();
        assertNotNull(targetNode);
        RectD layout = targetNode.getLayout().toRectD();
        assertFalse(layout.contains(new PointD(last.getX(), last.getY())),
                    "the path ends outside the target node");
        double gap = layout.getX() - last.getX();
        assertTrue(gap > 0 && gap < 15, "gap to the target border: " + gap);
    }

    @Test
    void stackedVerticesAreJoinedVertically() throws IOException {
        var canvas = editorCanvas();
        AspectEdgeCell edge = loneEdge(model(canvas));
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        // the target above the source, its centre slightly to the right
        move(canvas, source, 100, 200);
        move(canvas, target, 113, 40);
        List<Point2D> path = pathOf(assertInstanceOf(IEdge.class, YFilesCellStore.itemOf(edge)));
        Point2D start = path.get(0);
        Point2D end = path.get(path.size() - 1);
        assertEquals(start.getX(), end.getX(), 0.01, "vertical edge: " + path);
        RectD sourceBounds = nodeOf(source).getLayout().toRectD();
        RectD targetBounds = nodeOf(target).getLayout().toRectD();
        assertTrue(start.getX() > sourceBounds.getMinX() && start.getX() < sourceBounds.getMaxX(),
                   "start within the source: " + start + " in " + sourceBounds);
        assertTrue(end.getX() > targetBounds.getMinX() && end.getX() < targetBounds.getMaxX(),
                   "end within the target: " + end + " in " + targetBounds);
        assertTrue(start.getY() <= sourceBounds.getMinY() + 0.01, "leaves the source at the top");
        assertTrue(end.getY() >= targetBounds.getMaxY(), "enters the target from below");
    }

    @Test
    void neighbouringVerticesAreJoinedHorizontally() throws IOException {
        var canvas = editorCanvas();
        AspectEdgeCell edge = loneEdge(model(canvas));
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        // the target to the right of the source, its centre slightly lower
        move(canvas, source, 100, 100);
        move(canvas, target, 250, 103);
        List<Point2D> path = pathOf(assertInstanceOf(IEdge.class, YFilesCellStore.itemOf(edge)));
        Point2D start = path.get(0);
        Point2D end = path.get(path.size() - 1);
        assertEquals(start.getY(), end.getY(), 0.01, "horizontal edge: " + path);
        RectD sourceBounds = nodeOf(source).getLayout().toRectD();
        RectD targetBounds = nodeOf(target).getLayout().toRectD();
        assertTrue(start.getX() >= sourceBounds.getMaxX() - 0.01, "leaves the source at the right");
        assertTrue(end.getX() <= targetBounds.getMinX(), "enters the target from the left");
    }

    /**
     * A drag with Control held copies the dragged vertices and the edges between
     * them to where they were dragged, as one edit, and puts the originals back.
     */
    @Test
    void controlDragCopiesTheDraggedCells() throws IOException {
        var canvas = editorCanvas();
        AspectGraphViewModel model = model(canvas);
        EditHistory<AspectGraph> history = history(canvas);
        AspectEdgeCell edge = loneEdge(model);
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        int cells = model.getCells().size();
        Point2D sourcePos = source.getVisuals().getNodePos();
        INode sourceNode = nodeOf(source);
        INode targetNode = nodeOf(target);
        // what the end of the drag does, its preview having been dragged by (30, 40)
        assertTrue(canvas.copyMoved(List.of(sourceNode, targetNode), 30, 40));
        // the originals are where they were
        assertEquals(sourcePos, source.getVisuals().getNodePos());
        assertEquals(sourcePos.getX(), sourceNode.getLayout().getCenter().getX(), 0.01);
        // the copies are selected, at the dragged positions, with the edge between them
        assertEquals(cells + 3, model.getCells().size(), "cells after the copy");
        List<ViewCell<AspectGraph>> copies = canvas.getSelection();
        assertEquals(3, copies.size(), "the copies are selected: " + copies);
        boolean sourceCopied = false;
        for (var copy : copies) {
            if (copy instanceof AspectVertexCell vertex
                && vertex.getVisuals().getNodePos().getX() == sourcePos.getX() + 30
                && vertex.getVisuals().getNodePos().getY() == sourcePos.getY() + 40) {
                sourceCopied = true;
                assertEquals(source.getEditableLabels().toEditString(),
                             vertex.getEditableLabels().toEditString());
                // the copy shows its label right away, not after the next edit
                assertEquals(labelText(sourceNode), labelText(nodeOf(vertex)),
                             "label of the copied node");
            }
        }
        assertTrue(sourceCopied, "the source vertex was copied to the dragged position");
        assertTrue(history.canUndo());
        history.undo();
        assertEquals(cells, model.getCells().size(), "one undo removes the copies");
        assertFalse(history.canUndo());
        // a drag of an edge alone copies nothing
        assertFalse(canvas.copyMoved(List.of(assertInstanceOf(IEdge.class, YFilesCellStore.itemOf(edge))), 30, 40));
    }


    /** Returns a vertex cell of a canvas with editable labels, failing if there is none. */
    private static AspectVertexCell labelledVertex(YFilesAspectEditorCanvas canvas) {
        for (var candidate : vertices(canvas)) {
            if (!candidate.getEditableLabels().toEditString().isEmpty()) {
                return candidate;
            }
        }
        throw new AssertionError("no labelled vertex");
    }

    /** Waits for the tasks queued on the event thread to run. */
    private static void flushEventThread() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                // nothing: the wait is the point
            });
        } catch (InterruptedException | InvocationTargetException exc) {
            throw new AssertionError(exc);
        }
    }

    /**
     * Waits for a condition that the event thread brings about, flushing that thread
     * before every test of the condition. A single flush is not enough for a condition
     * that a task queues another task for, as the gestures of the canvas do: the flush
     * that runs the first task does not await the second. The pause between the tests is
     * short because a gesture of two clicks is timed: the second click counts as a double
     * click only within the desktop's multi-click interval of the first.
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    private static void waitFor(String description, BooleanSupplier condition) {
        long deadline = System.currentTimeMillis() + WAIT_TIMEOUT;
        while (true) {
            flushEventThread();
            if (condition.getAsBoolean()) {
                return;
            }
            if (System.currentTimeMillis() > deadline) {
                fail("timed out waiting for " + description);
            }
            try {
                Thread.sleep(WAIT_PAUSE);
            } catch (InterruptedException exc) {
                throw new AssertionError(exc);
            }
        }
    }

    /** Time, in milliseconds, that {@link #waitFor} allows a condition. */
    private static final long WAIT_TIMEOUT = 5000;
    /** Time, in milliseconds, between two tests of a condition in {@link #waitFor}. */
    private static final long WAIT_PAUSE = 5;

    /** Moves a vertex to a given centre. */
    private static void move(YFilesAspectEditorCanvas canvas, ViewVertex<AspectGraph> vertex,
                             double x, double y) {
        VisualMap visuals = new VisualMap();
        visuals.put(VisualKey.NODE_POS, new Point2D.Double(x, y));
        canvas.edit(Map.of(vertex, visuals));
    }

    /** Returns the node showing a given vertex cell, failing if there is none. */
    private static INode nodeOf(ViewVertex<AspectGraph> vertex) {
        return assertInstanceOf(INode.class, YFilesCellStore.itemOf(vertex));
    }

    /** Returns the text of the label of a node, or {@code null} if it has none. */
    private static @Nullable String labelText(INode node) {
        String result = null;
        for (ILabel label : node.getLabels()) {
            result = label.getText();
        }
        return result;
    }

    /** Returns a binary edge cell without parallels. */
    private static AspectEdgeCell loneEdge(AspectGraphViewModel model) {
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate && !candidate.isLoop()
                && ParallelEdges.rank(candidate) == 0) {
                return candidate;
            }
        }
        throw new AssertionError("no lone binary edge cell");
    }

    /** Returns the points of the segments of an edge's drawn path. */
    private static List<Point2D> pathOf(IEdge item) {
        IEdgeStyle style = item.getStyle();
        Path2D shape = new Path2D.Double();
        style.getRenderer().getPathGeometry(item, style).getPath().updatePath(shape, null);
        List<Point2D> result = new ArrayList<>();
        var iter = shape.getPathIterator(null);
        double[] coords = new double[6];
        while (!iter.isDone()) {
            int type = iter.currentSegment(coords);
            int last = type == PathIterator.SEG_CUBICTO
                ? 4
                : type == PathIterator.SEG_QUADTO
                    ? 2
                    : 0;
            if (type != PathIterator.SEG_CLOSE) {
                result.add(new Point2D.Double(coords[last], coords[last + 1]));
            }
            iter.next();
        }
        return result;
    }

    /** Returns the first binary edge cell of a model. */
    private static AspectEdgeCell binaryEdge(AspectGraphViewModel model) {
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate && !candidate.isLoop()) {
                return candidate;
            }
        }
        throw new AssertionError("no binary edge cell");
    }

    /** Creates an editing canvas on the fixture's start graph. */
    private static YFilesAspectEditorCanvas editorCanvas() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assertNotNull(startGraph);
        var controller
            = new AspectGraphViewController(null, DisplayKind.HOST.getGraphRole(), true);
        controller.setGrammar(grammar);
        var canvas = assertInstanceOf(YFilesAspectEditorCanvas.class, controller.getCanvas());
        AspectGraphViewModel model = canvas.newViewModel();
        model.setBeingEdited(true);
        model.loadGraph(startGraph);
        canvas.setViewModel(model);
        return canvas;
    }

    /** Returns the view model of a canvas, failing if it has none. */
    private static AspectGraphViewModel model(YFilesAspectEditorCanvas canvas) {
        AspectGraphViewModel result = canvas.getViewModel();
        assertNotNull(result);
        return result;
    }

    /** Returns the edit history of a canvas' view model, failing if it has none. */
    private static EditHistory<AspectGraph> history(YFilesAspectEditorCanvas canvas) {
        var result = model(canvas).getEditHistory();
        assertNotNull(result);
        return result;
    }

    /** Returns the first of the vertex cells of a canvas (see {@link #vertices}). */
    private static ViewVertex<AspectGraph> firstVertex(YFilesAspectEditorCanvas canvas) {
        return vertices(canvas).get(0);
    }

    /** Returns the vertex cells of a canvas, failing if there are fewer than three. */
    private static List<AspectVertexCell> vertices(YFilesAspectEditorCanvas canvas) {
        List<AspectVertexCell> result = new ArrayList<>();
        for (var cell : canvas.getCells()) {
            if (cell instanceof AspectVertexCell vertex) {
                result.add(vertex);
            }
        }
        assertTrue(result.size() >= 3, "fewer than three vertices");
        return result;
    }
}
