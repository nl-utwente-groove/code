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

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.undo.UndoableEdit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphUndoManager;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests the undo history of the graph editor on the JGraph backend, for the
 * edit sequence that adds an edge and gives it a label: the selection of the
 * new edge in between must not post an undoable edit of its own, since undoing
 * and redoing such an edit reorders the history and loses the label edit.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class EditorUndoTest {
    /** Grammar whose start graph is edited. */
    private static final String GRAMMAR = "junit/samples/ferryman.gps";

    @Test
    void addEdgeAndEditLabel() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assert startGraph != null; // the fixture grammar has a start graph
        var controller = new AspectGraphViewController(null, DisplayKind.HOST, true);
        controller.setGrammar(grammar);
        AspectJGraph canvas = (AspectJGraph) controller.getCanvas();
        AspectGraphViewModel model = canvas.newViewModel();
        model.loadGraph(startGraph);
        canvas.setViewModel(model);
        // selecting a cell scrolls it into view, which needs a viewport
        new JScrollPane(canvas);
        AspectJModel jModel = canvas.getNonNullModel();
        List<UndoableEdit> edits = new ArrayList<>();
        jModel.addUndoableEditListener(e -> edits.add(e.getEdit()));
        GraphUndoManager undoManager = new GraphUndoManager();
        jModel.addUndoableEditListener(undoManager);
        List<AspectVertexCell> vertices = new ArrayList<>();
        for (var cell : model.getCells()) {
            if (cell instanceof AspectVertexCell vertex) {
                vertices.add(vertex);
            }
        }
        // what AspectJGraph.addEdge does, without the mouse
        AspectEdgeCell edge = model.newEdge(null);
        edge.getEditableLabels().add("");
        var jEdge = new JEdge<>(edge);
        ConnectionSet connections = new ConnectionSet();
        connections.connect(jEdge, ((JVertex<?>) JCell.of(vertices.get(0))).getPort(), true);
        connections.connect(jEdge, ((JVertex<?>) JCell.of(vertices.get(1))).getPort(), false);
        edge
            .putVisual(VisualKey.POINTS,
                       List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10)));
        jModel.insert(new Object[] {jEdge}, null, connections, null, null);
        canvas.setSelectionCell(jEdge);
        // what completing the in-place editor does
        canvas.getGraphLayoutCache().valueForCellChanged(jEdge, "next");
        assertEquals(2, edits.size(), "the selection of a visible cell posted an edit");
        assertState(jModel, jEdge, true, "next");
        undoManager.undo();
        assertState(jModel, jEdge, true, "");
        undoManager.undo();
        assertState(jModel, jEdge, false, "");
        assertFalse(undoManager.canUndo());
        undoManager.redo();
        assertState(jModel, jEdge, true, "");
        undoManager.redo();
        assertState(jModel, jEdge, true, "next");
        assertFalse(undoManager.canRedo());
    }

    /** Asserts the presence and the label of the edge in the model and in the graph. */
    private static void assertState(AspectJModel jModel, JEdge<AspectGraph> jEdge,
                                    boolean present, String label) {
        assertEquals(present, jModel.getRoots().contains(jEdge), "edge presence");
        var edge = jEdge.getViewCell();
        assertEquals(present, edge.getSourceVertex() != null, "edge connection");
        List<String> labels = new ArrayList<>();
        for (String text : ((AspectEdgeCell) edge).getEditableLabels()) {
            labels.add(text);
        }
        assertEquals(List.of(label), labels, "editable labels");
        if (present) {
            assertEquals(1, edge.getEdges().size(), "graph edges");
            assertEquals(label, edge.getEdges().iterator().next().label().text(), "graph label");
        }
    }
}
