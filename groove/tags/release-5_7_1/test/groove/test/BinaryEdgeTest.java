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
 * $Id$
 */
package groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainFactory;
import groove.graph.plain.PlainLabel;
import groove.graph.plain.PlainNode;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version $Revision$ $Date: 2008-01-30 09:33:03 $
 */
@SuppressWarnings("all")
public class BinaryEdgeTest {
    static PlainNode n1, n2;
    static PlainEdge e;

    @BeforeClass
    public static void setUp() {
        n1 = PlainFactory.instance().createNode();
        n2 = PlainFactory.instance().createNode();
        e = PlainEdge.createEdge(n1, "a", n2);
    }

    @Test
    public void testQueries() {
        assertEquals(n1, e.source());
        assertEquals(PlainLabel.parseLabel(new String("a")), e.label());
        assertEquals(n2, e.target());
    }

    @Test
    public void testCreateAndCompare() {
        PlainEdge e2 = PlainEdge.createEdge(n1, "a", n2);

        assertEquals(e, e2);
        assertEquals(e2, e);

        assertTrue(!e.equals(PlainEdge.createEdge(n1, "a", n1)));
        assertTrue(!e.equals(PlainEdge.createEdge(n1, "b", n2)));
        assertTrue(!e.equals(PlainEdge.createEdge(n2, "a", n2)));
    }

    @Test
    public void testCopyAndCompare() {
        PlainEdge e2 =
            PlainEdge.createEdge(e.source(), e.label(), e.target());

        assertEquals(e, e2);
        assertEquals(e2, e);
    }

    @Test
    public void testHashSetEquals() {
        PlainEdge e2 = PlainEdge.createEdge(n1, "a", n2);

        Set<PlainEdge> s1 = new HashSet<>();
        s1.add(e);
        assertTrue(s1.contains(e2));

        Set<PlainEdge> s2 = new HashSet<>();
        s2.add(e2);
        assertEquals(s1, s2);
    }
}
