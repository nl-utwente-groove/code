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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests that an expression in a rule graph may call at most one enumerator
 * (gh #931). The expressions are those of {@code let}- and {@code test}-edges,
 * each taken as a whole; the test is on the normalised rule graph, where such
 * expressions are turned into product nodes. Also tests the display of such
 * edges, which marks an expression that calls an enumerator.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
public class EnumeratorExpressionTest {
    /** One enumerator is accepted, wherever in the expression it is called. */
    @Test
    public void testSingleEnumerator() {
        assertAccepted("new:let:x = int:range(n)");
        assertAccepted("new:let:x = lo + int:range(n)");
        assertAccepted("new:let:x = int:max(lo, int:range(n - lo))");
        assertAccepted("test:int:range(n) > lo");
    }

    /** Two edges of one node may each call an enumerator. */
    @Test
    public void testEnumeratorPerEdge() {
        assertAccepted("new:let:x = int:range(n)", "new:let:y = int:range(n)");
        assertAccepted("new:let:x = int:range(n)", "test:int:range(n) > lo");
    }

    /** More than one enumerator in an expression is an error. */
    @Test
    public void testSeveralEnumerators() {
        // not the same value twice, but all combinations
        assertRejected("new:let:x = int:range(n) + int:range(n)");
        assertRejected("new:let:x = int:range(n) + int:range(lo)");
        // one enumerator in the argument of another
        assertRejected("new:let:x = int:range(int:range(n))");
        // at any depth, and with other operators in between
        assertRejected("new:let:x = int:max(lo + int:range(n), int:range(n) - 1)");
        assertRejected("test:int:range(n) > int:range(lo)");
    }

    /**
     * An enumeration is displayed with the element-of sign between the name and
     * the expression, any other expression with an arrow.
     */
    @Test
    public void testDisplay() {
        assertDisplay("x → n + 1", "new:let:x = n + 1");
        assertDisplay("x ∈ range(n)", "new:let:x = int:range(n)");
        assertDisplay("x ∈ lo + range(n)", "new:let:x = lo + int:range(n)");
        // a test of a field of the node itself is displayed like an assignment
        assertDisplay("lo → n + 1", "test:lo == n + 1");
        assertDisplay("lo ∈ range(n)", "test:lo == int:range(n)");
        // any other test is displayed as it is written
        assertDisplay("range(n) > lo", "test:int:range(n) > lo");
    }

    /**
     * Asserts the display text of a rule node label, as far as the name, the
     * symbol and the expression are concerned.
     * @param expected the expected text, with a single space wherever the
     * display puts white space
     * @param label the label to be displayed
     */
    private void assertDisplay(String expected, String label) {
        var graph = build(label);
        var edge = graph
            .edgeSet()
            .stream()
            .filter(e -> e.isAssign() || e.isTest())
            .findAny()
            .orElseThrow();
        var text = edge
            .toLine(true, edge.source().getAspects())
            .toFlatString()
            .replaceAll("[\s  ]+", " ");
        assertTrue(text.endsWith(expected), () -> "Display of " + label + " is " + text);
    }

    /** Asserts that a rule node with given labels normalises without errors. */
    private void assertAccepted(String... labels) {
        var graph = normalise(labels);
        assertFalse(graph.hasErrors(), () -> graph.getErrors().toString());
    }

    /** Asserts that a rule node with given labels has the error of this test, and no other. */
    private void assertRejected(String... labels) {
        var graph = normalise(labels);
        assertTrue(graph.hasErrors(), () -> "No error for " + String.join(", ", labels));
        for (var error : graph.getErrors()) {
            assertTrue(error.toString().contains("more than one enumerator"), error::toString);
        }
    }

    /**
     * Builds the rule graph of one node with given labels and returns it in
     * normalised form. The node reads the integer fields {@code n} and
     * {@code lo}, for the expressions to refer to.
     */
    private AspectGraph normalise(String... labels) {
        return build(labels).normalise();
    }

    /**
     * Builds the rule graph of one node with given labels. The node reads the
     * integer fields {@code n} and {@code lo}, for the expressions to refer to.
     */
    private AspectGraph build(String... labels) {
        var plain = new PlainGraph("rule", GraphRole.RULE, true);
        var node = plain.addNode();
        for (var field : new String[] {"n", "lo"}) {
            var value = plain.addNode();
            plain.addEdge(value, "int:", value);
            plain.addEdge(node, field, value);
        }
        for (var label : labels) {
            plain.addEdge(node, label, node);
        }
        plain.setFixed();
        return AspectGraph.newInstance(plain);
    }
}
