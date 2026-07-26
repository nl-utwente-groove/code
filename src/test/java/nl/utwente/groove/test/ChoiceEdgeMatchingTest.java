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

import nl.utwente.groove.automaton.RegExpr;
import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.host.DefaultHostGraph;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.rule.RuleFactory;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.ImplicitTypeGraph;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.match.MatcherFactory;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests matching of atom-choice rule edges ({@code a|b}).
 * In multigraph mode, such edges bind a genuine host edge image (any witness
 * of the choice is a single host edge), so distinct witnesses give distinct
 * morphisms, and the edge image participates in the edge-injectivity check.
 * In simple-graph mode, choices keep the automaton-based semantics, where
 * only the end nodes are bound and witnesses are not distinguished.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class ChoiceEdgeMatchingTest {
    /** The (fixed, implicit) type graph against which all graphs are typed. */
    private final TypeGraph typeGraph;

    public ChoiceEdgeMatchingTest() {
        ImplicitTypeGraph typeGraph = new ImplicitTypeGraph();
        typeGraph.addLabel("a");
        typeGraph.addLabel("b");
        typeGraph.setFixed();
        this.typeGraph = typeGraph;
    }

    /** Creates a condition whose pattern consists of two nodes connected
     * by a choice edge {@code a|b}, and optionally also an atom edge {@code a}. */
    private Condition createCondition(Condition.Op op, boolean injective, boolean simple,
                                      boolean atomEdge) throws FormatException {
        RuleFactory factory = RuleFactory.newInstance(this.typeGraph.getFactory());
        RuleGraph pattern = new RuleGraph("pattern", injective, simple, factory);
        RuleNode r0 = factory.createNode();
        RuleNode r1 = factory.createNode();
        pattern.addNode(r0);
        pattern.addNode(r1);
        pattern.addEdge(factory.createEdge(r0, new RuleLabel(RegExpr.parse("a|b")), r1));
        if (atomEdge) {
            pattern.addEdge(factory.createEdge(r0, factory.createLabel("a"), r1));
        }
        Condition result = new Condition("choice", op, pattern, null, new GrammarProperties());
        result.setTypeGraph(this.typeGraph);
        result.setFixed();
        return result;
    }

    /** Creates a host graph with two nodes connected by
     * {@code aCopies} a-edges and {@code bCopies} b-edges. */
    private HostGraph createHost(boolean simple, int aCopies, int bCopies) {
        HostFactory factory = HostFactory.newInstance(this.typeGraph.getFactory(), simple);
        DefaultHostGraph result = new DefaultHostGraph("host", factory);
        HostNode h0 = result.addNode();
        HostNode h1 = result.addNode();
        for (int i = 0; i < aCopies; i++) {
            result.addEdge(h0, "a", h1);
        }
        for (int i = 0; i < bCopies; i++) {
            result.addEdge(h0, "b", h1);
        }
        result.setFixed();
        return result;
    }

    /** Returns the number of matches of the choice pattern into a host with
     * the given numbers of a- and b-edges, in multigraph mode. */
    private int matchCount(Condition.Op op, boolean injective, boolean atomEdge, int aCopies,
                           int bCopies) throws FormatException {
        Condition condition = createCondition(op, injective, false, atomEdge);
        HostGraph host = createHost(false, aCopies, bCopies);
        return MatcherFactory.instance(false).createMatcher(condition).findAll(host, null).size();
    }

    /** Universally quantified, every witness of the choice is a separate morphism. */
    @Test
    public void testForallTwoWitnesses() throws FormatException {
        assertEquals(2, matchCount(Condition.Op.FORALL, false, false, 1, 1));
    }

    /** A single witness gives a single morphism. */
    @Test
    public void testForallOneWitness() throws FormatException {
        assertEquals(1, matchCount(Condition.Op.FORALL, false, false, 1, 0));
    }

    /** Parallel copies of one operand are distinct witnesses. */
    @Test
    public void testForallParallelWitnesses() throws FormatException {
        assertEquals(2, matchCount(Condition.Op.FORALL, false, false, 2, 0));
    }

    /** Existentially quantified, the choice edge image is irrelevant,
     * so all witnesses collapse to a single proof. */
    @Test
    public void testExistsCollapse() throws FormatException {
        assertEquals(1, matchCount(Condition.Op.EXISTS, false, false, 1, 1));
    }

    /** Under injective matching, the choice edge may not share its image
     * with the atom edge, so it must fall back to the b-witness. */
    @Test
    public void testInjectiveDistinctWitness() throws FormatException {
        assertEquals(1, matchCount(Condition.Op.FORALL, true, true, 1, 1));
    }

    /** Under injective matching, a single shared witness is not enough. */
    @Test
    public void testInjectiveNoFreeWitness() throws FormatException {
        assertEquals(0, matchCount(Condition.Op.FORALL, true, true, 1, 0));
    }

    /** Under non-injective matching, the choice edge may share its image
     * with the atom edge. */
    @Test
    public void testNonInjectiveSharedWitness() throws FormatException {
        assertEquals(1, matchCount(Condition.Op.FORALL, false, true, 1, 0));
    }

    /** In simple-graph mode, the choice retains the automaton-based
     * semantics: witnesses are not distinguished, even universally. */
    @Test
    public void testSimpleModeCollapse() throws FormatException {
        Condition condition = createCondition(Condition.Op.FORALL, false, true, false);
        HostGraph host = createHost(true, 1, 1);
        assertEquals(1,
                     MatcherFactory.instance(true).createMatcher(condition).findAll(host, null)
                         .size());
    }
}
