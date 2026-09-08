/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.jgraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.EditHistory;
import nl.utwente.groove.gui.view.GraphClipboard;
import nl.utwente.groove.gui.view.GraphFragment;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests cut, copy and paste in the editor, through the backend-neutral clipboard:
 * the fragment built from a selection, and the paste as one edit of fresh cells.
 * Headless, the clipboard is the JVM-local one of {@link GraphClipboard}.
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class EditorClipboardTest {
    /** Grammar whose start graph is edited. */
    private static final String GRAMMAR = "junit/samples/ferryman.gps";

    @Test
    void fragmentHoldsTheSelectedVerticesAndTheEdgesBetweenThem() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        AspectEdgeCell edge = loneEdge(model);
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        // the source alone: no edge
        GraphFragment fragment = GraphFragment.of(List.of(source, edge));
        assertEquals(1, fragment.vertices().size());
        assertEquals(0, fragment.edges().size(), "an edge needs both ends in the fragment");
        assertEquals(source.getEditableLabels().toEditString(),
                     fragment.vertices().get(0).labels().toEditString());
        // both ends: the edge too, with its labels and its layout
        fragment = GraphFragment.of(List.of(source, target, edge));
        assertEquals(2, fragment.vertices().size());
        assertEquals(1, fragment.edges().size());
        var fragmentEdge = fragment.edges().get(0);
        assertEquals(0, fragmentEdge.source());
        assertEquals(1, fragmentEdge.target());
        assertEquals(edge.getEditableLabels().toEditString(),
                     fragmentEdge.labels().toEditString());
        assertEquals(edge.getVisuals().getLineStyle(), fragmentEdge.lineStyle());
        assertFalse(fragment.toText().isEmpty());
    }

    @Test
    void pasteInsertsFreshCellsAsOneEdit() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        EditHistory<AspectGraph> history = model.getEditHistory();
        assertNotNull(history);
        AspectEdgeCell edge = loneEdge(model);
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        int cellCount = model.getCells().size();
        var graph = model.getGraph();
        assertNotNull(graph);
        int nodeCount = graph.nodeCount();
        int edgeCount = graph.edgeCount();
        Set<Integer> numbers = new HashSet<>();
        for (var vertex : vertices(model)) {
            numbers.add(vertex.getNumber());
        }
        canvas.setSelectionCells(new Object[] {JCell.of(source), JCell.of(target), JCell.of(edge)});
        assertTrue(GraphClipboard.copy(canvas));
        assertTrue(GraphClipboard.hasFragment());
        assertFalse(history.canUndo(), "copying is no edit");
        assertTrue(GraphClipboard.paste(canvas));
        assertEquals(cellCount + 3, model.getCells().size(), "cells after the paste");
        graph = model.getGraph();
        assertNotNull(graph);
        assertEquals(nodeCount + 2, graph.nodeCount(), "nodes after the paste");
        assertTrue(graph.edgeCount() > edgeCount, "edges after the paste");
        // the pasted cells are selected, fresh, labelled as the originals, and offset
        List<ViewCell<AspectGraph>> pasted = canvas.getSelection();
        assertEquals(3, pasted.size(), "the pasted cells are selected: " + pasted);
        int pastedEdges = 0;
        List<Point2D> positions = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (var cell : pasted) {
            if (cell instanceof AspectVertexCell vertex) {
                assertFalse(numbers.contains(vertex.getNumber()), "fresh node number");
                assertFalse(vertex == source || vertex == target, "fresh cell");
                positions.add(vertex.getVisuals().getNodePos());
                labels.add(vertex.getEditableLabels().toEditString());
            } else if (cell instanceof AspectEdgeCell pastedEdge) {
                pastedEdges++;
                assertEquals(edge.getEditableLabels().toEditString(),
                             pastedEdge.getEditableLabels().toEditString());
                assertTrue(pasted.contains(pastedEdge.getSourceVertex()),
                           "the pasted edge connects pasted vertices");
                assertTrue(pasted.contains(pastedEdge.getTargetVertex()));
            }
        }
        assertEquals(1, pastedEdges);
        for (var original : List.of(source, target)) {
            assertTrue(labels.contains(original.getEditableLabels().toEditString()),
                       "labels of " + original + " pasted");
            Point2D pos = original.getVisuals().getNodePos();
            Point2D offset = new Point2D.Double(pos.getX() + GraphClipboard.PASTE_OFFSET,
                pos.getY() + GraphClipboard.PASTE_OFFSET);
            assertTrue(positions.contains(offset), "pasted at " + offset + ": " + positions);
        }
        // one undo takes the whole paste back
        assertTrue(history.canUndo());
        history.undo();
        assertEquals(cellCount, model.getCells().size(), "cells after undoing the paste");
        assertFalse(history.canUndo());
    }

    @Test
    void cutRemovesTheSelectionAndKeepsItForPasting() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectGraphViewModel model = canvas.getNonNullModel().getViewModel();
        EditHistory<AspectGraph> history = model.getEditHistory();
        assertNotNull(history);
        AspectVertexCell vertex = vertices(model).get(0);
        String labels = vertex.getEditableLabels().toEditString();
        int cellCount = model.getCells().size();
        canvas.setSelectionCell(JCell.of(vertex));
        assertTrue(GraphClipboard.cut(canvas));
        assertFalse(model.getCells().contains(vertex), "the cut vertex is gone");
        assertTrue(history.canUndo(), "cutting is an edit");
        assertTrue(GraphClipboard.paste(canvas));
        List<ViewCell<AspectGraph>> pasted = canvas.getSelection();
        assertEquals(1, pasted.size());
        AspectViewCell copy = (AspectViewCell) pasted.get(0);
        assertEquals(labels, copy.getEditableLabels().toEditString(), "the cut labels are back");
        history.undo();
        history.undo();
        assertEquals(cellCount, model.getCells().size(), "cells after undoing cut and paste");
        assertTrue(model.getCells().contains(vertex));
    }

    @Test
    void nothingToPasteWithoutASelection() throws IOException {
        AspectJGraph canvas = editorCanvas();
        canvas.clearSelection();
        assertFalse(GraphClipboard.copy(canvas), "an empty selection is not copied");
        assertFalse(GraphClipboard.cut(canvas));
    }

    /** Returns a binary edge cell whose ends have no other edge between them. */
    private static AspectEdgeCell loneEdge(AspectGraphViewModel model) {
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate && !candidate.isLoop()) {
                return candidate;
            }
        }
        throw new AssertionError("no binary edge cell");
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

    /** Creates an editing canvas on the fixture's start graph. */
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
}
