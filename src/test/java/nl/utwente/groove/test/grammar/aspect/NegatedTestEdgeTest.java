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

import java.io.IOException;
import java.util.Arrays;
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
 * Tests the meaning of a negated test ({@code not:test:}): the rule matches
 * if the tested fields have no values for which the test holds. This is the
 * meaning of any embargo, namely that the structure it stands for is absent;
 * in particular, the test also passes if a tested field has no value at all.
 * <p>
 * Every rule has a node {@code b} of type {@code B}, and is matched against
 * hosts with one such node, which has the values for its field {@code x} that
 * are listed in {@link #HOSTS}.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
public class NegatedTestEdgeTest {
    /**
     * The values of {@code b.x} in the hosts: a single value for which the
     * tests below hold, one for which they do not, no value, and both values.
     */
    private static final int[][] HOSTS = {{1}, {5}, {}, {1, 5}};

    /** A test is found once for every value of the field for which it holds. */
    @Test
    public void testPositive() throws Exception {
        assertMatches(counts(1, 0, 0, 1), edge("b", "test:x == 1", "b"));
        assertMatches(counts(1, 0, 0, 1), edge("b", "test:x < 3", "b"));
    }

    /** A negated test requires that the field has no value for which the test holds. */
    @Test
    public void testNegated() throws Exception {
        assertMatches(counts(0, 1, 1, 0), edge("b", "not:test:x == 1", "b"));
        assertMatches(counts(0, 1, 1, 0), edge("b", "not:test:x < 3", "b"));
        // which differs from the negated predicate, which needs a value
        assertMatches(counts(0, 1, 0, 1), edge("b", "test:x != 1", "b"));
        assertMatches(counts(0, 1, 0, 1), edge("b", "test:!(x < 3)", "b"));
    }

    /**
     * If the rule also reads the field, a negated test is about the value
     * that is read.
     */
    @Test
    public void testNegatedOnReadField() throws Exception {
        assertMatches(counts(0, 1, 0, 1), edge("v", "int:", "v"), edge("b", "x", "v"),
                      edge("b", "not:test:x == 1", "b"));
    }

    /**
     * A test on an embargo node is part of the embargo, whether or not it is
     * itself marked as one: there is no node for which the test holds.
     */
    @Test
    public void testOnEmbargoNode() throws Exception {
        assertMatches(counts(0, 1, 1, 0), edge("c", "not:", "c"), edge("c", "type:B", "c"),
                      edge("c", "test:x == 1", "c"));
        assertMatches(counts(0, 1, 1, 0), edge("c", "not:", "c"), edge("c", "type:B", "c"),
                      edge("c", "not:test:x == 1", "c"));
    }

    /** A negated test on a quantifier node is about the count of that quantifier. */
    @Test
    public void testNegatedCount() throws Exception {
        var quantifier = edge("q", "forall:", "q");
        var value = edge("v", "int:", "v");
        var level = edge("v", "at", "q");
        var field = edge("b", "x", "v");
        assertMatches(counts(0, 0, 0, 1), quantifier, value, level, field,
                      edge("q", "test:count > 1", "q"));
        assertMatches(counts(1, 1, 1, 0), quantifier, value, level, field,
                      edge("q", "not:test:count > 1", "q"));
    }

    /**
     * Asserts the number of matches of a rule in each of the hosts.
     * @param expected the expected number of matches, per element of {@link #HOSTS}
     * @param edges the edges of the rule, on top of the node {@code b}
     */
    private void assertMatches(int[] expected, String[]... edges) throws Exception {
        var grammar = getGrammar();
        var rule = new RuleModel(grammar, ruleGraph(edges)).toResource();
        for (int i = 0; i < HOSTS.length; i++) {
            int[] values = HOSTS[i];
            var host = new HostModel(grammar, hostGraph(values))
                .toResource()
                .clone(AlgebraFamily.DEFAULT);
            assertEquals(expected[i], rule.getProver().getAllMatches(host).size(),
                         () -> String
                             .format("Matches of %s for x = %s", describe(edges),
                                     Arrays.toString(values)));
        }
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
     * Builds a rule graph with a node {@code b} of type {@code B} and given
     * further edges, between nodes that are created as they are named.
     */
    private AspectGraph ruleGraph(String[][] edges) {
        var plain = new PlainGraph("rule", GraphRole.RULE, true);
        Map<String,PlainNode> nodes = new LinkedHashMap<>();
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
     * Builds a host graph with a node of type {@code B} that has given values
     * for its field {@code x}.
     */
    private AspectGraph hostGraph(int[] values) {
        var plain = new PlainGraph("host", GraphRole.HOST, true);
        var object = plain.addNode();
        plain.addEdge(object, "type:B", object);
        for (int value : values) {
            var valueNode = plain.addNode();
            plain.addEdge(valueNode, "int:" + value, valueNode);
            plain.addEdge(object, "x", valueNode);
        }
        plain.setFixed();
        return AspectGraph.newInstance(plain);
    }

    /** Returns the grammar in whose context the rules and hosts are built. */
    private static GrammarModel getGrammar() throws IOException {
        var result = grammar;
        if (result == null) {
            grammar = result = Groove.loadGrammar("junit/rules/attributes");
        }
        return result;
    }

    /** The grammar in whose context the rules and hosts are built, once loaded. */
    private static @Nullable GrammarModel grammar;
}
