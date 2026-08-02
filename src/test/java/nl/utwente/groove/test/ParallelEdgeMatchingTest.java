// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.host.DefaultHostGraph;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.rule.RuleFactory;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.ImplicitTypeGraph;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.match.MatcherFactory;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests matching of parallel rule edges onto parallel host edges.
 * In the category of multigraphs, edges carry identity, so a bundle of k
 * content-equal rule edges has one morphism onto a bundle of n content-equal
 * host edges per function from k to n: n^k in total, of which
 * n!/(n-k)! are injective. Non-injective matching (the default) admits all
 * of them; injective matching admits only the injective ones, which requires
 * the edge-injectivity check in the search plan (edge injectivity is implied
 * by node injectivity for simple patterns, but not for parallel bundles).
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class ParallelEdgeMatchingTest {
    /** The (fixed, implicit) type graph against which all graphs are typed. */
    private final TypeGraph typeGraph;

    public ParallelEdgeMatchingTest() {
        ImplicitTypeGraph typeGraph = new ImplicitTypeGraph();
        typeGraph.addLabel("a");
        typeGraph.setFixed();
        this.typeGraph = typeGraph;
    }

    /** Creates a condition whose pattern consists of two nodes connected
     * by a parallel bundle of {@code ruleCopies} content-equal a-edges. */
    private Condition createCondition(boolean injective, int ruleCopies) throws FormatException {
        RuleFactory factory = RuleFactory.newInstance(this.typeGraph.getFactory());
        RuleGraph pattern = new RuleGraph("pattern", injective, false, factory);
        RuleNode r0 = factory.createNode();
        RuleNode r1 = factory.createNode();
        pattern.addNode(r0);
        pattern.addNode(r1);
        for (int i = 0; i < ruleCopies; i++) {
            pattern.addEdge(factory.createEdge(r0, factory.createLabel("a"), r1, i));
        }
        // a universal condition, so that all pattern elements are relevant and
        // every morphism is reported separately; under EXISTS, matches that
        // differ only in irrelevant (non-anchor) images collapse to one witness
        Condition result
            = new Condition("bundle", Condition.Op.FORALL, pattern, null, new GrammarProperties());
        result.setTypeGraph(this.typeGraph);
        result.setFixed();
        return result;
    }

    /** Creates a host graph with two nodes connected by a parallel bundle
     * of {@code hostCopies} content-equal a-edges. */
    private HostGraph createHost(int hostCopies) {
        HostFactory factory = HostFactory.newInstance(this.typeGraph.getFactory(), false);
        DefaultHostGraph result = new DefaultHostGraph("host", factory);
        HostNode h0 = result.addNode();
        HostNode h1 = result.addNode();
        for (int i = 0; i < hostCopies; i++) {
            result.addEdge(h0, "a", h1);
        }
        result.setFixed();
        return result;
    }

    /** Returns the number of matches of a {@code ruleCopies}-bundle
     * into a {@code hostCopies}-bundle. */
    private int matchCount(boolean injective, int ruleCopies, int hostCopies) throws FormatException {
        Condition condition = createCondition(injective, ruleCopies);
        HostGraph host = createHost(hostCopies);
        return MatcherFactory.instance(false).createMatcher(condition).findAll(host, null).size();
    }

    /** A 2-bundle maps into a 2-bundle in 2^2 = 4 ways. */
    @Test
    public void testNonInjective2Into2() throws FormatException {
        assertEquals(4, matchCount(false, 2, 2));
    }

    /** Of those 4, only the 2 permutations are injective. */
    @Test
    public void testInjective2Into2() throws FormatException {
        assertEquals(2, matchCount(true, 2, 2));
    }

    /** Non-injectively, a 2-bundle collapses onto a single edge. */
    @Test
    public void testNonInjective2Into1() throws FormatException {
        assertEquals(1, matchCount(false, 2, 1));
    }

    /** Injectively, a 2-bundle needs two distinct host copies. */
    @Test
    public void testInjective2Into1() throws FormatException {
        assertEquals(0, matchCount(true, 2, 1));
    }

    /** Injective 2-bundle into a 3-bundle: 3*2 ordered pairs. */
    @Test
    public void testInjective2Into3() throws FormatException {
        assertEquals(6, matchCount(true, 2, 3));
    }

    /** Sanity check: a single edge matches each host copy once. */
    @Test
    public void testSingle1Into3() throws FormatException {
        assertEquals(3, matchCount(false, 1, 3));
        assertEquals(3, matchCount(true, 1, 3));
    }
}
