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
 * Tests matching of composite edge-image expressions: choices and inversions
 * of atoms and unnamed wildcards, such as {@code a|b}, {@code -a} or
 * {@code ?[b]|a}. In multigraph mode, such edges bind a genuine host edge
 * image (any witness is a single host edge, possibly matched inversely), so
 * distinct witnesses and orientations give distinct morphisms, and the edge
 * image participates in the edge-injectivity check. In simple-graph mode,
 * these expressions keep the automaton-based semantics, where only the end
 * nodes are bound and witnesses are not distinguished.
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
        typeGraph.addLabel("c");
        typeGraph.setFixed();
        this.typeGraph = typeGraph;
    }

    /** Creates a condition whose pattern consists of two nodes r0, r1 connected
     * by an edge with the given (parsed) label from r0 to r1, and optionally
     * also an atom edge {@code a} from r0 to r1. */
    private Condition createCondition(String labelText, Condition.Op op, boolean injective,
                                      boolean simple, boolean atomEdge) throws FormatException {
        RuleFactory factory = RuleFactory.newInstance(this.typeGraph.getFactory());
        RuleGraph pattern = new RuleGraph("pattern", injective, simple, factory);
        RuleNode r0 = factory.createNode();
        RuleNode r1 = factory.createNode();
        pattern.addNode(r0);
        pattern.addNode(r1);
        pattern.addEdge(factory.createEdge(r0, new RuleLabel(RegExpr.parse(labelText)), r1));
        if (atomEdge) {
            pattern.addEdge(factory.createEdge(r0, factory.createLabel("a"), r1));
        }
        Condition result = new Condition("choice", op, pattern, null, new GrammarProperties());
        result.setTypeGraph(this.typeGraph);
        result.setFixed();
        return result;
    }

    /** Creates a host graph with two nodes h0, h1 and an edge per given
     * specification: a plain label means an edge from h0 to h1, a label
     * prefixed with {@code -} means an edge from h1 to h0. */
    private HostGraph createHost(boolean simple, String... edges) {
        HostFactory factory = HostFactory.newInstance(this.typeGraph.getFactory(), simple);
        DefaultHostGraph result = new DefaultHostGraph("host", factory);
        HostNode h0 = result.addNode();
        HostNode h1 = result.addNode();
        for (String edge : edges) {
            if (edge.startsWith("-")) {
                result.addEdge(h1, edge.substring(1), h0);
            } else {
                result.addEdge(h0, edge, h1);
            }
        }
        result.setFixed();
        return result;
    }

    /** Returns the number of matches of the pattern into the host,
     * in multigraph mode. */
    private int matchCount(String labelText, Condition.Op op, boolean injective, boolean atomEdge,
                           String... hostEdges) throws FormatException {
        Condition condition = createCondition(labelText, op, injective, false, atomEdge);
        HostGraph host = createHost(false, hostEdges);
        return MatcherFactory.instance(false).createMatcher(condition).findAll(host, null).size();
    }

    /** Universally quantified, every witness of the choice is a separate morphism. */
    @Test
    public void testForallTwoWitnesses() throws FormatException {
        assertEquals(2, matchCount("a|b", Condition.Op.FORALL, false, false, "a", "b"));
    }

    /** A single witness gives a single morphism. */
    @Test
    public void testForallOneWitness() throws FormatException {
        assertEquals(1, matchCount("a|b", Condition.Op.FORALL, false, false, "a"));
    }

    /** Parallel copies of one operand are distinct witnesses. */
    @Test
    public void testForallParallelWitnesses() throws FormatException {
        assertEquals(2, matchCount("a|b", Condition.Op.FORALL, false, false, "a", "a"));
    }

    /** Existentially quantified, the choice edge image is irrelevant,
     * so all witnesses collapse to a single proof. */
    @Test
    public void testExistsCollapse() throws FormatException {
        assertEquals(1, matchCount("a|b", Condition.Op.EXISTS, false, false, "a", "b"));
    }

    /** Under injective matching, the choice edge may not share its image
     * with the atom edge, so it must fall back to the b-witness. */
    @Test
    public void testInjectiveDistinctWitness() throws FormatException {
        assertEquals(1, matchCount("a|b", Condition.Op.FORALL, true, true, "a", "b"));
    }

    /** Under injective matching, a single shared witness is not enough. */
    @Test
    public void testInjectiveNoFreeWitness() throws FormatException {
        assertEquals(0, matchCount("a|b", Condition.Op.FORALL, true, true, "a"));
    }

    /** Under non-injective matching, the choice edge may share its image
     * with the atom edge. */
    @Test
    public void testNonInjectiveSharedWitness() throws FormatException {
        assertEquals(1, matchCount("a|b", Condition.Op.FORALL, false, true, "a"));
    }

    /** An inverse atom matches a host edge from the target to the source image. */
    @Test
    public void testInverseAtom() throws FormatException {
        assertEquals(1, matchCount("-a", Condition.Op.FORALL, false, false, "-a"));
        assertEquals(0, matchCount("-a", Condition.Op.FORALL, false, false, "-b"));
    }

    /** A nested inversion distributes over the choice. */
    @Test
    public void testInverseChoice() throws FormatException {
        assertEquals(1, matchCount("-(a|b)", Condition.Op.FORALL, false, false, "-b"));
        assertEquals(2, matchCount("-(a|b)", Condition.Op.FORALL, false, false, "-a", "-b"));
        // without further constraints, a forward witness is matched with
        // swapped end bindings; with the atom edge pinning the node images,
        // an inversely directed witness is required
        assertEquals(1, matchCount("-(a|b)", Condition.Op.FORALL, false, false, "a"));
        assertEquals(0, matchCount("-(a|b)", Condition.Op.FORALL, false, true, "a"));
    }

    /** A single host edge matched by both operand directions counts as two
     * morphisms, with opposite end node bindings. */
    @Test
    public void testBothOrientations() throws FormatException {
        assertEquals(2, matchCount("a|-a", Condition.Op.FORALL, false, false, "a"));
    }

    /** An unnamed guarded wildcard can be a choice operand. */
    @Test
    public void testWildcardChoice() throws FormatException {
        assertEquals(2, matchCount("?[b]|a", Condition.Op.FORALL, false, false, "a", "b"));
        assertEquals(0, matchCount("?[b]|a", Condition.Op.FORALL, false, false, "c"));
    }

    /** Under injective matching, the forward alternative may not reuse the
     * edge taken by the atom edge, and the inverse alternative needs an
     * oppositely directed witness. */
    @Test
    public void testInjectiveInverse() throws FormatException {
        assertEquals(0, matchCount("a|-a", Condition.Op.FORALL, true, true, "a"));
        assertEquals(2, matchCount("a|-a", Condition.Op.FORALL, true, true, "a", "a"));
    }

    /** In simple-graph mode, composite expressions retain the automaton-based
     * semantics: witnesses are not distinguished, even universally. */
    @Test
    public void testSimpleModeCollapse() throws FormatException {
        Condition condition = createCondition("a|b", Condition.Op.FORALL, false, true, false);
        HostGraph host = createHost(true, "a", "b");
        assertEquals(1,
                     MatcherFactory.instance(true).createMatcher(condition).findAll(host, null)
                         .size());
    }
}
