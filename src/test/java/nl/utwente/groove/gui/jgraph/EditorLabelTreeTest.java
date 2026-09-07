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
 */
package nl.utwente.groove.gui.jgraph;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.tree.TreeModel;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.gui.view.CellStore.Connection;
import nl.utwente.groove.gui.look.VisualKey;
import java.util.List;
import java.awt.geom.Point2D;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.tree.LabelTree;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.EditableLabels;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;

/**
 * Checks that the label tree of an editor follows the edits: the label of a newly
 * created and then labelled edge appears in the tree, and a label without any remaining
 * occurrence disappears from it. The grammar has no type graph; its implicit type graph
 * holds the labels of the saved resources, so the label used is an existing one (a
 * label that is new to the grammar enters the type graph only when the graph is saved).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class EditorLabelTreeTest {
    /** Grammar whose start graph is edited. */
    private static final String GRAMMAR = "junit/samples/ferryman.gps";

    @Test
    void labelTreeFollowsTheEditedLabels() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assertNotNull(startGraph);
        var controller = new AspectGraphViewController(null, DisplayKind.HOST, true);
        controller.setGrammar(grammar);
        AspectJGraph canvas = (AspectJGraph) controller.getCanvas();
        AspectGraphViewModel model = canvas.newViewModel();
        model.setBeingEdited(true);
        model.loadGraph(startGraph);
        canvas.setViewModel(model);
        new JScrollPane(canvas);
        TypeTree tree = new TypeTree(canvas, false);
        controller.setLabelTree(tree);
        // the tree takes its model from the canvas when the canvas gets one; here it had one
        tree.updateModel();
        assertTrue(labels(tree).contains(LABEL), "label in the tree before the edits");
        // the label occurs once: on the edge that is removed
        AspectEdgeCell edge = null;
        for (var cell : model.getCells()) {
            if (cell instanceof AspectEdgeCell candidate
                && candidate.getEditableLabels().toEditString().equals(LABEL)) {
                edge = candidate;
                break;
            }
        }
        assertNotNull(edge, "no edge cell labelled " + LABEL);
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        model.remove(List.of(edge));
        assertFalse(labels(tree).contains(LABEL), "label without occurrences gone from the tree");
        // a new edge, labelled in the in-place editor afterwards, as the gesture does it
        AspectEdgeCell fresh = model.newEdge(null);
        fresh.getEditableLabels().add("");
        fresh
            .putVisual(VisualKey.POINTS,
                       List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 10)));
        model.insert(List.of(), List.of(fresh), List.of(new Connection<>(fresh, source, target)));
        assertFalse(labels(tree).contains(LABEL), "label in the tree before the new edge is labelled");
        var labels = new EditableLabels();
        labels.load(LABEL);
        model.changeLabels(fresh, labels);
        assertTrue(labels(tree).contains(LABEL), "label of the new edge in the tree");
    }

    /** A label occurring on exactly one edge of the start graph. */
    private static final String LABEL = "moored";

    /** Collects the label texts shown in a tree. */
    private static Set<String> labels(TypeTree tree) {
        Set<String> result = new HashSet<>();
        TreeModel treeModel = tree.getModel();
        collect(treeModel, treeModel.getRoot(), result);
        return result;
    }

    private static void collect(TreeModel model, Object node, Set<String> result) {
        if (node instanceof LabelTree.LabelTreeNode labelNode) {
            result.add(labelNode.getEntry().getContent().text());
        }
        for (int i = 0; i < model.getChildCount(node); i++) {
            collect(model, model.getChild(node, i), result);
        }
    }
}
