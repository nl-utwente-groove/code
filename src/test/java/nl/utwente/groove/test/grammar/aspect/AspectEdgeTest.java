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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.aspect.AspectParser;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.line.Line;

/**
 * Tests for the error detection and display-line rendering of
 * {@link AspectEdge}: role inference and conflicts, restrictions on regular
 * expressions, nesting-edge shape checks, and the lines shown in the editor.
 * All of this logic is otherwise only exercised when a user edits an
 * erroneous graph in the GUI.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class AspectEdgeTest {
    /** Creates an unfixed plain graph with a given role. */
    private PlainGraph plain(GraphRole role) {
        return new PlainGraph("test", role, true);
    }

    /** Fixes a plain graph and converts it to an aspect graph. */
    private AspectGraph aspect(PlainGraph plain) {
        plain.setFixed();
        return AspectGraph.newInstance(plain);
    }

    /** Asserts that a graph has an error whose message contains the given fragment. */
    private void assertError(AspectGraph graph, String fragment) {
        assertTrue(graph.hasErrors(), "expected errors in " + graph.getName());
        String all = graph.getErrors().toString();
        assertTrue(all.contains(fragment),
                   "no error containing '" + fragment + "' in: " + all);
    }

    /** Returns the (parsed) edge with a given inner text. */
    private AspectEdge edge(AspectGraph graph, String innerText) {
        for (AspectEdge edge : graph.edgeSet()) {
            if (edge.getInnerText().equals(innerText)) {
                return edge;
            }
        }
        fail("no edge with inner text '" + innerText + "'");
        throw new IllegalStateException(); // unreachable
    }

    /** An empty or node-only label on an edge between two distinct nodes is
     * an error on the directly constructed edge. (The plain-to-aspect
     * conversion never constructs these: it attaches such labels to the
     * source node instead.) */
    @Test
    public void testDirectConstructionErrors() {
        AspectGraph graph = new AspectGraph("direct", GraphRole.RULE, true);
        AspectNode n0 = graph.addNode();
        AspectNode n1 = graph.addNode();
        AspectParser parser = AspectParser.getInstance();
        AspectEdge empty = new AspectEdge(n0, parser.parse("", GraphRole.RULE), n1);
        assertTrue(empty.hasErrors());
        assertTrue(empty.getErrors().toString().contains("Empty edge label"));
        AspectEdge nodeOnly = new AspectEdge(n0, parser.parse("id:x", GraphRole.RULE), n1);
        assertTrue(nodeOnly.hasErrors());
        assertTrue(nodeOnly.getErrors().toString().contains("not allowed in edge label"));
    }

    /** Role inference: an unadorned edge between a creator and an eraser
     * node has conflicting inferred roles; between an eraser and an embargo
     * node the embargo wins. */
    @Test
    public void testRoleInference() {
        PlainGraph conflict = plain(GraphRole.RULE);
        PlainNode n0 = conflict.addNode();
        PlainNode n1 = conflict.addNode();
        conflict.addEdge(n0, "new:", n0);
        conflict.addEdge(n1, "del:", n1);
        conflict.addEdge(n0, "a", n1);
        assertError(aspect(conflict), "Conflicting source and target roles");

        PlainGraph embargo = plain(GraphRole.RULE);
        PlainNode m0 = embargo.addNode();
        PlainNode m1 = embargo.addNode();
        embargo.addEdge(m0, "del:", m0);
        embargo.addEdge(m1, "not:", m1);
        embargo.addEdge(m0, "a", m1);
        AspectGraph embargoGraph = aspect(embargo);
        assertFalse(embargoGraph.hasErrors());
    }

    /** Role compatibility: an explicit edge role must fit the roles of the
     * adjacent nodes. */
    @Test
    public void testRoleCompatibility() {
        PlainGraph plain = plain(GraphRole.RULE);
        PlainNode n0 = plain.addNode();
        PlainNode n1 = plain.addNode();
        plain.addEdge(n0, "del:", n0);
        plain.addEdge(n1, "del:", n1);
        plain.addEdge(n0, "new:a", n1);
        AspectGraph graph = aspect(plain);
        assertError(graph, "not compatible with source role");
        assertError(graph, "not compatible with target role");
    }

    /** Regular expressions are not allowed on creator or eraser edges,
     * and unnamed wildcards not on creators. */
    @Test
    public void testRegExprRestrictions() {
        PlainGraph creator = plain(GraphRole.RULE);
        PlainNode n0 = creator.addNode();
        PlainNode n1 = creator.addNode();
        creator.addEdge(n0, "new:a.b", n1);
        assertError(aspect(creator), "not allowed on creator");

        PlainGraph wildcard = plain(GraphRole.RULE);
        PlainNode w0 = wildcard.addNode();
        PlainNode w1 = wildcard.addNode();
        wildcard.addEdge(w0, "new:?", w1);
        assertError(aspect(wildcard), "Unnamed wildcard");

        PlainGraph eraser = plain(GraphRole.RULE);
        PlainNode e0 = eraser.addNode();
        PlainNode e1 = eraser.addNode();
        eraser.addEdge(e0, "del:a|b", e1);
        assertError(aspect(eraser), "not allowed on eraser");
    }

    /** Nesting edges must connect to quantifier nodes of the right kind. */
    @Test
    public void testNestingEdgeChecks() {
        // an at-edge from a quantified node to a non-quantifier
        PlainGraph at = plain(GraphRole.RULE);
        PlainNode a0 = at.addNode();
        PlainNode a1 = at.addNode();
        at.addEdge(a0, "forall:", a0);
        at.addEdge(a0, "at", a1);
        assertError(aspect(at), "should be quantifier");

        // an in-edge whose source is not a quantifier
        PlainGraph in = plain(GraphRole.RULE);
        PlainNode i0 = in.addNode();
        PlainNode i1 = in.addNode();
        in.addEdge(i1, "forall:", i1);
        in.addEdge(i0, "in", i1);
        assertError(aspect(in), "should be quantifier");

        // a count-edge to a non-int node
        PlainGraph count = plain(GraphRole.RULE);
        PlainNode c0 = count.addNode();
        PlainNode c1 = count.addNode();
        count.addEdge(c0, "forall:", c0);
        count.addEdge(c0, "count", c1);
        assertError(aspect(count), "should be int-node");
    }

    /** Attribute test edges must be boolean, non-field expressions. */
    @Test
    public void testPredicateChecks() {
        PlainGraph nonBool = plain(GraphRole.RULE);
        PlainNode n0 = nonBool.addNode();
        nonBool.addEdge(n0, "test:1+2", n0);
        assertError(aspect(nonBool), "not allowed as predicate expression");
    }

    /** Display-line rendering of role-prefixed, remark and attribute edges. */
    @Test
    public void testToLines() {
        PlainGraph plain = plain(GraphRole.RULE);
        PlainNode n0 = plain.addNode();
        PlainNode n1 = plain.addNode();
        plain.addEdge(n0, "del:a", n1);
        plain.addEdge(n0, "new:b", n1);
        plain.addEdge(n0, "not:c", n1);
        plain.addEdge(n0, "rem:first\nsecond", n1);
        plain.addEdge(n0, "let:x=3", n0);
        AspectGraph graph = aspect(plain);
        assertFalse(graph.hasErrors());
        Aspect.Map context = new Aspect.Map(false, GraphRole.RULE);
        // role prefixes are shown (as symbols) when the context does not
        // carry the role
        assertEquals("- a", edge(graph, "a").toLines(false, context).get(0).toFlatString());
        assertEquals("+ b", edge(graph, "b").toLines(false, context).get(0).toFlatString());
        assertEquals("! c", edge(graph, "c").toLines(false, context).get(0).toFlatString());
        // a multi-line remark is split into one line per text line
        List<Line> remark = edge(graph, "first\nsecond").toLines(false, context);
        assertEquals(2, remark.size());
        assertTrue(remark.get(0).toFlatString().contains("first"));
        assertTrue(remark.get(1).toFlatString().contains("second"));
        // an assignment is rendered with the assigned expression
        AspectEdge letEdge
            = graph.edgeSet().stream().filter(AspectEdge::isAssign).findFirst().get();
        String let = letEdge.toLines(true, context).get(0).toFlatString();
        assertTrue(let.contains("x"));
        assertTrue(let.contains("3"));
    }
}
