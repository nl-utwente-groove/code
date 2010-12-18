// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

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
 * $Id: BinaryEdgeTest.java,v 1.4 2008-01-30 09:33:03 iovka Exp $
 */
package groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version $Revision$ $Date: 2008-01-30 09:33:03 $
 */
@SuppressWarnings("all")
public class BinaryEdgeTest {
    static DefaultNode n1, n2;
    static DefaultEdge e;

    @BeforeClass
    public static void setUp() {
        n1 = DefaultNode.createNode();
        n2 = DefaultNode.createNode();
        e = DefaultEdge.createEdge(n1, "a", n2);
    }

    @Test
    public void testQueries() {
        assertEquals(n1, e.source());
        assertEquals(DefaultLabel.createLabel(new String("a")), e.label());
        assertEquals(n2, e.target());
    }

    @Test
    public void testCreateAndCompare() {
        Edge e2 = DefaultEdge.createEdge(n1, "a", n2);

        assertEquals(e, e2);
        assertEquals(e2, e);

        assertTrue(!e.equals(DefaultEdge.createEdge(n1, "a", n1)));
        assertTrue(!e.equals(DefaultEdge.createEdge(n1, "b", n2)));
        assertTrue(!e.equals(DefaultEdge.createEdge(n2, "a", n2)));
    }

    @Test
    public void testCopyAndCompare() {
        Edge e2 = DefaultEdge.createEdge(e.source(), e.label(), e.target());

        assertEquals(e, e2);
        assertEquals(e2, e);
    }

    @Test
    public void testHashSetEquals() {
        Edge e2 = DefaultEdge.createEdge(n1, "a", n2);

        Set<Edge> s1 = new HashSet<Edge>();
        s1.add(e);
        assertTrue(s1.contains(e2));

        Set<Edge> s2 = new HashSet<Edge>();
        s2.add(e2);
        assertEquals(s1, s2);
    }
}
