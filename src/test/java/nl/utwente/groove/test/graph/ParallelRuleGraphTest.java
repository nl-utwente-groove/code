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
package nl.utwente.groove.test.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleFactory;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleGraphMorphism;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.ImplicitTypeGraph;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests parallel-edge support in rule graphs: {@link RuleEdge}s carry a
 * parallel index that enters their equality, so that content-equal edges
 * with distinct indices can coexist in a non-simple {@link RuleGraph}.
 * The index is assigned explicitly (never counted implicitly), and is
 * preserved by rule graph morphisms and by the typing step of rule
 * compilation.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class ParallelRuleGraphTest {
    private final RuleFactory factory = RuleFactory.newInstance();
    private final RuleNode n0 = this.factory.createNode();
    private final RuleNode n1 = this.factory.createNode();
    private final RuleLabel a = this.factory.createLabel("a");

    /** The parallel index distinguishes content-equal edges;
     * index 0 is the default, so plain edge creation is unaffected. */
    @Test
    public void testParallelIndexInEquality() {
        RuleEdge e0 = this.factory.createEdge(this.n0, this.a, this.n1);
        RuleEdge e1 = this.factory.createEdge(this.n0, this.a, this.n1, 1);
        assertEquals(0, e0.getNumber());
        assertEquals(1, e1.getNumber());
        assertNotEquals(e0, e1);
        // indices are stable: re-creation yields equal edges
        assertEquals(e0, this.factory.createEdge(this.n0, this.a, this.n1));
        assertEquals(e1, this.factory.createEdge(this.n0, this.a, this.n1, 1));
    }

    /** A non-simple rule graph holds parallel copies side by side. */
    @Test
    public void testNonSimpleGraphHoldsParallelEdges() {
        RuleGraph graph = new RuleGraph("nonSimple", false, false, this.factory);
        assertFalse(graph.isSimple());
        graph.addNode(this.n0);
        graph.addNode(this.n1);
        assertTrue(graph.addEdge(this.factory.createEdge(this.n0, this.a, this.n1)));
        assertTrue(graph.addEdge(this.factory.createEdge(this.n0, this.a, this.n1, 1)));
        assertEquals(2, graph.edgeCount());
        // an equal copy of an existing edge still collapses
        assertFalse(graph.addEdge(this.factory.createEdge(this.n0, this.a, this.n1)));
        assertEquals(2, graph.edgeCount());
    }

    /** In a simple rule graph (the default), all edges carry index 0 and
     * content-equal edges collapse, as before. */
    @Test
    public void testSimpleGraphCollapsesEqualEdges() {
        RuleGraph graph = new RuleGraph("simple", false, this.factory);
        assertTrue(graph.isSimple());
        graph.addNode(this.n0);
        graph.addNode(this.n1);
        assertTrue(graph.addEdge(this.factory.createEdge(this.n0, this.a, this.n1)));
        assertFalse(graph.addEdge(this.factory.createEdge(this.n0, this.a, this.n1)));
        assertEquals(1, graph.edgeCount());
    }

    /** Rule graph morphisms preserve the parallel index, so parallel
     * copies keep distinct images. */
    @Test
    public void testMorphismPreservesParallelIndex() {
        RuleEdge e0 = this.factory.createEdge(this.n0, this.a, this.n1);
        RuleEdge e1 = this.factory.createEdge(this.n0, this.a, this.n1, 1);
        RuleGraphMorphism morphism = new RuleGraphMorphism(this.factory);
        RuleNode m0 = this.factory.createNode();
        RuleNode m1 = this.factory.createNode();
        morphism.putNode(this.n0, m0);
        morphism.putNode(this.n1, m1);
        RuleEdge i0 = morphism.mapEdge(e0);
        RuleEdge i1 = morphism.mapEdge(e1);
        assertNotNull(i0);
        assertNotNull(i1);
        assertNotEquals(i0, i1);
        assertEquals(0, i0.getNumber());
        assertEquals(1, i1.getNumber());
        assertEquals(m0, i0.source());
        assertEquals(m1, i0.target());
    }

    /** The typing step of rule compilation preserves the parallel index,
     * so a parallel bundle survives into the typed rule graph. */
    @Test
    public void testRuleTypingPreservesParallelIndex() throws FormatException {
        RuleGraph graph = new RuleGraph("untyped", false, false, this.factory);
        graph.addNode(this.n0);
        graph.addNode(this.n1);
        RuleEdge e0 = this.factory.createEdge(this.n0, this.a, this.n1);
        RuleEdge e1 = this.factory.createEdge(this.n0, this.a, this.n1, 1);
        graph.addEdge(e0);
        graph.addEdge(e1);
        ImplicitTypeGraph typeGraph = new ImplicitTypeGraph();
        typeGraph.addLabel("a");
        typeGraph.setFixed();
        RuleFactory typedFactory = RuleFactory.newInstance(typeGraph.getFactory());
        RuleGraphMorphism typing = typeGraph.analyzeRule(graph, new RuleGraphMorphism(typedFactory));
        RuleEdge t0 = typing.getEdge(e0);
        RuleEdge t1 = typing.getEdge(e1);
        assertNotNull(t0);
        assertNotNull(t1);
        assertNotEquals(t0, t1);
        assertEquals(0, t0.getNumber());
        assertEquals(1, t1.getNumber());
    }
}
