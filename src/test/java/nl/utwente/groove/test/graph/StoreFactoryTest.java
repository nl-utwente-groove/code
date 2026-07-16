/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostNode;

/**
 * Tests the number-indexed edge store of {@link nl.utwente.groove.graph.StoreFactory},
 * for simple and non-simple (multi-)edges.
 * @author Arend Rensink
 * @version $Revision$
 */
public class StoreFactoryTest {
    /** Tests that simple edges are pooled by content. */
    @Test
    public void testSimplePooling() {
        HostFactory factory = HostFactory.newInstance(true);
        HostNode n1 = factory.createNode();
        HostNode n2 = factory.createNode();
        HostEdge e1 = factory.createEdge(n1, "a", n2);
        HostEdge e2 = factory.createEdge(n1, "a", n2);
        assertSame(e1, e2);
        assertEquals(0, e1.getNumber());
        assertEquals(1, factory.getEdgeCount());
        HostEdge e3 = factory.createEdge(n1, "b", n2);
        assertNotSame(e1, e3);
        assertEquals(1, e3.getNumber());
        assertEquals(2, factory.getEdgeCount());
        assertSame(e1, factory.getEdge(0));
        assertSame(e3, factory.getEdge(1));
    }

    /** Tests numbered edge creation for simple edges. */
    @Test
    public void testSimpleNumberedCreate() {
        HostFactory factory = HostFactory.newInstance(true);
        HostNode n1 = factory.createNode();
        HostNode n2 = factory.createNode();
        HostEdge e1 = factory.createEdge(n1, factory.createLabel("a"), n2, 5);
        assertEquals(5, e1.getNumber());
        assertSame(e1, factory.getEdge(5));
        // requesting the same number and content returns the same edge
        assertSame(e1, factory.createEdge(n1, factory.createLabel("a"), n2, 5));
        // for simple edges, content takes precedence over the requested number
        assertSame(e1, factory.createEdge(n1, factory.createLabel("a"), n2, 7));
        assertFalse(factory.isUsedEdgeNr(7));
        // unnumbered creation of pooled content also returns the same edge
        assertSame(e1, factory.createEdge(n1, "a", n2));
        assertEquals(1, factory.getEdgeCount());
        // unnumbered creation of fresh content takes the first free number
        HostEdge e2 = factory.createEdge(n1, "b", n2);
        assertEquals(0, e2.getNumber());
    }

    /** Tests that non-simple edges with the same content stay distinct. */
    @Test
    public void testNonSimpleParallel() {
        HostFactory factory = HostFactory.newInstance(false);
        HostNode n1 = factory.createNode();
        HostNode n2 = factory.createNode();
        HostEdge e1 = factory.createEdge(n1, "a", n2);
        HostEdge e2 = factory.createEdge(n1, "a", n2);
        assertNotSame(e1, e2);
        assertNotEquals(e1, e2);
        assertEquals(0, e1.getNumber());
        assertEquals(1, e2.getNumber());
        assertEquals(2, factory.getEdgeCount());
        assertSame(e1, factory.getEdge(0));
        assertSame(e2, factory.getEdge(1));
    }

    /** Tests numbered edge creation and reuse for non-simple edges. */
    @Test
    public void testNonSimpleNumberedReuse() {
        HostFactory factory = HostFactory.newInstance(false);
        HostNode n1 = factory.createNode();
        HostNode n2 = factory.createNode();
        HostEdge e1 = factory.createEdge(n1, "a", n2);
        assertEquals(0, e1.getNumber());
        // requesting the number of an existing edge with matching content
        // returns that edge rather than a fresh parallel one
        assertSame(e1, factory.createEdge(n1, factory.createLabel("a"), n2, 0));
        assertEquals(1, factory.getEdgeCount());
        // an unused number gives a fresh parallel edge with that number
        HostEdge e2 = factory.createEdge(n1, factory.createLabel("a"), n2, 10);
        assertNotSame(e1, e2);
        assertEquals(10, e2.getNumber());
        assertSame(e2, factory.getEdge(10));
        // unnumbered creation still fills the lowest free number
        HostEdge e3 = factory.createEdge(n1, "a", n2);
        assertEquals(1, e3.getNumber());
        assertEquals(3, factory.getEdgeCount());
    }

    /** Tests that edge containment probes have no side effects. */
    @Test
    public void testContainsEdgeNoSideEffect() {
        HostFactory factory = HostFactory.newInstance(true);
        HostNode n1 = factory.createNode();
        HostNode n2 = factory.createNode();
        HostEdge e1 = factory.createEdge(n1, "a", n2);
        assertTrue(factory.containsEdge(e1));
        // an equal edge from another factory is not contained,
        // and probing for it does not change this factory
        HostFactory other = HostFactory.newInstance(true);
        HostNode m1 = other.createNode();
        HostNode m2 = other.createNode();
        HostEdge foreign = other.createEdge(m1, "a", m2);
        assertFalse(factory.containsEdge(foreign));
        assertEquals(1, factory.getEdgeCount());
        assertSame(e1, factory.getEdge(0));
    }
}
