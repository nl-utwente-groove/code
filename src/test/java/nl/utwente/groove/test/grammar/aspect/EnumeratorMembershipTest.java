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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.HostModel;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests what an enumeration means where a rule reads a value rather than
 * sets it (gh #931): it stands for some element, so an attribute whose value
 * is an enumeration is a membership test, and so is a test that equates a
 * field to an enumeration. A test may not call an enumerator in any other
 * form; it can test a node that has the enumeration as its value instead.
 * <p>
 * Every rule has a node {@code a} with a field {@code n} and a node {@code b}
 * with a field {@code x}, and is matched against the hosts of {@link #HOSTS};
 * the enumeration is {@code range(a.n)} throughout.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
public class EnumeratorMembershipTest {
    /**
     * The values of {@code a.n} and {@code b.x} in the hosts: {@code x} in a
     * range of three, {@code x} outside it, an empty range, and {@code x} in
     * and outside a range of one.
     */
    private static final int[][] HOSTS = {{3, 1}, {3, 5}, {0, 1}, {1, 0}, {1, 5}};

    /** A reader edge to an enumeration node tests membership. */
    @Test
    public void testReaderEdge() throws Exception {
        assertMatches(counts(1, 0, 0, 1, 0), edge("e", "int:range(a.n)", "e"), edge("b", "x", "e"));
        // also if the node is named
        assertMatches(counts(1, 0, 0, 1, 0), edge("e", "int:range(a.n)", "e"),
                      edge("e", "id:v", "e"), edge("b", "x", "e"));
    }

    /** An enumeration node that is an embargo tests that no element is the value. */
    @Test
    public void testEmbargoNode() throws Exception {
        assertMatches(counts(0, 1, 1, 0, 1), edge("e", "int:range(a.n)", "e"),
                      edge("e", "not:", "e"), edge("b", "x", "e"));
    }

    /**
     * An embargo edge to an enumeration node that is read is an error: it
     * would hold if some element is not the value.
     */
    @Test
    public void testEmbargoEdge() {
        assertRejected("Embargo edge to enumeration", edge("e", "int:range(a.n)", "e"),
                       edge("b", "not:x", "e"));
    }

    /**
     * A test that equates a field of its node to an enumeration tests
     * membership, like an edge to an enumeration node; as an embargo, it tests
     * that the value is no element, like an edge to such a node that is an
     * embargo.
     */
    @Test
    public void testFieldTest() throws Exception {
        assertMatches(counts(1, 0, 0, 1, 0), edge("b", "test:x == int:range(a.n)", "b"));
        assertMatches(counts(0, 1, 1, 0, 1), edge("b", "not:test:x == int:range(a.n)", "b"));
        // also under a quantifier
        assertMatches(counts(1, 0, 0, 1, 0), edge("q", "forallx:", "q"), edge("b", "at", "q"),
                      edge("b", "test:x == int:range(a.n)", "b"));
    }

    /**
     * A test may not call an enumerator in any other form: it would hold if it
     * holds for some element, which its text does not convey.
     */
    @Test
    public void testEnumeratorInTest() {
        var error = "Enumerator not allowed in test";
        assertRejected(error, edge("b", "test:int:range(a.n) > x", "b"));
        assertRejected(error, edge("b", "test:x != int:range(a.n)", "b"));
        assertRejected(error, edge("b", "test:!(x == int:range(a.n))", "b"));
        assertRejected(error, edge("b", "not:test:int:range(a.n) > x", "b"));
        // the field should be on the left hand side, and of the node itself
        assertRejected(error, edge("b", "test:int:range(a.n) == x", "b"));
        assertRejected(error, edge("b", "test:a.n == int:range(x)", "b"));
    }

    /**
     * A test of a named node that has the enumeration as its value holds if
     * it holds for some element; it is found once, however many elements
     * satisfy it.
     */
    @Test
    public void testElementNode() throws Exception {
        var element = edge("e", "int:range(a.n)", "e");
        var name = edge("e", "id:i", "e");
        // x is an element
        assertMatches(counts(1, 0, 0, 1, 0), element, name, edge("b", "test:x == i", "b"));
        // some element is greater than x
        assertMatches(counts(1, 0, 0, 0, 0), element, name, edge("b", "test:i > x", "b"));
        // some element differs from x, which is not the same as x being no element
        assertMatches(counts(1, 1, 0, 0, 1), element, name, edge("b", "test:i != x", "b"));
    }

    /**
     * Under a universal quantifier the meaning is the same: the quantifier
     * ranges over the nodes for which some element qualifies, and does not
     * require that all elements do. The quantifier is one that needs a match,
     * so that the number of rule matches tells whether {@code b} qualifies.
     */
    @Test
    public void testUnderQuantifier() throws Exception {
        var quantifier = edge("q", "forallx:", "q");
        var level = edge("b", "at", "q");
        var element = edge("e", "int:range(a.n)", "e");
        var elementLevel = edge("e", "at", "q");
        assertMatches(counts(1, 0, 0, 1, 0), quantifier, level, element, elementLevel,
                      edge("b", "x", "e"));
        assertMatches(counts(1, 0, 0, 0, 0), quantifier, level, element, elementLevel,
                      edge("e", "id:i", "e"), edge("b", "test:i > x", "b"));
    }

    /**
     * An edge to an enumeration node is displayed on its source node with the
     * element-of sign, an edge to any other expression with an arrow.
     */
    @Test
    public void testDisplay() {
        assertDisplay("x ∈ range(a.n)", "x", edge("e", "int:range(a.n)", "e"),
                      edge("b", "x", "e"));
        assertDisplay("! x ∈ range(a.n)", "x", edge("e", "int:range(a.n)", "e"),
                      edge("e", "not:", "e"), edge("b", "x", "e"));
        assertDisplay("x → a.n + 1", "x", edge("e", "int:a.n + 1", "e"), edge("b", "x", "e"));
    }

    /** An assignment, in contrast, is found once for every element. */
    @Test
    public void testAssignment() throws Exception {
        assertMatches(counts(3, 3, 0, 1, 1), edge("b", "let:x = int:range(a.n)", "b"));
    }

    /**
     * Asserts the number of matches of a rule in each of the hosts.
     * @param expected the expected number of matches, per element of {@link #HOSTS}
     * @param edges the edges of the rule, on top of the nodes {@code a} and {@code b}
     */
    private void assertMatches(int[] expected, String[]... edges) throws Exception {
        var grammar = getGrammar();
        var rule = new RuleModel(grammar, ruleGraph(edges)).toResource();
        for (int i = 0; i < HOSTS.length; i++) {
            int n = HOSTS[i][0];
            int x = HOSTS[i][1];
            var host = new HostModel(grammar, hostGraph(n, x))
                .toResource()
                .clone(AlgebraFamily.DEFAULT);
            assertEquals(expected[i], rule.getProver().getAllMatches(host).size(),
                         () -> String.format("Matches of %s for n = %d and x = %d", describe(edges), n, x));
        }
    }

    /**
     * Asserts that a rule is rejected with a given error, and no other.
     * @param error a part of the expected error message
     * @param edges the edges of the rule, on top of the nodes {@code a} and {@code b}
     */
    private void assertRejected(String error, String[]... edges) {
        var graph = ruleGraph(edges).normalise();
        assertTrue(graph.hasErrors(), () -> "No error for " + describe(edges));
        for (var found : graph.getErrors()) {
            assertTrue(found.toString().contains(error), found::toString);
        }
    }

    /**
     * Asserts the display text of an edge of a rule on its source node.
     * @param expected the expected text, with a single space wherever the
     * display puts white space
     * @param label the label of the edge to be displayed
     * @param edges the edges of the rule, on top of the nodes {@code a} and {@code b}
     */
    private void assertDisplay(String expected, String label, String[]... edges) {
        var edge = ruleGraph(edges)
            .edgeSet()
            .stream()
            .filter(e -> e.getInnerText().equals(label))
            .findAny()
            .orElseThrow();
        var text = edge
            .toLine(true, edge.source().getAspects())
            .toFlatString()
            .replaceAll("[\\s  ]+", " ");
        assertEquals(expected, text, () -> "Display of " + describe(edges));
    }

    /** Returns the expected numbers of matches, in the order of {@link #HOSTS}. */
    private int[] counts(int... counts) {
        assert counts.length == HOSTS.length;
        return counts;
    }

    /** Returns an edge of a rule, as its source node name, label and target node name. */
    private String[] edge(String source, String label, String target) {
        return new String[] {source, label, target};
    }

    /** Returns the labels of a given set of edges, to name the rule in a message. */
    private String describe(String[][] edges) {
        var result = new StringBuilder();
        for (var edge : edges) {
            result.append('[').append(edge[1]).append(']');
        }
        return result.toString();
    }

    /**
     * Builds a rule graph with a node {@code a} of type {@code A}, a node
     * {@code b} of type {@code B}, and given further edges, between nodes that
     * are created as they are named.
     */
    private AspectGraph ruleGraph(String[][] edges) {
        var plain = new PlainGraph("rule", GraphRole.RULE, true);
        Map<String,PlainNode> nodes = new LinkedHashMap<>();
        var a = plain.addNode();
        nodes.put("a", a);
        plain.addEdge(a, "type:A", a);
        plain.addEdge(a, "id:a", a);
        var b = plain.addNode();
        nodes.put("b", b);
        plain.addEdge(b, "type:B", b);
        for (var edge : edges) {
            var source = nodes.computeIfAbsent(edge[0], k -> plain.addNode());
            var target = nodes.computeIfAbsent(edge[2], k -> plain.addNode());
            plain.addEdge(source, edge[1], target);
        }
        plain.setFixed();
        return AspectGraph.newInstance(plain);
    }

    /**
     * Builds a host graph with a node of type {@code A} with field {@code n}
     * and a node of type {@code B} with field {@code x}, with given values.
     */
    private AspectGraph hostGraph(int n, int x) {
        var plain = new PlainGraph("host", GraphRole.HOST, true);
        addObject(plain, "A", "n", n);
        addObject(plain, "B", "x", x);
        plain.setFixed();
        return AspectGraph.newInstance(plain);
    }

    /** Adds a node of a given type with one integer field to a host graph. */
    private void addObject(PlainGraph plain, String type, String field, int value) {
        var object = plain.addNode();
        plain.addEdge(object, "type:" + type, object);
        var valueNode = plain.addNode();
        plain.addEdge(valueNode, "int:" + value, valueNode);
        plain.addEdge(object, field, valueNode);
    }

    /** Returns the grammar in whose context the rules and hosts are built. */
    private static GrammarModel getGrammar() throws IOException {
        var result = grammar;
        if (result == null) {
            grammar = result = Groove.loadGrammar("junit/rules/enumerators");
        }
        return result;
    }

    /** The grammar in whose context the rules and hosts are built, once loaded. */
    private static @Nullable GrammarModel grammar;
}
