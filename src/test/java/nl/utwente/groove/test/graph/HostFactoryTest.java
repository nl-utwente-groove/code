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
package nl.utwente.groove.test.graph;

import java.util.Set;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;

import nl.utwente.groove.grammar.host.DefaultHostGraph;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.type.TypeFactory;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.util.AIGenerated;

/** Tests the content-indexed edge pool of a non-simple host factory (gh #905). */
@AIGenerated("Claude Fable 5, 2026-08")
public class HostFactoryTest {
    /** Tests that pooled edge creation returns the first content-equal copy
     * not hit by the exclusion predicate, minting only as a last resort. */
    @Test
    public void testPooledCreation() {
        HostFactory factory = HostFactory.newInstance(TypeFactory.newInstance(), false);
        DefaultHostGraph graph = new DefaultHostGraph("pool", factory);
        HostNode a = graph.addNode();
        HostNode b = graph.addNode();
        TypeLabel label = factory.createLabel("l");
        Predicate<HostEdge> inGraph = graph::containsEdge;
        // the first pooled creation mints the head copy
        HostEdge e0 = factory.createEdge(a, label, b, inGraph);
        assertContent(a, label, b, e0);
        // while the head is not excluded, it is returned again
        Assert.assertSame(e0, factory.createEdge(a, label, b, inGraph));
        graph.addEdge(e0);
        // with the head excluded, a second copy is minted ...
        HostEdge e1 = factory.createEdge(a, label, b, inGraph);
        assertContent(a, label, b, e1);
        Assert.assertNotEquals(e0, e1);
        // ... and returned as long as it is not excluded itself
        Assert.assertSame(e1, factory.createEdge(a, label, b, inGraph));
        graph.addEdge(e1);
        HostEdge e2 = factory.createEdge(a, label, b, inGraph);
        Assert.assertNotEquals(e0, e2);
        Assert.assertNotEquals(e1, e2);
        // an empty exclusion always returns the head
        Assert.assertSame(e0, factory.createEdge(a, label, b, edge -> false));
        // the choice is by minting order, not by exclusion history
        Assert.assertSame(e1, factory.createEdge(a, label, b, Set.of(e0)::contains));
    }

    /** Tests that a factory copy reproduces the pool of the original. */
    @Test
    public void testPooledCreationAfterCopy() {
        HostFactory factory = HostFactory.newInstance(TypeFactory.newInstance(), false);
        DefaultHostGraph graph = new DefaultHostGraph("pool", factory);
        HostNode a = graph.addNode();
        HostNode b = graph.addNode();
        TypeLabel label = factory.createLabel("l");
        HostEdge e0 = factory.createEdge(a, label, b, edge -> false);
        HostEdge e1 = factory.createEdge(a, label, b, Set.of(e0)::contains);
        Assert.assertNotEquals(e0, e1);
        HostFactory copy = factory.copy();
        // the copy answers pooled requests from the shared edge store
        Assert.assertSame(e0, copy.createEdge(a, label, b, edge -> false));
        Assert.assertSame(e1, copy.createEdge(a, label, b, Set.of(e0)::contains));
        // a mint in the copy does not affect the original
        HostEdge e2 = copy.createEdge(a, label, b, Set.of(e0, e1)::contains);
        Assert.assertNotEquals(e0, e2);
        Assert.assertNotEquals(e1, e2);
        Assert.assertSame(e2, copy.createEdge(a, label, b, Set.of(e0, e1)::contains));
    }

    /** Tests that edges registered under an explicit number (as when loading
     * a graph from disk) participate in the pool. */
    @Test
    public void testExplicitNumbersJoinPool() {
        HostFactory factory = HostFactory.newInstance(TypeFactory.newInstance(), false);
        DefaultHostGraph graph = new DefaultHostGraph("pool", factory);
        HostNode a = graph.addNode();
        HostNode b = graph.addNode();
        TypeLabel label = factory.createLabel("l");
        HostEdge e0 = factory.createEdge(a, label, b, 10);
        HostEdge e1 = factory.createEdge(a, label, b, 20);
        Assert.assertNotEquals(e0, e1);
        Assert.assertSame(e0, factory.createEdge(a, label, b, edge -> false));
        Assert.assertSame(e1, factory.createEdge(a, label, b, Set.of(e0)::contains));
        // a minted copy takes a low free number and from then on precedes the
        // explicitly numbered copies in the canonical (number) order
        HostEdge e2 = factory.createEdge(a, label, b, Set.of(e0, e1)::contains);
        Assert.assertTrue(e2.getNumber() < e0.getNumber());
        Assert.assertSame(e2, factory.createEdge(a, label, b, edge -> false));
        Assert.assertSame(e2, factory.copy().createEdge(a, label, b, edge -> false));
    }

    private void assertContent(HostNode source, TypeLabel label, HostNode target, HostEdge edge) {
        Assert.assertEquals(source, edge.source());
        Assert.assertEquals(target, edge.target());
        Assert.assertEquals(label, edge.label());
    }
}
