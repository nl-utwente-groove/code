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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.gui.tree.TypeTree.TypeTreeNode;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Regression test for the order of the entries in the label tree of a graph
 * view: node types first, then flags, then binary edge labels, each group
 * alphabetically. In an untyped grammar all labels share the implicit node
 * type, and the tree used to list them in the iteration order of the type
 * graph's edge set. In a typed grammar the edge types are grouped under their
 * source node type, after its subtypes.
 * <p>
 * The graphs are rendered into a headless canvas of the JGraph backend, the way the
 * {@code Imager} does it, and the label tree is built from that.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class LabelTreeOrderTest {
    /** Untyped grammar whose start graph has binary labels and flags whose
     * names interleave alphabetically. */
    private static final String UNTYPED_GRAMMAR = "junit/samples/mergers.gps";
    /** Typed grammar whose type graph has a node type with subtypes, a
     * binary edge label and flags. */
    private static final String TYPED_GRAMMAR = "junit/samples/recipes.gps";
    /** Name of the type graph in {@link #TYPED_GRAMMAR}. */
    private static final String TYPE_GRAPH = "type";

    /** In an untyped host graph, the implicit node type comes first, then the
     * flags, then the binary labels, both alphabetically. */
    @Test
    void untypedHostGraphOrder() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(UNTYPED_GRAMMAR);
        AspectGraph graph = grammar.getStartGraphModel().getSource();
        assert graph != null; // the grammar has a single start graph
        TypeTree tree = buildTree(grammar, DisplayKind.HOST, graph);
        List<TypeTreeNode> rows = children(tree.getTopNode());
        for (var row : rows) {
            assertEquals(0, row.getChildCount(), "untyped entry has children: " + labelOf(row));
        }
        assertTrue(rows.get(0).getEntry().getContent() instanceof TypeNode n && n.isTopType(),
                   "first row is not the implicit node type: " + labelOf(rows.get(0)));
        assertEquals(List.of("flag:a", "flag:b", "flag:c", "a_to_a", "a_to_b", "a_to_c",
                             "b_to_b", "c_to_b"),
                     labelsOf(rows.subList(1, rows.size())));
    }

    /** In a typed graph, the node types are listed alphabetically, and under
     * each node type first its subtypes, then its flags, then its binary edge
     * labels. */
    @Test
    void typedGraphOrder() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(TYPED_GRAMMAR);
        AspectGraph graph = grammar.getTypeModel(QualName.parse(TYPE_GRAPH)).getSource();
        TypeTree tree = buildTree(grammar, DisplayKind.TYPE, graph);
        List<TypeTreeNode> nodeTypes = children(tree.getTopNode());
        assertEquals(List.of("type:A", "type:B", "type:C", "type:D", "type:Node"),
                     labelsOf(nodeTypes));
        List<TypeTreeNode> nodeChildren = children(nodeTypes.get(4));
        assertEquals(List.of("type:A", "type:B", "type:C", "type:D", "flag:available",
                             "flag:taken", "next"),
                     labelsOf(nodeChildren));
        // the subtypes are dependent nodes, the edge labels are top nodes
        for (int i = 0; i < nodeChildren.size(); i++) {
            assertEquals(i >= 4, nodeChildren.get(i).isTopNode(),
                         "top node status of " + labelOf(nodeChildren.get(i)));
        }
    }

    /** Loads a graph into a headless JGraph of a given display kind and
     * returns the label tree built for it. */
    private TypeTree buildTree(GrammarModel grammar, DisplayKind kind, AspectGraph graph) {
        var controller = new AspectGraphViewController(null, kind, false);
        controller.setGrammar(grammar);
        var jGraph = controller.getCanvas();
        var model = jGraph.newViewModel();
        model.loadGraph(graph);
        jGraph.setViewModel(model);
        TypeTree result = new TypeTree(jGraph, true);
        result.synchroniseModel();
        return result;
    }

    /** Returns the children of a tree node, which must all be label entries. */
    private List<TypeTreeNode> children(DefaultMutableTreeNode node) {
        List<TypeTreeNode> result = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            var child = node.getChildAt(i);
            assertTrue(child instanceof TypeTreeNode, "unexpected tree node " + child);
            result.add((TypeTreeNode) child);
        }
        return result;
    }

    /** Returns the prefixed labels of a list of tree nodes. */
    private List<String> labelsOf(List<TypeTreeNode> nodes) {
        return nodes.stream().map(this::labelOf).toList();
    }

    /** Returns the prefixed label of the type element wrapped in a tree node. */
    private String labelOf(TypeTreeNode node) {
        return node.getEntry().getContent().label().toParsableString();
    }
}
