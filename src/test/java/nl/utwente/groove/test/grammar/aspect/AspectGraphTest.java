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
package nl.utwente.groove.test.grammar.aspect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests for the graph-level operations of {@link AspectGraph} that are
 * otherwise only reachable through the GUI: renumbering, relabelling,
 * colouring, merging start graphs, unwrapping and renaming, plus the
 * conversion round trip with plain graphs they are all built on.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class AspectGraphTest {
    /** Creates an unfixed plain graph with a given name and role. */
    private PlainGraph plain(String name, GraphRole role) {
        return new PlainGraph(name, role, true);
    }

    /** Fixes a plain graph and converts it to an aspect graph. */
    private AspectGraph aspect(PlainGraph plain) {
        plain.setFixed();
        return AspectGraph.newInstance(plain);
    }

    /** Returns the sorted multiset of plain edge labels of an aspect graph. */
    private List<String> plainLabels(AspectGraph graph) {
        return graph
            .toPlainGraph()
            .edgeSet()
            .stream()
            .map(e -> e.label().text())
            .sorted()
            .collect(Collectors.toList());
    }

    /** Converting a plain graph to an aspect graph and back preserves the
     * labels; empty and fixed graphs behave as documented. */
    @Test
    public void testPlainRoundTrip() {
        PlainGraph plain = plain("host", GraphRole.HOST);
        PlainNode n0 = plain.addNode();
        PlainNode n1 = plain.addNode();
        plain.addEdge(n0, "a", n1);
        plain.addEdge(n0, "flag:f", n0);
        plain.addEdge(n1, "rem:comment", n1);
        AspectGraph graph = aspect(plain);
        assertFalse(graph.hasErrors());
        assertEquals(2, graph.nodeCount());
        assertEquals(List.of("a", "flag:f", "rem:comment"), plainLabels(graph));

        AspectGraph empty = AspectGraph.emptyGraph("e", GraphRole.RULE, true);
        assertTrue(empty.isFixed());
        assertEquals(0, empty.nodeCount());
        assertEquals(GraphRole.RULE, empty.getRole());
        assertEquals(QualName.name("e"), empty.getQualName());
    }

    /** Renaming and setting the name both update the qualified name. */
    @Test
    public void testRenameAndSetName() {
        AspectGraph graph = aspect(plain("old", GraphRole.HOST));
        AspectGraph renamed = graph.rename(QualName.name("sub", "fresh"));
        assertEquals(QualName.name("sub", "fresh"), renamed.getQualName());
        assertTrue(renamed.isFixed());
        // setName is the unfixed-graph variant
        AspectGraph clone = graph.clone();
        clone.setName("direct");
        assertEquals(QualName.name("direct"), clone.getQualName());
    }

    /** Renumbering compresses non-consecutive node numbers to a consecutive
     * sequence from 0, and is a no-op (same instance) on graphs that are
     * already consecutively numbered. */
    @Test
    public void testRenumber() {
        PlainGraph plain = plain("host", GraphRole.HOST);
        PlainNode n5 = plain.addNode(5);
        PlainNode n7 = plain.addNode(7);
        plain.addEdge(n5, "a", n7);
        AspectGraph graph = aspect(plain);
        AspectGraph renumbered = graph.renumber();
        assertNotSame(graph, renumbered);
        Set<Integer> numbers = new TreeSet<>();
        for (AspectNode node : renumbered.nodeSet()) {
            numbers.add(node.getNumber());
        }
        assertEquals(Set.of(0, 1), numbers);
        assertEquals(plainLabels(graph), plainLabels(renumbered));
        // a second renumbering changes nothing
        assertSame(renumbered, renumbered.renumber());
    }

    /** Relabelling binary edges and flags, in host graphs and inside rule
     * regular expressions; a relabelling that changes nothing returns the
     * same instance. */
    @Test
    public void testRelabel() {
        PlainGraph plain = plain("host", GraphRole.HOST);
        PlainNode n0 = plain.addNode();
        PlainNode n1 = plain.addNode();
        plain.addEdge(n0, "a", n1);
        plain.addEdge(n0, "flag:f", n0);
        AspectGraph graph = aspect(plain);
        // binary edge label
        AspectGraph relabelled
            = graph.relabel(TypeLabel.createLabel("a"), TypeLabel.createLabel("b"));
        assertEquals(List.of("b", "flag:f"), plainLabels(relabelled));
        // flag label
        relabelled = graph
            .relabel(TypeLabel.createLabel(EdgeRole.FLAG, "f"),
                     TypeLabel.createLabel(EdgeRole.FLAG, "g"));
        assertEquals(List.of("a", "flag:g"), plainLabels(relabelled));
        // a label that does not occur leaves the graph untouched
        assertSame(graph,
                   graph.relabel(TypeLabel.createLabel("absent"), TypeLabel.createLabel("b")));

        // relabelling inside a rule regular expression
        PlainGraph rulePlain = plain("rule", GraphRole.RULE);
        PlainNode r0 = rulePlain.addNode();
        PlainNode r1 = rulePlain.addNode();
        rulePlain.addEdge(r0, "a|c", r1);
        AspectGraph rule = aspect(rulePlain);
        AspectGraph ruleRelabelled
            = rule.relabel(TypeLabel.createLabel("a"), TypeLabel.createLabel("b"));
        assertEquals(List.of("b|c"), plainLabels(ruleRelabelled));
    }

    /** Regression for the empty-label assertion: the loader accepts an edge
     * with an empty label (turning it into a node label, as in the
     * start-with-final fixture graph of mc.gps), and relabelling must
     * preserve such labels rather than reject the graph. */
    @Test
    public void testRelabelWithEmptyLabel() {
        PlainGraph plain = plain("host", GraphRole.HOST);
        PlainNode n0 = plain.addNode();
        PlainNode n1 = plain.addNode();
        plain.addEdge(n0, "a", n1);
        plain.addEdge(n1, "", n1);
        AspectGraph graph = aspect(plain);
        AspectGraph relabelled
            = graph.relabel(TypeLabel.createLabel("a"), TypeLabel.createLabel("b"));
        assertEquals(List.of("b"), plainLabels(relabelled));
        // the empty label survives as a node label (the plain-graph view
        // suppresses empty labels, so it is checked on the node itself)
        var node = relabelled.nodeSet().stream().filter(n -> n.getNumber() == 1).findFirst().get();
        assertEquals(1, node.getNodeLabels().size());
        assertTrue(node.getNodeLabels().get(0).toString().isEmpty());
    }

    /** Colouring a node adds the colour aspect, recolouring to the same
     * colour is a no-op, and colouring with {@code null} resets. */
    @Test
    public void testColour() throws FormatException {
        PlainGraph plain = plain("host", GraphRole.HOST);
        PlainNode n0 = plain.addNode();
        PlainNode n1 = plain.addNode();
        plain.addEdge(n0, "a", n1);
        AspectGraph graph = aspect(plain);
        var red = AspectKind.COLOR.newAspect("red", GraphRole.HOST);
        var node = graph.nodeSet().stream().filter(n -> n.getNumber() == 0).findFirst().get();
        AspectGraph coloured = graph.colour(Set.of(node), red);
        assertNotSame(graph, coloured);
        assertTrue(plainLabels(coloured).stream().anyMatch(l -> l.startsWith("color:")));
        // recolouring with the same colour changes nothing
        var colouredNode
            = coloured.nodeSet().stream().filter(n -> n.getNumber() == 0).findFirst().get();
        assertSame(coloured, coloured.colour(Set.of(colouredNode), red));
        // resetting the colour removes the aspect again
        AspectGraph reset = coloured.colour(Set.of(colouredNode), null);
        assertFalse(plainLabels(reset).stream().anyMatch(l -> l.contains("color")));
    }

    /** Merging start graphs: nodes with a shared node identifier are merged,
     * others stay distinct; the name concatenates the graph names; the
     * empty collection yields {@code null}. */
    @Test
    public void testMergeGraphs() {
        assertNull(AspectGraph.mergeGraphs(List.of()));
        PlainGraph plain1 = plain("g1", GraphRole.HOST);
        PlainNode n0 = plain1.addNode();
        plain1.addEdge(n0, "id:x", n0);
        plain1.addEdge(n0, "flag:f1", n0);
        AspectGraph graph1 = aspect(plain1);
        PlainGraph plain2 = plain("g2", GraphRole.HOST);
        PlainNode m0 = plain2.addNode();
        PlainNode m1 = plain2.addNode();
        plain2.addEdge(m0, "id:x", m0);
        plain2.addEdge(m0, "b", m1);
        AspectGraph graph2 = aspect(plain2);
        AspectGraph merged = AspectGraph.mergeGraphs(List.of(graph1, graph2));
        assertEquals("g1_g2", merged.getName());
        // the two id:x nodes are merged; m1 stays separate
        assertEquals(2, merged.nodeCount());
        assertEquals(List.of("b", "flag:f1", "id:x"), plainLabels(merged));
    }

    /** Unwrapping strips the literal-text marker from labels, leaving all
     * other aspects in place. */
    @Test
    public void testUnwrap() {
        PlainGraph plain = plain("host", GraphRole.HOST);
        PlainNode n0 = plain.addNode();
        PlainNode n1 = plain.addNode();
        plain.addEdge(n0, ":a|b", n1);
        plain.addEdge(n0, "flag:f", n0);
        AspectGraph graph = aspect(plain);
        assertEquals(List.of(":a|b", "flag:f"), plainLabels(graph));
        AspectGraph unwrapped = graph.unwrap();
        assertTrue(unwrapped.isFixed());
        assertEquals(List.of("a|b", "flag:f"), plainLabels(unwrapped));
    }
}
